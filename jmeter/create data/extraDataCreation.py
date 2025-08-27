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
        user_counter = EXTRA_USERS
        product_counter = EXTRA_PRODUCTS
        iterations = max(user_counter, product_counter)

        list_coroutines = []

        for index in range(0, iterations):
            if user_counter != 0:
                list_coroutines.append(post_user(session))
                print(f"created new coroutine post_user #{index + 1}")
                user_counter -= 1

            if product_counter != 0:
                list_coroutines.append(post_product(session))
                print(f"created new coroutine post_product #{index + 1}")
                product_counter -= 1

        list_iterator = batched(list_coroutines, MAX_CONCURRENT_OPERATIONS)
        for index, batched_list in enumerate(list_iterator):
            print(f"Batch number {index} started")
            await gather(*batched_list)


if __name__ == "__main__":
    run(main())
