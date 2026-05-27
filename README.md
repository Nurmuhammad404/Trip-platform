# Trip Platform

A multi-user travel management system built in Java with a client-server architecture, SQLite persistence, and role-based access control.

---

## Domain Entity Hierarchy

```
BaseEntity  (id: Long)
├── User        — username, email, password (SHA-256), fullName, phoneNumber, role
├── Flight      — flightNumber, departure, destination, departureTime, arrivalTime, price, availableSeats, airline
├── Hotel       — name, city, address, starRating, pricePerNight, availableRooms, amenities
├── Car         — brand, model, location, pricePerDay, available
├── Place       — name, city, category, description, rating
├── Tour        — name, description, price, duration, maxParticipants
├── Taxi        — driverName, city, pricePerKm, available, rating
└── Booking     — userId, serviceType, serviceId, bookingDate, status, totalPrice, customerName, customerEmail
```

---

## Storage Solution

| Storage       | Used by           | Configuration                  |
|---------------|-------------------|--------------------------------|
| SQLite (JDBC) | Server            | `db.path` in `app.properties`  |
| CSV files     | Console app       | `src/main/resources/data/`     |
| Aviationstack API | Server (flights only) | `api.aviationstack.key` |

Storage is switchable between SQLite and CSV via the `dao` property in `app.properties`:
```properties
dao=sqlite      # or: dao=csv
db.path=trip-platform.db
db.pool.size=5
```

The server always uses JDBC DAOs backed by SQLite. Flight data is fetched live from the [Aviationstack REST API](https://aviationstack.com) with CSV as fallback.

---

## Client–Server Communication Protocol

- **Transport:** TCP sockets
- **Format:** Line-delimited JSON — each request and response is a single JSON object followed by `\n`
- **Port:** `7777` (configurable via `server.port` in `app.properties`)

### Request structure
```json
{
  "command": "BOOK_HOTEL",
  "token":   "550e8400-e29b-41d4-a716-446655440000",
  "params":  { "id": "3" }
}
```

### Response structure
```json
{
  "status":  "OK",
  "message": "Hotel booked! Booking id: 12",
  "data":    { ... }
}
```

Response statuses: `OK`, `ERROR`, `UNAUTHORIZED`, `FORBIDDEN`

---

## Data Serialization / Deserialization

**Library:** [Google Gson](https://github.com/google/gson)

| Direction          | Where                    | Method                                         |
|--------------------|--------------------------|------------------------------------------------|
| Client → Server    | [ServerConnection.java](src/main/java/com/epam/trip/client/ServerConnection.java) | `gson.toJson(request)` → `PrintWriter.println` |
| Server → Client    | [ClientHandler.java](src/main/java/com/epam/trip/server/ClientHandler.java)       | `gson.toJson(response)` → `PrintWriter.println` |
| Server ← Client    | [ClientHandler.java](src/main/java/com/epam/trip/server/ClientHandler.java)       | `gson.fromJson(line, Request.class)`           |
| Client ← Server    | [ServerConnection.java](src/main/java/com/epam/trip/client/ServerConnection.java) | `gson.fromJson(line, Response.class)`          |

---

## Layers

### Server Layers

| Layer        | Classes                                                                 |
|--------------|-------------------------------------------------------------------------|
| **Protocol** | [Request](src/main/java/com/epam/trip/protocol/Request.java), [Response](src/main/java/com/epam/trip/protocol/Response.java), [Command](src/main/java/com/epam/trip/protocol/Command.java) |
| **Server**   | [Server](src/main/java/com/epam/trip/server/Server.java), [ClientHandler](src/main/java/com/epam/trip/server/ClientHandler.java) |
| **Auth**     | [AuthService](src/main/java/com/epam/trip/auth/AuthService.java), [AuthServiceImpl](src/main/java/com/epam/trip/auth/AuthServiceImpl.java), [SessionManager](src/main/java/com/epam/trip/auth/SessionManager.java), [Session](src/main/java/com/epam/trip/auth/Session.java) |
| **DAO**      | [BaseJdbcDao](src/main/java/com/epam/trip/dao/db/BaseJdbcDao.java), [JdbcUserDao](src/main/java/com/epam/trip/dao/db/JdbcUserDao.java), [JdbcHotelDao](src/main/java/com/epam/trip/dao/db/JdbcHotelDao.java), … |
| **DB Infra** | [ConnectionPool](src/main/java/com/epam/trip/dao/db/ConnectionPool.java), [DatabaseManager](src/main/java/com/epam/trip/dao/db/DatabaseManager.java) |
| **API**      | [FlightApiService](src/main/java/com/epam/trip/api/FlightApiService.java), [AviationstackClient](src/main/java/com/epam/trip/api/AviationstackClient.java) |
| **Entity**   | [BaseEntity](src/main/java/com/epam/trip/entity/BaseEntity.java), User, Flight, Hotel, Car, Place, Tour, Taxi, Booking |

### Client Layers

| Layer        | Classes                                                                 |
|--------------|-------------------------------------------------------------------------|
| **UI**       | [ClientApp](src/main/java/com/epam/trip/client/ClientApp.java) — console menus per role |
| **Protocol** | [ServerConnection](src/main/java/com/epam/trip/client/ServerConnection.java) — JSON serialization over TCP |
| **Auth**     | Token stored in `ServerConnection`; sent with every request after login |

---

## Client Roles

| Role        | Description                                  |
|-------------|----------------------------------------------|
| `VISITOR`   | Anonymous session — browse only              |
| `USER`      | Authenticated — browse, search, book, manage bookings |
| `ADMIN`     | Full access — all USER commands + user management and flight management |

Role hierarchy is enforced server-side in [`ClientHandler.requireRole()`](src/main/java/com/epam/trip/server/ClientHandler.java) using `session.hasRole(required)` which compares role ordinals (`VISITOR=0`, `USER=1`, `ADMIN=2`).

---

## Command Format

Commands are sent as JSON over TCP. The interactive client maps menu numbers to commands automatically. The wire format is:

```
{"command":"<COMMAND>","token":"<TOKEN_OR_NULL>","params":{"key":"value",...}}
```

### Commands by Role

#### VISITOR (no login required)

| Command          | Params           | Description                    |
|------------------|------------------|--------------------------------|
| `PING`           | —                | Health check                   |
| `HELP`           | —                | List available commands        |
| `REGISTER`       | `username`, `email`, `password`, `fullName`, `phone` | Create account |
| `LOGIN`          | `username`, `password` | Authenticate; returns `token` |
| `LIST_FLIGHTS`   | —                | List all flights (live API)    |
| `LIST_HOTELS`    | —                | List all hotels                |
| `LIST_CARS`      | —                | List all cars                  |
| `LIST_PLACES`    | —                | List all tourist places        |
| `LIST_TOURS`     | —                | List all tours                 |
| `LIST_TAXIS`     | —                | List all taxis                 |
| `SEARCH_FLIGHTS` | `departure`, `destination` | Filter flights by route |
| `SEARCH_HOTELS`  | `city`           | Filter hotels by city          |
| `SEARCH_CARS`    | `location`       | Filter cars by location        |
| `SEARCH_PLACES`  | `city` or `category` | Filter places              |
| `SEARCH_TAXIS`   | `city`           | Filter taxis by city           |

#### USER (requires login)

All VISITOR commands, plus:

| Command           | Params                   | Description                    |
|-------------------|--------------------------|--------------------------------|
| `BOOK_FLIGHT`     | `id`                     | Book a flight by ID            |
| `BOOK_HOTEL`      | `id`                     | Book a hotel room              |
| `RENT_CAR`        | `id`                     | Rent a car                     |
| `BOOK_TAXI`       | `id`                     | Book a taxi                    |
| `VIEW_BOOKINGS`   | —                        | List own bookings              |
| `CANCEL_BOOKING`  | `id`                     | Cancel a booking               |
| `ADD_HOTEL`       | `name`, `city`, `address`, `stars`, `price`, `rooms`, `amenities` | Add hotel |
| `ADD_CAR`         | `brand`, `model`, `location`, `price` | Add car            |
| `ADD_PLACE`       | `name`, `city`, `category`, `description`, `rating` | Add place |
| `ADD_TOUR`        | `name`, `description`, `price`, `duration`, `maxParticipants` | Add tour |
| `ADD_TAXI`        | `driver`, `city`, `pricePerKm`, `rating` | Add taxi          |
| `DELETE_HOTEL`    | `id`                     | Delete a hotel                 |
| `DELETE_CAR`      | `id`                     | Delete a car                   |
| `DELETE_PLACE`    | `id`                     | Delete a place                 |
| `DELETE_TOUR`     | `id`                     | Delete a tour                  |
| `DELETE_TAXI`     | `id`                     | Delete a taxi                  |
| `LOGOUT`          | —                        | End session                    |

#### ADMIN (requires admin account)

All USER commands, plus:

| Command            | Params                    | Description                   |
|--------------------|---------------------------|-------------------------------|
| `ADD_FLIGHT`       | `flightNumber`, `departure`, `destination`, `depTime`, `arrTime`, `price`, `seats`, `airline` | Add flight (in-memory) |
| `DELETE_FLIGHT`    | `id`                      | Remove flight (in-memory)     |
| `LIST_USERS`       | —                         | List all users                |
| `ADD_USER`         | `username`, `email`, `password`, `role` | Create user    |
| `DELETE_USER`      | `id`                      | Delete a user                 |
| `UPDATE_USER_ROLE` | `id`, `role`              | Change user role              |

---

## java.util.concurrent Entities

| Entity | Layer | File | Purpose |
|--------|-------|------|---------|
| [`ExecutorService`](src/main/java/com/epam/trip/server/Server.java#L32) (CachedThreadPool) | Server | [Server.java](src/main/java/com/epam/trip/server/Server.java) | Spawns one thread per connected client |
| [`AtomicInteger`](src/main/java/com/epam/trip/server/Server.java#L35) | Server | [Server.java](src/main/java/com/epam/trip/server/Server.java) | Thread-safe client ID counter |
| [`volatile boolean`](src/main/java/com/epam/trip/server/Server.java#L36) | Server | [Server.java](src/main/java/com/epam/trip/server/Server.java) | Shutdown flag visible across threads |
| [`ConcurrentHashMap`](src/main/java/com/epam/trip/auth/SessionManager.java#L8) | Auth | [SessionManager.java](src/main/java/com/epam/trip/auth/SessionManager.java) | Thread-safe session store (token → Session) |
| `synchronized` + `wait/notifyAll` | DB Infra | [ConnectionPool.java](src/main/java/com/epam/trip/dao/db/ConnectionPool.java) | Block callers when pool is exhausted; wake them on release |

---

## Request Synchronization

### Connection Pool (DB layer)

All DB access goes through [`ConnectionPool`](src/main/java/com/epam/trip/dao/db/ConnectionPool.java):

- [`acquire()`](src/main/java/com/epam/trip/dao/db/ConnectionPool.java#L30) — `synchronized`; if pool is empty and max connections reached, calls `wait(remaining)` until a connection is released (5-second timeout)
- [`release(conn)`](src/main/java/com/epam/trip/dao/db/ConnectionPool.java#L54) — `synchronized`; returns connection to pool and calls `notifyAll()` to wake waiting threads
- [`closeAll()`](src/main/java/com/epam/trip/dao/db/ConnectionPool.java#L61) — `synchronized`; safely closes all connections on shutdown

### DatabaseManager Singleton (DB layer)

[`DatabaseManager.getInstance()`](src/main/java/com/epam/trip/dao/db/DatabaseManager.java#L26) is `synchronized` to ensure safe lazy initialization under concurrent access.

### Session Store (Auth layer)

[`SessionManager`](src/main/java/com/epam/trip/auth/SessionManager.java) uses `ConcurrentHashMap` — all `put`, `get`, and `remove` operations are atomic without explicit locking.

---

## Logging

**Library:** SLF4J + Logback ([logback.xml](src/main/resources/logback.xml))

| Appender | Output | Pattern |
|----------|--------|---------|
| `CONSOLE` | stdout | `[time] LEVEL logger — message` |
| `FILE`    | `logs/trip-platform.log` | Rolling daily, 7-day retention |

Log levels:
- `DEBUG` — `com.epam.trip` package (connection pool, request handling)
- `INFO` — server startup/shutdown, client connect/disconnect
- `ERROR` — unhandled exceptions, DB errors

Logged events include: server start/stop, each client connect/disconnect, login/logout/register, DB connection lifecycle.

---

## Unit Test Coverage

**Framework:** JUnit 5 + Mockito

| Layer      | Test class                                                                              | What is tested |
|------------|-----------------------------------------------------------------------------------------|----------------|
| Auth       | [AuthServiceImplTest](src/test/java/com/epam/trip/auth/AuthServiceImplTest.java)        | Login, registration, duplicate detection, logout — DAO mocked with Mockito |
| Validation | [FlightValidatorTest](src/test/java/com/epam/trip/validation/FlightValidatorTest.java)  | OOP-style validator: valid/invalid flight data, ValidationResult accumulation |

Mock objects are used in `AuthServiceImplTest` via `@Mock GenericDao<User>` and `@ExtendWith(MockitoExtension.class)`.

---

## Integration Tests

| Test class | What is tested |
|------------|----------------|
| [JdbcUserDaoIntegrationTest](src/test/java/com/epam/trip/dao/JdbcUserDaoIntegrationTest.java) | Full CRUD cycle against a real in-memory SQLite database (`jdbc:sqlite::memory:`) using `@BeforeAll` / `@AfterAll` lifecycle and ordered test execution (`@TestMethodOrder`) |

Run all tests:
```bash
mvn test
```

---

## Running the Application

### Start the server
```bash
mvn exec:java -Dexec.mainClass="com.epam.trip.Main"
# or
.\run.ps1
```

### Start a client (in a separate terminal)
```bash
mvn exec:java -Dexec.mainClass="com.epam.trip.client.ClientApp"
# with custom host/port:
mvn exec:java -Dexec.mainClass="com.epam.trip.client.ClientApp" -Dexec.args="localhost 7777"
```

Default admin credentials: `admin` / `admin123`

---

## Other Implementation Details

- **Connection pool** — fixed-size pool with blocking acquire and `wait/notify` (no busy-spin). Pool size is configurable (`db.pool.size`).
- **DataSeeder** — [`DataSeeder.seedIfEmpty()`](src/main/java/com/epam/trip/startup/DataSeeder.java) populates the SQLite DB with sample data on first run.
- **Password hashing** — SHA-256 via [`AuthServiceImpl.hash()`](src/main/java/com/epam/trip/auth/AuthServiceImpl.java).
- **Graceful shutdown** — `Runtime.getRuntime().addShutdownHook(...)` in [`Server.java`](src/main/java/com/epam/trip/server/Server.java#L80) ensures DB connections are closed and thread pool is shut down on Ctrl+C.
- **Flight data** — served live from Aviationstack REST API with retry logic (`api.retry.count`); falls back to empty list on API failure.
- **Design patterns** — Factory (DaoFactory, ServiceFactory, ControllerFactory), Singleton (DatabaseManager, SessionManager), Template Method (BaseJdbcDao), Strategy (DataSource), DAO.

---

## Author

**Nurmuhammad**
