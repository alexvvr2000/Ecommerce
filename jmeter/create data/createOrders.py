from argparse import ArgumentParser
from asyncio import gather, run
from pathlib import Path

from aiofiles import open
from aiohttp import ClientSession
from faker import Faker

from data_generation.order import create_order
from data_generation.user import post_user

parser = ArgumentParser()

parser.add_argument("-f", "--outputFolder", default=".", type=Path)
parser.add_argument("-c", "--maxUserCount", type=int)
parser.add_argument("-o", "--maxUserOrders", type=int)
parser.add_argument("-i", "--maxItemsOrder", type=int)
parser.add_argument("-u", "--maxProductOrderQuantity", type=int)
parser.add_argument("-pc", "--maxProductCount", type=int)

args = parser.parse_args()

OUTPUT_FOLDER = args.outputFolder
MAX_USER_COUNT = args.maxUserCount
MAX_USER_ORDERS = args.maxUserOrders
MAX_ITEMS_ORDER = args.maxItemsOrder
MAX_PRODUCT_ORDER_QUANTITY = args.maxProductOrderQuantity
MAX_PRODUCT_COUNT = args.maxProductCount

fake = Faker("es_MX")


async def get_users_with_orders(
        client_session: ClientSession,
        max_user_orders: int,
        max_items_order: int,
        max_item_count: int
) -> list[int]:
    async def create_user_orders() -> int:
        user_id = await post_user(client_session)
        for _ in range(0, max_user_orders):
            await create_order(
                client_session,
                user_id,
                fake.random_int(1, max_items_order),
                fake.random_int(1, max_item_count)
            )
        return user_id

    order_routines = [create_user_orders() for _ in range(0, MAX_USER_COUNT)]
    return await gather(*order_routines)


async def write_users_with_orders(client_session: ClientSession, output_folder: Path) -> Path:
    new_file = Path(output_folder, "data.csv")
    if Path.exists(new_file):
        Path.unlink(new_file)

    async with open(new_file, "w") as output_file:
        await output_file.write("user_id\n")
        user_list_int = await get_users_with_orders(
            client_session,
            MAX_USER_ORDERS,
            MAX_ITEMS_ORDER,
            MAX_PRODUCT_COUNT
        )
        user_list_str = [str(user_id) for user_id in user_list_int]
        user_data = "\n".join(user_list_str) + "\n"
        await output_file.write(user_data)

    return new_file


async def main() -> None:
    async with ClientSession() as session:
        csv_data_path = await write_users_with_orders(session, OUTPUT_FOLDER)
        print(f"New data in file: {csv_data_path}")


if __name__ == "__main__":
    run(main())
