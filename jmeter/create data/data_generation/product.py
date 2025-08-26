from dataclasses import dataclass, asdict
from os import environ

from aiohttp import ClientSession
from faker import Faker

from .connection import make_call_endpoint

fake = Faker("es_MX")
BASE_URL_PRODUCT = environ.get("urlProduct", "http://localhost:8080/api/v1/products")


@dataclass
class Product:
    name: str
    price: float
    mdFormatDescription: str
    mainImageUrl: str


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


async def post_product(client_session: ClientSession) -> int:
    product_dict = asdict(create_product())
    try:
        returned_data = await make_call_endpoint(client_session, BASE_URL_PRODUCT, product_dict)
        product_id: int = returned_data.get("id")  # type: ignore
        print(f"New product: {product_id}")
        return product_id
    except Exception as e:
        print(e)
    return -1
