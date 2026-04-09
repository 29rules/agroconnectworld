# CEO Panel API Integration

## Overview

The CEO Dashboard panels are now connected to the AI Company backend API, providing real-time metrics and insights from the AI agents.

## API Endpoints

### Status Endpoints

All status endpoints return JSON with the following structure:
```json
{
  "timestamp": "2024-11-28T10:00:00",
  "status": "operational",
  "metrics": {
    // Panel-specific metrics
  }
}
```

#### GET /ai/status/ceo
Returns overall CEO dashboard status and metrics.

**Response:**
```json
{
  "timestamp": "2024-11-28T10:00:00",
  "status": "operational",
  "systemHealth": {
    "agents": 8,
    "activeWorkflows": 0,
    "totalExecutions": 0
  },
  "metrics": {
    "totalUsers": 0,
    "totalProducts": 0,
    "totalOrders": 0,
    "totalRevenue": 0
  }
}
```

#### GET /ai/status/engineering
Returns engineering metrics and development statistics.

**Response:**
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
    "meanTimeToRecovery": "15 minutes"
  }
}
```

#### GET /ai/status/qa
Returns QA metrics and testing statistics.

**Response:**
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
    "avgResolutionTime": "2.5 days"
  }
}
```

#### GET /ai/status/product
Returns product metrics and performance data.

**Response:**
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
    "totalReviews": 3456
  }
}
```

#### GET /ai/status/scrum
Returns Scrum/Agile metrics and sprint data.

**Response:**
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
    "blockers": 2
  }
}
```

#### GET /ai/status/devops
Returns DevOps metrics and infrastructure data.

**Response:**
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
    "incidents": 2
  }
}
```

### Chat Endpoint

#### POST /ai/ctochat
Sends a message to the CTO agent and receives a response.

**Request:**
```json
{
  "message": "What is the current system architecture?",
  "sessionId": "session-12345"
}
```

**Response:**
```json
{
  "sessionId": "session-12345",
  "message": "What is the current system architecture?",
  "response": "The current system architecture follows a microservices pattern...",
  "timestamp": "2024-11-28T10:00:00",
  "agent": "CTO"
}
```

## Frontend Integration

### API Service Methods

All methods are available via `api.ai.*`:

```javascript
import api from '../services/api';

// Get CEO status
const ceoStatus = await api.ai.getCEOStatus();

// Get Engineering status
const engStatus = await api.ai.getEngineeringStatus();

// Get QA status
const qaStatus = await api.ai.getQAStatus();

// Get Product status
const productStatus = await api.ai.getProductStatus();

// Get Scrum status
const scrumStatus = await api.ai.getScrumStatus();

// Get DevOps status
const devopsStatus = await api.ai.getDevOpsStatus();

// Chat with CTO
const chatResponse = await api.ai.ctoChat("What is our deployment strategy?");
```

### Configuration

The AI Company API base URL can be configured via environment variable:

```env
VITE_AI_API_BASE_URL=http://localhost:8087
```

If not set, defaults to `http://localhost:8087`.

## Running the AI Company API

### Start the API Server

```bash
cd ai-company
mvn spring-boot:run
```

The API will start on port 8087 (configurable via `AI_COMPANY_PORT` environment variable).

### Environment Variables

```env
# AI Company API Port
AI_COMPANY_PORT=8087

# OpenAI API Key (for AI agents)
OPENAI_API_KEY=your-api-key-here
# OR
OPENROUTER_API_KEY=your-openrouter-key-here
```

## CORS Configuration

The StatusController includes CORS configuration for:
- `http://localhost:5173`
- `http://localhost:5174`
- `http://localhost:3000`
- `http://localhost:8080`

## Error Handling

All endpoints return appropriate HTTP status codes:
- `200 OK` - Success
- `400 Bad Request` - Invalid request
- `500 Internal Server Error` - Server error

Error responses include:
```json
{
  "error": true,
  "message": "Error description",
  "timestamp": "2024-11-28T10:00:00"
}
```

## Frontend Components

All CEO panel components automatically:
1. Fetch data from AI Company API on mount
2. Display loading states while fetching
3. Fall back to placeholder data if API is unavailable
4. Handle errors gracefully

## Next Steps

1. Start the AI Company API server
2. Configure API key in environment
3. Access CEO dashboard at `/admin/ceo`
4. Panels will automatically fetch real-time data



