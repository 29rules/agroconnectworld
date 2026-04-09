# Jira Export Guide

## Purpose

The Jira Export System provides tools to export backlog items and epics to Jira CSV format for import into Jira.

## Components

### 1. JiraTaskExporter
**Purpose:** Converts BacklogItems to Jira CSV format.

**CSV Column Format:**
- **Summary:** Item title
- **Description:** Item description (includes acceptance criteria, tags, dependencies)
- **Issue Type:** Task, Story, Bug, Epic (mapped from tags or story points)
- **Priority:** High, Medium, Low (mapped from BacklogItem.Priority)
- **Story Points:** Story point estimate
- **Assignee:** Agent owner (can be mapped to Jira username)

**Features:**
- Automatic CSV escaping (handles commas, quotes, newlines)
- Description includes acceptance criteria, tags, dependencies
- Issue type mapping based on tags or story points
- Custom issue type mapping support
- Priority mapping from BacklogItem.Priority

---

### 2. JiraEpicExporter
**Purpose:** Converts epics to Jira epics format.

**Epic CSV Format:**
- **Summary:** Epic name
- **Description:** Epic description (includes related stories)
- **Issue Type:** Epic
- **Priority:** Epic priority (highest from related stories)
- **Story Points:** Total story points (sum of related stories)
- **Assignee:** Epic owner (most common from related stories)

**Features:**
- Epic grouping from backlog items by epic tag
- Automatic story point aggregation
- Priority determination from related stories
- Owner determination from related stories

---

## Usage Examples

### Exporting Backlog Items to Jira

```java
// Get backlog items
BacklogManager backlogManager = new BacklogManager();
List<BacklogItem> backlogItems = backlogManager.getAllItems();

// Export to Jira CSV
JiraTaskExporter exporter = new JiraTaskExporter();
Path csvFile = exporter.exportToCsv(backlogItems, "backlog_export");

System.out.println("Exported to: " + csvFile);
```

### Exporting with Custom Issue Type Mapping

```java
// Custom mapping function
Function<BacklogItem, String> issueTypeMapping = item -> {
    // Custom logic to determine issue type
    if (item.getTags() != null && item.getTags().contains("bug")) {
        return "Bug";
    } else if (item.getStoryPoints() != null && item.getStoryPoints() >= 13) {
        return "Epic";
    } else {
        return "Story";
    }
};

// Export with custom mapping
JiraTaskExporter exporter = new JiraTaskExporter();
Path csvFile = exporter.exportToCsvWithCustomMapping(
    backlogItems, 
    "backlog_export_custom", 
    issueTypeMapping
);
```

### Exporting Epics to Jira

```java
// Create epics
List<JiraEpicExporter.Epic> epics = new ArrayList<>();

Epic epic1 = new Epic(
    "EPIC-001",
    "User Authentication",
    "Epic for user authentication features",
    BacklogItem.Priority.HIGH,
    "EngineerAgent",
    relatedStories
);

Epic epic2 = new Epic(
    "EPIC-002",
    "Product Catalog",
    "Epic for product catalog features",
    BacklogItem.Priority.MEDIUM,
    "FullStackAgent",
    relatedStories2
);

// Export epics
JiraEpicExporter epicExporter = new JiraEpicExporter();
Path epicCsvFile = epicExporter.exportEpicsToCsv(epics, "epics_export");
```

### Exporting Epics from Backlog Items

```java
// Get backlog items with epic tags
BacklogManager backlogManager = new BacklogManager();
List<BacklogItem> backlogItems = backlogManager.getAllItems();

// Tag items with epic names
BacklogItem item1 = backlogManager.getItem("item-1");
item1.addTag("epic-user-auth");

BacklogItem item2 = backlogManager.getItem("item-2");
item2.addTag("epic-user-auth");

// Export epics (automatically groups by epic tag)
JiraEpicExporter epicExporter = new JiraEpicExporter();
Path epicCsvFile = epicExporter.exportEpicsFromBacklogItems(
    backlogItems, 
    "epics_from_backlog"
);
```

---

## CSV Format

### Task/Story CSV Format

```csv
Summary,Description,Issue Type,Priority,Story Points,Assignee
User Registration,"As a user, I want to register so that I can access the platform.

Acceptance Criteria:
1. User can register with email and password
2. Email validation is performed

Tags: frontend, backend
Created: 2024-01-01T10:00:00",Story,High,5,EngineerAgent
Product List,"Display list of products with filters.

Acceptance Criteria:
1. Products are displayed in grid layout
2. Filters work correctly

Tags: frontend
Created: 2024-01-02T10:00:00",Story,Medium,3,FullStackAgent
```

### Epic CSV Format

```csv
Summary,Description,Issue Type,Priority,Story Points,Assignee
User Authentication,"Epic containing 5 stories:

1. User Registration (5 SP)
2. User Login (3 SP)
3. Password Reset (2 SP)
4. Email Verification (2 SP)
5. Profile Management (5 SP)

Total Stories: 5
Total Story Points: 17",Epic,High,17,EngineerAgent
```

---

## Issue Type Mapping

### Default Mapping

The `JiraTaskExporter` uses the following logic to determine issue type:

1. **Check Tags:**
   - If tag contains "bug" or "defect" → **Bug**
   - If tag contains "task" → **Task**
   - If tag contains "epic" → **Epic**

2. **Check Story Points:**
   - If story points >= 13 → **Epic** (large items)

3. **Default:**
   - **Story** (for user stories)

### Custom Mapping

You can provide a custom mapping function:

```java
Function<BacklogItem, String> customMapping = item -> {
    // Your custom logic
    if (item.getTitle().contains("Bug")) {
        return "Bug";
    }
    return "Story";
};
```

---

## Priority Mapping

Priority is mapped from `BacklogItem.Priority`:

- `HIGH` → `High`
- `MEDIUM` → `Medium`
- `LOW` → `Low`

---

## Assignee Mapping

By default, the agent owner name is used as the assignee. In a real implementation, you would map agent names to Jira usernames:

```java
// Example mapping
Map<String, String> agentToJiraUser = Map.of(
    "EngineerAgent", "john.doe",
    "QAAgent", "jane.smith",
    "FullStackAgent", "bob.jones"
);
```

---

## Description Format

The description includes:

1. **Item Description:** Original description from backlog item
2. **Acceptance Criteria:** Numbered list of acceptance criteria
3. **Tags:** Comma-separated list of tags
4. **Dependencies:** List of dependent item IDs
5. **Created Date:** ISO format timestamp

Example:
```
As a user, I want to register so that I can access the platform.

Acceptance Criteria:
1. User can register with email and password
2. Email validation is performed

Tags: frontend, backend
Dependencies: item-5, item-7
Created: 2024-01-01T10:00:00
```

---

## Epic Grouping

Epics are automatically grouped from backlog items by:

1. **Epic Tag:** Items with tags containing "epic" are grouped
2. **Epic Name:** Extracted from tag (e.g., "epic-user-auth" → "User Auth")
3. **Related Stories:** All items with the same epic tag
4. **Priority:** Highest priority from related stories
5. **Owner:** Most common owner from related stories
6. **Story Points:** Sum of all related story points

---

## File Outputs

All CSV files are saved to:
```
ai-company/outputs/jira/
```

**Files Generated:**
- `{filename}.csv` - Task/Story export
- `{filename}_epics.csv` - Epic export

---

## Importing into Jira

### Steps

1. **Export CSV:**
   ```java
   JiraTaskExporter exporter = new JiraTaskExporter();
   Path csvFile = exporter.exportToCsv(backlogItems, "backlog_export");
   ```

2. **Open Jira:**
   - Go to your Jira project
   - Click "..." → "Import issues from CSV"

3. **Upload CSV:**
   - Select the exported CSV file
   - Map columns (should auto-detect)
   - Configure field mappings if needed

4. **Import:**
   - Review preview
   - Click "Import"

### Field Mapping

Ensure Jira fields match CSV columns:

- **Summary** → Jira Summary
- **Description** → Jira Description
- **Issue Type** → Jira Issue Type
- **Priority** → Jira Priority
- **Story Points** → Jira Story Points (if enabled)
- **Assignee** → Jira Assignee

---

## Best Practices

### Before Export

1. **Review Backlog:** Ensure all items are ready for export
2. **Check Tags:** Add epic tags for epic grouping
3. **Verify Assignees:** Ensure agent owners are set
4. **Validate Story Points:** Ensure story points are set

### During Export

1. **Use Descriptive Filenames:** Include date or sprint number
2. **Export Incrementally:** Export by sprint or priority
3. **Test Import:** Test with small batch first

### After Export

1. **Verify CSV:** Check CSV file for formatting issues
2. **Test Import:** Import small batch to Jira first
3. **Map Fields:** Configure Jira field mappings
4. **Review Import:** Verify imported issues in Jira

---

## Troubleshooting

### Common Issues

1. **CSV Format Errors:**
   - Check for special characters in descriptions
   - Ensure proper CSV escaping
   - Verify line endings

2. **Issue Type Not Found:**
   - Ensure issue types exist in Jira
   - Check custom mapping function
   - Verify tag names

3. **Assignee Not Found:**
   - Map agent names to Jira usernames
   - Check Jira user permissions
   - Leave assignee empty if needed

4. **Story Points Not Imported:**
   - Ensure Story Points field is enabled in Jira
   - Check field configuration
   - Verify numeric format

---

## Integration Points

The Jira Export System integrates with:

1. **BacklogManager** - For backlog items
2. **BacklogItem** - For item data
3. **SprintPlan** - For sprint-related exports
4. **ScrumMasterAgent** - For epic definitions

---

## Zero-Impact Compliance

All export operations:
- ✅ Generate CSV files only
- ✅ Read backlog data
- ✅ Never modify code or systems
- ✅ Provide export functionality
- ✅ Support Jira import
- ✅ Full audit trail with file outputs

The Jira Export System is a data export tool that supports Agile development without making actual changes to code or systems.

---

## Future Enhancements

Potential future enhancements:

1. **Jira API Integration:** Direct import via Jira REST API
2. **Bidirectional Sync:** Sync changes back from Jira
3. **Custom Field Mapping:** Configurable field mappings
4. **Bulk Operations:** Batch import/export operations
5. **Validation:** Pre-import validation
6. **Templates:** Export templates for different Jira configurations
7. **History Tracking:** Track export history and changes



