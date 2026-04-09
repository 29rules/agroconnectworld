# CTO Agent System Audit Command

## Command Sent

**Message:** "CTO, initiate the full AgroConnectWorld system audit."

**Endpoint:** `POST /ai/ctochat`

**Session ID:** `audit-{timestamp}`

---

## How to Execute

### Option 1: Via API (curl)

```bash
curl -X POST http://localhost:8087/ai/ctochat \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <CEO_JWT_TOKEN>" \
  -d '{
    "message": "CTO, initiate the full AgroConnectWorld system audit.",
    "sessionId": "audit-$(date +%s)"
  }'
```

### Option 2: Via CEO Portal

1. Navigate to CEO Portal
2. Go to "AI Chat" tab
3. Send message: "CTO, initiate the full AgroConnectWorld system audit."
4. CTO Agent will autonomously execute the audit

### Option 3: Via Postman/API Client

- **Method:** POST
- **URL:** `http://localhost:8087/ai/ctochat`
- **Headers:**
  - `Content-Type: application/json`
  - `Authorization: Bearer <CEO_JWT_TOKEN>`
- **Body:**
```json
{
  "message": "CTO, initiate the full AgroConnectWorld system audit.",
  "sessionId": "audit-1234567890"
}
```

---

## What Happens

1. **CTO Agent Receives Command**
   - Message processed via `/ai/ctochat` endpoint
   - CTO Agent's `reviewArchitecture()` method called

2. **CTO Agent Recognizes Audit Request**
   - Agent understands it needs to run a system audit
   - Autonomously decides to call `runSystemAudit()` tool

3. **SystemAuditTool Executes**
   - `runSystemAudit()` tool is called
   - Executes `SystemAuditService.initiateFullSystemAudit()`

4. **Full System Audit Runs**
   - Analyzes all microservices
   - Reviews gateway, frontend, configurations
   - Checks Docker, Nginx, database
   - Evaluates security, code quality, deployment readiness

5. **Reports Generated**
   - **FULL_SYSTEM_AUDIT.md** - Comprehensive system analysis
   - **ARCHITECTURE_REPORT.md** - Architecture deep dive
   - **SECURITY_REPORT.md** - Security assessment
   - **ENGINEERING_READINESS_REPORT.md** - Engineering maturity
   - **CI_CD_READINESS_REPORT.md** - CI/CD pipeline assessment
   - **DEPLOYMENT_PLAN.md** - Dev → UAT → Staging → Prod plan
   - **CEO_ACTION_ITEMS.md** - Prioritized CEO actions

6. **Results Returned**
   - JSON response with audit status
   - List of generated reports
   - Audit ID and timestamps

---

## Expected Response

```json
{
  "sessionId": "audit-1234567890",
  "message": "CTO, initiate the full AgroConnectWorld system audit.",
  "response": "{\"status\":\"COMPLETED\",\"auditId\":\"uuid\",\"totalReports\":7,\"reports\":[\"FULL_SYSTEM_AUDIT.md\",\"ARCHITECTURE_REPORT.md\",\"SECURITY_REPORT.md\",\"ENGINEERING_READINESS_REPORT.md\",\"CI_CD_READINESS_REPORT.md\",\"DEPLOYMENT_PLAN.md\",\"CEO_ACTION_ITEMS.md\"],\"startTime\":\"2025-11-28T10:00:00\",\"endTime\":\"2025-11-28T10:15:00\",\"message\":\"✅ System audit completed successfully. Generated 7 reports in /ai-company/reports/\"}",
  "timestamp": "2025-11-28T10:15:00",
  "agent": "CTO",
  "status": "success"
}
```

---

## Reports Location

All reports are saved to: `/ai-company/reports/`

- `FULL_SYSTEM_AUDIT.md`
- `ARCHITECTURE_REPORT.md`
- `SECURITY_REPORT.md`
- `ENGINEERING_READINESS_REPORT.md`
- `CI_CD_READINESS_REPORT.md`
- `DEPLOYMENT_PLAN.md`
- `CEO_ACTION_ITEMS.md`

---

## Prerequisites

1. ✅ **AI Company API Running**
   - Port 8087
   - `mvn spring-boot:run` or Docker Compose

2. ✅ **SystemAuditTool Injected**
   - `AgentRegistryConfig` runs at startup
   - SystemAuditTool injected into CTO Agent

3. ✅ **CEO JWT Token**
   - Login as CEO user
   - Get token from `/api/auth/login`
   - Use token in Authorization header

4. ✅ **SystemAuditService Available**
   - Spring bean initialized
   - Can access CTO Agent via AgentRegistry

---

## Troubleshooting

**Issue:** 401 Unauthorized
- **Solution:** Get valid CEO JWT token from login endpoint

**Issue:** 500 Internal Server Error
- **Solution:** Check if SystemAuditTool is properly injected
- Check AI Company API logs

**Issue:** CTO Agent doesn't call runSystemAudit()
- **Solution:** Verify SystemAuditTool is in CTO Agent's tools list
- Check AgentRegistryConfig logs for injection success

**Issue:** Reports not generated
- **Solution:** Check `/ai-company/reports/` directory permissions
- Verify SystemAuditService can write files

---

**Last Updated:** 2025-11-28



