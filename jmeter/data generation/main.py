from dataclasses import dataclass, asdict
from decimal import Decimal
from typing import List, Optional
from faker import Faker
from aiohttp import ClientSession, ClientConnectorError, ClientError
from asyncio import gather, run

MAX_ITEMS_ORDER = 15
MAX_PRODUCT_ORDER_QUANTITY = 20
MAX_USER_COUNT = 10
MAX_USER_ORDERS = 30
BASE_URL_USER = "http://localhost:8080/api/v1/users"
BASE_URL_PRODUCT = "http://localhost:8080/api/v1/products"
BASE_URL_ORDER = "http://localhost:8080/api/v1/orders"

fake = Faker("es_MX")


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
                    id = response_json.get("id")
                    print(f"New product with id {id}")
                    return id
                except Exception as e:
                    print(e)

    except ClientConnectorError:
        print(
            "Error: No se pudo conectar al servidor. ¿Está corriendo en localhost:8080?"
        )
    except ClientError as e:
        print(f"Error de cliente: {e}")
    except Exception as e:
        print(f"Error inesperado: {e}")
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
                    id = response_json.get("id")
                    print(f"New user with id {id}")
                    return id
                except Exception as e:
                    print(e)

    except ClientConnectorError:
        print(
            "Error: No se pudo conectar al servidor. ¿Está corriendo en localhost:8080?"
        )
    except ClientError as e:
        print(f"Error de cliente: {e}")
    except Exception as e:
        print(f"Error inesperado: {e}")
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
                    id = response_json.get("id")
                    print(f"New order with id {id}")
                    return PersistedUserOrder(user_id, id)
                except Exception as e:
                    print(e)

    except ClientConnectorError:
        print(
            "Error: No se pudo conectar al servidor. ¿Está corriendo en localhost:8080?"
        )
    except ClientError as e:
        print(f"Error de cliente: {e}")
    except Exception as e:
        print(f"Error inesperado: {e}")
    return PersistedUserOrder(user_id, -1)

async def get_users_with_orders() -> List[int]:
    async def create_user_orders() -> int:
        user_id = await post_user()
        for _ in range(0, MAX_USER_ORDERS):
            await create_order(user_id, fake.random_int(1, MAX_ITEMS_ORDER))
        return user_id

    order_routines = [create_user_orders() for _ in range(0, MAX_USER_COUNT)]
    return await gather(*order_routines)

async def get_order_product_average_price(user_id: int) -> tuple[int, Decimal]:
    print(f"Process started for user {user_id}")
    url_query = f"{BASE_URL_ORDER}/getAverageProductPrice?userId={user_id}"
    try:
        async with ClientSession() as session:
            async with session.post(
                url_query,
                headers={"Content-Type": "application/json"},
            ) as response:
                print(f"Status order: {response.status}")
                if response.status == 200:
                    response_text = await response.text()
                    
                    try:
                        average_price = Decimal(response_text.strip())
                        print(f"Average price for user {user_id}: {average_price}")
                        return (user_id, average_price)
                    
                    except Exception as e:
                        print(f"Error converting response to Decimal: {e}")
                        return (user_id, Decimal("-1"))
                
                else:
                    print(f"Error response status: {response.status}")
                    return (user_id, Decimal("-1"))

    except ClientConnectorError:
        print(
            "Error: No se pudo conectar al servidor. ¿Está corriendo en localhost:8080?"
        )
    except ClientError as e:
        print(f"Error de cliente: {e}")
    except Exception as e:
        print(f"Error inesperado: {e}")
    return (-1, Decimal("-1"))

async def main() -> None:
    order_list = await get_users_with_orders()
    print(f"Created {len(order_list)} users with orders")
    
    # Usar asyncio.gather en lugar de threads
    tasks = [get_order_product_average_price(user_id) for user_id in order_list]
    results = await gather(*tasks, return_exceptions=True)
    
    print("All results:", results)

if __name__ == "__main__":
    run(main())
