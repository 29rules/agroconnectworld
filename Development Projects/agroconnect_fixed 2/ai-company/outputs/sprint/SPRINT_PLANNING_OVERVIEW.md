# Sprint Planning System Overview

## Purpose

The Sprint Planning System provides comprehensive sprint planning capabilities, including velocity tracking, capacity calculation, and intelligent item selection from the backlog.

## Components

### 1. SprintPlan
**Purpose:** Represents a complete sprint plan.

**Fields:**
- `sprintId`: Unique sprint identifier
- `startDate`: Sprint start date
- `endDate`: Sprint end date
- `sprintGoal`: Clear sprint goal
- `committedStories`: List of committed user stories
- `storyPointTotal`: Total story points committed
- `velocityForecast`: Predicted velocity for this sprint
- `risks`: List of identified risks
- `dependencies`: List of dependencies
- `blockers`: List of blockers
- `capacitySummary`: Agent capacity summary

**Helper Methods:**
- `getDurationDays()`: Calculates sprint duration
- `isOnTrack()`: Checks if committed points <= forecast
- `addCommittedStory()`: Adds a story to sprint
- `addRisk()`, `addDependency()`, `addBlocker()`: Adds planning items

---

### 2. SprintPlanner
**Purpose:** Creates sprint plans using ScrumMasterAgent and BacklogManager.

**Selection Criteria:**
1. **Velocity History:** Uses historical velocity to predict capacity
2. **Priority:** Selects HIGH priority items first
3. **Dependencies:** Ensures dependencies are satisfied
4. **Agent Capacity:** Respects agent available capacity

**Process:**
1. Get velocity forecast from SprintVelocityTracker
2. Get agent capacities from SprintCapacityCalculator
3. Select items from backlog based on criteria
4. Convert items to committed stories
5. Identify risks, dependencies, and blockers
6. Use ScrumMasterAgent to refine plan
7. Output SprintPlan

---

### 3. SprintVelocityTracker
**Purpose:** Tracks completed story points and predicts future velocity.

**Features:**
- Records sprint velocities
- Predicts velocity using simple linear prediction (average of last 3 sprints)
- Tracks velocity trends (increasing, decreasing, stable)
- Calculates average velocity over N sprints

**Prediction Method:**
- Simple linear: Average of last 3 sprints
- Default: 20 story points if no history
- Single data point: Use that value

---

### 4. SprintCapacityCalculator
**Purpose:** Estimates team capacity based on multiple factors.

**Factors:**
1. **Agent Availability:** Hours per day, availability factor
2. **Complexity:** Complexity factor (0.5 to 2.0)
3. **Past Performance:** Performance factor (0.5 to 1.5)
4. **Special Constraints:** Holidays, reduced capacity

**Calculation:**
```
Capacity = Working Days × Hours Per Day × Story Points Per Hour
         × Availability Factor × Complexity Factor × Past Performance Factor
```

**Default Values:**
- Hours per day: 6 (accounting for meetings, overhead)
- Story points per hour: 1/6 (1 story point per 6 hours)
- Availability: 1.0 (100%)
- Complexity: 1.0 (normal)
- Performance: 1.0 (average)

---

## Usage Examples

### Creating a Sprint Plan

```java
// Initialize components
BacklogManager backlogManager = new BacklogManager();
ChatLanguageModel chatModel = // ... initialize
SprintPlanner planner = new SprintPlanner(chatModel, backlogManager);

// Create sprint plan
SprintPlan plan = planner.createSprintPlan(
    1,  // Sprint number
    2,  // Duration in weeks
    "Implement user authentication and product catalog",
    "session-123"
);

System.out.println("Sprint Goal: " + plan.getSprintGoal());
System.out.println("Committed Stories: " + plan.getCommittedStories().size());
System.out.println("Story Points: " + plan.getStoryPointTotal());
System.out.println("Velocity Forecast: " + plan.getVelocityForecast());
System.out.println("On Track: " + plan.isOnTrack());
```

### Tracking Velocity

```java
SprintVelocityTracker tracker = new SprintVelocityTracker();

// Record completed velocity
tracker.recordSprintVelocity(1, 25); // Sprint 1 completed 25 points
tracker.recordSprintVelocity(2, 28); // Sprint 2 completed 28 points
tracker.recordSprintVelocity(3, 23); // Sprint 3 completed 23 points

// Predict next sprint
int predicted = tracker.predictVelocity(4); // Predicts based on last 3 sprints

// Get trend
String trend = tracker.getVelocityTrend(); // INCREASING, DECREASING, or STABLE
```

### Calculating Capacity

```java
SprintCapacityCalculator calculator = new SprintCapacityCalculator();

// Set agent capacity
SprintCapacityCalculator.AgentCapacity capacity = new SprintCapacityCalculator.AgentCapacity();
capacity.setAgentName("EngineerAgent");
capacity.setHoursPerDay(6.0);
capacity.setStoryPointsPerHour(1.0 / 6.0);
capacity.setAvailabilityFactor(0.9); // 90% available
capacity.setComplexityFactor(1.0); // Normal complexity
capacity.setPastPerformanceFactor(1.1); // 10% above average
calculator.setAgentCapacity("EngineerAgent", capacity);

// Add holidays
calculator.addHoliday(LocalDate.of(2024, 12, 25)); // Christmas

// Calculate capacities for 2-week sprint
Map<String, Integer> capacities = calculator.calculateAgentCapacities(2);
// Returns: { "EngineerAgent": 10, "QAAgent": 8, ... }
```

---

## Sprint Planning Process

### Step 1: Velocity Prediction
```
SprintVelocityTracker.predictVelocity(sprintNumber)
→ Returns: Predicted story points for sprint
```

### Step 2: Capacity Calculation
```
SprintCapacityCalculator.calculateAgentCapacities(sprintDurationWeeks)
→ Returns: Map of agent names to capacity (story points)
```

### Step 3: Item Selection
```
SprintPlanner.selectSprintItems(velocityForecast, agentCapacities)
→ Criteria:
   - Priority (HIGH first)
   - Ready (has acceptance criteria and estimate)
   - Not blocked
   - Within velocity limit
   - Within agent capacity
→ Returns: Selected backlog items
```

### Step 4: Plan Creation
```
SprintPlanner.createSprintPlan(...)
→ Creates SprintPlan with:
   - Committed stories
   - Story point total
   - Velocity forecast
   - Risks, dependencies, blockers
   - Capacity summary
```

### Step 5: ScrumMaster Refinement
```
ScrumMasterAgent.createSprintPlan(...)
→ Refines plan with:
   - Epic breakdown
   - Task breakdown
   - Acceptance criteria
   - Risk assessment
```

---

## Velocity Prediction

### Simple Linear Prediction
Uses average of last 3 sprints:

```java
prediction = (velocity[n-2] + velocity[n-1] + velocity[n]) / 3
```

### Default Prediction
- No history: 20 story points
- Single data point: Use that value
- Multiple data points: Average of last 3

### Velocity Trends
- **INCREASING:** Last 3 sprints show increasing velocity
- **DECREASING:** Last 3 sprints show decreasing velocity
- **STABLE:** Velocity is relatively stable

---

## Capacity Calculation

### Working Days Calculation
```
Working Days = Sprint Days - Weekends - Holidays
```

### Agent Capacity Formula
```
Agent Capacity = Working Days × Hours Per Day × Story Points Per Hour
               × Availability Factor × Complexity Factor × Past Performance Factor
```

### Example Calculation
```
2-week sprint = 10 working days (excluding weekends)
EngineerAgent:
  - Hours per day: 6
  - Story points per hour: 1/6
  - Availability: 0.9 (90%)
  - Complexity: 1.0 (normal)
  - Performance: 1.1 (110%)
  
Capacity = 10 × 6 × (1/6) × 0.9 × 1.0 × 1.1
         = 10 × 0.9 × 1.1
         = 9.9 ≈ 10 story points
```

---

## Item Selection Algorithm

1. **Get TODO items** from backlog
2. **Sort by priority** (HIGH first)
3. **Filter ready items** (has acceptance criteria and estimate)
4. **Filter unblocked items** (dependencies satisfied)
5. **Select items** until velocity limit reached:
   - Check velocity limit
   - Check agent capacity
   - Add to sprint
6. **Stop** when velocity forecast reached

---

## Sprint Plan Structure

```json
{
  "sprintId": "SPRINT-1",
  "startDate": "2024-01-01",
  "endDate": "2024-01-14",
  "sprintGoal": "Implement user authentication",
  "committedStories": [
    {
      "storyId": "item-id",
      "title": "User Registration",
      "storyPoints": 5,
      "assignedAgent": "EngineerAgent",
      "acceptanceCriteria": ["..."],
      "priority": "HIGH"
    }
  ],
  "storyPointTotal": 25,
  "velocityForecast": 25,
  "risks": ["..."],
  "dependencies": ["..."],
  "blockers": ["..."],
  "capacitySummary": "Agent Capacities: ..."
}
```

---

## Best Practices

### Velocity Tracking
1. **Record Accurately:** Only count completed story points
2. **Track Consistently:** Use same estimation method
3. **Review Trends:** Monitor velocity trends over time
4. **Adjust Predictions:** Refine predictions based on patterns

### Capacity Planning
1. **Be Realistic:** Account for meetings and overhead
2. **Consider Holidays:** Exclude holidays from capacity
3. **Factor Complexity:** Adjust for task complexity
4. **Track Performance:** Use past performance to adjust

### Sprint Planning
1. **Use Velocity:** Base commitment on historical velocity
2. **Respect Capacity:** Don't overcommit agents
3. **Check Dependencies:** Ensure dependencies are satisfied
4. **Identify Risks:** Identify and plan for risks early
5. **Balance Workload:** Distribute work evenly across agents

---

## Integration Points

The Sprint Planning System integrates with:

1. **ScrumMasterAgent** - For sprint plan refinement
2. **BacklogManager** - For item selection
3. **SprintVelocityTracker** - For velocity prediction
4. **SprintCapacityCalculator** - For capacity calculation
5. **BacklogReporter** - For backlog health checks

---

## Zero-Impact Compliance

All sprint planning operations:
- ✅ Create plans and forecasts only
- ✅ Track velocity and capacity
- ✅ Select items from backlog
- ✅ Never modify code or systems
- ✅ Provide planning data for agents
- ✅ Require approval for execution
- ✅ Full audit trail with timestamps

The Sprint Planning System is a planning and coordination tool that supports Agile development without making actual changes to code or systems.



