# Burndown Chart & Analytics Overview

## Purpose

The Analytics System provides comprehensive burndown chart generation and story point analytics for sprint tracking and velocity analysis.

## Components

### 1. BurndownChartGenerator
**Purpose:** Generates burndown charts from SprintPlan and SprintVelocityTracker.

**Inputs:**
- `SprintPlan` - Sprint plan with committed stories
- `SprintVelocityTracker` - Velocity tracking data

**Outputs:**
- ASCII burndown graph (console/logs)
- Text file saved in `outputs/analytics/burndown_{sprintId}.txt`
- PNG chart (future enhancement)

**Features:**
- Ideal burn line (linear)
- Actual burn line (from completed work)
- Visual comparison
- Summary statistics
- Deviation calculation

---

### 2. StoryPointAnalytics
**Purpose:** Calculates and visualizes story point burn analytics.

**Features:**
- **Ideal Burn Calculation:** Linear burn rate based on sprint duration
- **Actual Burn Calculation:** Based on completed story points
- **Deviation Graph:** Visualizes difference between ideal and actual
- **Analytics Report:** Comprehensive report with summary and graphs

**Methods:**
- `calculateIdealBurn()` - Calculates ideal burn data points
- `calculateActualBurn()` - Calculates actual burn data points
- `calculateDeviation()` - Calculates deviation between ideal and actual
- `generateDeviationGraph()` - Generates ASCII deviation graph
- `generateAnalyticsReport()` - Generates comprehensive report

---

## Usage Examples

### Generating Burndown Chart

```java
// Initialize components
SprintPlan sprintPlan = // ... get sprint plan
SprintVelocityTracker velocityTracker = new SprintVelocityTracker();

// Generate burndown chart
BurndownChartGenerator generator = new BurndownChartGenerator();
String chart = generator.generateBurndownChart(sprintPlan, velocityTracker);

// Print chart
System.out.println(chart);
```

### Calculating Story Point Analytics

```java
// Initialize analytics
StoryPointAnalytics analytics = new StoryPointAnalytics();

// Calculate ideal burn
List<StoryPointAnalytics.BurnDataPoint> idealBurn = 
    analytics.calculateIdealBurn(sprintPlan);

// Calculate actual burn (from completed work)
int completedStoryPoints = 15; // From sprint tracking
List<StoryPointAnalytics.BurnDataPoint> actualBurn = 
    analytics.calculateActualBurn(sprintPlan, completedStoryPoints);

// Calculate deviation
List<StoryPointAnalytics.DeviationDataPoint> deviations = 
    analytics.calculateDeviation(idealBurn, actualBurn);

// Generate deviation graph
String deviationGraph = analytics.generateDeviationGraph(deviations, sprintPlan.getSprintId());
System.out.println(deviationGraph);

// Generate comprehensive report
String report = analytics.generateAnalyticsReport(sprintPlan, completedStoryPoints);
System.out.println(report);
```

### Integration with Sprint Planning

```java
// After sprint planning
SprintPlan sprintPlan = planner.createSprintPlan(1, 2, "Sprint goal", "session-123");

// Track progress (daily)
int completedStoryPoints = trackCompletedStoryPoints(sprintPlan);

// Generate analytics
StoryPointAnalytics analytics = new StoryPointAnalytics();
String report = analytics.generateAnalyticsReport(sprintPlan, completedStoryPoints);

// Generate burndown chart
BurndownChartGenerator generator = new BurndownChartGenerator();
String chart = generator.generateBurndownChart(sprintPlan, velocityTracker);

// Send to team
sendToSlack("#sprint-analytics", chart + "\n\n" + report);
```

---

## Burndown Chart Format

### ASCII Chart Example

```
================================================================================
BURNDOWN CHART
================================================================================

   50 |                                                                   
   45 |                                                                   
   40 |                                                                   
   35 |                                                                   
   30 |                                                                   
   25 |                                                                   
   20 |                                                                   
   15 |                                                                   
   10 |                                                                   
    5 |                                                                   
    0 +--------------------------------------------------------------------
       0  1  2  3  4  5  6  7  8  9 10

Legend:
  - Ideal burn (linear)
  * Actual burn

Summary:
  Total Story Points: 50
  Ideal Remaining: 0.0
  Actual Remaining: 5.0
  Deviation: 5.0
================================================================================
```

### Chart Elements

- **Y-axis:** Story points remaining
- **X-axis:** Days in sprint
- **Ideal Line (-):** Linear burn from total to zero
- **Actual Line (*):** Actual burn based on completed work
- **Intersection (+):** Where lines cross

---

## Deviation Graph Format

### ASCII Deviation Graph Example

```
================================================================================
STORY POINT DEVIATION GRAPH
================================================================================

   5.0 |                                                                   
   2.5 |                                                                   
   0.0 |--------------------------------------------------------------------
  -2.5 |                                                                   
  -5.0 +--------------------------------------------------------------------
       0  1  2  3  4  5  6  7  8  9 10

Legend:
  - Zero line (on track)
  * Deviation from ideal
  Positive = ahead of schedule
  Negative = behind schedule

Summary:
  Current Deviation: -2.5 story points
  Average Deviation: -1.2 story points
  Status: BEHIND
================================================================================
```

### Deviation Interpretation

- **Positive Deviation:** Ahead of schedule (burning faster than ideal)
- **Negative Deviation:** Behind schedule (burning slower than ideal)
- **Zero Deviation:** On track (burning at ideal rate)

---

## Analytics Report Format

### Report Structure

```
================================================================================
STORY POINT ANALYTICS REPORT
================================================================================

SUMMARY:
--------------------------------------------------------------------------------
Sprint: SPRINT-1
Total Story Points: 50
Completed Story Points: 45
Remaining Story Points: 5
Ideal Remaining: 0.0
Actual Remaining: 5.0
Current Deviation: 5.0

[Deviation Graph]

================================================================================
```

---

## Data Structures

### BurndownDataPoint
```java
public class BurndownDataPoint {
    private LocalDate date;
    private int dayNumber;
    private double idealRemaining;
    private double actualRemaining;
}
```

### BurnDataPoint
```java
public class BurnDataPoint {
    private LocalDate date;
    private int dayNumber;
    private double burned;
    private double remaining;
    private boolean isIdeal;
}
```

### DeviationDataPoint
```java
public class DeviationDataPoint {
    private LocalDate date;
    private int dayNumber;
    private double deviation;
}
```

---

## File Outputs

All charts and reports are saved to:
```
ai-company/outputs/analytics/
```

**Files Generated:**
- `burndown_{sprintId}.txt` - Burndown chart
- `deviation_{sprintId}.txt` - Deviation graph
- `analytics_{sprintId}.txt` - Analytics report

---

## Best Practices

### Burndown Chart Usage
1. **Daily Updates:** Update burndown chart daily with actual progress
2. **Track Completion:** Accurately track completed story points
3. **Compare Trends:** Compare actual vs ideal to identify trends
4. **Early Warning:** Use deviation to identify issues early

### Analytics Usage
1. **Regular Analysis:** Generate analytics reports weekly
2. **Trend Tracking:** Track deviation trends over multiple sprints
3. **Velocity Adjustment:** Use analytics to adjust velocity predictions
4. **Team Communication:** Share analytics with team for visibility

### Integration
1. **Sprint Planning:** Generate charts after sprint planning
2. **Daily Standups:** Include burndown charts in daily standups
3. **Sprint Reviews:** Show analytics in sprint reviews
4. **Retrospectives:** Use analytics for sprint retrospectives

---

## Calculation Methods

### Ideal Burn Calculation
```
Ideal Burn Per Day = Total Story Points / Sprint Days
Ideal Remaining = Total Story Points - (Ideal Burn Per Day × Day Number)
```

### Actual Burn Calculation
```
Actual Burn Per Day = Completed Story Points / Sprint Days
Actual Remaining = Total Story Points - (Actual Burn Per Day × Day Number)
```

### Deviation Calculation
```
Deviation = Actual Remaining - Ideal Remaining
```

---

## Future Enhancements

Potential future enhancements:

1. **PNG Chart Generation:** Generate PNG images using charting library
2. **Historical Trends:** Track burndown trends across multiple sprints
3. **Predictive Analytics:** Predict sprint completion based on current burn rate
4. **Velocity Correlation:** Correlate burndown with velocity predictions
5. **Interactive Charts:** Generate interactive HTML charts
6. **Export Formats:** Export to CSV, JSON, or other formats
7. **Real-time Updates:** Real-time burndown updates as work progresses
8. **Agent-specific Burndown:** Burndown charts per agent

---

## Integration Points

The Analytics System integrates with:

1. **SprintPlan** - For sprint data and committed stories
2. **SprintVelocityTracker** - For velocity data
3. **BacklogManager** - For backlog state
4. **StandupAgent** - For daily progress updates
5. **ScrumMasterAgent** - For sprint coordination

---

## Zero-Impact Compliance

All analytics operations:
- ✅ Generate charts and reports only
- ✅ Read sprint and velocity data
- ✅ Calculate analytics
- ✅ Never modify code or systems
- ✅ Provide visibility into sprint progress
- ✅ Support decision-making
- ✅ Full audit trail with file outputs

The Analytics System is a reporting and visibility tool that supports Agile development without making actual changes to code or systems.

---

## Troubleshooting

### Common Issues

1. **Empty Charts:** Ensure sprint plan has dates and story points
2. **Incorrect Scaling:** Check that story points are reasonable
3. **Missing Data:** Verify completed story points are tracked
4. **File Write Errors:** Check file permissions for output directory

### Error Messages

- `"ERROR: Sprint dates not set"` - Set start and end dates in sprint plan
- `"ERROR: No story points in sprint"` - Add committed stories with story points
- `"ERROR: No deviation data available"` - Calculate ideal and actual burn first



