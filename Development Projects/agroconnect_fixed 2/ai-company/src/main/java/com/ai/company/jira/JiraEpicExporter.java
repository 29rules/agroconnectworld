package com.ai.company.jira;

import com.ai.company.backlog.BacklogItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Jira Epic Exporter
 * 
 * Converts epics to Jira epics format for import.
 * 
 * Epic CSV Format:
 * - Summary: Epic name
 * - Description: Epic description
 * - Issue Type: Epic
 * - Priority: Epic priority
 * - Story Points: Total story points (sum of related stories)
 * - Assignee: Epic owner
 * 
 * SAFETY:
 * - Read-only export
 * - Never modifies code or systems
 * - Only generates CSV files
 */
public class JiraEpicExporter {
    
    private static final Logger log = LoggerFactory.getLogger(JiraEpicExporter.class);
    
    private static final String OUTPUT_DIR = "ai-company/outputs/jira";
    private static final String CSV_HEADER = "Summary,Description,Issue Type,Priority,Story Points,Assignee";
    
    /**
     * Represents an epic with related stories.
     */
    public static class Epic {
        private String epicId;
        private String epicName;
        private String description;
        private BacklogItem.Priority priority;
        private String owner;
        private List<BacklogItem> relatedStories;
        
        public Epic(String epicId, String epicName, String description, 
                   BacklogItem.Priority priority, String owner, List<BacklogItem> relatedStories) {
            this.epicId = epicId;
            this.epicName = epicName;
            this.description = description;
            this.priority = priority;
            this.owner = owner;
            this.relatedStories = relatedStories;
        }
        
        // Getters
        public String getEpicId() { return epicId; }
        public String getEpicName() { return epicName; }
        public String getDescription() { return description; }
        public BacklogItem.Priority getPriority() { return priority; }
        public String getOwner() { return owner; }
        public List<BacklogItem> getRelatedStories() { return relatedStories; }
        
        /**
         * Calculates total story points for epic.
         */
        public int getTotalStoryPoints() {
            return relatedStories.stream()
                .filter(item -> item.getStoryPoints() != null)
                .mapToInt(BacklogItem::getStoryPoints)
                .sum();
        }
    }
    
    /**
     * Exports epics to Jira CSV format.
     * 
     * @param epics List of epics to export
     * @param filename Output filename (without extension)
     * @return Path to exported CSV file
     */
    public Path exportEpicsToCsv(List<Epic> epics, String filename) {
        log.info("Exporting {} epics to Jira CSV format", epics.size());
        
        try {
            Path outputDir = Paths.get(OUTPUT_DIR);
            if (!Files.exists(outputDir)) {
                Files.createDirectories(outputDir);
            }
            
            String csvFilename = filename.endsWith(".csv") ? filename : filename + "_epics.csv";
            Path csvFile = outputDir.resolve(csvFilename);
            
            try (FileWriter writer = new FileWriter(csvFile.toFile())) {
                writer.write(CSV_HEADER);
                writer.write("\n");
                
                for (Epic epic : epics) {
                    String row = buildEpicCsvRow(epic);
                    writer.write(row);
                    writer.write("\n");
                }
            }
            
            log.info("Jira epic CSV export completed: {}", csvFile);
            return csvFile;
            
        } catch (IOException e) {
            log.error("Error exporting epics to Jira CSV", e);
            throw new RuntimeException("Failed to export epics to Jira CSV", e);
        }
    }
    
    /**
     * Groups backlog items by epic tag and exports as epics.
     * 
     * @param backlogItems List of backlog items
     * @param filename Output filename
     * @return Path to exported CSV file
     */
    public Path exportEpicsFromBacklogItems(List<BacklogItem> backlogItems, String filename) {
        log.info("Grouping backlog items by epic and exporting to Jira CSV");
        
        // Group items by epic tag
        Map<String, List<BacklogItem>> epicGroups = backlogItems.stream()
            .filter(item -> item.getTags() != null && 
                   item.getTags().stream().anyMatch(tag -> tag.toLowerCase().contains("epic")))
            .collect(Collectors.groupingBy(item -> {
                // Find epic tag
                return item.getTags().stream()
                    .filter(tag -> tag.toLowerCase().contains("epic"))
                    .findFirst()
                    .orElse("epic-unknown");
            }));
        
        // Convert to Epic objects
        List<Epic> epics = epicGroups.entrySet().stream()
            .map(entry -> {
                String epicTag = entry.getKey();
                List<BacklogItem> items = entry.getValue();
                
                // Use first item's properties for epic
                BacklogItem firstItem = items.get(0);
                
                // Extract epic name from tag or use first item title
                String epicName = epicTag.replace("epic-", "").replace("Epic-", "");
                if (epicName.isEmpty() || epicName.equals("unknown")) {
                    epicName = firstItem.getTitle() + " (Epic)";
                }
                
                // Build epic description
                StringBuilder description = new StringBuilder();
                description.append("Epic containing ").append(items.size()).append(" stories:\n\n");
                for (int i = 0; i < items.size(); i++) {
                    BacklogItem item = items.get(i);
                    description.append(String.format("%d. %s", i + 1, item.getTitle()));
                    if (item.getStoryPoints() != null) {
                        description.append(String.format(" (%d SP)", item.getStoryPoints()));
                    }
                    description.append("\n");
                }
                
                // Determine priority (use highest priority from items)
                BacklogItem.Priority epicPriority = items.stream()
                    .map(BacklogItem::getPriority)
                    .filter(p -> p != null)
                    .max((p1, p2) -> p1.compareTo(p2))
                    .orElse(BacklogItem.Priority.MEDIUM);
                
                // Determine owner (use most common owner)
                String owner = items.stream()
                    .map(BacklogItem::getAgentOwner)
                    .filter(o -> o != null && !o.isEmpty())
                    .collect(Collectors.groupingBy(o -> o, Collectors.counting()))
                    .entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(null);
                
                return new Epic(epicTag, epicName, description.toString(), 
                               epicPriority, owner, items);
            })
            .collect(Collectors.toList());
        
        return exportEpicsToCsv(epics, filename);
    }
    
    /**
     * Builds a CSV row from an epic.
     */
    private String buildEpicCsvRow(Epic epic) {
        StringBuilder row = new StringBuilder();
        
        // Summary (escape commas and quotes)
        row.append(escapeCsvField(epic.getEpicName()));
        row.append(",");
        
        // Description (escape commas and quotes)
        String description = buildEpicDescription(epic);
        row.append(escapeCsvField(description));
        row.append(",");
        
        // Issue Type (always Epic)
        row.append("Epic");
        row.append(",");
        
        // Priority (map from BacklogItem.Priority)
        String priority = mapToJiraPriority(epic.getPriority());
        row.append(priority);
        row.append(",");
        
        // Story Points (total from related stories)
        int totalStoryPoints = epic.getTotalStoryPoints();
        row.append(totalStoryPoints > 0 ? String.valueOf(totalStoryPoints) : "");
        row.append(",");
        
        // Assignee (map agent name to Jira user or leave empty)
        String assignee = mapToJiraAssignee(epic.getOwner());
        row.append(assignee);
        
        return row.toString();
    }
    
    /**
     * Builds epic description.
     */
    private String buildEpicDescription(Epic epic) {
        StringBuilder description = new StringBuilder();
        
        if (epic.getDescription() != null && !epic.getDescription().isEmpty()) {
            description.append(epic.getDescription());
        }
        
        // Add story count
        if (epic.getRelatedStories() != null && !epic.getRelatedStories().isEmpty()) {
            if (description.length() > 0) {
                description.append("\n\n");
            }
            description.append(String.format("Total Stories: %d\n", epic.getRelatedStories().size()));
            description.append(String.format("Total Story Points: %d", epic.getTotalStoryPoints()));
        }
        
        return description.toString();
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
     */
    private String mapToJiraAssignee(String owner) {
        if (owner == null || owner.isEmpty()) {
            return "";
        }
        
        // In real implementation, would map to actual Jira usernames
        return owner;
    }
    
    /**
     * Escapes CSV field (handles commas, quotes, newlines).
     */
    private String escapeCsvField(String field) {
        if (field == null) {
            return "";
        }
        
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            String escaped = field.replace("\"", "\"\"");
            return "\"" + escaped + "\"";
        }
        
        return field;
    }
}



