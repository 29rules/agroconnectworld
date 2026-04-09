# Multi-Agent Task Planner Overview

## Architecture

The task planner system provides intelligent task decomposition and multi-agent coordination for the AgroConnectWorld AI Company.

## Components

### 1. TaskNode
- **Purpose**: Represents a single task in the planning system
- **Fields**: id, description, assignedAgent, dependencies, status, priority, estimatedEffort
- **Location**: `com.ai.company.planner.TaskNode`

### 2. TaskGraph
- **Purpose**: Maintains DAG (Directed Acyclic Graph) structure of tasks
- **Features**:
  - Cycle detection
  - Topological sorting
  - Dependency management
  - Ready task identification
- **Location**: `com.ai.company.planner.TaskGraph`

### 3. TaskPlanner
- **Purpose**: Uses LLM to break down requests into subtasks
- **Features**:
  - LLM-powered task decomposition
  - JSON parsing and validation
  - Task graph creation
  - Planning output generation
- **Location**: `com.ai.company.planner.TaskPlanner`

### 4. TaskAssignmentEngine
- **Purpose**: Maps subtasks to appropriate agents
- **Features**:
  - Capability-based assignment
  - Workload balancing
  - Keyword matching
  - Specialized task handling
- **Location**: `com.ai.company.planner.TaskAssignmentEngine`

### 5. PlanningOutput
- **Purpose**: Structured JSON output of planning process
- **Fields**: planId, tasks, executionOrder, agentAssignments, timeline, dependencies
- **Location**: `com.ai.company.planner.PlanningOutput`

## Data Flow

```
High-Level Request
    ↓
TaskPlanner.plan()
    ↓
LLM Task Decomposition
    ↓
Parse Subtasks (JSON)
    ↓
TaskAssignmentEngine.assignAgent()
    ↓
Create TaskGraph
    ↓
Validate DAG (Cycle Detection)
    ↓
Topological Sort
    ↓
Build PlanningOutput
    ↓
Structured JSON Plan
```

## Usage Example

```java
// Initialize components
ChatLanguageModel chatModel = OpenAiChatModel.builder()
    .apiKey("your-api-key")
    .modelName("gpt-4")
    .build();

TaskAssignmentEngine assignmentEngine = new TaskAssignmentEngine();
TaskPlanner planner = new TaskPlanner(chatModel, assignmentEngine);

// Plan tasks
PlanningOutput plan = planner.plan(
    "Implement user authentication system",
    "session-123"
);

// Access plan details
List<PlanningOutput.TaskInfo> tasks = plan.getTasks();
List<String> executionOrder = plan.getExecutionOrder();
Map<String, List<String>> agentAssignments = plan.getAgentAssignments();
```

## Task Lifecycle

1. **PENDING**: Task created, waiting for dependencies
2. **IN_PROGRESS**: Task assigned and being worked on
3. **COMPLETED**: Task finished successfully
4. **FAILED**: Task encountered an error
5. **BLOCKED**: Task blocked by dependencies or other issues

## Agent Assignment Rules

- **Architecture tasks** → `architect_agent`
- **Backend implementation** → `engineer_agent`
- **Frontend implementation** → `fullstack_agent`
- **Infrastructure tasks** → `devops_agent`
- **Epic/user story creation** → `product_manager_agent`
- **Testing tasks** → `qa_agent`
- **Approval/review** → `cto_agent`

## DAG Validation

The system ensures:
- No circular dependencies
- Valid task references
- Proper topological ordering
- Ready task identification

## Zero-Impact Mode

All tasks produced are:
- Specification-only
- No code modifications
- No configuration changes
- No production deployments
- Read-only operations

## Integration Points

### With Agents
- Tasks assigned to specific agents
- Agents execute tasks in dependency order
- Status updates flow back to planner

### With Workflows
- Planning workflow uses TaskPlanner
- Other workflows can query task status
- Cross-workflow task coordination

### With Memory System
- Plans saved to memory
- Past plans recalled for similar requests
- Learning from previous planning sessions

## Future Enhancements

1. **Dynamic Replanning**: Adjust plans based on task outcomes
2. **Resource Constraints**: Consider agent availability
3. **Deadline Management**: Time-based task scheduling
4. **Risk Assessment**: Identify high-risk tasks
5. **Parallel Execution**: Optimize for concurrent task execution
6. **Plan Optimization**: AI-powered plan improvement



