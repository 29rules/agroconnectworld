# AI Company API Controllers Overview

## Package: `com.ai.company.api`

This package contains REST controllers that expose AI agent functionality through structured JSON APIs.

## Controllers

### 1. StatusController

**Path:** `/ai/status`

Provides status endpoints for CEO dashboard panels. Each endpoint calls the respective AI agent and returns structured JSON.

#### Endpoints

##### GET `/ai/status/ceo`
- **Agent:** CTOAgent
- **Method:** `reviewArchitecture()`
- **Response:**
```json
{
  "timestamp": "2024-11-28T10:00:00",
  "status": "operational",
  "systemHealth": {
    "agents": 8,
    "activeWorkflows": 0,
    "totalExecutions": 0,
    "assessment": "AI-generated assessment..."
  },
  "metrics": {
    "totalUsers": 0,
    "totalProducts": 0,
    "totalOrders": 0,
    "totalRevenue": 0
  }
}
```

##### GET `/ai/status/engineering`
- **Agent:** EngineerAgent
- **Method:** `createImplementationSpec()`
- **Response:**
```json
{
  "timestamp": "2024-11-28T10:00:00",
  "status": "operational",
  "metrics": {
    "activeDevelopers": 12,
    "codeCommits": 1247,
    "pullRequests": 89,
    "codeReviewTime": "2.5 days",
    "testCoverage": 78,
    "buildSuccessRate": 94,
    "deploymentFrequency": "Daily",
    "meanTimeToRecovery": "15 minutes",
    "assessment": "AI-generated assessment..."
  }
}
```

##### GET `/ai/status/qa`
- **Agent:** QAAgent
- **Method:** `createTestPlan()`
- **Response:**
```json
{
  "timestamp": "2024-11-28T10:00:00",
  "status": "operational",
  "metrics": {
    "totalTests": 1247,
    "passingTests": 1156,
    "failingTests": 91,
    "testCoverage": 78,
    "bugCount": 23,
    "criticalBugs": 3,
    "resolvedBugs": 156,
    "avgResolutionTime": "2.5 days",
    "assessment": "AI-generated assessment..."
  }
}
```

##### GET `/ai/status/product`
- **Agent:** ProductManagerAgent
- **Method:** `createEpic()`
- **Response:**
```json
{
  "timestamp": "2024-11-28T10:00:00",
  "status": "operational",
  "metrics": {
    "totalProducts": 1247,
    "activeProducts": 1156,
    "pendingApproval": 45,
    "lowStock": 23,
    "categories": 12,
    "totalRevenue": 2456789,
    "avgRating": 4.6,
    "totalReviews": 3456,
    "assessment": "AI-generated assessment..."
  }
}
```

##### GET `/ai/status/scrum`
- **Agent:** ScrumMasterAgent (placeholder - requires proper initialization)
- **Response:**
```json
{
  "timestamp": "2024-11-28T10:00:00",
  "status": "operational",
  "metrics": {
    "activeSprints": 3,
    "completedSprints": 12,
    "totalStoryPoints": 89,
    "completedStoryPoints": 67,
    "velocity": 22.3,
    "teamVelocity": 67,
    "sprintProgress": 75,
    "blockers": 2,
    "assessment": "Scrum metrics retrieved successfully..."
  }
}
```

##### GET `/ai/status/devops`
- **Agent:** DevOpsAgent
- **Method:** `analyzeInfrastructure()`
- **Response:**
```json
{
  "timestamp": "2024-11-28T10:00:00",
  "status": "operational",
  "metrics": {
    "deployments": 45,
    "deploymentSuccess": 42,
    "deploymentFailure": 3,
    "uptime": 99.8,
    "avgResponseTime": 245,
    "serverCount": 8,
    "activeServices": 12,
    "incidents": 2,
    "assessment": "AI-generated assessment..."
  }
}
```

---

### 2. CTOChatController

**Path:** `/ai`

Provides chat endpoints for interacting with AI agents.

#### Endpoints

##### POST `/ai/ctochat`
Chat with CTO Agent.

**Request:**
```json
{
  "message": "What is the current system architecture?",
  "sessionId": "session-12345" // optional
}
```

**Response:**
```json
{
  "sessionId": "session-12345",
  "message": "What is the current system architecture?",
  "response": "AI-generated response...",
  "timestamp": "2024-11-28T10:00:00",
  "agent": "CTO",
  "status": "success"
}
```

##### POST `/ai/chat/{agent}`
Chat with any available agent.

**Supported Agents:**
- `cto` - CTOAgent
- `product` - ProductManagerAgent
- `engineer` - EngineerAgent
- `qa` - QAAgent
- `scrum` - ScrumMasterAgent (placeholder)
- `devops` - DevOpsAgent

**Request:**
```json
{
  "message": "Create a test plan for user authentication",
  "sessionId": "session-12345" // optional
}
```

**Response:**
```json
{
  "sessionId": "session-12345",
  "message": "Create a test plan for user authentication",
  "response": "AI-generated response...",
  "timestamp": "2024-11-28T10:00:00",
  "agent": "qa",
  "status": "success"
}
```

##### GET `/ai/agents`
Get list of available agents.

**Response:**
```json
{
  "agents": ["cto_agent", "architect_agent", "engineer_agent", ...],
  "availableAgents": ["cto", "product", "engineer", "qa", "scrum", "devops"],
  "timestamp": "2024-11-28T10:00:00"
}
```

---

## Agent Methods Used

### CTOAgent
- `reviewArchitecture(String proposal, String sessionId)` - Used by `/ai/status/ceo` and `/ai/ctochat`

### ProductManagerAgent
- `createEpic(String vision, String sessionId)` - Used by `/ai/status/product` and `/ai/chat/product`

### EngineerAgent
- `createImplementationSpec(String architecture, String sessionId)` - Used by `/ai/status/engineering` and `/ai/chat/engineer`

### QAAgent
- `createTestPlan(String feature, String sessionId)` - Used by `/ai/status/qa` and `/ai/chat/qa`

### DevOpsAgent
- `analyzeInfrastructure(String sessionId)` - Used by `/ai/status/devops` and `/ai/chat/devops`

### ScrumMasterAgent
- Currently returns placeholder data (requires proper ChatLanguageModel initialization)

---

## Error Handling

All endpoints return structured error responses:

```json
{
  "error": true,
  "message": "Error description",
  "timestamp": "2024-11-28T10:00:00",
  "status": "error"
}
```

HTTP Status Codes:
- `200 OK` - Success
- `400 Bad Request` - Invalid request (missing message, unknown agent)
- `500 Internal Server Error` - Server error

---

## CORS Configuration

All controllers include CORS configuration for:
- `http://localhost:5173`
- `http://localhost:5174`
- `http://localhost:3000`
- `http://localhost:8080`

---

## Usage Example

### Frontend Integration

```javascript
// Get CEO status
const response = await fetch('http://localhost:8087/ai/status/ceo');
const data = await response.json();

// Chat with CTO
const chatResponse = await fetch('http://localhost:8087/ai/ctochat', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    message: 'What is our deployment strategy?',
    sessionId: 'session-12345'
  })
});
const chatData = await chatResponse.json();
```

---

## Notes

1. **ScrumMasterAgent**: Currently returns placeholder data. To fully integrate, ensure ScrumMasterAgent is properly initialized in AgentRegistry with ChatLanguageModel.

2. **Session Management**: All chat endpoints support session IDs for conversation continuity. If not provided, a new UUID is generated.

3. **Structured JSON**: All responses follow a consistent structure with `timestamp`, `status`, and agent-specific data.

4. **Agent Registry**: Controllers use `AgentRegistry` to access agents, ensuring proper initialization and singleton pattern.



