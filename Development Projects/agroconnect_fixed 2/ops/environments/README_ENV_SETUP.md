# Environment Setup Guide

This guide explains how to set up each environment with SSL certificates, environment variables, and testing.

---

## Quick Start

### 1. Development Environment

```bash
cd ops/environments/dev
cp .env.example .env
# Edit .env with your values
docker compose -f docker-compose.dev.yml up -d
```

**Access:**
- Frontend: http://dev.agroconnectworld.com:8080
- pgAdmin: http://localhost:5050 (with `--profile tools`)

---

### 2. UAT Environment

```bash
cd ops/environments/uat
cp .env.example .env
# Edit .env with your values (database, JWT secret, etc.)
docker compose -f docker-compose.uat.yml up -d

# Run QA tests
docker compose -f docker-compose.uat.yml --profile qa up qa-test-runner

# Approval workflow
./approval-workflow.sh init
./approval-workflow.sh approve qa-lead
./approval-workflow.sh approve ceo
./approval-workflow.sh approve cto
```

**Access:**
- Frontend: http://uat.agroconnectworld.com:8080

---

### 3. Pre-Production (Staging) Environment

#### Step 1: Set Environment Variables

```bash
cd ops/environments/preprod
cp .env.example .env
# Edit .env with your values
```

#### Step 2: Set Up SSL Certificates

**Option A: Let's Encrypt (Recommended)**
```bash
cd ssl
./setup-ssl.sh letsencrypt
```

**Option B: Self-Signed (Testing Only)**
```bash
cd ssl
./setup-ssl.sh self-signed
```

**Option C: Manual Setup**
See `ssl/manual-setup.md` for detailed instructions.

#### Step 3: Start Services

```bash
cd ..
docker compose -f docker-compose.preprod.yml up -d

# With monitoring
docker compose -f docker-compose.preprod.yml --profile monitoring up -d
```

**Access:**
- Frontend: https://staging.agroconnectworld.com
- Prometheus: http://staging.agroconnectworld.com:9090
- Grafana: http://staging.agroconnectworld.com:3000

---

### 4. Production Environment

#### Step 1: Set Environment Variables

```bash
cd ops/environments/production
cp .env.example .env
# Edit .env with PRODUCTION values
# ⚠️  Use strong, unique passwords and secrets!
```

#### Step 2: Set Up SSL Certificates

**Option A: Let's Encrypt (Recommended)**
```bash
cd ssl
./setup-ssl.sh letsencrypt
```

**Option B: Commercial Certificate**
See `ssl/manual-setup.md` for instructions.

#### Step 3: Start Services

```bash
cd ..
docker compose -f docker-compose.prod.yml up -d

# With load balancer
docker compose -f docker-compose.prod.yml --profile load-balancer up -d

# With backup service
docker compose -f docker-compose.prod.yml --profile backup up -d
```

**Access:**
- Frontend: https://www.agroconnectworld.com
- Metrics: https://www.agroconnectworld.com/metrics (internal only)

---

## SSL Certificate Setup

### Staging (Pre-Production)

```bash
cd ops/environments/preprod/ssl
./setup-ssl.sh letsencrypt
# Or for testing: ./setup-ssl.sh self-signed
```

### Production

```bash
cd ops/environments/production/ssl
./setup-ssl.sh letsencrypt
```

**Manual Setup:**
See `ops/environments/{preprod,production}/ssl/manual-setup.md`

---

## Environment Variables

### Required Variables

Each environment requires a `.env` file with:

**Common:**
- `DB_USERNAME` - Database username
- `DB_PASSWORD` - Database password
- `JWT_SECRET` - JWT signing secret (min 32 chars for production)
- `VITE_OPENROUTER_API_KEY` - OpenRouter API key for chatbot

**Environment-Specific:**
- See `.env.example` files in each environment directory

### Creating .env Files

```bash
# Development
cd ops/environments/dev
cp .env.example .env
# Edit .env

# UAT
cd ops/environments/uat
cp .env.example .env
# Edit .env

# Pre-Production
cd ops/environments/preprod
cp .env.example .env
# Edit .env

# Production
cd ops/environments/production
cp .env.example .env
# Edit .env with PRODUCTION values
```

**⚠️  Security:**
- Never commit `.env` files to version control
- Use strong, unique passwords
- Rotate secrets regularly
- Use secrets management in production (AWS Secrets Manager, HashiCorp Vault)

---

## Testing Blue-Green Deployment

### Test in Staging

```bash
cd ops/deployment
./test-blue-green.sh
```

This will:
1. Validate deployment scripts
2. Test health check functionality
3. Verify rollback capability
4. Check Docker Compose configuration

### Actual Deployment Test

```bash
# Ensure staging is running
cd ops/environments/preprod
docker compose -f docker-compose.preprod.yml up -d

# Test deployment
cd ../../deployment
./blue-green-deploy.sh preprod gateway

# Monitor deployment
./health-check.sh preprod all

# Rollback if needed
./rollback.sh preprod gateway
```

---

## Monitoring & Alerting Setup

### Step 1: Configure Monitoring

```bash
cd ops/monitoring
cp .env.example .env
# Edit .env with your values
```

### Step 2: Configure Alerting

**Slack:**
1. Create Slack webhook (see `ALERTING_SETUP.md`)
2. Add to `.env`: `SLACK_WEBHOOK_URL=...`

**Email:**
1. Configure SMTP settings in `.env`
2. For Gmail: Use App Password (not regular password)

See `ops/monitoring/ALERTING_SETUP.md` for detailed instructions.

### Step 3: Start Monitoring Stack

```bash
cd ops/monitoring
docker compose -f docker-compose.monitoring.yml up -d
```

**Access:**
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000 (admin/admin)
- Alertmanager: http://localhost:9093

### Step 4: Test Alerts

```bash
# Test Slack alert
curl -X POST http://localhost:9093/api/v1/alerts \
  -H "Content-Type: application/json" \
  -d '[{"labels":{"alertname":"TestAlert","severity":"critical"}}]'

# Test email alert
curl -X POST http://localhost:9093/api/v1/alerts \
  -H "Content-Type: application/json" \
  -d '[{"labels":{"alertname":"TestEmail","severity":"warning"}}]'
```

---

## Verification Checklist

### Development
- [ ] `.env` file created and configured
- [ ] Services start successfully
- [ ] Frontend accessible at http://dev.agroconnectworld.com:8080
- [ ] Hot reload working (Vite HMR)
- [ ] Debug port accessible (5005)

### UAT
- [ ] `.env` file created and configured
- [ ] Services start successfully
- [ ] Frontend accessible at http://uat.agroconnectworld.com:8080
- [ ] QA test runner works
- [ ] Approval workflow tested

### Pre-Production
- [ ] `.env` file created and configured
- [ ] SSL certificates set up
- [ ] Services start successfully
- [ ] Frontend accessible at https://staging.agroconnectworld.com
- [ ] Prometheus collecting metrics
- [ ] Grafana dashboards accessible

### Production
- [ ] `.env` file created with production values
- [ ] SSL certificates set up (Let's Encrypt or commercial)
- [ ] Services start successfully
- [ ] Frontend accessible at https://www.agroconnectworld.com
- [ ] Load balancer configured
- [ ] Backup service running
- [ ] Monitoring stack configured
- [ ] Alerts configured and tested

---

## Troubleshooting

### SSL Certificate Issues

**Problem:** Certificate not found
```bash
# Check certificate exists
ls -la ops/environments/{preprod,production}/ssl/*.crt

# Verify certificate
openssl x509 -in staging.agroconnectworld.com.crt -text -noout
```

**Problem:** Certificate expired
```bash
# Renew Let's Encrypt certificate
certbot renew

# Or run auto-renewal script
./ops/environments/production/ssl/renew.sh
```

### Environment Variable Issues

**Problem:** Services not starting
```bash
# Check .env file exists
ls -la ops/environments/*/.env

# Verify variables are set
docker compose -f docker-compose.prod.yml config | grep -i password
```

### Deployment Issues

**Problem:** Blue-green deployment fails
```bash
# Check service health
./ops/deployment/health-check.sh preprod all

# Check logs
docker logs gateway_service_preprod

# Manual rollback
./ops/deployment/rollback.sh preprod gateway
```

### Alerting Issues

**Problem:** Alerts not sending
```bash
# Check Alertmanager config
curl http://localhost:9093/api/v1/status/config

# Check Alertmanager logs
docker logs alertmanager

# Test webhook manually (Slack)
curl -X POST $SLACK_WEBHOOK_URL -d '{"text":"Test"}'
```

---

## Next Steps

1. ✅ Set up SSL certificates
2. ✅ Configure environment variables
3. ✅ Test blue-green deployment
4. ✅ Configure alerting channels
5. ⏭️ Create Grafana dashboards
6. ⏭️ Set up CI/CD pipeline
7. ⏭️ Configure auto-scaling
8. ⏭️ Set up disaster recovery testing

---

**Last Updated:** 2025-11-28



