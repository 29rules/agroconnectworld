# How to Start AI Company API

## Quick Start

The AI Company API must be running on **port 8087** for the CEO Portal to work.

### Option 1: Maven (Recommended)

```bash
cd ai-company
mvn spring-boot:run
```

### Option 2: Run JAR (if built)

```bash
cd ai-company
mvn clean package
java -jar target/ai-company-1.0.0.jar
```

### Option 3: Docker (if configured)

```bash
docker compose up ai-company
```

## Verify It's Running

Once started, verify with:

```bash
curl http://localhost:8087/actuator/health
```

Should return: `{"status":"UP"}`

## Configuration

- **Port:** 8087 (configurable via `AI_COMPANY_PORT` env var)
- **API Key:** Set `OPENAI_API_KEY` or `OPENROUTER_API_KEY` environment variable
- **Base URL:** `http://localhost:8087`

## Troubleshooting

**Port already in use:**
```bash
lsof -i :8087
kill -9 <PID>
```

**API Key missing:**
```bash
export OPENAI_API_KEY=your-key-here
# or
export OPENROUTER_API_KEY=your-key-here
```

**Check logs:**
The API will show startup logs in the terminal where you ran `mvn spring-boot:run`

## Expected Output

When started successfully, you should see:
```
Started ApiApplication in X.XXX seconds
```

Then the CEO Portal should be able to connect to `/ai/ctochat` endpoint.



