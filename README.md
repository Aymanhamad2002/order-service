# Order Service

The order backend for Polar Bookshop. It uses Spring WebFlux and Spring Data R2DBC to process orders reactively, PostgreSQL for persistence, and Flyway for schema migrations.

## Order workflow

1. Look up the requested ISBN in Catalog Service.
2. Persist an accepted order when a book is found, or a rejected order when the catalog lookup returns no book.
3. Publish accepted order IDs to RabbitMQ destination `order-accepted`.
4. Consume `order-dispatched` events using the `dispatchOrder` function and update the stored status.

## API

| Method | Path | Purpose |
| --- | --- | --- |
| GET | `/orders` | List orders belonging to the authenticated user |
| POST | `/orders` | Submit an order |

API requests require a valid JWT. A submission body contains `isbn` and `quantity`, where quantity is between 1 and 5:

```json
{"isbn": "9781617297731", "quantity": 1}
```

## Run locally

Start PostgreSQL, RabbitMQ, Keycloak, and Catalog Service using the [project setup](https://github.com/Aymanhamad2002/edge-service#run-locally). The database `polardb_order` must exist.

```powershell
.\gradlew.bat bootRun
```

The service listens on `http://localhost:9002`. Start Dispatcher Service to complete the asynchronous dispatch workflow. Health and metrics are available at `/actuator/health` and `/actuator/prometheus`.

| Environment override | Default |
| --- | --- |
| `SPRING_R2DBC_URL` | `r2dbc:postgresql://localhost:5432/polardb_order` |
| `SPRING_R2DBC_USERNAME` / `SPRING_R2DBC_PASSWORD` | Local demo database credentials |
| `SPRING_FLYWAY_URL` | `jdbc:postgresql://localhost:5432/polardb_order` |
| `POLAR_CATALOG_SERVICE_URI` | `http://localhost:9001` |
| `SPRING_RABBITMQ_HOST` / `SPRING_RABBITMQ_PORT` | `localhost` / `5672` |
| `SPRING_RABBITMQ_USERNAME` / `SPRING_RABBITMQ_PASSWORD` | Local demo broker credentials |
| `SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI` | `http://localhost:8080/realms/PolarBookshop` |

Flyway uses JDBC separately from the R2DBC connection. Override both URLs when changing database hosts. Its credentials default to the R2DBC credentials through configuration references.

## Related repositories

| Repository | Responsibility |
| --- | --- |
| [edge-service](https://github.com/Aymanhamad2002/edge-service) | Gateway, login, sessions, and full project overview |
| [catalog-service](https://github.com/Aymanhamad2002/catalog-service) | Book catalog and PostgreSQL persistence |
| [order-service](https://github.com/Aymanhamad2002/order-service) | Orders and asynchronous dispatch tracking |
| [dispatcher-service](https://github.com/Aymanhamad2002/dispatcher-service) | Event-driven packing and labeling |

## Build and test

Java 17 is configured in the Gradle toolchain. Use the included Gradle wrapper:

```powershell
.\gradlew.bat test
.\gradlew.bat bootJar
.\gradlew.bat bootBuildImage
```

On macOS/Linux, use `./gradlew` instead. Building container images requires Docker. Catalog and order tests include database integration tests; have Docker available for Testcontainers.

## Development configuration

The values in `src/main/resources/application.yml` are local development defaults. Demo credentials such as `user` / `password` are public, disposable values for local infrastructure; never reuse them for shared environments or real data.

Spring Boot allows environment variables to override YAML values without changing the file. For any shared or production environment, supply unique credentials externally through deployment configuration or a secret manager, and do not commit them.

See [Spring Boot externalized configuration](https://docs.spring.io/spring-boot/reference/features/external-config.html) and [OWASP secrets management](https://cheatsheetseries.owasp.org/cheatsheets/Secrets_Management_Cheat_Sheet.html).

