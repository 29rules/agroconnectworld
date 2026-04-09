# Sprint Retrospective System Overview

## Purpose

The Sprint Retrospective System provides AI-powered retrospective report generation based on sprint results, burndown data, blockers, and velocity reports.

## Components

### 1. RetroAgent
**Purpose:** AI agent that generates sprint retrospective reports.

**Inputs:**
- `sprintResults`: Sprint results summary (completed stories, metrics)
- `burndownData`: Burndown chart data showing progress
- `blockers`: List of blockers encountered during sprint
- `velocityReport`: Velocity tracking and predictions

**Outputs (JSON):**
```json
{
  "what_went_well": [
    {
      "item": "What went well description",
      "category": "process|communication|tools|team|delivery",
      "impact": "high|medium|low",
      "evidence": "Supporting evidence or metrics"
    }
  ],
  "what_didnt_go_well": [
    {
      "item": "What didn't go well description",
      "category": "process|communication|tools|team|delivery|technical",
      "severity": "high|medium|low",
      "root_cause": "Root cause analysis",
      "impact": "Impact on sprint goal"
    }
  ],
  "improvements": [
    {
      "improvement": "Actionable improvement suggestion",
      "category": "process|communication|tools|team|delivery|technical",
      "priority": "high|medium|low",
      "owner": "Who should implement",
      "timeline": "When to implement",
      "success_criteria": "How to measure success"
    }
  ],
  "process_suggestions": [
    {
      "suggestion": "Process improvement suggestion",
      "rationale": "Why this would help",
      "implementation": "How to implement",
      "expected_benefit": "Expected benefit"
    }
  ],
  "morale_assessment": {
    "overall_morale": "high|medium|low",
    "team_satisfaction": "high|medium|low",
    "stress_level": "high|medium|low",
    "engagement": "high|medium|low",
    "factors": ["Factor affecting morale"],
    "recommendations": ["Recommendation to improve morale"]
  }
}
```

**Features:**
- Uses LangChain4j `@AiService`
- Structured JSON output
- Comprehensive retrospective analysis
- Team morale assessment
- Actionable improvements
- Zero-impact mode (reporting only)

---

### 2. RetroReportBuilder
**Purpose:** Converts JSON retrospective report into formatted summaries.

**Formats:**
1. **Markdown:** Structured markdown with sections and formatting
2. **Plain Text:** Simple text format for logs/emails

**Markdown Features:**
- Clear section headers
- Categorized items
- Action items with owners
- Morale assessment summary

**Plain Text Features:**
- Simple text format
- Clear section separators
- Easy to read in logs/emails

---

## Usage Examples

### Basic Retrospective Generation

```java
// Initialize components
ChatLanguageModel chatModel = // ... initialize
RetroAgent retroAgent = new RetroAgent(chatModel);

// Prepare inputs
String sprintResults = """
    Sprint Results:
    - Completed Stories: 8/10
    - Story Points Completed: 40/50
    - Sprint Goal: Achieved 80%
    """;

String burndownData = """
    Burndown Data:
    - Started with 50 story points
    - Completed 40 story points
    - Deviation: -10 story points (behind schedule)
    """;

String blockers = """
    Blockers:
    - Database migration issue (Day 3-5)
    - API integration delay (Day 7-9)
    """;

String velocityReport = """
    Velocity Report:
    - Predicted: 25 story points
    - Actual: 20 story points
    - Velocity trend: Decreasing
    """;

// Generate retrospective
String sessionId = "retro-" + System.currentTimeMillis();
String jsonReport = retroAgent.generateRetrospective(
    sessionId,
    sprintResults,
    burndownData,
    blockers,
    velocityReport
);
```

### Building Formatted Reports

```java
// Build markdown report
RetroReportBuilder builder = new RetroReportBuilder();
String markdownReport = builder.buildMarkdownReport(jsonReport);
System.out.println(markdownReport);

// Build plain text report
String plainTextReport = builder.buildPlainTextReport(jsonReport);
System.out.println(plainTextReport);
```

### Integration with Sprint Planning

```java
// After sprint completion
SprintPlan sprintPlan = // ... get completed sprint plan
int completedStoryPoints = 40;

// Get burndown data
BurndownChartGenerator generator = new BurndownChartGenerator();
String burndownChart = generator.generateBurndownChart(sprintPlan, velocityTracker);

// Get blockers from sprint
String blockers = collectBlockersFromSprint(sprintPlan);

// Get velocity report
String velocityReport = generateVelocityReport(velocityTracker);

// Generate retrospective
RetroAgent retroAgent = new RetroAgent(chatModel);
String jsonReport = retroAgent.generateRetrospective(
    "sprint-" + sprintPlan.getSprintId(),
    formatSprintResults(sprintPlan, completedStoryPoints),
    burndownChart,
    blockers,
    velocityReport
);

// Format and share
RetroReportBuilder builder = new RetroReportBuilder();
String formattedReport = builder.buildMarkdownReport(jsonReport);
sendToSlack("#retrospectives", formattedReport);
```

---

## Output Formats

### Markdown Report Example

```markdown
# Sprint Retrospective Report

---

## ✅ What Went Well

### Completed 8 out of 10 stories
- **Category:** Delivery
- **Impact:** High
- **Evidence:** 80% completion rate, all high-priority stories completed

### Effective communication during blockers
- **Category:** Communication
- **Impact:** Medium
- **Evidence:** Blockers resolved within 2 days

## ❌ What Didn't Go Well

### Database migration blocker
- **Category:** Technical
- **Severity:** High
- **Root Cause:** Insufficient testing in staging environment
- **Impact:** Delayed 3 stories by 2 days

## 🚀 Improvements

### Improve staging environment testing
- **Category:** Process
- **Priority:** High
- **Owner:** DevOpsAgent
- **Timeline:** Next sprint
- **Success Criteria:** All migrations tested in staging before production

## 💡 Process Suggestions

### Implement daily blocker review
- **Rationale:** Early blocker identification prevents delays
- **Implementation:** Add blocker review to daily standup
- **Expected Benefit:** Reduce blocker resolution time by 50%

## 😊 Team Morale Assessment

### Overall Assessment

- **Overall Morale:** Medium
- **Team Satisfaction:** Medium
- **Stress Level:** Medium
- **Engagement:** High

### Factors Affecting Morale

- Blockers caused some frustration
- Good progress on high-priority items
- Team collaboration was strong

### Recommendations

- Celebrate completed stories
- Address blocker prevention process
- Maintain team communication

---

*Generated by RetroAgent*
```

### Plain Text Report Example

```
================================================================================
SPRINT RETROSPECTIVE REPORT
================================================================================

WHAT WENT WELL:
--------------------------------------------------------------------------------
1. Completed 8 out of 10 stories
   Category: Delivery | Impact: High
   Evidence: 80% completion rate, all high-priority stories completed

2. Effective communication during blockers
   Category: Communication | Impact: Medium
   Evidence: Blockers resolved within 2 days

WHAT DIDN'T GO WELL:
--------------------------------------------------------------------------------
1. Database migration blocker
   Category: Technical | Severity: High
   Root Cause: Insufficient testing in staging environment
   Impact: Delayed 3 stories by 2 days

IMPROVEMENTS:
--------------------------------------------------------------------------------
1. Improve staging environment testing
   Category: Process | Priority: High | Owner: DevOpsAgent | Timeline: Next sprint
   Success Criteria: All migrations tested in staging before production

TEAM MORALE ASSESSMENT:
--------------------------------------------------------------------------------
Overall Morale: Medium
Team Satisfaction: Medium
Stress Level: Medium
Engagement: High

Factors Affecting Morale:
  • Blockers caused some frustration
  • Good progress on high-priority items
  • Team collaboration was strong

Recommendations:
  • Celebrate completed stories
  • Address blocker prevention process
  • Maintain team communication

================================================================================
```

---

## JSON Schema

### what_went_well
```json
{
  "item": "string - What went well description",
  "category": "string - process|communication|tools|team|delivery",
  "impact": "string - high|medium|low",
  "evidence": "string - Supporting evidence or metrics"
}
```

### what_didnt_go_well
```json
{
  "item": "string - What didn't go well description",
  "category": "string - process|communication|tools|team|delivery|technical",
  "severity": "string - high|medium|low",
  "root_cause": "string - Root cause analysis",
  "impact": "string - Impact on sprint goal"
}
```

### improvements
```json
{
  "improvement": "string - Actionable improvement suggestion",
  "category": "string - process|communication|tools|team|delivery|technical",
  "priority": "string - high|medium|low",
  "owner": "string - Who should implement",
  "timeline": "string - When to implement",
  "success_criteria": "string - How to measure success"
}
```

### process_suggestions
```json
{
  "suggestion": "string - Process improvement suggestion",
  "rationale": "string - Why this would help",
  "implementation": "string - How to implement",
  "expected_benefit": "string - Expected benefit"
}
```

### morale_assessment
```json
{
  "overall_morale": "string - high|medium|low",
  "team_satisfaction": "string - high|medium|low",
  "stress_level": "string - high|medium|low",
  "engagement": "string - high|medium|low",
  "factors": ["string - Factor affecting morale"],
  "recommendations": ["string - Recommendation to improve morale"]
}
```

---

## Retrospective Principles

1. **Focus on Process, Not People:** Critique processes and systems, not individuals
2. **Be Constructive:** Focus on solutions, not just problems
3. **Actionable Improvements:** All improvements should be actionable and specific
4. **Celebrate Successes:** Acknowledge what went well
5. **Learn from Challenges:** Use challenges as learning opportunities
6. **Safe Space:** Create environment for honest feedback

---

## Best Practices

### Input Preparation
1. **Sprint Results:** Provide comprehensive sprint results with metrics
2. **Burndown Data:** Include burndown chart data showing progress
3. **Blockers:** List all blockers with severity and resolution time
4. **Velocity Report:** Include velocity tracking and trends

### Report Generation
1. **Use Session IDs:** Use unique session IDs for memory continuity
2. **Regular Retrospectives:** Generate retrospectives after each sprint
3. **Consistent Format:** Use same input format for consistency

### Report Distribution
1. **Team Sharing:** Share reports with entire team
2. **Action Items:** Track improvements and process suggestions
3. **Follow-up:** Review improvements in next sprint planning

---

## Integration Points

The Retrospective System integrates with:

1. **SprintPlan** - For sprint data and results
2. **BurndownChartGenerator** - For burndown data
3. **SprintVelocityTracker** - For velocity reports
4. **BacklogManager** - For sprint completion data
5. **StandupAgent** - For daily progress data

---

## Zero-Impact Compliance

All retrospective operations:
- ✅ Generate reports only
- ✅ Analyze sprint data
- ✅ Provide insights and recommendations
- ✅ Never modify code or systems
- ✅ Support continuous improvement
- ✅ Full audit trail with timestamps

The Retrospective System is a reporting and facilitation tool that supports Agile development without making actual changes to code or systems.

---

## Automation

### Sprint Retrospective Automation

```java
// Schedule retrospective after sprint end
@Scheduled(cron = "0 0 17 * * FRI") // Every Friday at 5 PM
public void generateSprintRetrospective() {
    // Get completed sprint
    SprintPlan sprintPlan = getCompletedSprint();
    
    // Collect data
    String sprintResults = formatSprintResults(sprintPlan);
    String burndownData = getBurndownData(sprintPlan);
    String blockers = collectBlockers(sprintPlan);
    String velocityReport = generateVelocityReport();
    
    // Generate retrospective
    RetroAgent retroAgent = new RetroAgent(chatModel);
    String jsonReport = retroAgent.generateRetrospective(
        "sprint-" + sprintPlan.getSprintId(),
        sprintResults,
        burndownData,
        blockers,
        velocityReport
    );
    
    // Format and send
    RetroReportBuilder builder = new RetroReportBuilder();
    String formattedReport = builder.buildMarkdownReport(jsonReport);
    
    // Send to team
    sendToSlack("#retrospectives", formattedReport);
    
    // Log
    log.info("Sprint retrospective generated and sent");
}
```

---

## Error Handling

The RetroReportBuilder includes error handling:

- **JSON Parsing Errors:** Returns error message in formatted report
- **Missing Fields:** Uses defaults (e.g., "Unknown" for missing items)
- **Empty Data:** Shows appropriate "No data" messages
- **Invalid Format:** Returns error report with details

---

## Future Enhancements

Potential future enhancements:

1. **Historical Trends:** Track retrospective trends over multiple sprints
2. **Improvement Tracking:** Track implementation of improvements
3. **Team Sentiment Analysis:** Analyze team sentiment from retrospectives
4. **Predictive Analytics:** Predict potential issues based on patterns
5. **Integration with Jira/GitHub:** Pull data from issue trackers
6. **Interactive Retrospectives:** Generate interactive HTML reports
7. **Action Item Tracking:** Track action items from previous retrospectives



