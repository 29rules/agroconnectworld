# Scrum Master Agent Overview

## Purpose

The Scrum Master Agent facilitates Agile development processes and coordinates the AI agent team to deliver value incrementally through well-planned sprints.

## Core Responsibilities

### 1. Sprint Planning
Creates comprehensive sprint plans with:
- Clear sprint goals
- Epics broken down into user stories
- User stories broken down into tasks
- Acceptance criteria
- Effort estimates
- Capacity planning

### 2. Backlog Management
Maintains healthy backlog by:
- Prioritizing backlog items
- Refining user stories (INVEST compliance)
- Removing obsolete items
- Ensuring proper sizing
- Maintaining clear prioritization

### 3. Burndown Charts
Generates burndown chart data to:
- Track sprint progress
- Identify velocity trends
- Predict sprint completion
- Identify if sprint is on track

### 4. Daily Standups
Creates daily standup summaries:
- What did I do yesterday?
- What will I do today?
- Are there any blockers?
- Identifies impediments
- Coordinates team activities

### 5. Weekly Summaries
Creates weekly progress summaries:
- Highlights achievements
- Identifies risks and issues
- Plans next week activities
- Tracks velocity and capacity

### 6. Velocity & Capacity Prediction
Predicts team performance:
- Historical velocity analysis
- Capacity calculation
- Velocity-based sprint planning
- Realistic scope commitment

### 7. Blocker & Dependency Management
Identifies and manages:
- Blockers early detection
- Task dependencies
- Blocker escalation
- Resolution coordination

### 8. Agent Coordination
Coordinates all AI agents:
- ProductManagerAgent (requirements)
- ArchitectAgent (design)
- CTOAgent (approvals)
- EngineerAgent (implementation)
- QAAgent (testing)
- DevOpsAgent (deployment)
- FullStackAgent (integration)
- SupervisorAgent (safety)

---

## Key Methods

### createSprintPlan()
Creates a comprehensive sprint plan with all required fields.

**Parameters:**
- `sprintNumber`: Sprint number
- `sprintDuration`: Duration in weeks (default: 2)
- `sprintGoal`: High-level sprint goal
- `sessionId`: Session ID for tracking

**Returns:** JSON sprint plan with:
- Sprint goal
- Epics
- User stories
- Tasks
- Acceptance criteria
- Risks
- Capacity plan
- Predicted velocity
- Dependencies
- Blockers
- Deliverables

---

### breakDownEpic()
Breaks down an epic into user stories and tasks.

**Parameters:**
- `epicDescription`: Epic description
- `sessionId`: Session ID

**Returns:** JSON breakdown with:
- User stories (INVEST-compliant)
- Tasks for each story
- Acceptance criteria
- Estimates

---

### maintainBacklogHealth()
Maintains backlog health and prioritization.

**Parameters:**
- `backlogItems`: List of backlog items
- `sessionId`: Session ID

**Returns:** Backlog health report with:
- Prioritization recommendations
- Refinement suggestions
- Obsolete item identification
- Size recommendations

---

### generateBurndownChart()
Generates burndown chart data for sprint tracking.

**Parameters:**
- `sprintNumber`: Sprint number
- `completedTasks`: List of completed tasks
- `totalTasks`: Total tasks in sprint
- `sessionId`: Session ID

**Returns:** Burndown chart data with:
- Daily progress
- Remaining work
- Ideal burndown line
- On-track status
- Predicted completion

---

### createDailyStandup()
Creates daily standup summary.

**Parameters:**
- `date`: Date for standup
- `teamUpdates`: List of team member updates
- `sessionId`: Session ID

**Returns:** Daily standup summary with:
- Participant updates
- Completed work
- Planned work
- Blockers
- Action items

---

### createWeeklySummary()
Creates weekly progress summary.

**Parameters:**
- `weekStartDate`: Week start date
- `weekProgress`: Week progress data
- `sessionId`: Session ID

**Returns:** Weekly summary with:
- Achievements
- Completed work
- Risks and issues
- Next week plan
- Velocity tracking

---

### predictVelocityAndCapacity()
Predicts team velocity and capacity.

**Parameters:**
- `historicalVelocity`: Historical velocity data
- `teamCapacity`: Team capacity information
- `sessionId`: Session ID

**Returns:** Velocity and capacity prediction with:
- Predicted velocity (story points)
- Confidence level
- Capacity calculation
- Sprint scope recommendation

---

### identifyBlockersAndDependencies()
Identifies blockers and dependencies.

**Parameters:**
- `sprintContext`: Sprint context and tasks
- `sessionId`: Session ID

**Returns:** Blockers and dependencies report with:
- Identified blockers
- Task dependencies
- Blocker severity
- Resolution plans

---

### coordinateAgentsForSprint()
Coordinates all agents for sprint planning.

**Parameters:**
- `sprintGoal`: Sprint goal
- `sessionId`: Session ID

**Returns:** Coordination plan with:
- Agent assignments
- Coordination sequence
- Handoff points
- Communication plan

---

## JSON Output Schema

### Required Fields

All sprint plans must include:

1. **sprint_goal** - Clear, achievable sprint goal
2. **epics** - List of epics with IDs, names, priorities
3. **user_stories** - List of user stories with:
   - Story ID, title, description
   - Acceptance criteria
   - Story points
   - Priority and status
4. **tasks** - List of tasks with:
   - Task ID, title, description
   - Estimated hours
   - Assigned agent
   - Status and dependencies
5. **acceptance_criteria** - Map of story IDs to criteria
6. **risks** - List of risks with probability and impact
7. **capacity_plan** - Capacity calculation
8. **predicted_velocity** - Velocity prediction
9. **dependencies** - Task dependencies
10. **blockers** - Identified blockers
11. **deliverables** - Sprint deliverables

---

## Agile Principles

### INVEST Compliance
User stories must be:
- **Independent:** Can be developed in any order
- **Negotiable:** Details can be refined
- **Valuable:** Delivers user/business value
- **Estimable:** Can be estimated
- **Small:** Completable in one sprint
- **Testable:** Has clear acceptance criteria

### Story Point Estimation
Use Fibonacci sequence: 1, 2, 3, 5, 8, 13

- 1 point: Trivial (< 2 hours)
- 2 points: Small (2-4 hours)
- 3 points: Medium (1 day)
- 5 points: Large (2-3 days)
- 8 points: Very large (1 week)
- 13 points: Too large (break down)

### Velocity Rules
- Velocity = Completed story points, not planned
- Historical velocity is best predictor
- Capacity accounts for overhead
- Don't overcommit

---

## Scrum Ceremonies

### Sprint Planning
- **Duration:** 2-4 hours for 2-week sprint
- **Output:** Sprint plan with goal, stories, tasks
- **Participants:** All agents

### Daily Standup
- **Duration:** 15 minutes
- **Output:** Daily standup summary
- **Format:** Yesterday, Today, Blockers

### Sprint Review
- **Duration:** 1-2 hours
- **Output:** Demo, feedback, backlog updates
- **Participants:** All agents, stakeholders

### Sprint Retrospective
- **Duration:** 1-2 hours
- **Output:** Action items for improvement
- **Format:** What went well, what to improve

---

## Agent Coordination Flow

```
1. ProductManagerAgent
   ↓ (Requirements & Priorities)
2. ArchitectAgent
   ↓ (Technical Design)
3. CTOAgent
   ↓ (Approval)
4. EngineerAgent
   ↓ (Implementation)
5. QAAgent
   ↓ (Testing)
6. DevOpsAgent
   ↓ (Deployment - with approvals)
7. SupervisorAgent
   ↓ (Final approval)
8. Production
```

---

## Usage Examples

### Creating a Sprint Plan

```java
ScrumMasterAgent scrumMaster = new ScrumMasterAgent(chatModel);
String sprintPlan = scrumMaster.createSprintPlan(
    1,  // Sprint number
    2,  // Duration in weeks
    "Implement user authentication and product catalog",
    "session-123"
);
```

### Breaking Down an Epic

```java
String epicDescription = "Build seller dashboard for AgroConnectWorld";
String breakdown = scrumMaster.breakDownEpic(epicDescription, "session-123");
```

### Creating Daily Standup

```java
LocalDate today = LocalDate.now();
List<String> updates = Arrays.asList(
    "EngineerAgent: Completed auth-service implementation",
    "QAAgent: Running integration tests",
    "DevOpsAgent: Setting up staging environment"
);
String standup = scrumMaster.createDailyStandup(today, updates, "session-123");
```

### Generating Burndown Chart

```java
List<String> completed = Arrays.asList("TASK-001", "TASK-002", "TASK-003");
String burndown = scrumMaster.generateBurndownChart(1, completed, 10, "session-123");
```

---

## Zero-Impact Mode

**IMPORTANT:** The Scrum Master Agent operates in ZERO-IMPACT MODE.

**Allowed:**
- ✅ Create plans and summaries
- ✅ Coordinate agents
- ✅ Generate reports and charts
- ✅ Identify blockers and dependencies

**Not Allowed:**
- ❌ Modify code
- ❌ Deploy to production
- ❌ Make system changes
- ❌ Execute tasks directly

**All outputs are planning documents that must be:**
- Reviewed by CTO
- Approved by Supervisor
- Executed by appropriate agents
- Deployed through proper workflows

---

## Best Practices

1. **Keep Stories Small:** Completable in one sprint
2. **Clear Acceptance Criteria:** Every story needs testable criteria
3. **Realistic Estimates:** Base on historical velocity
4. **Identify Blockers Early:** Don't wait for critical blockers
5. **Track Dependencies:** Map all task dependencies
6. **Regular Communication:** Facilitate daily standups
7. **Velocity-Based Planning:** Use historical data
8. **Continuous Improvement:** Use retrospectives

---

## Integration Points

The Scrum Master Agent integrates with:

1. **ProductManagerAgent** - Requirements and priorities
2. **ArchitectAgent** - Technical design
3. **CTOAgent** - Approvals
4. **EngineerAgent** - Implementation
5. **QAAgent** - Testing
6. **DevOpsAgent** - Deployment
7. **FullStackAgent** - Integration
8. **SupervisorAgent** - Safety and compliance

---

## Output Examples

### Sprint Plan Example

```json
{
  "sprint_goal": "Implement user authentication and basic product catalog",
  "sprint_number": 1,
  "sprint_duration_weeks": 2,
  "epics": [
    {
      "epic_id": "EPIC-001",
      "epic_name": "User Authentication",
      "priority": "HIGH"
    }
  ],
  "user_stories": [
    {
      "story_id": "US-001",
      "title": "As a user, I want to register so that I can access the platform",
      "story_points": 5,
      "acceptance_criteria": [
        "User can register with email and password",
        "Email validation is performed",
        "Password meets security requirements"
      ]
    }
  ],
  "tasks": [
    {
      "task_id": "TASK-001",
      "story_id": "US-001",
      "title": "Create registration API endpoint",
      "estimated_hours": 4,
      "assigned_agent": "EngineerAgent"
    }
  ],
  "predicted_velocity": {
    "story_points": 25,
    "confidence": "MEDIUM"
  }
}
```

---

## Limitations

1. **Planning Only:** Does not execute tasks
2. **No Code Changes:** Never modifies code
3. **Approval Required:** Plans must be approved
4. **Agent Dependent:** Relies on other agents for execution

---

## Future Enhancements

- Integration with project management tools
- Automated task assignment
- Real-time sprint tracking
- Velocity trend analysis
- Automated blocker detection
- Integration with CI/CD for task completion tracking

---

## Zero-Impact Compliance

All Scrum Master operations:
- ✅ Create planning documents only
- ✅ Coordinate agents without executing
- ✅ Generate reports and summaries
- ✅ Never modify code or systems
- ✅ Require approvals for execution
- ✅ Provide full audit trails

The Scrum Master Agent is a facilitator and coordinator, ensuring smooth Agile processes while maintaining safety and compliance.



