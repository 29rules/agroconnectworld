# Environment Variables Guide

This document contains all environment variables needed for each environment. Copy these to `.env` files in each environment directory.

---

## Development Environment

**File:** `ops/environments/dev/.env`

```env
# Project Configuration
PROJECT_NAME=agroconnect-dev
ENV=development

# Database Configuration
DB_USERNAME=agro_user
DB_PASSWORD=agro_pass
POSTGRES_DB=agro_master
POSTGRES_PORT=5432

# JWT Configuration
JWT_SECRET=dev-secret-key-change-in-production
JWT_EXPIRATION_MS=86400000

# Redis Configuration
REDIS_PORT=6379

# API Keys
VITE_OPENROUTER_API_KEY=your_openrouter_api_key_here

# Frontend Configuration
VITE_API_BASE_URL=http://dev.agroconnectworld.com/api

# Nginx Configuration
NGINX_HTTP_PORT=8080

# Development Features
DEBUG_MODE=true
HOT_RELOAD=true

# Grafana (if using monitoring in dev)
GRAFANA_ADMIN_USER=admin
GRAFANA_ADMIN_PASSWORD=dev_admin_pass
```

---

## UAT Environment

**File:** `ops/environments/uat/.env`

```env
# Project Configuration
PROJECT_NAME=agroconnect-uat
ENV=uat

# Database Configuration
DB_USERNAME=uat_db_user
DB_PASSWORD=your_secure_uat_password_here
POSTGRES_DB=agro_master
POSTGRES_PORT=5433

# JWT Configuration
JWT_SECRET=your_secure_jwt_secret_for_uat_here
JWT_EXPIRATION_MS=86400000

# Redis Configuration
REDIS_PORT=6380

# API Keys
VITE_OPENROUTER_API_KEY=your_openrouter_api_key_here

# Frontend Configuration
VITE_API_BASE_URL=http://uat.agroconnectworld.com/api

# Nginx Configuration
NGINX_HTTP_PORT=8080

# UAT Specific
CEO_EMAIL=ceo@agroconnectworld.com
CEO_PASSWORD=your_ceo_password_here

# Approval Workflow
QA_LEAD_EMAIL=qa-lead@agroconnectworld.com
CTO_EMAIL=cto@agroconnectworld.com

# Grafana (if using monitoring in UAT)
GRAFANA_ADMIN_USER=admin
GRAFANA_ADMIN_PASSWORD=your_grafana_password_here
```

---

## Pre-Production (Staging) Environment

**File:** `ops/environments/preprod/.env`

```env
# Project Configuration
PROJECT_NAME=agroconnect-preprod
ENV=preprod

# Database Configuration
DB_USERNAME=preprod_db_user
DB_PASSWORD=your_secure_preprod_password_here
POSTGRES_DB=agro_master
POSTGRES_PORT=5434

# JWT Configuration
JWT_SECRET=your_secure_jwt_secret_for_preprod_here
JWT_EXPIRATION_MS=86400000

# Redis Configuration
REDIS_PORT=6381

# API Keys
VITE_OPENROUTER_API_KEY=your_openrouter_api_key_here

# Frontend Configuration
VITE_API_BASE_URL=https://staging.agroconnectworld.com/api

# Nginx Configuration
NGINX_HTTP_PORT=80
NGINX_HTTPS_PORT=443

# SSL Configuration
SSL_EMAIL=admin@agroconnectworld.com
SSL_DOMAIN=staging.agroconnectworld.com

# Monitoring
GRAFANA_ADMIN_USER=admin
GRAFANA_ADMIN_PASSWORD=your_grafana_password_here
PROMETHEUS_RETENTION_DAYS=7

# Alerting (Optional for preprod)
SLACK_WEBHOOK_URL=
ALERT_EMAIL_TO=devops@agroconnectworld.com
SMTP_HOST=smtp.gmail.com:587
SMTP_USER=your_smtp_user
SMTP_PASSWORD=your_smtp_password
ALERT_EMAIL_FROM=alerts@agroconnectworld.com
```

---

## Production Environment

**File:** `ops/environments/production/.env`

```env
# Project Configuration
PROJECT_NAME=agroconnect-prod
ENV=production

# Database Configuration
DB_USERNAME=prod_db_user
DB_PASSWORD=your_very_secure_production_password_here
POSTGRES_DB=agro_master

# JWT Configuration
JWT_SECRET=your_very_secure_jwt_secret_for_production_here_min_32_chars
JWT_EXPIRATION_MS=86400000

# Redis Configuration
REDIS_PORT=6379

# API Keys
VITE_OPENROUTER_API_KEY=your_openrouter_api_key_here

# Frontend Configuration
VITE_API_BASE_URL=https://www.agroconnectworld.com/api

# Nginx Configuration
NGINX_HTTP_PORT=80
NGINX_HTTPS_PORT=443

# SSL Configuration
SSL_EMAIL=admin@agroconnectworld.com
SSL_DOMAIN=www.agroconnectworld.com

# Monitoring
GRAFANA_ADMIN_USER=admin
GRAFANA_ADMIN_PASSWORD=your_very_secure_grafana_password_here
PROMETHEUS_RETENTION_DAYS=30

# Alerting - Slack
SLACK_WEBHOOK_URL=https://hooks.slack.com/services/YOUR/WEBHOOK/URL
SLACK_CHANNEL_CRITICAL=#alerts-critical
SLACK_CHANNEL_WARNING=#alerts-warning

# Alerting - Email
ALERT_EMAIL_TO=devops@agroconnectworld.com
ONCALL_EMAIL=oncall@agroconnectworld.com
TEAM_EMAIL=team@agroconnectworld.com
ALERT_EMAIL_FROM=alerts@agroconnectworld.com

# SMTP Configuration
SMTP_HOST=smtp.gmail.com:587
SMTP_USER=your_smtp_username
SMTP_PASSWORD=your_smtp_password
SMTP_FROM=alerts@agroconnectworld.com

# Backup Configuration
BACKUP_SCHEDULE=0 2 * * *  # Daily at 2 AM UTC
BACKUP_RETENTION_DAYS=30
BACKUP_STORAGE_PATH=/backups

# Security
ALLOWED_ORIGINS=https://www.agroconnectworld.com
CORS_ENABLED=true

# Performance
MAX_CONNECTIONS=100
CONNECTION_TIMEOUT=30
```

---

## Monitoring Stack

**File:** `ops/monitoring/.env`

```env
# Grafana Configuration
GRAFANA_ADMIN_USER=admin
GRAFANA_ADMIN_PASSWORD=your_secure_grafana_password_here

# Alerting - Slack
SLACK_WEBHOOK_URL=https://hooks.slack.com/services/YOUR/WEBHOOK/URL

# Alerting - Email
ALERT_EMAIL_TO=devops@agroconnectworld.com
ONCALL_EMAIL=oncall@agroconnectworld.com
TEAM_EMAIL=team@agroconnectworld.com
ALERT_EMAIL_FROM=alerts@agroconnectworld.com

# SMTP Configuration
SMTP_HOST=smtp.gmail.com:587
SMTP_USER=your_smtp_username
SMTP_PASSWORD=your_smtp_password

# Prometheus Configuration
PROMETHEUS_RETENTION_DAYS=30
ENV=production

# Loki Configuration
LOKI_RETENTION_DAYS=7
```

---

## Quick Setup Commands

### Create .env Files

```bash
# Development
cd ops/environments/dev
cat > .env << 'EOF'
# Paste Development environment variables from above
EOF

# UAT
cd ../uat
cat > .env << 'EOF'
# Paste UAT environment variables from above
EOF

# Pre-Production
cd ../preprod
cat > .env << 'EOF'
# Paste Pre-Production environment variables from above
EOF

# Production
cd ../production
cat > .env << 'EOF'
# Paste Production environment variables from above
EOF

# Monitoring
cd ../../monitoring
cat > .env << 'EOF'
# Paste Monitoring environment variables from above
EOF
```

---

## Security Best Practices

1. **Never commit .env files to version control**
   - Add to `.gitignore`
   - Use `.env.example` as template

2. **Use strong passwords:**
   - Minimum 16 characters
   - Mix of uppercase, lowercase, numbers, symbols
   - Unique for each environment

3. **JWT Secrets:**
   - Minimum 32 characters for production
   - Use cryptographically secure random generator
   - Different secret for each environment

4. **Rotate secrets regularly:**
   - JWT secrets: Every 90 days
   - Database passwords: Every 180 days
   - API keys: As needed

5. **Use secrets management in production:**
   - AWS Secrets Manager
   - HashiCorp Vault
   - Azure Key Vault
   - Google Secret Manager

---

**Last Updated:** 2025-11-28



