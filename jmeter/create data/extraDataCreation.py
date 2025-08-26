from argparse import ArgumentParser
from asyncio import gather, run

from aiohttp import ClientSession

from data_generation.product import post_product
from data_generation.user import post_user

parser = ArgumentParser()
parser.add_argument("-eu", "--extraUsers", type=int)
parser.add_argument("-ep", "--extraProducts", type=int)

args = parser.parse_args()

EXTRA_USERS = args.extraUsers
EXTRA_PRODUCTS = args.extraProducts


async def main() -> None:
    async with ClientSession() as session:
        users_coroutines = []
        products_coroutines = []

        if EXTRA_USERS != 0:
            for i in range(EXTRA_USERS):
                users_coroutines.append(post_user(session))
                print(f"created new coroutine post_user #{i + 1}")

        if EXTRA_PRODUCTS != 0:
            for i in range(EXTRA_PRODUCTS):
                products_coroutines.append(post_product(session))
                print(f"created new coroutine post_product #{i + 1}")
        await gather(*users_coroutines, *products_coroutines)


if __name__ == "__main__":
    run(main())
