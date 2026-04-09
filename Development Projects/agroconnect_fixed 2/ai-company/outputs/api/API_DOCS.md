# AI Company API Documentation

## Overview

The AI Company API provides REST endpoints for executing AI agents, running workflows, and obtaining CTO approvals. This is a pure meta-layer API that does not modify any AgroConnectWorld backend code.

## Base URL

```
http://localhost:8080/api
```

## Authentication

Currently, the API does not require authentication. In production, implement JWT or API key authentication.

## Endpoints

### Agent Execution

#### Run Agent
Execute a specific AI agent with input.

**Endpoint:** `POST /api/agents/{agentName}/run`

**Path Parameters:**
- `agentName` (string, required): Name of the agent to execute
  - Valid values: `cto_agent`, `architect_agent`, `engineer_agent`, `devops_agent`, `fullstack_agent`, `product_manager_agent`, `qa_agent`, `supervisor_agent`

**Request Body:**
```json
{
  "input": "string (required)",
  "session_id": "string (optional, default: 'default-session')",
  "parameters": {
    "key": "value"
  }
}
```

**Response:**
```json
{
  "agent_name": "cto_agent",
  "response": "Agent response text",
  "session_id": "session-123",
  "timestamp": "2024-11-27T20:00:00Z",
  "status": "success",
  "metadata": {}
}
```

**Example:**
```bash
curl -X POST http://localhost:8080/api/agents/architect_agent/run \
  -H "Content-Type: application/json" \
  -d '{
    "input": "Design authentication system architecture",
    "session_id": "session-123"
  }'
```

#### List Agents
Get list of available agents.

**Endpoint:** `GET /api/agents`

**Response:**
```json
{
  "agents": ["cto_agent", "architect_agent", "engineer_agent", ...],
  "total": 7
}
```

### Workflow Execution

#### Run Workflow
Execute a multi-agent workflow.

**Endpoint:** `POST /api/workflows/{workflowName}/run`

**Path Parameters:**
- `workflowName` (string, required): Name of the workflow
  - Valid values: `planning`, `architecture`, `development`, `review`, `testing`

**Request Body:**
```json
{
  "input": "string (required)",
  "session_id": "string (optional)",
  "workflow_parameters": {
    "key": "value"
  }
}
```

**Response:**
```json
{
  "workflow_name": "planning",
  "status": "completed",
  "session_id": "session-123",
  "timestamp": "2024-11-27T20:00:00Z",
  "results": {
    "epics": "...",
    "user_stories": "...",
    "acceptance_criteria": "..."
  },
  "execution_steps": [
    {
      "step_name": "create_epics",
      "agent": "product_manager_agent",
      "status": "completed",
      "output": "..."
    }
  ],
  "metadata": {}
}
```

**Example:**
```bash
curl -X POST http://localhost:8080/api/workflows/planning/run \
  -H "Content-Type: application/json" \
  -d '{
    "input": "Create product roadmap for order management system",
    "session_id": "session-123"
  }'
```

#### List Workflows
Get list of available workflows.

**Endpoint:** `GET /api/workflows`

**Response:**
```json
{
  "workflows": ["planning", "architecture", "development", "review", "testing"],
  "total": 5
}
```

### CTO Operations

#### Approve Decision
Get CTO approval for a decision.

**Endpoint:** `POST /api/cto/approve`

**Request Body:**
```json
{
  "decision": "string (required)",
  "context": "string (optional)",
  "session_id": "string (optional)",
  "request_type": "approval|review|risk_assessment (default: approval)"
}
```

**Response:**
```json
{
  "decision_type": "approval|rejection|modification_request",
  "approved": true,
  "review_summary": "Brief summary of review",
  "risk_level": "low|medium|high|critical",
  "recommendations": ["Recommendation 1", "Recommendation 2"],
  "session_id": "session-123",
  "timestamp": "2024-11-27T20:00:00Z",
  "technical_assessment": {
    "impact_analysis": "Analysis of impact",
    "compliance_check": "Zero-impact compliance verification",
    "breaking_changes": false
  },
  "metadata": {}
}
```

**Example:**
```bash
curl -X POST http://localhost:8080/api/cto/approve \
  -H "Content-Type: application/json" \
  -d '{
    "decision": "Proposed architecture for new feature",
    "context": "Feature requires new microservice",
    "session_id": "session-123",
    "request_type": "approval"
  }'
```

#### Review Architecture
CTO review of architecture.

**Endpoint:** `POST /api/cto/review`

Same request/response format as `/approve`, but automatically sets `request_type` to "review".

#### Assess Risk
CTO risk assessment.

**Endpoint:** `POST /api/cto/assess-risk`

Same request/response format as `/approve`, but automatically sets `request_type` to "risk_assessment".

## Available Agents

1. **cto_agent** / **supervisor_agent**: Chief Technology Officer
2. **architect_agent**: AI Architect
3. **engineer_agent**: AI Engineer
4. **devops_agent**: DevOps Specialist
5. **fullstack_agent**: Full-Stack Developer
6. **product_manager_agent**: Product Manager
7. **qa_agent**: QA Specialist

## Available Workflows

1. **planning**: Product planning workflow (PM → CTO)
2. **architecture**: Architecture design workflow (Architect → DevOps → CTO)
3. **development**: Development specification workflow (Engineer + FullStack → CTO)
4. **review**: Review and approval workflow (All agents → CTO)
5. **testing**: Testing workflow (QA → All agents → CTO)

## Error Responses

All endpoints return standard error responses:

```json
{
  "status": "error",
  "error": "Error message",
  "timestamp": "2024-11-27T20:00:00Z"
}
```

**HTTP Status Codes:**
- `200 OK`: Success
- `400 Bad Request`: Invalid request (invalid agent/workflow name, missing required fields)
- `500 Internal Server Error`: Server error during execution

## Zero-Impact Mode

**IMPORTANT**: All API operations operate in zero-impact mode:
- ✅ Read-only access to codebase
- ✅ Specification generation only
- ✅ No code modifications
- ✅ No configuration changes
- ✅ No production deployments

## Rate Limiting

Currently, no rate limiting is implemented. In production, implement rate limiting to prevent abuse.

## Session Management

Sessions are used to maintain context across multiple API calls. Use the same `session_id` for related operations.

## Best Practices

1. **Use Sessions**: Maintain session context for related operations
2. **Error Handling**: Always check `status` field in responses
3. **Workflow Order**: Execute workflows in order: planning → architecture → development → review → testing
4. **CTO Approval**: Always get CTO approval before proceeding to next workflow
5. **Input Quality**: Provide clear, specific inputs for better agent responses

## Examples

### Complete Workflow Example

```bash
# 1. Planning
curl -X POST http://localhost:8080/api/workflows/planning/run \
  -H "Content-Type: application/json" \
  -d '{
    "input": "Create product roadmap for user authentication",
    "session_id": "auth-feature-123"
  }'

# 2. Architecture
curl -X POST http://localhost:8080/api/workflows/architecture/run \
  -H "Content-Type: application/json" \
  -d '{
    "input": "Design authentication system architecture",
    "session_id": "auth-feature-123"
  }'

# 3. CTO Approval
curl -X POST http://localhost:8080/api/cto/approve \
  -H "Content-Type: application/json" \
  -d '{
    "decision": "Architecture design for authentication",
    "context": "From architecture workflow",
    "session_id": "auth-feature-123"
  }'
```



