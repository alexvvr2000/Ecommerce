from argparse import ArgumentParser
from asyncio import run
from typing import Generator
from data_generation.product import create_product, Product
from data_generation.user import create_user, User
from pathlib import Path

parser = ArgumentParser()
parser.add_argument("-eu", "--extraUsers", type=int)
parser.add_argument("-ep", "--extraProducts", type=int)
parser.add_argument("-of", "--outputFolder", type=Path, default=500)

args = parser.parse_args()

EXTRA_USERS: int = args.extraUsers
EXTRA_PRODUCTS: int = args.extraProducts
MAX_CONCURRENT_OPERATIONS: int = args.maxConcurrentOperations
OUTPUT_FOLDER: Path = args.outputFolder

if not OUTPUT_FOLDER.is_dir():
    raise NotADirectoryError()

def product_creator(quantity: int) -> Generator[Product, None, None]:
    for _ in range(0, quantity):
        yield create_product()

def user_creator(quantity: int) -> Generator[User, None, None]:
    for _ in range(0, quantity):
        yield create_user()

async def main() -> None:
    return


if __name__ == "__main__":
    run(main())
