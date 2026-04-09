# Build Promotion Flow

**Date:** 2025-11-28  
**Status:** ✅ **IMPLEMENTED**

---

## Overview

The Build Promotion Flow automates the promotion of builds through environments:
1. **Merge PR** → dev build
2. **CEO Approve** → uat build
3. **CEO Approve** → staging
4. **CEO Approve** → production

---

## Promotion Flow

```
┌─────────────────┐
│   PR Merged     │
│   to develop    │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  DEV Build      │
│  (Automatic)    │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  CEO Approve    │
│  for UAT?       │
└────────┬────────┘
         │ YES
         ▼
┌─────────────────┐
│  UAT Build      │
│  (With Approval)│
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  CEO Approve    │
│  for Staging?   │
└────────┬────────┘
         │ YES
         ▼
┌─────────────────┐
│  Staging Build  │
│  (With Approval)│
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  CEO Approve    │
│  for Production?│
└────────┬────────┘
         │ YES
         ▼
┌─────────────────┐
│  Production     │
│  (Zero-Downtime)│
└─────────────────┘
```

---

## Automated Checks

For each promotion, the AI Company automatically:

### 1. ✅ Check Code
- **Linting** - ESLint (frontend), Checkstyle (backend)
- **Code Coverage** - Minimum coverage thresholds
- **Static Analysis** - Code quality metrics

### 2. ✅ Run Tests
- **Unit Tests** - All microservices
- **Integration Tests** - API endpoints
- **E2E Tests** - Critical user flows

### 3. ✅ Run Security Checks
- **OWASP Dependency Check** - Vulnerable dependencies
- **Trivy Scan** - Container vulnerabilities
- **Secret Scanning** - Exposed secrets

### 4. ✅ Build Artifacts
- **Docker Images** - All 8 services
- **Tag with Environment** - dev, uat, staging, prod
- **Push to GHCR** - GitHub Container Registry

### 5. ✅ Deploy
- **SSH Deployment** - To target environment
- **Docker Compose** - Service orchestration
- **Health Checks** - Verify deployment

### 6. ✅ Notify CEO Portal
- **Promotion Status** - Success/Failure
- **Build Details** - ID, branch, commit
- **Deployment URL** - Environment link
- **Workflow Link** - GitHub Actions run

---

## Implementation Details

### Components

1. **BuildPromotionAgent** - AI agent for promotion decisions
2. **BuildPromotionService** - Orchestrates promotion flow
3. **PromotionController** - REST API for CEO Portal
4. **GitHub Workflow** - `.github/workflows/promotion.yml`

### Promotion Steps

Each promotion executes:

1. **Code Quality Check**
   - Linting passes
   - Coverage meets threshold
   - Static analysis clean

2. **Test Execution**
   - All unit tests pass
   - Integration tests pass
   - E2E tests pass

3. **Security Scan**
   - No critical vulnerabilities
   - No high vulnerabilities (configurable)
   - Security score acceptable

4. **Artifact Build**
   - Docker images built
   - Tagged with environment
   - Pushed to registry

5. **AI Analysis**
   - Build quality assessment
   - Promotion recommendation
   - Risk analysis

6. **Deployment**
   - Trigger deployment workflow
   - Wait for completion
   - Verify health

7. **Notification**
   - CEO Portal webhook
   - Status update
   - Deployment URL

---

## API Endpoints

### Promote Build
```http
POST /ai/promotion/promote
Content-Type: application/json
Authorization: Bearer <CEO_JWT_TOKEN>

{
  "fromEnvironment": "dev",
  "toEnvironment": "uat",
  "buildId": "build-123",
  "branch": "develop",
  "commit": "abc123"
}
```

### Approve Promotion (CEO)
```http
POST /ai/promotion/approve
Content-Type: application/json
Authorization: Bearer <CEO_JWT_TOKEN>

{
  "fromEnvironment": "uat",
  "toEnvironment": "staging",
  "buildId": "build-123",
  "branch": "release/v1.0.0",
  "commit": "abc123",
  "approvedBy": "ceo@agroconnectworld.com"
}
```

### Get Promotion Checklist
```http
GET /ai/promotion/checklist/{environment}
Authorization: Bearer <CEO_JWT_TOKEN>
```

### Get Promotion Status
```http
GET /ai/promotion/status/{buildId}
Authorization: Bearer <CEO_JWT_TOKEN>
```

---

## GitHub Workflow

### Manual Promotion
The `promotion.yml` workflow can be triggered manually with:
- Source environment (dev, uat, staging)
- Target environment (uat, staging, production)
- Build ID
- Branch name
- Commit SHA

### Automatic Triggers

1. **DEV Build** - Triggers on PR merge to `develop`
2. **UAT Build** - Triggers after DEV deployment completes
3. **Staging Build** - Triggers after UAT with CEO approval
4. **Production Build** - Triggers after Staging with CEO approval

---

## CEO Portal Integration

### Promotion Dashboard
- View pending promotions
- Approve/reject promotions
- See promotion history
- Monitor deployment status

### Notifications
- Real-time promotion status
- Build quality metrics
- Security scan results
- Deployment URLs

---

## Promotion Criteria

### DEV → UAT
- ✅ All tests pass
- ✅ No critical security issues
- ✅ Code quality score > 80
- ✅ CEO approval required

### UAT → Staging
- ✅ All tests pass
- ✅ No critical/high security issues
- ✅ Code quality score > 85
- ✅ Regression tests pass
- ✅ CEO approval required

### Staging → Production
- ✅ All tests pass
- ✅ No critical/high security issues
- ✅ Code quality score > 90
- ✅ Load tests pass
- ✅ Architecture validation pass
- ✅ CEO approval required

---

## Reports

Promotion results saved to:
- `ai-company/reports/promotions/promotion_{buildId}_{timestamp}.json`

Each report includes:
- Build information
- Code quality results
- Test results
- Security scan results
- Artifact information
- Deployment status
- AI analysis
- Issues and recommendations

---

## Security

- All endpoints require CEO role
- JWT token validation
- Approval workflows enforced
- Audit trail for all promotions

---

## Status

✅ **FULLY IMPLEMENTED**

- ✅ Build promotion service
- ✅ Automated checks (code, tests, security)
- ✅ Artifact building
- ✅ Deployment automation
- ✅ CEO Portal notifications
- ✅ GitHub workflow integration
- ✅ Approval workflows

---

**Last Updated:** 2025-11-28



