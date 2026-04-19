# Profile Intelligence Service

A REST API that accepts a name, enriches it with demographic data from external APIs, and stores the result. Built with **Java**, **Javalin**, **MySQL**, and deployed on **AWS EC2** behind **Nginx**.

---

## Tech Stack

- **Java 21** with Javalin (lightweight HTTP framework)
- **MySQL 8** running in Docker
- **HikariCP** for connection pooling
- **Jackson** for JSON serialization
- **UUID v7** via `uuid-creator` for time-ordered IDs
- **Maven Shade Plugin** for fat JAR packaging
- **Nginx** as reverse proxy
- **Certbot** for SSL

---

## External APIs

| API | Endpoint | Data extracted |
|---|---|---|
| Genderize | `https://api.genderize.io?name={name}` | `gender`, `gender_probability`, `sample_size` |
| Agify | `https://api.agify.io?name={name}` | `age`, `age_group` |
| Nationalize | `https://api.nationalize.io?name={name}` | `country_id`, `country_probability` |

No API keys required.

---

## Project Structure

```
src/main/java/com/yourapp/
├── Main.java
├── controller/
│   └── PersonProfileController.java
├── dao/
│   └── PersonProfileDao.java
├── db/
│   └── Database.java
├── dto/
│   ├── ApiResponse.java
│   ├── ExternalApiResponses.java
├── exception/
│   └── ExternalApiException.java
├── model/
│   ├── PersonProfile.java
│   └── PersonProfileSummary.java
└── service/
    ├── EnrichmentService.java
    └── PersonProfileService.java
```

---

## API Endpoints

### `POST /api/profiles`

Accepts a name, calls all three external APIs, and stores the enriched result.
Idempotent — submitting the same name twice returns the existing record.

**Request:**
```json
{ "name": "emmanuel" }
```

**Response 201 (created):**
```json
{
  "status": "success",
  "data": {
    "id": "b3f9c1e2-7d4a-4c91-9c2a-1f0a8e5b6d12",
    "name": "emmanuel",
    "gender": "male",
    "gender_probability": 0.99,
    "sample_size": 1234,
    "age": 25,
    "age_group": "adult",
    "country_id": "NG",
    "country_probability": 0.85,
    "created_at": "2026-04-01T12:00:00Z"
  }
}
```

**Response 200 (already exists):**
```json
{
  "status": "success",
  "message": "Profile already exists",
  "data": { "...existing profile..." }
}
```

**Response 502 (external API failure):**
```json
{
  "status": "502",
  "message": "Genderize returned an invalid response"
}
```

---

### `GET /api/profiles`

Returns all profiles. Supports optional query parameters for filtering.

| Param | Example |
|---|---|
| `gender` | `?gender=male` |
| `country_id` | `?country_id=NG` |
| `age_group` | `?age_group=adult` |

Params can be combined: `/api/profiles?gender=male&country_id=NG`

**Response 200:**
```json
{
  "status": "success",
  "data": [
    {
      "id": "b3f9c1e2-7d4a-4c91-9c2a-1f0a8e5b6d12",
      "name": "emmanuel",
      "gender": "male",
      "age": 25,
      "age_group": "adult",
      "country_id": "NG"
    }
  ]
}
```

---

### `GET /api/profiles/{id}`

Returns a single profile by ID.

**Response 200:** full profile object
**Response 404:** `{ "status": "error", "message": "Profile not found" }`

---

### `DELETE /api/profiles/{id}`

Deletes a profile by ID.

**Response 204:** no content
**Response 404:** `{ "status": "error", "message": "Profile not found" }`

---

## Age Group Classification

| Range | Group |
|---|---|
| 0 – 12 | `child` |
| 13 – 19 | `teenager` |
| 20 – 59 | `adult` |
| 60+ | `senior` |

---

## Running Locally

### 1. Start MySQL

```bash
docker run -d \
  --name mysql \
  --restart unless-stopped \
  -e MYSQL_ROOT_PASSWORD=secret \
  -e MYSQL_DATABASE=myappdb \
  -e MYSQL_USER=appuser \
  -e MYSQL_PASSWORD=apppass \
  -p 3306:3306 \
  mysql:8
```

### 2. Build the fat JAR

```bash
mvn clean package -DskipTests
```

### 3. Run

```bash
java -jar target/your-app-1.0-SNAPSHOT.jar
```

The app starts on `http://localhost:8080` and runs schema migrations automatically on startup.

---

## Deployment (AWS EC2 + Nginx)

### Run as a systemd service

```bash
sudo nano /etc/systemd/system/profile-api.service
```

```ini
[Unit]
Description=Profile Intelligence API
After=network.target

[Service]
User=ubuntu
WorkingDirectory=/home/ubuntu/your-project
ExecStart=java -jar /home/ubuntu/your-project/target/your-app-1.0-SNAPSHOT.jar
Restart=on-failure
RestartSec=5
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
```

```bash
sudo systemctl daemon-reload
sudo systemctl enable profile-api
sudo systemctl start profile-api
```

### Nginx config (`/etc/nginx/conf.d/backend.conf`)

```nginx
server {
    listen 80;
    server_name your-domain.duckdns.org;

    location / {
        proxy_pass         http://localhost:8080;
        proxy_http_version 1.1;
        proxy_set_header   Host              $host;
        proxy_set_header   X-Real-IP         $remote_addr;
        proxy_set_header   X-Forwarded-For   $proxy_add_x_forwarded_for;
    }
}
```

### SSL with Certbot

```bash
sudo apt install -y certbot python3-certbot-nginx
sudo certbot --nginx -d your-domain.duckdns.org
```

---

## Useful Commands

```bash
# Rebuild and redeploy
mvn clean package -DskipTests && sudo systemctl restart profile-api

# View live logs
sudo journalctl -u profile-api -f

# Check MySQL
docker ps
docker logs mysql
```
