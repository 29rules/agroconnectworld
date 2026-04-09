package com.ai.company.jira;

import com.ai.company.backlog.BacklogItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Jira Task Exporter
 * 
 * Converts BacklogItems to Jira CSV format for import.
 * 
 * CSV Column Format:
 * - Summary: Item title
 * - Description: Item description
 * - Issue Type: Task, Story, Bug, etc.
 * - Priority: High, Medium, Low
 * - Story Points: Story point estimate
 * - Assignee: Agent owner
 * 
 * SAFETY:
 * - Read-only export
 * - Never modifies code or systems
 * - Only generates CSV files
 */
public class JiraTaskExporter {
    
    private static final Logger log = LoggerFactory.getLogger(JiraTaskExporter.class);
    
    private static final String OUTPUT_DIR = "ai-company/outputs/jira";
    private static final String CSV_HEADER = "Summary,Description,Issue Type,Priority,Story Points,Assignee";
    
    /**
     * Exports backlog items to Jira CSV format.
     * 
     * @param backlogItems List of backlog items to export
     * @param filename Output filename (without extension)
     * @return Path to exported CSV file
     */
    public Path exportToCsv(List<BacklogItem> backlogItems, String filename) {
        log.info("Exporting {} backlog items to Jira CSV format", backlogItems.size());
        
        try {
            // Create output directory
            Path outputDir = Paths.get(OUTPUT_DIR);
            if (!Files.exists(outputDir)) {
                Files.createDirectories(outputDir);
            }
            
            // Create CSV file
            String csvFilename = filename.endsWith(".csv") ? filename : filename + ".csv";
            Path csvFile = outputDir.resolve(csvFilename);
            
            try (FileWriter writer = new FileWriter(csvFile.toFile())) {
                // Write header
                writer.write(CSV_HEADER);
                writer.write("\n");
                
                // Write items
                for (BacklogItem item : backlogItems) {
                    String row = buildCsvRow(item);
                    writer.write(row);
                    writer.write("\n");
                }
            }
            
            log.info("Jira CSV export completed: {}", csvFile);
            return csvFile;
            
        } catch (IOException e) {
            log.error("Error exporting to Jira CSV", e);
            throw new RuntimeException("Failed to export to Jira CSV", e);
        }
    }
    
    /**
     * Builds a CSV row from a backlog item.
     */
    private String buildCsvRow(BacklogItem item) {
        StringBuilder row = new StringBuilder();
        
        // Summary (escape commas and quotes)
        row.append(escapeCsvField(item.getTitle() != null ? item.getTitle() : ""));
        row.append(",");
        
        // Description (escape commas and quotes, include acceptance criteria)
        String description = buildDescription(item);
        row.append(escapeCsvField(description));
        row.append(",");
        
        // Issue Type (map from status/type)
        String issueType = mapToIssueType(item);
        row.append(issueType);
        row.append(",");
        
        // Priority (map from BacklogItem.Priority)
        String priority = mapToJiraPriority(item.getPriority());
        row.append(priority);
        row.append(",");
        
        // Story Points
        String storyPoints = item.getStoryPoints() != null ? 
            String.valueOf(item.getStoryPoints()) : "";
        row.append(storyPoints);
        row.append(",");
        
        // Assignee (map agent name to Jira user or leave empty)
        String assignee = mapToJiraAssignee(item.getAgentOwner());
        row.append(assignee);
        
        return row.toString();
    }
    
    /**
     * Builds description from backlog item.
     */
    private String buildDescription(BacklogItem item) {
        StringBuilder description = new StringBuilder();
        
        if (item.getDescription() != null && !item.getDescription().isEmpty()) {
            description.append(item.getDescription());
        }
        
        // Add acceptance criteria
        if (item.getAcceptanceCriteria() != null && !item.getAcceptanceCriteria().isEmpty()) {
            if (description.length() > 0) {
                description.append("\n\nAcceptance Criteria:\n");
            } else {
                description.append("Acceptance Criteria:\n");
            }
            for (int i = 0; i < item.getAcceptanceCriteria().size(); i++) {
                description.append(String.format("%d. %s\n", 
                    i + 1, item.getAcceptanceCriteria().get(i)));
            }
        }
        
        // Add tags
        if (item.getTags() != null && !item.getTags().isEmpty()) {
            if (description.length() > 0) {
                description.append("\nTags: ");
            } else {
                description.append("Tags: ");
            }
            description.append(String.join(", ", item.getTags()));
        }
        
        // Add dependencies
        if (item.getDependencies() != null && !item.getDependencies().isEmpty()) {
            if (description.length() > 0) {
                description.append("\n\nDependencies: ");
            } else {
                description.append("Dependencies: ");
            }
            description.append(String.join(", ", item.getDependencies()));
        }
        
        // Add metadata
        if (item.getCreatedAt() != null) {
            if (description.length() > 0) {
                description.append("\n\nCreated: ");
            } else {
                description.append("Created: ");
            }
            description.append(item.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
        
        return description.toString();
    }
    
    /**
     * Maps backlog item to Jira issue type.
     */
    private String mapToIssueType(BacklogItem item) {
        // Default to Story for user stories
        // Can be customized based on tags or other criteria
        if (item.getTags() != null) {
            for (String tag : item.getTags()) {
                String lowerTag = tag.toLowerCase();
                if (lowerTag.contains("bug") || lowerTag.contains("defect")) {
                    return "Bug";
                } else if (lowerTag.contains("task")) {
                    return "Task";
                } else if (lowerTag.contains("epic")) {
                    return "Epic";
                }
            }
        }
        
        // Default based on story points
        if (item.getStoryPoints() != null && item.getStoryPoints() >= 13) {
            return "Epic"; // Large items might be epics
        }
        
        return "Story"; // Default to Story
    }
    
    /**
     * Maps BacklogItem.Priority to Jira priority.
     */
    private String mapToJiraPriority(BacklogItem.Priority priority) {
        if (priority == null) {
            return "Medium";
        }
        
        switch (priority) {
            case HIGH:
                return "High";
            case MEDIUM:
                return "Medium";
            case LOW:
                return "Low";
            default:
                return "Medium";
        }
    }
    
    /**
     * Maps agent owner to Jira assignee.
     * 
     * In real implementation, would map to actual Jira usernames.
     * For now, returns agent name or empty string.
     */
    private String mapToJiraAssignee(String agentOwner) {
        if (agentOwner == null || agentOwner.isEmpty()) {
            return "";
        }
        
        // In real implementation, would have a mapping:
        // EngineerAgent -> jira-username
        // QAAgent -> jira-username
        // etc.
        
        // For now, return agent name (can be configured later)
        return agentOwner;
    }
    
    /**
     * Escapes CSV field (handles commas, quotes, newlines).
     */
    private String escapeCsvField(String field) {
        if (field == null) {
            return "";
        }
        
        // If field contains comma, quote, or newline, wrap in quotes and escape quotes
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            // Escape quotes by doubling them
            String escaped = field.replace("\"", "\"\"");
            return "\"" + escaped + "\"";
        }
        
        return field;
    }
    
    /**
     * Exports backlog items with custom issue type mapping.
     * 
     * @param backlogItems List of backlog items
     * @param filename Output filename
     * @param issueTypeMapping Custom mapping function for issue types
     * @return Path to exported CSV file
     */
    public Path exportToCsvWithCustomMapping(List<BacklogItem> backlogItems, String filename,
                                             java.util.function.Function<BacklogItem, String> issueTypeMapping) {
        log.info("Exporting {} backlog items with custom issue type mapping", backlogItems.size());
        
        try {
            Path outputDir = Paths.get(OUTPUT_DIR);
            if (!Files.exists(outputDir)) {
                Files.createDirectories(outputDir);
            }
            
            String csvFilename = filename.endsWith(".csv") ? filename : filename + ".csv";
            Path csvFile = outputDir.resolve(csvFilename);
            
            try (FileWriter writer = new FileWriter(csvFile.toFile())) {
                writer.write(CSV_HEADER);
                writer.write("\n");
                
                for (BacklogItem item : backlogItems) {
                    String row = buildCsvRowWithCustomMapping(item, issueTypeMapping);
                    writer.write(row);
                    writer.write("\n");
                }
            }
            
            log.info("Jira CSV export with custom mapping completed: {}", csvFile);
            return csvFile;
            
        } catch (IOException e) {
            log.error("Error exporting to Jira CSV with custom mapping", e);
            throw new RuntimeException("Failed to export to Jira CSV", e);
        }
    }
    
    /**
     * Builds CSV row with custom issue type mapping.
     */
    private String buildCsvRowWithCustomMapping(BacklogItem item,
                                                java.util.function.Function<BacklogItem, String> issueTypeMapping) {
        StringBuilder row = new StringBuilder();
        
        row.append(escapeCsvField(item.getTitle() != null ? item.getTitle() : ""));
        row.append(",");
        
        String description = buildDescription(item);
        row.append(escapeCsvField(description));
        row.append(",");
        
        String issueType = issueTypeMapping.apply(item);
        row.append(issueType);
        row.append(",");
        
        String priority = mapToJiraPriority(item.getPriority());
        row.append(priority);
        row.append(",");
        
        String storyPoints = item.getStoryPoints() != null ? 
            String.valueOf(item.getStoryPoints()) : "";
        row.append(storyPoints);
        row.append(",");
        
        String assignee = mapToJiraAssignee(item.getAgentOwner());
        row.append(assignee);
        
        return row.toString();
    }
}



