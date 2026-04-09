# Observability System Overview

## Introduction

The Observability System provides comprehensive logging, monitoring, and audit trail capabilities for the AgroConnectWorld AI Company. It tracks all agent executions and workflow runs, enabling debugging, performance analysis, and compliance auditing.

## Components

### 1. AgentExecutionLog

Tracks individual agent execution details.

**Fields**:
- `executionId` (UUID): Unique identifier for the execution
- `timestamp` (Instant): When the execution occurred
- `agentName` (String): Name of the agent
- `sessionId` (String): Session identifier for context
- `status` (ExecutionStatus): Execution status
- `promptSummary` (String): Summary of the input prompt
- `responseSummary` (String): Summary of the agent response
- `durationMs` (Long): Execution duration in milliseconds
- `tokenCount` (Integer): Number of tokens used
- `modelUsed` (String): LLM model used
- `warnings` (List<String>): List of warnings
- `errors` (List<String>): List of errors
- `workflowName` (String): Associated workflow name
- `taskId` (String): Associated task ID
- `metadata` (Map): Additional metadata

**Status Values**:
- `PENDING`: Execution not yet started
- `RUNNING`: Execution in progress
- `SUCCESS`: Execution completed successfully
- `WARNING`: Execution completed with warnings
- `ERROR`: Execution failed
- `CANCELLED`: Execution was cancelled

### 2. WorkflowExecutionLog

Tracks workflow execution across multiple agents.

**Fields**:
- `workflowExecutionId` (UUID): Unique identifier for the workflow execution
- `startTimestamp` (Instant): When the workflow started
- `endTimestamp` (Instant): When the workflow ended
- `workflowName` (String): Name of the workflow
- `sessionId` (String): Session identifier
- `status` (WorkflowStatus): Workflow status
- `agentExecutions` (List<AgentExecutionLog>): List of agent executions in the workflow
- `totalSteps` (int): Total number of steps
- `completedSteps` (int): Number of completed steps
- `failedSteps` (int): Number of failed steps
- `inputSummary` (String): Summary of workflow input
- `outputSummary` (String): Summary of workflow output
- `totalDurationMs` (Long): Total workflow duration
- `totalTokenCount` (Integer): Total tokens used across all agents
- `warnings` (List<String>): Aggregated warnings
- `errors` (List<String>): Aggregated errors
- `initiatedBy` (String): Who/what initiated the workflow
- `metadata` (Map): Additional metadata

**Status Values**:
- `PENDING`: Workflow not yet started
- `RUNNING`: Workflow in progress
- `SUCCESS`: Workflow completed successfully
- `WARNING`: Workflow completed with warnings
- `ERROR`: Workflow failed
- `CANCELLED`: Workflow was cancelled

### 3. AuditTrailService

Service for managing and querying execution logs.

**Key Methods**:
- `logAgentExecution()`: Log an agent execution
- `logWorkflowExecution()`: Log a workflow execution
- `getAgentExecution()`: Get agent execution by ID
- `getWorkflowExecution()`: Get workflow execution by ID
- `getAgentExecutions()`: Get all executions for an agent
- `getWorkflowExecutions()`: Get all executions for a workflow
- `getSessionExecutions()`: Get all executions for a session
- `getAgentExecutionsWithErrors()`: Get agent executions with errors
- `getWorkflowExecutionsWithErrors()`: Get workflow executions with errors
- `getAgentStatistics()`: Get statistics for an agent
- `getWorkflowStatistics()`: Get statistics for a workflow

**Indexes**:
- Agent name index for fast lookup by agent
- Session index for fast lookup by session
- Workflow name index for fast lookup by workflow

### 4. LogFormatter

Formats logs for output in various formats.

**Methods**:
- `formatAsJson()`: Format as JSON
- `formatAsText()`: Format as human-readable text
- `formatSummary()`: Format as brief summary

## Usage Examples

### Logging Agent Execution

```java
AgentExecutionLog log = new AgentExecutionLog("architect_agent", "session-123");
log.setPromptSummary("Design authentication system architecture");
log.setResponseSummary("Architecture proposal with microservices...");
log.setDurationMs(1500L);
log.setTokenCount(2500);
log.setModelUsed("gpt-4");
log.markSuccess();

auditTrailService.logAgentExecution(log);
```

### Logging Workflow Execution

```java
WorkflowExecutionLog workflowLog = new WorkflowExecutionLog("planning", "session-123");
workflowLog.setInputSummary("Create product roadmap");
workflowLog.markStarted();

// Add agent executions
AgentExecutionLog agentLog1 = new AgentExecutionLog("product_manager_agent", "session-123");
agentLog1.markSuccess();
workflowLog.addAgentExecution(agentLog1);

AgentExecutionLog agentLog2 = new AgentExecutionLog("cto_agent", "session-123");
agentLog2.markSuccess();
workflowLog.addAgentExecution(agentLog2);

workflowLog.markCompleted();
auditTrailService.logWorkflowExecution(workflowLog);
```

### Querying Logs

```java
// Get all executions for an agent
List<AgentExecutionLog> logs = auditTrailService.getAgentExecutions("architect_agent");

// Get all executions for a session
Map<String, List<?>> sessionLogs = auditTrailService.getSessionExecutions("session-123");

// Get executions with errors
List<AgentExecutionLog> errorLogs = auditTrailService.getAgentExecutionsWithErrors();

// Get statistics
Map<String, Object> stats = auditTrailService.getAgentStatistics("architect_agent");
```

### Formatting Logs

```java
LogFormatter formatter = new LogFormatter();

// Format as JSON
String json = formatter.formatAsJson(agentLog);

// Format as text
String text = formatter.formatAsText(agentLog);

// Format as summary
String summary = formatter.formatSummary(agentLog);
```

## Integration Points

### With Agents
- Agents create execution logs before execution
- Agents update logs with results after execution
- Agents add warnings/errors as needed

### With Workflows
- Workflows create workflow logs at start
- Workflows add agent execution logs as steps complete
- Workflows update status and metrics

### With API
- API endpoints can query logs
- API can return formatted logs
- API can provide statistics

### With Validation
- Validation results added to logs
- Validation errors tracked
- Compliance checks logged

## Logging Best Practices

1. **Log Early**: Create log entry at start of execution
2. **Update Frequently**: Update log as execution progresses
3. **Include Context**: Add session ID, workflow name, task ID
4. **Track Performance**: Log duration and token usage
5. **Capture Issues**: Log all warnings and errors
6. **Add Metadata**: Include relevant metadata for debugging

## Query Patterns

### By Agent
```java
List<AgentExecutionLog> logs = auditTrailService.getAgentExecutions("architect_agent");
```

### By Session
```java
Map<String, List<?>> logs = auditTrailService.getSessionExecutions("session-123");
```

### By Workflow
```java
List<WorkflowExecutionLog> logs = auditTrailService.getWorkflowExecutions("planning");
```

### By Time Range
```java
Instant start = Instant.now().minus(Duration.ofHours(24));
Instant end = Instant.now();
List<AgentExecutionLog> logs = auditTrailService.getAgentExecutions(start, end);
```

### With Errors
```java
List<AgentExecutionLog> errorLogs = auditTrailService.getAgentExecutionsWithErrors();
```

## Statistics

### Agent Statistics
- Total executions
- Successful executions
- Executions with warnings
- Executions with errors
- Average duration
- Total tokens used

### Workflow Statistics
- Total executions
- Successful executions
- Executions with warnings
- Executions with errors
- Average duration
- Average completion percentage

## Storage

Currently uses in-memory storage (ConcurrentHashMap). In production:
- Use database (PostgreSQL, MongoDB)
- Implement retention policies
- Add archival for old logs
- Support distributed tracing

## Future Enhancements

1. **Distributed Tracing**: Track requests across services
2. **Metrics Collection**: Aggregate metrics over time
3. **Alerting**: Alert on errors or performance issues
4. **Dashboards**: Visual dashboards for monitoring
5. **Log Aggregation**: Centralized log collection
6. **Performance Analysis**: Identify bottlenecks
7. **Cost Tracking**: Track token usage and costs



