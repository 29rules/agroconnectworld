# AgroConnectWorld

[![CI](https://github.com/29rules/AgroConnectWorld/actions/workflows/ci.yml/badge.svg)](https://github.com/29rules/AgroConnectWorld/actions/workflows/ci.yml)

A full-stack B2B agricultural marketplace with an AI-powered CEO portal backed by a multi-agent AI company (LangChain4j).

---

## Stack

| Layer | Tech |
|-------|------|
| Frontend | React 19, Vite, Bootstrap 5 |
| Gateway | Spring Cloud Gateway |
| Microservices | Spring Boot 3.3 (Java 21) × 6 |
| Database | PostgreSQL 16 (one schema per service) |
| AI Agents | LangChain4j 0.29 + OpenRouter/OpenAI |
| Infrastructure | Docker Compose, Nginx |

---

## Quick Start

### 1. Prerequisites

- [Docker Desktop](https://docs.docker.com/get-docker/)
- [OpenRouter API key](https://openrouter.ai/) (free tier — for AI agents)

### 2. Configure

```bash
cp ops/.env.example ops/.env
# Edit ops/.env — set OPENROUTER_API_KEY and ADMIN_SECRET

cp frontend/.env.example frontend/.env
# Edit frontend/.env — set VITE_OPENROUTER_API_KEY (optional, for chatbot)
```

### 3. Start

```bash
./start.sh
```

### 4. Seed data (first run only)

```bash
./start.sh seed
```

---

## URLs

| URL | What |
|-----|------|
| http://localhost:8080 | Main app |
| http://localhost:8080/admin/ceo | CEO Portal + AI Agents |
| http://localhost:8087/actuator/health | AI Company health |

## Demo Accounts (password: `Password123!`)

| Email | Role |
|-------|------|
| ceo@agroconnect.com | CEO — full AI portal |
| buyer@agroconnect.com | BUYER |
| supplier@agroconnect.com | SUPPLIER |
| admin@agroconnect.com | ADMIN |

---

## Promote a User to CEO

```bash
curl -X POST "http://localhost:8080/api/auth/promote-ceo?email=you@example.com" \
  -H "X-Admin-Secret: your-admin-secret-from-env"
```

Then log out and back in to get a fresh JWT with the CEO role.

---

## CEO Portal / AI Agents

Navigate to `/admin/ceo` and use the **AI Chat** tab to talk to agents:

- `"What is the current system architecture?"`
- `"Create a sprint plan for adding payment integration"`
- `"CTO, initiate the full AgroConnectWorld system audit"`

Available agents: CTO, AI Architect, AI Engineer, DevOps, Product Manager, QA, Scrum Master.

---

## Other Commands

```bash
./start.sh down      # stop everything
./start.sh status    # check service health
./start.sh local     # print local dev instructions (no Docker)
```

---

## Project Layout

```
AgroConnectWorld/
├── start.sh                 ← One-command startup
├── frontend/                ← React + Vite
├── backend/
│   ├── gateway/             ← Spring Cloud Gateway (port 8080)
│   ├── auth-service/        ← JWT auth (port 8081)
│   ├── product-service/     ← (port 8082)
│   ├── supplier-service/    ← (port 8083)
│   ├── quote-service/       ← (port 8084)
│   ├── order-service/       ← (port 8085)
│   └── contact-service/     ← (port 8086)
├── ai-company/              ← AI agents — LangChain4j (port 8087)
└── ops/
    ├── docker-compose.yml
    ├── .env.example
    ├── seed.sql             ← Demo data
    └── nginx/
```

---

## Architecture

```
Browser → Nginx :8080
            ├── /         → Frontend (React)
            ├── /api/*    → Gateway → microservices → PostgreSQL
            └── /ai/*     → AI Company → OpenRouter → LLM
```

JWT tokens signed with a shared secret validated at Gateway and AI Company layers.
CEO role required for /admin/ceo routes and all /ai/* endpoints.

---

## Security Notes for Production

- Remove or restrict `/api/auth/promote-ceo` — use DB migration for roles instead
- Rotate `JWT_SECRET`: `openssl rand -base64 32`
- Never commit `ops/.env` to git (it is gitignored)
