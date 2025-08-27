from argparse import ArgumentParser
from asyncio import gather, run
from itertools import batched

from aiohttp import ClientSession

from data_generation.product import post_product
from data_generation.user import post_user

parser = ArgumentParser()
parser.add_argument("-eu", "--extraUsers", type=int)
parser.add_argument("-ep", "--extraProducts", type=int)
parser.add_argument("-mc", "--maxConcurrentOperations", type=int, default=500)

args = parser.parse_args()

EXTRA_USERS = args.extraUsers
EXTRA_PRODUCTS = args.extraProducts
MAX_CONCURRENT_OPERATIONS = args.maxConcurrentOperations


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

        list_coroutines = users_coroutines + products_coroutines
        list_iterator = batched(list_coroutines, MAX_CONCURRENT_OPERATIONS)
        for index, batched_list in enumerate(list_iterator):
            print(f"Batch number {index} started")
            await gather(*batched_list)


if __name__ == "__main__":
    run(main())
