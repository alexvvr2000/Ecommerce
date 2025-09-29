from aiohttp import ClientSession, ClientConnectorError, ClientError
from typing import Any


async def make_call_endpoint(session: ClientSession, endpoint: str, json: dict[str, Any]) -> dict[str, Any]:
    try:
        async with session.post(
                endpoint,
                json=json,
                headers={"Content-Type": "application/json"},
        ) as response:
            try:
                response_json = await response.json()
                return response_json
            except Exception as e:
                print(e)
    except ClientConnectorError:
        print(
            f"Couldn't connect to endpoint {endpoint}"
        )
    except ClientError as e:
        print(f"Client error: {e}")
    except Exception as e:
        print(f"Unexpected error: {e}")
    return dict()
