from dataclasses import dataclass, asdict
from typing import List, Optional
from faker import Faker
from aiohttp import ClientSession, ClientConnectorError, ClientError
from asyncio import gather, run
from argparse import ArgumentParser
from aiofiles import open
from pathlib import Path

parser = ArgumentParser()
parser.add_argument("-i", "--maxItemsOrder", type=int)
parser.add_argument("-u", "--maxProductOrderQuantity", type=int)
parser.add_argument("-c", "--maxUserCount", type=int)
parser.add_argument("-o", "--maxUserOrders", type=int)
parser.add_argument("-uu", "--urlUser", default="http://localhost:8080/api/v1/users", type=str)
parser.add_argument("-up", "--urlProduct", default="http://localhost:8080/api/v1/products", type=str)
parser.add_argument("-uo", "--urlOrder", default="http://localhost:8080/api/v1/orders", type=str)
parser.add_argument("-f", "--outputFolder", default=".", type=Path)

args = parser.parse_args()

MAX_ITEMS_ORDER = args.maxItemsOrder
MAX_PRODUCT_ORDER_QUANTITY = args.maxProductOrderQuantity
MAX_USER_COUNT = args.maxUserCount
MAX_USER_ORDERS = args.maxUserOrders
BASE_URL_USER = args.urlUser
BASE_URL_PRODUCT = args.urlProduct
BASE_URL_ORDER = args.urlOrder
OUTPUT_FOLDER = args.outputFolder

fake = Faker("es_MX")

if not Path.is_dir(OUTPUT_FOLDER):
    raise NotADirectoryError("You must")


@dataclass
class Product:
    name: str
    price: float
    mdFormatDescription: str
    mainImageUrl: str


@dataclass
class User:
    curp: str
    fullName: str
    email: str
    phoneNumber: str
    password: str
    rfc: Optional[str]


@dataclass
class OrderItem:
    productId: int
    quantity: int


@dataclass
class Order:
    platformUserId: int
    orderItems: List[OrderItem]


@dataclass
class PersistedUserOrder:
    persistedUserId: int
    persistedOrderId: int


def create_product() -> Product:
    return Product(
        name=fake.word().title() + " " + fake.word().title(),
        price=fake.pyfloat(
            left_digits=3,
            right_digits=2,
            positive=True,
            min_value=50.0,
            max_value=500.0,
        ),
        mdFormatDescription=f"**{fake.text(max_nb_chars=100)}**",
        mainImageUrl=fake.image_url(),
    )


def create_user() -> User:
    return User(
        curp=fake.curp(),
        fullName=fake.name(),
        email=fake.email(),
        phoneNumber=fake.phone_number(),
        password=fake.password(
            length=12, special_chars=True, digits=True, upper_case=True, lower_case=True
        ),
        rfc=fake.rfc(),
    )


async def post_product() -> int:
    product_dict = asdict(create_product())
    try:
        async with ClientSession() as session:
            async with session.post(
                BASE_URL_PRODUCT,
                json=product_dict,
                headers={"Content-Type": "application/json"},
            ) as response:
                print(f"Status post_user: {response.status}")
                try:
                    response_json = await response.json()
                    product_id = response_json.get("id")
                    print(f"New product with id {product_id}")
                    return product_id
                except Exception as e:
                    print(e)

    except ClientConnectorError:
        print(
            f"Couldn't connect to endpoint {BASE_URL_PRODUCT}"
        )
    except ClientError as e:
        print(f"Client error: {e}")
    except Exception as e:
        print(f"Unexpected error: {e}")
    return -1


async def post_user() -> int:
    user_dict = asdict(create_user())
    try:
        async with ClientSession() as session:
            async with session.post(
                BASE_URL_USER,
                json=user_dict,
                headers={"Content-Type": "application/json"},
            ) as response:
                print(f"Status post_user: {response.status}")
                try:
                    response_json = await response.json()
                    user_id = response_json.get("id")
                    print(f"New user with id {user_id}")
                    return user_id
                except Exception as e:
                    print(e)

    except ClientConnectorError:
        print(
            f"Couldn't connect to endpoint {BASE_URL_USER}"
        )
    except ClientError as e:
        print(f"Error de cliente: {e}")
    except Exception as e:
        print(f"Unexpected error: {e}")
    return -1


async def create_order(user_id: int, product_amount: int) -> PersistedUserOrder:
    product_list: List[OrderItem] = []
    for _ in range(0, product_amount):
        product_id = await post_product()
        product_list.append(
            OrderItem(product_id, fake.random_int(1, MAX_PRODUCT_ORDER_QUANTITY))
        )

    new_order = Order(user_id, product_list)
    order_dict = asdict(new_order)
    try:
        async with ClientSession() as session:
            async with session.post(
                BASE_URL_ORDER,
                json=order_dict,
                headers={"Content-Type": "application/json"},
            ) as response:
                print(f"Status order: {response.status}")
                try:
                    response_json = await response.json()
                    order_id = response_json.get("id")
                    print(f"New order with id {order_id}")
                    return PersistedUserOrder(user_id, order_id)
                except Exception as e:
                    print(e)

    except ClientConnectorError:
        print(
            f"Couldn't connect to endpoint {BASE_URL_ORDER}"
        )
    except ClientError as e:
        print(f"Client error: {e}")
    except Exception as e:
        print(f"Unexpected error: {e}")
    return PersistedUserOrder(user_id, -1)

async def get_users_with_orders() -> List[int]:
    async def create_user_orders() -> int:
        user_id = await post_user()
        for _ in range(0, MAX_USER_ORDERS):
            await create_order(user_id, fake.random_int(1, MAX_ITEMS_ORDER))
        return user_id

    order_routines = [create_user_orders() for _ in range(0, MAX_USER_COUNT)]
    return await gather(*order_routines)

async def write_user_id(output_folder: Path) -> Path:
    new_file = Path(output_folder, "data.csv")
    if Path.exists(new_file):
        Path.unlink(new_file)

    async with open(new_file, "w") as output_file:
        await output_file.write("user_id\n")
        user_list_int = await get_users_with_orders()
        user_list_str = [str(user_id) for user_id in user_list_int]
        user_data = "\n".join(user_list_str) + "\n"
        await output_file.write(user_data)

    return new_file

async def main() -> None:
    csv_data_path = await write_user_id(OUTPUT_FOLDER)
    print(f"New data in file: {csv_data_path}")

if __name__ == "__main__":
    run(main())
