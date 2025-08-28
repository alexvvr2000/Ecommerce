from dataclasses import dataclass, asdict
from os import environ
from typing import Optional
from uuid import uuid4

from aiohttp import ClientSession
from faker import Faker

from .connection import make_call_endpoint

fake = Faker("es_MX")
BASE_URL_USER = environ.get("urlUser", "http://localhost:8080/api/v1/users")


@dataclass
class User:
    curp: str
    full_name: str
    email: str
    phone_number: str
    password: str
    rfc: Optional[str]
    id: Optional[int] = None
    deleted: bool = False


def create_user(user_id: Optional[int] = None) -> User:
    return User(
        curp=str(uuid4())[:18],
        full_name=fake.name(),
        email=f"em_{str(uuid4()).replace("-", "")}@gmail.com",
        phone_number=fake.phone_number(),
        password=fake.password(
            length=12, special_chars=True, digits=True, upper_case=True, lower_case=True
        ),
        rfc=str(uuid4()),
        id=user_id
    )


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
