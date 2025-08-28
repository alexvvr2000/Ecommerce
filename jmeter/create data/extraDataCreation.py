from argparse import ArgumentParser
from asyncio import run
from pathlib import Path
from typing import Generator

from data_generation.product import create_product, Product
from data_generation.user import create_user, User

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


def product_creator(quantity: int) -> Generator[tuple[Product, int], None, None]:
    for index in range(1, quantity + 1):
        yield create_product(), index


def user_creator(quantity: int) -> Generator[tuple[User, int], None, None]:
    for index in range(1, quantity + 1):
        yield create_user(), index


async def main() -> None:
    return


if __name__ == "__main__":
    run(main())
