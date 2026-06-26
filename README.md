# Snip — a tiny URL shortener 🔗

Paste a long URL → get a short code back (`snip/abc123`) → every visit to that
code redirects to the original link. Built with **Spring Boot 3**, **Spring Data
JPA**, and **H2 / MySQL**.

A small, readable project that demonstrates the core of how bit.ly works.

---

## What it does
- **Shorten** any long URL into a 6-character code (or your own custom alias).
- **Redirect** (`HTTP 302`) from the short code to the original URL.
- **Count clicks** on every redirect.
- A clean **web UI** plus a JSON **REST API**.

## Tech stack
| Concern | Choice |
|---|---|
| Language / build | Java 17, Maven |
| Framework | Spring Boot 3.3 (Web, Validation) |
| Persistence | Spring Data JPA (Hibernate) |
| Database | H2 (default, file-based) · MySQL (profile) |
| Frontend | Static HTML + vanilla JS |
| Tests | JUnit 5, MockMvc |

---

## Run it

### Option A — Maven Wrapper (no Maven install needed)
```bash
./mvnw spring-boot:run          # macOS/Linux
mvnw.cmd spring-boot:run        # Windows
```

### Option B — your own Maven
```bash
mvn spring-boot:run
```

Then open **http://localhost:8080**.

> Uses an H2 file database at `./data/getit.*`, so your links survive restarts.
> The H2 web console is disabled by default (it must never be public on a
> deployed app). To browse the tables locally, start with it enabled:
> `./mvnw spring-boot:run -Dspring-boot.run.arguments=--spring.h2.console.enabled=true`
> then open http://localhost:8080/h2-console (JDBC URL `jdbc:h2:file:./data/getit`, user `sa`, no password).

### Run against MySQL instead
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```
Configure credentials in `src/main/resources/application-mysql.properties`
(or via `MYSQL_USER` / `MYSQL_PASSWORD` env vars). The `getit` database is
created automatically.

### Build a runnable jar
```bash
mvn clean package
java -jar target/get-it-1.0.0.jar
```

---

## REST API

### Create a short link
```bash
curl -X POST http://localhost:8080/api/shorten \
  -H "Content-Type: application/json" \
  -d '{"url": "https://spring.io/projects/spring-boot"}'
```
```json
{
  "shortCode": "a3Kf9Q",
  "shortUrl": "http://localhost:8080/a3Kf9Q",
  "brandedUrl": "snip/a3Kf9Q",
  "longUrl": "https://spring.io/projects/spring-boot",
  "createdAt": "2026-06-25T10:00:00Z",
  "clickCount": 0
}
```
Optional custom alias: add `"alias": "my-link"` to the body.

### Use the short link
```
GET http://localhost:8080/a3Kf9Q   ->   302 redirect to the long URL
```

### Other endpoints
| Method | Path | Description |
|---|---|---|
| `GET` | `/api/urls/{code}` | Stats for one link |
| `GET` | `/api/urls?limit=20` | Recent links |
| `DELETE` | `/api/urls/{code}` | Delete a link |

---

## How it's structured
```
com.getit
├── controller   UrlRestController (API) · RedirectController (the 302 redirect)
├── service      UrlService (logic) · ShortCodeGenerator (random Base62 codes)
├── repository   UrlMappingRepository (Spring Data JPA)
├── model        UrlMapping (@Entity)
├── dto          CreateUrlRequest · UrlResponse
├── exception    Not-found / bad-request + GlobalExceptionHandler
└── config       AppProperties (app.* settings)
```

### Design notes
- **Short codes** are random 6-char Base62 strings; the service checks the DB for
  uniqueness and retries on the (very rare) collision. Custom aliases are
  supported and validated.
- The **redirect** path pattern `/{code:[A-Za-z0-9_-]{1,16}}` deliberately
  excludes dots, so it never shadows static files (`/index.html`, `/favicon.ico`).
- URLs are **normalised** (a missing scheme defaults to `https://`) and validated
  before saving.
- Switching databases is just a **profile** — no code changes.

## Tests
```bash
mvn test
```
Covers code generation, click counting, alias rules, URL validation, and the
shorten → redirect flow (MockMvc). Tests run on an in-memory H2 database.
