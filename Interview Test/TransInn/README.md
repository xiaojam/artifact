# XDock Take-Home Assignment

A small order-intake service used for candidate screening. The application accepts
transport orders over three channels — a JSON REST API, a SOAP/XML endpoint, and an
SFTP drop directory — and exposes the results of file imports over HTTP.

## Architecture

The stack is defined in `docker-compose.yml` and consists of two services:

| Service  | Description | Ports |
|----------|-------------|-------|
| `app`    | The order-intake service. | `8080` (HTTP API + Swagger UI) |
| `sftpgo` | An [SFTPGo](https://github.com/drakkan/sftpgo) server. Clients upload order files here; uploads land in a volume shared with the app. | `2022` (SFTP), `8081` (web admin) |

The two services share the `orders-data` volume: files uploaded by the SFTP user
`orders` end up in `/data/orders` inside the app container, where the app picks them
up for import (configured via the `ORDER_IMPORT_DIR` environment variable).

The compose file also defines the **`order-network`** Docker network. Any services
you add as part of the assignment should connect to this network to reach the app.

## Getting started

The order-intake stack is started via Docker Compose.

- **API base URL:** http://localhost:8080
- **OpenAPI / Swagger UI:** http://localhost:8080/docs/ (raw spec at http://localhost:8080/docs/docs.yaml)
- **SFTP:** `sftp -P 2022 orders@localhost` (credentials: `orders` / `orders`, user provisioned from `sftpgo/orders-user.json`)
- **SFTPGo admin UI:** http://localhost:8081 (credentials: `admin` / `adminpass`)

## Endpoints

| Endpoint | Description |
|----------|-------------|
| `POST /orders` | Creates an order from a JSON body. Returns `201` with the created order (including a generated UUID `id`), or `400` with a list of validation errors. |
| `POST /orders/failing` | Same contract as `POST /orders`, but may also return `500 Internal Server Error`. |
| `POST /soap/orders` | Creates an order from a SOAP/XML request. Returns a `soap:Envelope` wrapping the created order, or a `soap:Fault` listing validation errors. |
| `GET /imports` | Returns the per-file import results from the SFTP drop directory — for each file, either the parsed order or an error message. |
| `GET /docs/` | Swagger UI with the full OpenAPI 3.1 specification, including request/response schemas and examples. |

## Logs

Application logs are written to `/var/log/app.log` inside the `app` container
(configured via the `LOG_FILE` environment variable in `docker-compose.yml` and the
`Dockerfile`).

## Configuration

| Environment variable | Default | Purpose |
|----------------------|---------|---------|
| `ORDER_IMPORT_DIR` | `/data/orders` | Directory watched for incoming order files (shared with SFTPGo) |
| `LOG_FILE` | `/var/log/app.log` | Path of the application log file |

## Assignment

Create a Java Spring Boot application inside the `application/` directory of this
repository that integrates with the xdock app.

The time limit will be communicated to candidates via a separate message.

### Definition of done

A runnable application with at least the required tasks completed — the more
tasks, the better.

### Required tasks

- Integrate with the `POST /orders` and `POST /soap/orders` endpoints, and submit
  order files via SFTP.
- Your application is tested via a single REST endpoint that accepts the
  integration channel as an enum value and forwards the order over the
  corresponding xdock channel:
  - `REST` — `POST /orders`
  - `SOAP` — `POST /soap/orders`
  - `SFTP_REST` — upload the order as a JSON file via SFTP
  - `SFTP_SOAP` — upload the order as an XML file via SFTP
- SFTP uploads must be validated via the xdock app.
- Any errors from the xdock app must be propagated to the caller of your
  implementation.
- Set up OpenAPI documentation for your application.
- Cover one part of your application with unit tests.

### Optional tasks

- Integrate reliably with the `POST /orders/failing` endpoint: make your
  integration tolerate its failures so that callers of your application still get
  their orders created. You do not need to know why the endpoint fails to do this.
- Dockerize your application, using a docker-compose file separate from the one in
  the project root.
- Set up logging for your implementation that is suitable for production use.
- Debug the `POST /orders/failing` endpoint and find the root cause of its
  failures. This is possible without looking at the source code of the xdock app.