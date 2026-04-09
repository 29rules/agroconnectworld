package com.ai.company.backlog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Backlog Manager
 * 
 * Manages the product backlog with operations for:
 * - Adding items
 * - Updating items
 * - Reprioritizing
 * - Reordering backlog
 * - Assigning agents
 * - Generating backlog snapshots for planning
 * 
 * SAFETY:
 * - All operations are read/write of backlog data only
 * - Never modifies code or systems
 * - Provides snapshots for planning
 */
@Component
public class BacklogManager {
    
    private static final Logger log = LoggerFactory.getLogger(BacklogManager.class);
    
    private final List<BacklogItem> backlog;
    private final Map<String, BacklogItem> itemMap; // For fast lookup by ID
    
    public BacklogManager() {
        this.backlog = new ArrayList<>();
        this.itemMap = new HashMap<>();
    }
    
    /**
     * Adds a new item to the backlog.
     * 
     * @param item Backlog item to add
     * @return Added item with generated ID
     */
    public BacklogItem addItem(BacklogItem item) {
        if (item.getId() == null || item.getId().isEmpty()) {
            item.setId(UUID.randomUUID().toString());
        }
        
        backlog.add(item);
        itemMap.put(item.getId(), item);
        
        log.info("Added backlog item: {} (ID: {})", item.getTitle(), item.getId());
        return item;
    }
    
    /**
     * Adds a new item with title and description.
     * 
     * @param title Item title
     * @param description Item description
     * @return Created backlog item
     */
    public BacklogItem addItem(String title, String description) {
        BacklogItem item = new BacklogItem(title, description);
        return addItem(item);
    }
    
    /**
     * Updates an existing backlog item.
     * 
     * @param itemId Item ID to update
     * @param updates Map of field names to new values
     * @return Updated item or null if not found
     */
    public BacklogItem updateItem(String itemId, Map<String, Object> updates) {
        BacklogItem item = itemMap.get(itemId);
        if (item == null) {
            log.warn("Backlog item not found: {}", itemId);
            return null;
        }
        
        updates.forEach((field, value) -> {
            switch (field.toLowerCase()) {
                case "title":
                    item.setTitle((String) value);
                    break;
                case "description":
                    item.setDescription((String) value);
                    break;
                case "priority":
                    if (value instanceof BacklogItem.Priority) {
                        item.setPriority((BacklogItem.Priority) value);
                    } else if (value instanceof String) {
                        item.setPriority(BacklogItem.Priority.valueOf(((String) value).toUpperCase()));
                    }
                    break;
                case "status":
                    if (value instanceof BacklogItem.Status) {
                        item.setStatus((BacklogItem.Status) value);
                    } else if (value instanceof String) {
                        item.setStatus(BacklogItem.Status.valueOf(((String) value).toUpperCase()));
                    }
                    break;
                case "storypoints":
                case "story_points":
                    if (value instanceof Integer) {
                        item.setStoryPoints((Integer) value);
                    } else if (value instanceof Number) {
                        item.setStoryPoints(((Number) value).intValue());
                    }
                    break;
                case "agentowner":
                case "agent_owner":
                    item.setAgentOwner((String) value);
                    break;
            }
        });
        
        item.touch();
        log.info("Updated backlog item: {} (ID: {})", item.getTitle(), itemId);
        return item;
    }
    
    /**
     * Reprioritizes items in the backlog.
     * 
     * @param itemIds Ordered list of item IDs (highest priority first)
     * @return Number of items reprioritized
     */
    public int reprioritize(List<String> itemIds) {
        int count = 0;
        
        for (int i = 0; i < itemIds.size(); i++) {
            String itemId = itemIds.get(i);
            BacklogItem item = itemMap.get(itemId);
            if (item != null) {
                // Set priority based on position
                if (i < itemIds.size() / 3) {
                    item.setPriority(BacklogItem.Priority.HIGH);
                } else if (i < (itemIds.size() * 2) / 3) {
                    item.setPriority(BacklogItem.Priority.MEDIUM);
                } else {
                    item.setPriority(BacklogItem.Priority.LOW);
                }
                count++;
            }
        }
        
        log.info("Reprioritized {} backlog items", count);
        return count;
    }
    
    /**
     * Reorders the backlog based on priority and status.
     * 
     * @return Reordered backlog list
     */
    public List<BacklogItem> reorderBacklog() {
        List<BacklogItem> reordered = new ArrayList<>(backlog);
        
        // Sort by: Priority (HIGH first), then Status (TODO first), then CreatedAt (oldest first)
        reordered.sort((a, b) -> {
            // Priority comparison
            int priorityCompare = b.getPriority().compareTo(a.getPriority()); // HIGH before MEDIUM before LOW
            if (priorityCompare != 0) {
                return priorityCompare;
            }
            
            // Status comparison (TODO before IN_PROGRESS before DONE)
            int statusCompare = getStatusOrder(a.getStatus()) - getStatusOrder(b.getStatus());
            if (statusCompare != 0) {
                return statusCompare;
            }
            
            // CreatedAt comparison (oldest first)
            return a.getCreatedAt().compareTo(b.getCreatedAt());
        });
        
        log.info("Reordered backlog: {} items", reordered.size());
        return reordered;
    }
    
    /**
     * Gets status order for sorting.
     */
    private int getStatusOrder(BacklogItem.Status status) {
        switch (status) {
            case TODO: return 1;
            case IN_PROGRESS: return 2;
            case BLOCKED: return 3;
            case DONE: return 4;
            case CANCELLED: return 5;
            default: return 99;
        }
    }
    
    /**
     * Assigns an item to an agent.
     * 
     * @param itemId Item ID
     * @param agentName Agent name
     * @return Updated item or null if not found
     */
    public BacklogItem assignAgent(String itemId, String agentName) {
        BacklogItem item = itemMap.get(itemId);
        if (item == null) {
            log.warn("Backlog item not found: {}", itemId);
            return null;
        }
        
        item.setAgentOwner(agentName);
        log.info("Assigned item {} to agent: {}", itemId, agentName);
        return item;
    }
    
    /**
     * Generates a backlog snapshot for planning.
     * 
     * @param filterStatus Filter by status (null = all)
     * @param filterPriority Filter by priority (null = all)
     * @return Backlog snapshot
     */
    public BacklogSnapshot generateSnapshot(BacklogItem.Status filterStatus, 
                                           BacklogItem.Priority filterPriority) {
        List<BacklogItem> filtered = backlog.stream()
            .filter(item -> filterStatus == null || item.getStatus() == filterStatus)
            .filter(item -> filterPriority == null || item.getPriority() == filterPriority)
            .collect(Collectors.toList());
        
        BacklogSnapshot snapshot = new BacklogSnapshot();
        snapshot.setSnapshotId(UUID.randomUUID().toString());
        snapshot.setGeneratedAt(LocalDateTime.now());
        snapshot.setItems(new ArrayList<>(filtered));
        snapshot.setTotalItems(filtered.size());
        snapshot.setTotalStoryPoints(filtered.stream()
            .filter(item -> item.getStoryPoints() != null)
            .mapToInt(BacklogItem::getStoryPoints)
            .sum());
        
        // Calculate by status
        Map<BacklogItem.Status, Long> statusCounts = filtered.stream()
            .collect(Collectors.groupingBy(BacklogItem::getStatus, Collectors.counting()));
        snapshot.setStatusDistribution(statusCounts);
        
        // Calculate by priority
        Map<BacklogItem.Priority, Long> priorityCounts = filtered.stream()
            .collect(Collectors.groupingBy(BacklogItem::getPriority, Collectors.counting()));
        snapshot.setPriorityDistribution(priorityCounts);
        
        // Calculate by agent
        Map<String, Long> agentCounts = filtered.stream()
            .filter(item -> item.getAgentOwner() != null)
            .collect(Collectors.groupingBy(BacklogItem::getAgentOwner, Collectors.counting()));
        snapshot.setAgentDistribution(agentCounts);
        
        log.info("Generated backlog snapshot: {} items, {} story points", 
            snapshot.getTotalItems(), snapshot.getTotalStoryPoints());
        
        return snapshot;
    }
    
    /**
     * Gets an item by ID.
     */
    public BacklogItem getItem(String itemId) {
        return itemMap.get(itemId);
    }
    
    /**
     * Gets all items.
     */
    public List<BacklogItem> getAllItems() {
        return new ArrayList<>(backlog);
    }
    
    /**
     * Gets items by status.
     */
    public List<BacklogItem> getItemsByStatus(BacklogItem.Status status) {
        return backlog.stream()
            .filter(item -> item.getStatus() == status)
            .collect(Collectors.toList());
    }
    
    /**
     * Gets items by priority.
     */
    public List<BacklogItem> getItemsByPriority(BacklogItem.Priority priority) {
        return backlog.stream()
            .filter(item -> item.getPriority() == priority)
            .collect(Collectors.toList());
    }
    
    /**
     * Gets items assigned to an agent.
     */
    public List<BacklogItem> getItemsByAgent(String agentName) {
        return backlog.stream()
            .filter(item -> agentName.equals(item.getAgentOwner()))
            .collect(Collectors.toList());
    }
    
    /**
     * Removes an item from the backlog.
     */
    public boolean removeItem(String itemId) {
        BacklogItem item = itemMap.remove(itemId);
        if (item != null) {
            backlog.remove(item);
            log.info("Removed backlog item: {} (ID: {})", item.getTitle(), itemId);
            return true;
        }
        return false;
    }
    
    /**
     * Backlog snapshot for planning.
     */
    public static class BacklogSnapshot {
        private String snapshotId;
        private LocalDateTime generatedAt;
        private List<BacklogItem> items;
        private int totalItems;
        private int totalStoryPoints;
        private Map<BacklogItem.Status, Long> statusDistribution;
        private Map<BacklogItem.Priority, Long> priorityDistribution;
        private Map<String, Long> agentDistribution;
        
        // Getters and Setters
        public String getSnapshotId() { return snapshotId; }
        public void setSnapshotId(String snapshotId) { this.snapshotId = snapshotId; }
        public LocalDateTime getGeneratedAt() { return generatedAt; }
        public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
        public List<BacklogItem> getItems() { return items; }
        public void setItems(List<BacklogItem> items) { this.items = items; }
        public int getTotalItems() { return totalItems; }
        public void setTotalItems(int totalItems) { this.totalItems = totalItems; }
        public int getTotalStoryPoints() { return totalStoryPoints; }
        public void setTotalStoryPoints(int totalStoryPoints) { this.totalStoryPoints = totalStoryPoints; }
        public Map<BacklogItem.Status, Long> getStatusDistribution() { return statusDistribution; }
        public void setStatusDistribution(Map<BacklogItem.Status, Long> statusDistribution) { this.statusDistribution = statusDistribution; }
        public Map<BacklogItem.Priority, Long> getPriorityDistribution() { return priorityDistribution; }
        public void setPriorityDistribution(Map<BacklogItem.Priority, Long> priorityDistribution) { this.priorityDistribution = priorityDistribution; }
        public Map<String, Long> getAgentDistribution() { return agentDistribution; }
        public void setAgentDistribution(Map<String, Long> agentDistribution) { this.agentDistribution = agentDistribution; }
    }
}

