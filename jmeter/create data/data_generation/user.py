from dataclasses import dataclass, asdict
from os import environ
from typing import Optional

from aiohttp import ClientSession
from faker import Faker

from .connection import make_call_endpoint

fake = Faker("es_MX")
BASE_URL_USER = environ.get("urlUser", "http://localhost:8080/api/v1/users")


@dataclass
class User:
    curp: str
    fullName: str
    email: str
    phoneNumber: str
    password: str
    rfc: Optional[str]


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
