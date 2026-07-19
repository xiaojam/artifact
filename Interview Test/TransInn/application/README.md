## How to Run

1. Make sure the main Xdock stack is running in the root directory:
~~~bash
docker-compose up -d
~~~

2. Navigate to the `application/` directory where this integration service resides:
~~~bash
cd application
~~~

3. Build the Spring Boot application (creates the `.jar` in the `target/` directory):
~~~bash
./mvnw clean package -DskipTests
~~~

4. Start the integration service container:
~~~bash
docker-compose up --build -d
~~~

The integration service will now be available on port `8090`.

## Usage

You can submit orders via the REST endpoint: `POST /api/v1/integration/forward`

Pass the desired channel as a query parameter `?channel=...`
(Available options: `REST`, `SOAP`, `SFTP_REST`, `SFTP_SOAP`, `REST_FAILING`).

### Sample cURL Request

~~~bash
curl --location --request POST 'http://localhost:8090/api/v1/integration/forward?channel=REST_FAILING' \
--header 'Content-Type: application/json' \
--data-raw '{
  "consignor": "ACME",
  "consignee": "Globex",
  "invoiceTo": "ACME",
  "notifyParty": "Globex",
  "collectionDate": "2026-06-10",
  "collectionAddress": "Rotterdam",
  "deliveryDate": "2026-06-12",
  "deliveryAddress": "Berlin",
  "palletType": "EUR",
  "quantity": 4,
  "weight": 1200,
  "loadMeters": 2.4
}'
~~~

---

## Debugging POST /orders/failing

I ran the Xdock stack and sent multiple requests to the `/orders/failing` endpoint using this integration service and monitored the application logs by executing:
~~~bash
docker exec -it <xdock_container_id> tail -f /var/log/app.log
~~~

Based on the application logs, the endpoint does not have a genuine technical or infrastructure issue. Instead, it is intentionally programmed to fail randomly. The following warning repeatedly appeared in the logs during the `Internal Server Error` responses:

> WARN <empty>.Main - Endpoint has been set up to fail on random interval. You've found the bug!

