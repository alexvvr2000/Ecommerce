from asyncio import gather
from dataclasses import dataclass, asdict
from itertools import batched
from os import environ
from typing import List

from aiohttp import ClientSession
from faker import Faker

from .connection import make_call_endpoint
from .product import post_product

fake = Faker("es_MX")
BASE_URL_ORDER = environ.get("urlOrder", "http://localhost:8080/api/v1/orders")


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


async def select_order_items(
        client_session: ClientSession, product_amount: int, product_quantity: int
        , batch_size: int
) -> List[
    OrderItem]:
    product_list: List[OrderItem] = []
    product_batch = [
        post_product(client_session)
        for _ in range(0, product_amount)
    ]
    for list_product_tasks in batched(product_batch, batch_size):
        current_products = await gather(*list_product_tasks)
        product_list.extend([
            OrderItem(product_id, fake.random_int(1, product_quantity))
            for product_id in current_products
        ])
    return product_list


async def create_order(client_session: ClientSession, user_id: int, product_amount: int,
                       product_quantity: int, batch_size: int) -> PersistedUserOrder:
    product_list: List[OrderItem] = await select_order_items(
        client_session, product_amount, product_quantity, batch_size
    )
    new_order = Order(user_id, product_list)
    order_dict = asdict(new_order)
    try:
        returned_data = await make_call_endpoint(client_session, BASE_URL_ORDER, order_dict)
        order_id: int = returned_data.get("id")  # type: ignore
        print(f"New order: {order_id}")
        return PersistedUserOrder(user_id, order_id)
    except Exception as e:
        print(e)
    return PersistedUserOrder(user_id, -1)
