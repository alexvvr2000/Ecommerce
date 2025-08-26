from argparse import ArgumentParser
from asyncio import gather, run
from dataclasses import dataclass, asdict
from pathlib import Path
from typing import List, Optional, Any

from aiofiles import open
from aiohttp import ClientSession, ClientConnectorError, ClientError
from faker import Faker

parser = ArgumentParser()
parser.add_argument("-uu", "--urlUser", default="http://localhost:8080/api/v1/users", type=str)
parser.add_argument("-up", "--urlProduct", default="http://localhost:8080/api/v1/products", type=str)
parser.add_argument("-uo", "--urlOrder", default="http://localhost:8080/api/v1/orders", type=str)
parser.add_argument("-u", "--maxProductOrderQuantity", type=int)
parser.add_argument("-i", "--maxItemsOrder", type=int)
parser.add_argument("-o", "--maxUserOrders", type=int)
parser.add_argument("-c", "--maxUserCount", type=int)
parser.add_argument("-f", "--outputFolder", default=".", type=Path)
parser.add_argument("-eu", "--extraUsers", default=0, type=int)
parser.add_argument("-ep", "--extraProducts", default=0, type=int)

args = parser.parse_args()

MAX_ITEMS_ORDER = args.maxItemsOrder
MAX_PRODUCT_ORDER_QUANTITY = args.maxProductOrderQuantity
MAX_USER_COUNT = args.maxUserCount
MAX_USER_ORDERS = args.maxUserOrders
BASE_URL_USER = args.urlUser
BASE_URL_PRODUCT = args.urlProduct
BASE_URL_ORDER = args.urlOrder
OUTPUT_FOLDER = args.outputFolder
EXTRA_USERS = args.extraUsers
EXTRA_PRODUCTS = args.extraProducts

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


async def make_call_endpoint(session: ClientSession, endpoint: str, json: dict[str, Any]) -> dict[str, Any]:
    try:
        async with session.post(
                endpoint,
                json=json,
                headers={"Content-Type": "application/json"},
        ) as response:
            try:
                response_json = await response.json()
                return response_json
            except Exception as e:
                print(e)
    except ClientConnectorError:
        print(
            f"Couldn't connect to endpoint {endpoint}"
        )
    except ClientError as e:
        print(f"Client error: {e}")
    except Exception as e:
        print(f"Unexpected error: {e}")
    return dict()


async def post_product(client_session: ClientSession) -> int:
    product_dict = asdict(create_product())
    try:
        returned_data = await make_call_endpoint(client_session, BASE_URL_PRODUCT, product_dict)
        product_id: int = returned_data.get("id")
        print(f"New product: {product_id}")
        return product_id
    except Exception as e:
        print(e)
    return -1


async def post_user(client_session: ClientSession) -> int:
    user_dict = asdict(create_user())
    try:
        returned_data = await make_call_endpoint(client_session, BASE_URL_USER, user_dict)
        user_id: int = returned_data.get("id")
        print(f"New user: {user_id}")
        return user_id
    except Exception as e:
        print(e)
    return -1


async def select_order_items(client_session: ClientSession, product_amount: int) -> List[OrderItem]:
    product_list: List[OrderItem] = []
    for _ in range(0, product_amount):
        product_id = await post_product(client_session)
        product_list.append(
            OrderItem(product_id, fake.random_int(1, MAX_PRODUCT_ORDER_QUANTITY))
        )
    return product_list


async def create_order(client_session: ClientSession, user_id: int, product_amount: int) -> PersistedUserOrder:
    product_list: List[OrderItem] = await select_order_items(client_session, product_amount)
    new_order = Order(user_id, product_list)
    order_dict = asdict(new_order)
    try:
        returned_data = await make_call_endpoint(client_session, BASE_URL_ORDER, order_dict)
        order_id: int = returned_data.get("id")
        print(f"New order: {order_id}")
        return PersistedUserOrder(user_id, order_id)
    except Exception as e:
        print(e)
    return PersistedUserOrder(user_id, -1)


async def get_users_with_orders(client_session: ClientSession, max_user_orders: int, max_items_order: int) -> List[int]:
    async def create_user_orders() -> int:
        user_id = await post_user(client_session)
        for _ in range(0, max_user_orders):
            await create_order(client_session, user_id, fake.random_int(1, max_items_order))
        return user_id

    order_routines = [create_user_orders() for _ in range(0, MAX_USER_COUNT)]
    return await gather(*order_routines)


async def write_users_with_orders(client_session: ClientSession, output_folder: Path) -> Path:
    new_file = Path(output_folder, "data.csv")
    if Path.exists(new_file):
        Path.unlink(new_file)

    async with open(new_file, "w") as output_file:
        await output_file.write("user_id\n")
        user_list_int = await get_users_with_orders(client_session, MAX_USER_ORDERS, MAX_ITEMS_ORDER)
        user_list_str = [str(user_id) for user_id in user_list_int]
        user_data = "\n".join(user_list_str) + "\n"
        await output_file.write(user_data)

    return new_file


async def main() -> None:
    async with ClientSession() as session:
        users_coroutines = [post_user(session) for _ in range(EXTRA_USERS)] if EXTRA_USERS != 0 else []
        products_coroutines = [post_product(session) for _ in range(EXTRA_PRODUCTS)] if EXTRA_PRODUCTS != 0 else []
        await gather(*users_coroutines, *products_coroutines)
        csv_data_path = await write_users_with_orders(session, OUTPUT_FOLDER)
        print(f"New data in file: {csv_data_path}")


if __name__ == "__main__":
    run(main())
