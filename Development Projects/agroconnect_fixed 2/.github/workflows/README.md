# CI/CD Pipeline Documentation

This directory contains GitHub Actions workflows for the AgroConnectWorld platform.

## Workflows Overview

### 1. `ci.yml` - Build & Test
**Trigger:** PR or push to `main`/`develop` branches

**Features:**
- ✅ Frontend build and linting
- ✅ Backend services build (all 7 microservices)
- ✅ Unit tests execution
- ✅ Security scanning (OWASP Dependency Check)
- ✅ AI code review (CodeRabbit)
- ✅ Multi-service matrix builds

**Services Tested:**
- Frontend (Vite/React)
- Gateway
- Auth Service
- Product Service
- Supplier Service
- Quote Service
- Order Service
- Contact Service

---

### 2. `docker-publish.yml` - Docker Image Publishing
**Trigger:** Tags (`v*`), push to `develop`/`main`, or manual dispatch

**Features:**
- ✅ Builds all 8 Docker images (frontend + 7 microservices)
- ✅ Pushes to GitHub Container Registry (GHCR)
- ✅ Parallel builds using matrix strategy
- ✅ Docker layer caching for faster builds

**Images Published:**
- `ghcr.io/{owner}/agroconnect-frontend`
- `ghcr.io/{owner}/agroconnect-gateway`
- `ghcr.io/{owner}/agroconnect-auth-service`
- `ghcr.io/{owner}/agroconnect-product-service`
- `ghcr.io/{owner}/agroconnect-supplier-service`
- `ghcr.io/{owner}/agroconnect-quote-service`
- `ghcr.io/{owner}/agroconnect-order-service`
- `ghcr.io/{owner}/agroconnect-contact-service`

---

### 3. `dev.yml` - Deploy to DEV
**Trigger:** Push to `develop` branch or manual dispatch

**Features:**
- ✅ Builds all Docker images
- ✅ Pushes to GHCR with `dev` tag
- ✅ Deploys to DEV server via SSH
- ✅ Runs smoke tests
- ✅ Notifies CEO Portal

**Environment:** `dev`  
**URL:** http://dev.agroconnectworld.com

**Required Secrets:**
- `DEV_SSH_HOST` - Dev server hostname/IP
- `DEV_SSH_USER` - SSH username
- `DEV_SSH_KEY` - SSH private key
- `CEO_PORTAL_WEBHOOK` - CEO Portal webhook URL (optional)

---

### 4. `uat.yml` - Deploy to UAT
**Trigger:** Push to `release/**` branches or manual dispatch

**Features:**
- ✅ Builds all Docker images
- ✅ Pushes to GHCR with `uat` tag
- ✅ Deploys to UAT server
- ✅ Runs regression test suite
- ✅ QA approval required
- ✅ CEO approval required
- ✅ Notifies CEO Portal

**Environment:** `uat`, `uat-qa-approval`, `uat-ceo-approval`  
**URL:** http://uat.agroconnectworld.com

**Required Secrets:**
- `UAT_SSH_HOST` - UAT server hostname/IP
- `UAT_SSH_USER` - SSH username
- `UAT_SSH_KEY` - SSH private key
- `CEO_PORTAL_WEBHOOK` - CEO Portal webhook URL (optional)

**Approval Workflow:**
1. Deployment starts automatically
2. QA team approval required (GitHub Environment)
3. CEO approval required (GitHub Environment)
4. Deployment completes after approvals

---

### 5. `staging.yml` - Deploy to Pre-Prod
**Trigger:** Completion of UAT deployment or manual dispatch

**Features:**
- ✅ Builds all Docker images
- ✅ Pushes to GHCR with `staging` tag
- ✅ Security checks (Trivy, OWASP ZAP)
- ✅ Architecture validations
- ✅ Deploys to staging server
- ✅ Runs load tests (k6)
- ✅ Health checks

**Environment:** `staging`  
**URL:** https://staging.agroconnectworld.com

**Required Secrets:**
- `STAGING_SSH_HOST` - Staging server hostname/IP
- `STAGING_SSH_USER` - SSH username
- `STAGING_SSH_KEY` - SSH private key

---

### 6. `prod.yml` - Deploy to Production
**Trigger:** Completion of staging deployment or manual dispatch (with confirmation)

**Features:**
- ✅ Pre-deployment validation
- ✅ Builds all Docker images
- ✅ Pushes to GHCR with `prod` tag
- ✅ CEO approval required
- ✅ Zero-downtime blue-green deployment
- ✅ Real-time monitoring
- ✅ Comprehensive health checks
- ✅ Automatic rollback on failure
- ✅ Post-deployment monitoring
- ✅ CEO Portal notifications

**Environment:** `production`  
**URL:** https://www.agroconnectworld.com

**Required Secrets:**
- `PROD_SSH_HOST` - Production server hostname/IP
- `PROD_SSH_USER` - SSH username
- `PROD_SSH_KEY` - SSH private key
- `CEO_PORTAL_WEBHOOK` - CEO Portal webhook URL (optional)

**Manual Deployment:**
When manually triggering, you must:
1. Provide version number
2. Type "DEPLOY" in confirmation field

**Approval Workflow:**
1. CEO approval required (GitHub Environment)
2. Zero-downtime deployment
3. Monitoring for 2 minutes
4. Automatic rollback on failure

---

## Required GitHub Secrets

### SSH Deployment Secrets
```
DEV_SSH_HOST
DEV_SSH_USER
DEV_SSH_KEY

UAT_SSH_HOST
UAT_SSH_USER
UAT_SSH_KEY

STAGING_SSH_HOST
STAGING_SSH_USER
STAGING_SSH_KEY

PROD_SSH_HOST
PROD_SSH_USER
PROD_SSH_KEY
```

### Integration Secrets (Optional)
```
CEO_PORTAL_WEBHOOK  # CEO Portal webhook URL
OPENAI_API_KEY      # For AI code review
SLACK_WEBHOOK       # Slack notifications
```

---

## GitHub Environments Setup

To enable approval workflows, configure GitHub Environments:

1. Go to **Settings** → **Environments**
2. Create environments:
   - `dev`
   - `uat`
   - `uat-qa-approval` (with required reviewers: QA team)
   - `uat-ceo-approval` (with required reviewers: CEO)
   - `staging`
   - `production` (with required reviewers: CEO)

3. Configure protection rules:
   - **Required reviewers:** Add QA team and CEO
   - **Wait timer:** Optional delay before deployment
   - **Deployment branches:** Restrict to specific branches

---

## Deployment Flow

```
┌─────────────────┐
│   PR Created    │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  CI: Build &    │
│     Test        │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ Merge to develop│
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  Deploy to DEV  │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ Release branch  │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  Deploy to UAT  │
│  (QA + CEO      │
│   Approval)     │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ Deploy to Staging│
│ (Load Tests)    │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ Deploy to Prod  │
│ (CEO Approval + │
│  Zero-Downtime) │
└─────────────────┘
```

---

## Troubleshooting

### Build Failures
- Check logs in GitHub Actions
- Verify Dockerfile syntax
- Check Maven/Node dependencies

### Deployment Failures
- Verify SSH keys are correct
- Check server connectivity
- Review deployment scripts
- Check Docker Compose files

### Approval Issues
- Verify GitHub Environments are configured
- Check required reviewers are set
- Ensure users have proper permissions

### Health Check Failures
- Check service logs
- Verify port mappings
- Check database connectivity
- Review environment variables

---

## Best Practices

1. **Always test in DEV first**
2. **Get approvals before production**
3. **Monitor deployments closely**
4. **Use blue-green for zero-downtime**
5. **Keep deployment scripts updated**
6. **Document any manual steps**
7. **Review security scans regularly**

---

**Last Updated:** 2025-11-28



