# Genderize API

A lightweight REST API built with Java and Javalin that predicts gender from a given name using the [Genderize.io](https://genderize.io) public API.

## Tech Stack

- **Java 21**
- **Javalin** — embedded HTTP server
- **Gson** — JSON parsing
- **Maven** — build tool
- **Nginx** — reverse proxy
- **AWS EC2** — deployment (Ubuntu)

## Project Structure

```
src/
  main/java/
    Main.java                 # Server setup, routes, exception handlers
    GenderizeService.java     # Genderize API call + response formatting
    MissingNameException.java # Thrown when name param is missing or empty
    InvalidNameException.java # Thrown when name contains non-alphabetic characters
pom.xml
```

## Endpoint

### `GET /api/classify`

Predicts the gender of a given name.

**Query Parameters**

| Parameter | Type   | Required | Description        |
|-----------|--------|----------|--------------------|
| `name`    | string | Yes      | The name to query  |

**Success Response — 200 OK**

```json
{
  "status": "success",
  "data": {
    "name": "John",
    "gender": "male",
    "probability": 0.99,
    "sample_size": 1234,
    "is_confident": true,
    "processed_at": "2026-04-15T13:00:00Z"
  }
}
```

**Fields**

| Field          | Description                                                  |
|----------------|--------------------------------------------------------------|
| `gender`       | `male` or `female`                                           |
| `probability`  | Confidence score from Genderize (0 to 1)                     |
| `sample_size`  | Number of samples Genderize used (renamed from `count`)      |
| `is_confident` | `true` if probability >= 0.7 AND sample_size >= 100          |
| `processed_at` | UTC ISO 8601 timestamp generated at request time             |

## Error Responses

| Scenario                              | Status | Response                                              |
|---------------------------------------|--------|-------------------------------------------------------|
| Missing or empty `name`               | 400    | `{ "status": "error", "message": "..." }`             |
| Non-alphabetic `name` (e.g. `John1`)  | 422    | `{ "status": "error", "message": "..." }`             |
| Genderize API failure                 | 502    | `{ "status": "error", "message": "..." }`             |
| Unexpected server error               | 500    | `{ "status": "error", "message": "..." }`             |

## Running Locally

**Prerequisites:** Java 21+, Maven

```bash
# Clone the repo
git clone <your-repo-url>
cd stagezero

# Build
mvn clean package -DskipTests

# Run
java -jar target/genderize-api.jar
```

The server starts on `http://localhost:8080`.

```bash
# Example request
curl "http://localhost:8080/api/classify?name=John"
```

## Deployment on AWS EC2

### Prerequisites

- EC2 instance (Ubuntu)
- Nginx installed
- Java 21 installed: `sudo apt install -y openjdk-21-jdk`

### Steps

**1. Build the fat JAR locally**
```bash
mvn clean package -DskipTests
```

**2. Copy the JAR to EC2**
```bash
scp -i your-key.pem target/genderize-api.jar ubuntu@<EC2-IP>:/home/ubuntu/app/
```

**3. Create a systemd service**
```bash
sudo nano /etc/systemd/system/genderize-api.service
```

```ini
[Unit]
Description=Genderize API
After=network.target

[Service]
User=ubuntu
WorkingDirectory=/home/ubuntu/app
ExecStart=/usr/bin/java -jar /home/ubuntu/app/genderize-api.jar
Restart=on-failure
RestartSec=5

[Install]
WantedBy=multi-user.target
```

```bash
sudo systemctl daemon-reload
sudo systemctl enable genderize-api
sudo systemctl start genderize-api
```

**4. Configure Nginx**

```nginx
server {
    listen 80;
    server_name <your-ec2-public-ip>;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

```bash
sudo systemctl reload nginx
```

### Redeploying a New Version

```bash
# Locally
mvn clean package -DskipTests
scp -i your-key.pem target/genderize-api.jar ubuntu@<EC2-IP>:/home/ubuntu/app/

# On EC2
sudo systemctl restart genderize-api
```