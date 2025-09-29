from argparse import ArgumentParser
from asyncio import run
from dataclasses import dataclass
from faker import Faker
from pandas import DataFrame
from pathlib import Path
from typing import Generator

from data_generation.product import create_product, Product
from data_generation.user import create_user, User

parser = ArgumentParser()
parser.add_argument("-eu", "--extraUsers", type=int)
parser.add_argument("-ep", "--extraProducts", type=int)
parser.add_argument("-of", "--outputFolder", type=Path, default=500)
parser.add_argument("-puf", "--platformUserFile", type=str, default="platform_user.csv")
parser.add_argument("-pf", "--passwordsFile", type=str, default="platform_user_password.csv")
parser.add_argument("-prf", "--productFile", type=str, default="product.csv")

args = parser.parse_args()

EXTRA_USERS: int = args.extraUsers
EXTRA_PRODUCTS: int = args.extraProducts
OUTPUT_FOLDER: Path = args.outputFolder

if not OUTPUT_FOLDER.is_dir():
    raise NotADirectoryError()


@dataclass
class PlatformUserPasswordEntry:
    id: int
    password: str
    platform_user_id: int


def product_creator(quantity: int) -> Generator[Product, None, None]:
    for index in range(1, quantity + 1):
        yield create_product(index)


def user_creator(quantity: int) -> Generator[User, None, None]:
    for index in range(1, quantity + 1):
        yield create_user(index)


def password_creator(quantity: int) -> Generator[PlatformUserPasswordEntry, None, None]:
    fake = Faker("es_MX")

    for index in range(1, quantity + 1):
        yield PlatformUserPasswordEntry(
            id=index,
            password=fake.password(
                length=12,
                special_chars=True, digits=True, upper_case=True, lower_case=True
            ),
            platform_user_id=index
        )


async def main() -> None:
    platform_user_csv_file: Path = Path(OUTPUT_FOLDER, args.platformUserFile)
    password_user_csv_file: Path = Path(OUTPUT_FOLDER, args.passwordsFile)
    product_csv_file: Path = Path(OUTPUT_FOLDER, args.productFile)

    product_df = DataFrame(product_creator(EXTRA_PRODUCTS))
    user_df = DataFrame(user_creator(EXTRA_USERS))
    password_df = DataFrame(password_creator(EXTRA_USERS))

    product_df = product_df.rename(columns={
        'mdFormatDescription': 'md_format_description',
        'mainImageUrl': 'main_image_url'
    })

    user_df = user_df.rename(columns={
        'fullName': 'full_name',
        'phoneNumber': 'phone_number'
    })

    product_df.to_csv(product_csv_file, index=False, encoding="utf-8")
    user_df.to_csv(platform_user_csv_file, index=False, encoding="utf-8")
    password_df.to_csv(password_user_csv_file, index=False, encoding="utf-8")


if __name__ == "__main__":
    run(main())
