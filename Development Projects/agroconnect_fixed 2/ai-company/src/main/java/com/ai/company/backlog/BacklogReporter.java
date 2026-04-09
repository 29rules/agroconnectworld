package com.ai.company.backlog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Backlog Reporter
 * 
 * Generates comprehensive reports on backlog health and status.
 * 
 * Reports:
 * - Backlog health
 * - Priority breakdown
 * - Workload per agent
 * - Story point totals
 * - Status distribution
 */
public class BacklogReporter {
    
    private static final Logger log = LoggerFactory.getLogger(BacklogReporter.class);
    
    private final BacklogManager backlogManager;
    
    public BacklogReporter(BacklogManager backlogManager) {
        this.backlogManager = backlogManager;
    }
    
    /**
     * Generates a comprehensive backlog health report.
     * 
     * @return Backlog health report
     */
    public String generateBacklogHealthReport() {
        log.info("Generating backlog health report");
        
        List<BacklogItem> allItems = backlogManager.getAllItems();
        
        StringBuilder report = new StringBuilder();
        report.append("=".repeat(80)).append("\n");
        report.append("BACKLOG HEALTH REPORT\n");
        report.append("=".repeat(80)).append("\n\n");
        report.append("Generated: ").append(LocalDateTime.now()).append("\n\n");
        
        // Overall statistics
        report.append("--- Overall Statistics ---\n");
        report.append("Total Items: ").append(allItems.size()).append("\n");
        
        int readyItems = (int) allItems.stream().filter(BacklogItem::isReady).count();
        report.append("Ready Items: ").append(readyItems).append("\n");
        report.append("Ready Percentage: ").append(
            allItems.isEmpty() ? 0 : (readyItems * 100 / allItems.size())).append("%\n\n");
        
        // Items without acceptance criteria
        long itemsWithoutCriteria = allItems.stream()
            .filter(item -> item.getAcceptanceCriteria().isEmpty())
            .count();
        if (itemsWithoutCriteria > 0) {
            report.append("⚠️  Items without acceptance criteria: ").append(itemsWithoutCriteria).append("\n");
        }
        
        // Items without estimates
        long itemsWithoutEstimate = allItems.stream()
            .filter(item -> item.getStoryPoints() == null || item.getStoryPoints() == 0)
            .count();
        if (itemsWithoutEstimate > 0) {
            report.append("⚠️  Items without story point estimates: ").append(itemsWithoutEstimate).append("\n");
        }
        
        // Blocked items
        long blockedItems = allItems.stream()
            .filter(item -> item.getStatus() == BacklogItem.Status.BLOCKED)
            .count();
        if (blockedItems > 0) {
            report.append("⚠️  Blocked items: ").append(blockedItems).append("\n");
        }
        
        // Health score calculation
        double healthScore = calculateHealthScore(allItems);
        report.append("\nBacklog Health Score: ").append(String.format("%.1f", healthScore)).append("/100\n");
        
        if (healthScore >= 80) {
            report.append("Status: EXCELLENT - Backlog is in great shape\n");
        } else if (healthScore >= 60) {
            report.append("Status: GOOD - Minor improvements recommended\n");
        } else if (healthScore >= 40) {
            report.append("Status: FAIR - Significant improvements needed\n");
        } else {
            report.append("Status: POOR - Major backlog refinement required\n");
        }
        
        report.append("\n");
        
        return report.toString();
    }
    
    /**
     * Generates a priority breakdown report.
     * 
     * @return Priority breakdown report
     */
    public String generatePriorityBreakdown() {
        log.info("Generating priority breakdown report");
        
        List<BacklogItem> allItems = backlogManager.getAllItems();
        
        StringBuilder report = new StringBuilder();
        report.append("=".repeat(80)).append("\n");
        report.append("PRIORITY BREAKDOWN REPORT\n");
        report.append("=".repeat(80)).append("\n\n");
        
        Map<BacklogItem.Priority, List<BacklogItem>> byPriority = allItems.stream()
            .collect(Collectors.groupingBy(BacklogItem::getPriority));
        
        for (BacklogItem.Priority priority : BacklogItem.Priority.values()) {
            List<BacklogItem> items = byPriority.getOrDefault(priority, Collections.emptyList());
            int count = items.size();
            int storyPoints = items.stream()
                .filter(item -> item.getStoryPoints() != null)
                .mapToInt(BacklogItem::getStoryPoints)
                .sum();
            
            report.append("Priority: ").append(priority).append("\n");
            report.append("  Items: ").append(count).append("\n");
            report.append("  Story Points: ").append(storyPoints).append("\n");
            report.append("  Percentage: ").append(
                allItems.isEmpty() ? 0 : (count * 100 / allItems.size())).append("%\n\n");
        }
        
        return report.toString();
    }
    
    /**
     * Generates a workload per agent report.
     * 
     * @return Workload per agent report
     */
    public String generateWorkloadPerAgent() {
        log.info("Generating workload per agent report");
        
        List<BacklogItem> allItems = backlogManager.getAllItems();
        
        StringBuilder report = new StringBuilder();
        report.append("=".repeat(80)).append("\n");
        report.append("WORKLOAD PER AGENT REPORT\n");
        report.append("=".repeat(80)).append("\n\n");
        
        // Group by agent
        Map<String, List<BacklogItem>> byAgent = allItems.stream()
            .filter(item -> item.getAgentOwner() != null && !item.getAgentOwner().isEmpty())
            .collect(Collectors.groupingBy(BacklogItem::getAgentOwner));
        
        if (byAgent.isEmpty()) {
            report.append("No items assigned to agents.\n");
            return report.toString();
        }
        
        // Calculate workload for each agent
        Map<String, AgentWorkload> workloads = new HashMap<>();
        for (Map.Entry<String, List<BacklogItem>> entry : byAgent.entrySet()) {
            String agent = entry.getKey();
            List<BacklogItem> items = entry.getValue();
            
            AgentWorkload workload = new AgentWorkload();
            workload.setAgentName(agent);
            workload.setTotalItems(items.size());
            workload.setTotalStoryPoints(items.stream()
                .filter(item -> item.getStoryPoints() != null)
                .mapToInt(BacklogItem::getStoryPoints)
                .sum());
            
            // Count by status
            workload.setTodoCount((int) items.stream()
                .filter(item -> item.getStatus() == BacklogItem.Status.TODO)
                .count());
            workload.setInProgressCount((int) items.stream()
                .filter(item -> item.getStatus() == BacklogItem.Status.IN_PROGRESS)
                .count());
            workload.setDoneCount((int) items.stream()
                .filter(item -> item.getStatus() == BacklogItem.Status.DONE)
                .count());
            workload.setBlockedCount((int) items.stream()
                .filter(item -> item.getStatus() == BacklogItem.Status.BLOCKED)
                .count());
            
            workloads.put(agent, workload);
        }
        
        // Sort by total story points (descending)
        List<AgentWorkload> sortedWorkloads = new ArrayList<>(workloads.values());
        sortedWorkloads.sort((a, b) -> Integer.compare(b.getTotalStoryPoints(), a.getTotalStoryPoints()));
        
        // Generate report
        for (AgentWorkload workload : sortedWorkloads) {
            report.append("Agent: ").append(workload.getAgentName()).append("\n");
            report.append("  Total Items: ").append(workload.getTotalItems()).append("\n");
            report.append("  Total Story Points: ").append(workload.getTotalStoryPoints()).append("\n");
            report.append("  TODO: ").append(workload.getTodoCount()).append("\n");
            report.append("  IN_PROGRESS: ").append(workload.getInProgressCount()).append("\n");
            report.append("  DONE: ").append(workload.getDoneCount()).append("\n");
            report.append("  BLOCKED: ").append(workload.getBlockedCount()).append("\n");
            report.append("\n");
        }
        
        // Unassigned items
        long unassignedCount = allItems.stream()
            .filter(item -> item.getAgentOwner() == null || item.getAgentOwner().isEmpty())
            .count();
        if (unassignedCount > 0) {
            report.append("Unassigned Items: ").append(unassignedCount).append("\n");
        }
        
        return report.toString();
    }
    
    /**
     * Generates a story point totals report.
     * 
     * @return Story point totals report
     */
    public String generateStoryPointTotals() {
        log.info("Generating story point totals report");
        
        List<BacklogItem> allItems = backlogManager.getAllItems();
        
        StringBuilder report = new StringBuilder();
        report.append("=".repeat(80)).append("\n");
        report.append("STORY POINT TOTALS REPORT\n");
        report.append("=".repeat(80)).append("\n\n");
        
        // Total story points
        int totalStoryPoints = allItems.stream()
            .filter(item -> item.getStoryPoints() != null)
            .mapToInt(BacklogItem::getStoryPoints)
            .sum();
        report.append("Total Story Points: ").append(totalStoryPoints).append("\n\n");
        
        // By status
        report.append("--- By Status ---\n");
        Map<BacklogItem.Status, Integer> byStatus = allItems.stream()
            .filter(item -> item.getStoryPoints() != null)
            .collect(Collectors.groupingBy(
                BacklogItem::getStatus,
                Collectors.summingInt(BacklogItem::getStoryPoints)
            ));
        
        for (BacklogItem.Status status : BacklogItem.Status.values()) {
            int points = byStatus.getOrDefault(status, 0);
            if (points > 0) {
                report.append(status).append(": ").append(points).append(" story points\n");
            }
        }
        report.append("\n");
        
        // By priority
        report.append("--- By Priority ---\n");
        Map<BacklogItem.Priority, Integer> byPriority = allItems.stream()
            .filter(item -> item.getStoryPoints() != null)
            .collect(Collectors.groupingBy(
                BacklogItem::getPriority,
                Collectors.summingInt(BacklogItem::getStoryPoints)
            ));
        
        for (BacklogItem.Priority priority : BacklogItem.Priority.values()) {
            int points = byPriority.getOrDefault(priority, 0);
            if (points > 0) {
                report.append(priority).append(": ").append(points).append(" story points\n");
            }
        }
        report.append("\n");
        
        // Items without estimates
        long itemsWithoutEstimate = allItems.stream()
            .filter(item -> item.getStoryPoints() == null || item.getStoryPoints() == 0)
            .count();
        if (itemsWithoutEstimate > 0) {
            report.append("Items without estimates: ").append(itemsWithoutEstimate).append("\n");
        }
        
        return report.toString();
    }
    
    /**
     * Generates a status distribution report.
     * 
     * @return Status distribution report
     */
    public String generateStatusDistribution() {
        log.info("Generating status distribution report");
        
        List<BacklogItem> allItems = backlogManager.getAllItems();
        
        StringBuilder report = new StringBuilder();
        report.append("=".repeat(80)).append("\n");
        report.append("STATUS DISTRIBUTION REPORT\n");
        report.append("=".repeat(80)).append("\n\n");
        
        Map<BacklogItem.Status, Long> distribution = allItems.stream()
            .collect(Collectors.groupingBy(BacklogItem::getStatus, Collectors.counting()));
        
        int total = allItems.size();
        
        for (BacklogItem.Status status : BacklogItem.Status.values()) {
            long count = distribution.getOrDefault(status, 0L);
            double percentage = total > 0 ? (count * 100.0 / total) : 0;
            
            report.append(status).append(": ").append(count)
                  .append(" (").append(String.format("%.1f", percentage)).append("%)\n");
        }
        
        report.append("\nTotal Items: ").append(total).append("\n");
        
        return report.toString();
    }
    
    /**
     * Generates a comprehensive report combining all reports.
     * 
     * @return Comprehensive backlog report
     */
    public String generateComprehensiveReport() {
        log.info("Generating comprehensive backlog report");
        
        StringBuilder report = new StringBuilder();
        report.append(generateBacklogHealthReport()).append("\n");
        report.append(generatePriorityBreakdown()).append("\n");
        report.append(generateWorkloadPerAgent()).append("\n");
        report.append(generateStoryPointTotals()).append("\n");
        report.append(generateStatusDistribution()).append("\n");
        
        return report.toString();
    }
    
    /**
     * Calculates backlog health score (0-100).
     */
    private double calculateHealthScore(List<BacklogItem> items) {
        if (items.isEmpty()) {
            return 100.0; // Empty backlog is healthy
        }
        
        double score = 100.0;
        
        // Deduct for items without acceptance criteria
        long itemsWithoutCriteria = items.stream()
            .filter(item -> item.getAcceptanceCriteria().isEmpty())
            .count();
        score -= (itemsWithoutCriteria * 100.0 / items.size()) * 0.3; // 30% weight
        
        // Deduct for items without estimates
        long itemsWithoutEstimate = items.stream()
            .filter(item -> item.getStoryPoints() == null || item.getStoryPoints() == 0)
            .count();
        score -= (itemsWithoutEstimate * 100.0 / items.size()) * 0.2; // 20% weight
        
        // Deduct for blocked items
        long blockedItems = items.stream()
            .filter(item -> item.getStatus() == BacklogItem.Status.BLOCKED)
            .count();
        score -= (blockedItems * 100.0 / items.size()) * 0.3; // 30% weight
        
        // Deduct for items without agent assignment (if in progress)
        long unassignedInProgress = items.stream()
            .filter(item -> item.getStatus() == BacklogItem.Status.IN_PROGRESS)
            .filter(item -> item.getAgentOwner() == null || item.getAgentOwner().isEmpty())
            .count();
        score -= (unassignedInProgress * 100.0 / items.size()) * 0.2; // 20% weight
        
        return Math.max(0, Math.min(100, score));
    }
    
    /**
     * Agent workload data structure.
     */
    public static class AgentWorkload {
        private String agentName;
        private int totalItems;
        private int totalStoryPoints;
        private int todoCount;
        private int inProgressCount;
        private int doneCount;
        private int blockedCount;
        
        // Getters and Setters
        public String getAgentName() { return agentName; }
        public void setAgentName(String agentName) { this.agentName = agentName; }
        public int getTotalItems() { return totalItems; }
        public void setTotalItems(int totalItems) { this.totalItems = totalItems; }
        public int getTotalStoryPoints() { return totalStoryPoints; }
        public void setTotalStoryPoints(int totalStoryPoints) { this.totalStoryPoints = totalStoryPoints; }
        public int getTodoCount() { return todoCount; }
        public void setTodoCount(int todoCount) { this.todoCount = todoCount; }
        public int getInProgressCount() { return inProgressCount; }
        public void setInProgressCount(int inProgressCount) { this.inProgressCount = inProgressCount; }
        public int getDoneCount() { return doneCount; }
        public void setDoneCount(int doneCount) { this.doneCount = doneCount; }
        public int getBlockedCount() { return blockedCount; }
        public void setBlockedCount(int blockedCount) { this.blockedCount = blockedCount; }
    }
}



