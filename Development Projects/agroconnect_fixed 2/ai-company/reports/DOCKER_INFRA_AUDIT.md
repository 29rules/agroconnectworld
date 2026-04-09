# Docker Infrastructure Audit Report

**Date:** 2025-11-28  
**Auditor:** AI Company Infrastructure Team  
**Scope:** Complete Docker infrastructure for AgroConnectWorld platform

---

## Executive Summary

This audit verifies Docker infrastructure across all microservices, environments, and deployment configurations. The audit covers Dockerfiles, build contexts, port mappings, environment variables, Postgres volumes, Nginx reverse proxy, and local-to-cloud parity.

**Overall Status:** ⚠️ **ISSUES FOUND** - Critical port mismatches and missing services detected.

---

## 1. Dockerfiles Audit

### 1.1 Microservice Dockerfiles

| Service | Dockerfile Path | Base Image | Build Stage | EXPOSE Port | Status |
|---------|----------------|------------|-------------|-------------|--------|
| **auth-service** | `backend/auth-service/Dockerfile` | `eclipse-temurin:21-jdk` → `eclipse-temurin:21-jre-jammy` | Multi-stage | `8081` | ✅ Valid |
| **gateway** | `backend/gateway/Dockerfile` | `eclipse-temurin:21-jdk` → `eclipse-temurin:21-jre-jammy` | Multi-stage | `8080` | ✅ Valid |
| **product-service** | `backend/product-service/Dockerfile` | `eclipse-temurin:21-jdk` → `eclipse-temurin:21-jre-jammy` | Multi-stage | `8082` | ✅ Valid |
| **supplier-service** | `backend/supplier-service/Dockerfile` | `eclipse-temurin:21-jdk` → `eclipse-temurin:21-jre-jammy` | Multi-stage | `8083` | ✅ Valid |
| **quote-service** | `backend/quote-service/Dockerfile` | `eclipse-temurin:21-jdk` → `eclipse-temurin:21-jre-jammy` | Multi-stage | `8084` | ✅ Valid |
| **order-service** | `backend/order-service/Dockerfile` | `eclipse-temurin:21-jdk` → `eclipse-temurin:21-jre-jammy` | Multi-stage | `8085` | ✅ Valid |
| **contact-service** | `backend/contact-service/Dockerfile` | `eclipse-temurin:21-jdk` → `eclipse-temurin:21-jre-jammy` | Multi-stage | `8086` | ✅ Valid |
| **frontend** | `frontend/Dockerfile` | `node:20-alpine` → `nginx:alpine` | Multi-stage | `8081` | ✅ Valid |

### 1.2 Dockerfile Issues

**⚠️ Issue 1: Redundant Maven Installation**
- **Location:** All backend Dockerfiles
- **Problem:** Multiple attempts to install Maven (microdnf, yum, apt-get) in same Dockerfile
- **Impact:** Increased build time, larger image size
- **Recommendation:** Use single package manager based on base image

**Example:**
```dockerfile
# Current (redundant)
RUN microdnf install -y maven || \
    (yum -y install maven && yum clean all) || \
    (apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*)

RUN apt-get update && \
    apt-get install -y maven && \
    rm -rf /var/lib/apt/lists/*
```

**✅ Recommendation:** Use `maven:3.9-eclipse-temurin-21` as builder image or single package manager.

---

## 2. Build Context Verification

### 2.1 Build Contexts in docker-compose Files

| Service | Expected Context | Actual Context | Status |
|---------|------------------|----------------|--------|
| **frontend** | `../../frontend` | `../../frontend` | ✅ Correct |
| **gateway** | `../../backend/gateway` | `../../backend/gateway` | ✅ Correct |
| **auth-service** | `../../backend/auth-service` | `../../backend/auth-service` | ✅ Correct |
| **product-service** | `../../backend/product-service` | `../../backend/product-service` | ✅ Correct |
| **supplier-service** | `../../backend/supplier-service` | `../../backend/supplier-service` | ✅ Correct |
| **quote-service** | `../../backend/quote-service` | `../../backend/quote-service` | ✅ Correct |
| **order-service** | `../../backend/order-service` | `../../backend/order-service` | ✅ Correct |
| **contact-service** | `../../backend/contact-service` | `../../backend/contact-service` | ✅ Correct |

**Status:** ✅ **ALL BUILD CONTEXTS CORRECT**

All docker-compose files use relative paths `../../` from `ops/environments/*/` directories, correctly pointing to service directories.

---

## 3. Port Mapping Audit

### 3.1 Port Configuration Matrix

| Service | Dockerfile EXPOSE | application.properties | docker-compose.dev.yml | docker-compose.uat.yml | docker-compose.preprod.yml | docker-compose.prod.yml | Status |
|---------|-------------------|------------------------|------------------------|------------------------|----------------------------|-------------------------|--------|
| **gateway** | `8080` | `8080` | `8080` | `8080` | `8080` | `8080` | ✅ Match |
| **auth-service** | `8081` | `8081` | `8082` ❌ | `8082` ❌ | `8082` ❌ | `8082` ❌ | ❌ **MISMATCH** |
| **product-service** | `8082` | `8082` | `8083` ❌ | `8083` ❌ | `8083` ❌ | `8083` ❌ | ❌ **MISMATCH** |
| **supplier-service** | `8083` | `8083` | ❌ Missing | ❌ Missing | ❌ Missing | ❌ Missing | ❌ **MISSING** |
| **quote-service** | `8084` | `8084` | ❌ Missing | ❌ Missing | ❌ Missing | ❌ Missing | ❌ **MISSING** |
| **order-service** | `8085` | `8085` | ❌ Missing | ❌ Missing | ❌ Missing | ❌ Missing | ❌ **MISSING** |
| **contact-service** | `8086` | `8086` | ❌ Missing | ❌ Missing | ❌ Missing | ❌ Missing | ❌ **MISSING** |
| **frontend** | `8081` | N/A | `8081` | `8081` | `8081` | `8081` | ✅ Match |

### 3.2 Critical Port Mismatches

**❌ CRITICAL ISSUE 1: auth-service Port Mismatch**
- **Dockerfile:** `EXPOSE 8081`
- **application.properties:** `server.port=8081`
- **docker-compose files:** `expose: - "8082"`
- **Impact:** Service will fail to start or be unreachable
- **Fix Required:** Change docker-compose files to expose `8081` OR update Dockerfile/application.properties to use `8082`

**❌ CRITICAL ISSUE 2: product-service Port Mismatch**
- **Dockerfile:** `EXPOSE 8082`
- **application.properties:** `server.port=8082`
- **docker-compose files:** `expose: - "8083"`
- **Impact:** Service will fail to start or be unreachable
- **Fix Required:** Change docker-compose files to expose `8082` OR update Dockerfile/application.properties to use `8083`

### 3.3 Missing Services

**❌ CRITICAL ISSUE 3: Missing Services in Environment docker-compose Files**

The following services are defined in `ops/docker-compose.yml` but **MISSING** from environment-specific files:
- `supplier-service` (port 8083)
- `quote-service` (port 8084)
- `order-service` (port 8085)
- `contact-service` (port 8086)

**Impact:** These services cannot be deployed in dev, UAT, preprod, or production environments.

**Fix Required:** Add all missing services to:
- `ops/environments/dev/docker-compose.dev.yml`
- `ops/environments/uat/docker-compose.uat.yml`
- `ops/environments/preprod/docker-compose.preprod.yml`
- `ops/environments/production/docker-compose.prod.yml`

---

## 4. Environment Variables Audit

### 4.1 Required Environment Variables

**Base docker-compose.yml (`ops/docker-compose.yml`):**
- ✅ `POSTGRES_USER` (default: `agro`)
- ✅ `POSTGRES_PASSWORD` (default: `agro_pass`)
- ✅ `POSTGRES_DB` (default: `agro_master`)
- ✅ `POSTGRES_PORT` (default: `5432`)
- ✅ `REDIS_PORT` (default: `6379`)
- ✅ `GATEWAY_PORT` (default: `8080`)
- ✅ `NGINX_HTTP_PORT` (default: `8080`)
- ✅ `MINIO_ROOT_USER` (default: `agroadmin`)
- ✅ `MINIO_ROOT_PASSWORD` (default: `agroadminpass`)
- ✅ `MINIO_PORT` (default: `9000`)
- ✅ `MINIO_CONSOLE_PORT` (default: `9001`)

**Environment-Specific Variables:**

| Variable | Dev | UAT | Preprod | Prod | Status |
|----------|-----|-----|---------|------|--------|
| `DB_USERNAME` | ✅ | ✅ | ✅ | ✅ | ✅ Declared |
| `DB_PASSWORD` | ✅ | ✅ | ✅ | ✅ | ✅ Declared |
| `JWT_SECRET` | ✅ | ✅ | ✅ | ✅ | ✅ Declared |
| `VITE_API_BASE_URL` | ✅ | ✅ | ✅ | ✅ | ✅ Declared |
| `VITE_OPENROUTER_API_KEY` | ✅ | ✅ | ✅ | ✅ | ✅ Declared |
| `SPRING_PROFILES_ACTIVE` | ✅ `dev` | ✅ `uat` | ✅ `preprod` | ✅ `prod` | ✅ Declared |
| `GRAFANA_ADMIN_USER` | ✅ | ❌ | ✅ | ✅ | ⚠️ Missing in UAT |
| `GRAFANA_ADMIN_PASSWORD` | ✅ | ❌ | ✅ | ✅ | ⚠️ Missing in UAT |
| `SSL_EMAIL` | N/A | N/A | ✅ | ✅ | ✅ Declared |
| `SSL_DOMAIN` | N/A | N/A | ✅ | ✅ | ✅ Declared |

### 4.2 Environment Variable Issues

**⚠️ Issue 1: Missing Grafana Variables in UAT**
- **Location:** `ops/environments/uat/docker-compose.uat.yml`
- **Problem:** Grafana service not defined, but variables may be needed for future monitoring
- **Impact:** Low (UAT may not need Grafana)
- **Recommendation:** Add Grafana service to UAT if monitoring is required

**✅ Issue 2: Environment Variables Documentation**
- **Status:** Complete guide exists at `ops/ENV_VARIABLES_GUIDE.md`
- **Recommendation:** Ensure all teams use this guide when creating `.env` files

---

## 5. Postgres Volumes Audit

### 5.1 Volume Configuration

| Environment | Volume Name | Mount Point | Status |
|-------------|-------------|-------------|--------|
| **Base** | `pg_data` | `/var/lib/postgresql/data` | ✅ Correct |
| **Dev** | `pg_data` | `/var/lib/postgresql/data` | ✅ Correct |
| **UAT** | `pg_data_uat` | `/var/lib/postgresql/data` | ✅ Correct |
| **Preprod** | `pg_data_preprod` | `/var/lib/postgresql/data` | ✅ Correct |
| **Prod** | `pg_data_prod` | `/var/lib/postgresql/data` | ✅ Correct |

**Status:** ✅ **ALL POSTGRES VOLUMES CORRECTLY CONFIGURED**

### 5.2 Volume Isolation

**✅ Good Practice:** Each environment uses separate volume names:
- `pg_data` (base/dev)
- `pg_data_uat` (UAT)
- `pg_data_preprod` (preprod)
- `pg_data_prod` (production)

This ensures data isolation between environments.

### 5.3 Backup Configuration

**Production:**
- ✅ Backup volume mount: `./backups:/backups`
- ✅ Backup service defined with cron schedule
- ✅ Backup script mounted: `./backup-script.sh:/backup-script.sh:ro`

**Status:** ✅ **PRODUCTION BACKUP CONFIGURED**

---

## 6. Nginx Reverse Proxy Audit

### 6.1 Nginx Configuration Files

| Environment | Config File | Server Name | Ports | SSL | Status |
|-------------|-------------|-------------|-------|-----|--------|
| **Base** | `ops/nginx/default.conf` | `_` | `80` | ❌ No | ✅ Valid |
| **Dev** | `ops/nginx/dev.conf` | `dev.agroconnectworld.com` | `80` | ❌ No | ✅ Valid |
| **UAT** | `ops/nginx/uat.conf` | `uat.agroconnectworld.com` | `80` | ❌ No | ✅ Valid |
| **Preprod** | `Preprod** | `ops/nginx/staging.conf` | `staging.agroconnectworld.com` | `80, 443` | ✅ Yes | ✅ Valid |
| **Prod** | `ops/nginx/production.conf` | `www.agroconnectworld.com` | `80, 443` | ✅ Yes | ✅ Valid |

### 6.2 Reverse Proxy Configuration

**✅ Frontend Proxy:**
- All configs correctly proxy `/` to `frontend_service_*:8081`
- SPA fallback configured (try_files)

**✅ API Gateway Proxy:**
- All configs correctly proxy `/api/` to `gateway_service_*:8080`
- Headers properly forwarded (Host, X-Real-IP, X-Forwarded-For, X-Forwarded-Proto)

**✅ CORS Configuration:**
- Dev: `Access-Control-Allow-Origin *` (permissive for development)
- UAT: Restricted CORS
- Preprod: Restricted CORS
- Prod: `Access-Control-Allow-Origin https://www.agroconnectworld.com` (strict)

**✅ Security Headers (Production):**
- `Strict-Transport-Security`
- `X-Frame-Options`
- `X-Content-Type-Options`
- `X-XSS-Protection`
- `Referrer-Policy`
- `Content-Security-Policy`

**✅ Rate Limiting (Production):**
- API limit: `10r/s`
- General limit: `30r/s`

**Status:** ✅ **NGINX CONFIGURATION VALID**

### 6.3 Nginx Container Configuration

| Environment | Container Name | Depends On | Health Check | Status |
|-------------|----------------|------------|--------------|--------|
| **Base** | `edge_service` | frontend, gateway | ✅ Yes | ✅ Valid |
| **Dev** | `edge_service_dev` | frontend, gateway | ✅ Yes | ✅ Valid |
| **UAT** | `edge_service_uat` | frontend, gateway | ✅ Yes | ✅ Valid |
| **Preprod** | `edge_service_preprod` | frontend, gateway | ✅ Yes | ✅ Valid |
| **Prod** | `edge_service_prod` | frontend, gateway | ✅ Yes | ✅ Valid |

**Status:** ✅ **ALL NGINX CONTAINERS PROPERLY CONFIGURED**

---

## 7. Local → Cloud Parity Audit

### 7.1 Service Parity

| Service | Base docker-compose.yml | Dev | UAT | Preprod | Prod | Parity |
|---------|------------------------|-----|-----|---------|------|--------|
| **nginx** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ Match |
| **frontend** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ Match |
| **gateway** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ Match |
| **auth-service** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ Match |
| **product-service** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ Match |
| **supplier-service** | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ **MISSING** |
| **quote-service** | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ **MISSING** |
| **order-service** | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ **MISSING** |
| **contact-service** | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ **MISSING** |
| **postgres** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ Match |
| **redis** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ Match |
| **minio** | ✅ | ❌ | ❌ | ❌ | ❌ | ⚠️ **OPTIONAL** |

**Status:** ⚠️ **PARITY ISSUES** - 4 services missing from environment files

### 7.2 Network Parity

| Network | Base | Dev | UAT | Preprod | Prod | Parity |
|---------|------|-----|-----|---------|------|--------|
| **web** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ Match |
| **internal** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ Match |

**Status:** ✅ **NETWORK PARITY CORRECT**

### 7.3 Volume Parity

| Volume Type | Base | Dev | UAT | Preprod | Prod | Parity |
|-------------|------|-----|-----|---------|------|--------|
| **Postgres** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ Match |
| **Redis** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ Match |
| **MinIO** | ✅ | ❌ | ❌ | ❌ | ❌ | ⚠️ **OPTIONAL** |

**Status:** ✅ **VOLUME PARITY CORRECT** (MinIO optional)

### 7.4 Environment-Specific Features

**Dev:**
- ✅ Hot reload (Vite HMR port 5173)
- ✅ Debug port (Java debug port 5005)
- ✅ Source code mounting
- ✅ pgAdmin (optional profile)

**UAT:**
- ✅ QA test runner (optional profile)
- ✅ CEO portal validator (optional profile)

**Preprod:**
- ✅ SSL/TLS (ports 80, 443)
- ✅ Prometheus (optional profile)
- ✅ Grafana (optional profile)

**Prod:**
- ✅ SSL/TLS (ports 80, 443)
- ✅ Load balancer (optional profile)
- ✅ Backup service (optional profile)
- ✅ Security headers
- ✅ Rate limiting

**Status:** ✅ **ENVIRONMENT-SPECIFIC FEATURES CORRECTLY IMPLEMENTED**

---

## 8. Critical Issues Summary

### 8.1 Critical Issues (Must Fix)

1. **❌ CRITICAL: auth-service Port Mismatch**
   - **Files Affected:** All environment docker-compose files
   - **Fix:** Change `expose: - "8082"` to `expose: - "8081"` OR update Dockerfile/application.properties to use `8082`
   - **Priority:** P0 (Blocks deployment)

2. **❌ CRITICAL: product-service Port Mismatch**
   - **Files Affected:** All environment docker-compose files
   - **Fix:** Change `expose: - "8083"` to `expose: - "8082"` OR update Dockerfile/application.properties to use `8083`
   - **Priority:** P0 (Blocks deployment)

3. **❌ CRITICAL: Missing Services in Environment Files**
   - **Services:** supplier-service, quote-service, order-service, contact-service
   - **Files Affected:** All environment docker-compose files
   - **Fix:** Add service definitions from `ops/docker-compose.yml` to environment files
   - **Priority:** P0 (Blocks full functionality)

### 8.2 High Priority Issues

4. **⚠️ HIGH: Redundant Maven Installation in Dockerfiles**
   - **Files Affected:** All backend Dockerfiles
   - **Fix:** Use single package manager or `maven:3.9-eclipse-temurin-21` builder image
   - **Priority:** P1 (Performance impact)

### 8.3 Low Priority Issues

5. **⚠️ LOW: Missing Grafana Variables in UAT**
   - **Files Affected:** `ops/environments/uat/docker-compose.uat.yml`
   - **Fix:** Add Grafana service if monitoring is required
   - **Priority:** P2 (Optional feature)

---

## 9. Recommendations

### 9.1 Immediate Actions (Before Deployment)

1. **Fix Port Mismatches:**
   ```yaml
   # In all environment docker-compose files:
   auth_service:
     expose:
       - "8081"  # Change from 8082
   
   product_service:
     expose:
       - "8082"  # Change from 8083
   ```

2. **Add Missing Services:**
   - Copy `supplier-service`, `quote-service`, `order-service`, `contact-service` definitions from `ops/docker-compose.yml` to all environment files
   - Ensure port mappings match Dockerfiles and application.properties

3. **Verify Health Checks:**
   - Update health check URLs to match corrected ports
   - Test health checks after port fixes

### 9.2 Short-Term Improvements

1. **Optimize Dockerfiles:**
   - Remove redundant Maven installation attempts
   - Use `maven:3.9-eclipse-temurin-21` as builder image

2. **Standardize Port Configuration:**
   - Create a `PORTS.md` document listing all service ports
   - Use environment variables for port configuration

3. **Add Service Discovery:**
   - Consider using Docker Compose service names for service discovery
   - Document service dependencies

### 9.3 Long-Term Improvements

1. **CI/CD Integration:**
   - Add Docker build validation in CI pipeline
   - Automate port mapping verification

2. **Monitoring:**
   - Add Prometheus service discovery for all environments
   - Create Grafana dashboards for service health

3. **Documentation:**
   - Create deployment runbook
   - Document troubleshooting procedures

---

## 10. Verification Checklist

### Pre-Deployment Checklist

- [ ] Fix auth-service port mismatch (8082 → 8081)
- [ ] Fix product-service port mismatch (8083 → 8082)
- [ ] Add supplier-service to all environment files
- [ ] Add quote-service to all environment files
- [ ] Add order-service to all environment files
- [ ] Add contact-service to all environment files
- [ ] Update health check URLs to match corrected ports
- [ ] Test all services start correctly
- [ ] Verify Nginx can reach all services
- [ ] Test API endpoints through gateway

### Post-Deployment Checklist

- [ ] Verify all services are healthy
- [ ] Test frontend → gateway → services flow
- [ ] Verify database connections
- [ ] Test authentication flow
- [ ] Verify SSL certificates (preprod/prod)
- [ ] Test backup service (prod)
- [ ] Verify monitoring stack (preprod/prod)

---

## 11. Conclusion

**Overall Assessment:** ⚠️ **ISSUES FOUND - FIXES REQUIRED**

The Docker infrastructure is well-structured with proper multi-stage builds, network isolation, and environment-specific configurations. However, **critical port mismatches** and **missing services** must be fixed before deployment.

**Key Strengths:**
- ✅ Proper multi-stage Docker builds
- ✅ Correct build contexts
- ✅ Good network isolation
- ✅ Environment-specific configurations
- ✅ Postgres volumes correctly configured
- ✅ Nginx reverse proxy properly configured
- ✅ SSL/TLS configured for preprod/prod

**Critical Fixes Required:**
- ❌ Fix auth-service port mismatch
- ❌ Fix product-service port mismatch
- ❌ Add missing services to environment files

**Estimated Fix Time:** 2-4 hours

---

**Report Generated:** 2025-11-28  
**Next Review:** After fixes are applied



