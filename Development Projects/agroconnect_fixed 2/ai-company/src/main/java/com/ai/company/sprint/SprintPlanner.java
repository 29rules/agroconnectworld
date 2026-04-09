package com.ai.company.sprint;

import com.ai.company.agents.scrummaster.ScrumMasterAgent;
import com.ai.company.backlog.BacklogItem;
import com.ai.company.backlog.BacklogManager;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Sprint Planner
 * 
 * Creates sprint plans using ScrumMasterAgent and BacklogManager.
 * 
 * Selection Criteria:
 * - Velocity history
 * - Priority
 * - Dependencies
 * - Agent available capacity
 * 
 * Outputs: SprintPlan with committed stories and forecasts.
 */
@Component
public class SprintPlanner {
    
    private static final Logger log = LoggerFactory.getLogger(SprintPlanner.class);
    
    private final ScrumMasterAgent scrumMasterAgent;
    private final BacklogManager backlogManager;
    private final SprintVelocityTracker velocityTracker;
    private final SprintCapacityCalculator capacityCalculator;
    
    @Autowired
    public SprintPlanner(ChatLanguageModel chatModel, BacklogManager backlogManager) {
        this.scrumMasterAgent = new ScrumMasterAgent(chatModel);
        this.backlogManager = backlogManager;
        this.velocityTracker = new SprintVelocityTracker();
        this.capacityCalculator = new SprintCapacityCalculator();
    }
    
    /**
     * Creates a sprint plan.
     * 
     * @param sprintNumber Sprint number
     * @param sprintDuration Duration in weeks (default: 2)
     * @param sprintGoal Sprint goal
     * @param sessionId Session ID
     * @return Sprint plan
     */
    public SprintPlan createSprintPlan(int sprintNumber, int sprintDuration, String sprintGoal, String sessionId) {
        log.info("Creating sprint plan for Sprint {}", sprintNumber);
        
        SprintPlan plan = new SprintPlan();
        plan.setSprintId("SPRINT-" + sprintNumber);
        
        // Set dates
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusWeeks(sprintDuration);
        plan.setStartDate(startDate);
        plan.setEndDate(endDate);
        plan.setSprintGoal(sprintGoal);
        
        // Get velocity forecast
        int velocityForecast = velocityTracker.predictVelocity(sprintNumber);
        plan.setVelocityForecast(velocityForecast);
        
        // Get agent capacities
        Map<String, Integer> agentCapacities = capacityCalculator.calculateAgentCapacities(sprintDuration);
        plan.setCapacitySummary(formatCapacitySummary(agentCapacities));
        
        // Select items from backlog
        List<BacklogItem> selectedItems = selectSprintItems(velocityForecast, agentCapacities);
        
        // Convert to committed stories
        for (BacklogItem item : selectedItems) {
            SprintPlan.CommittedStory story = convertToCommittedStory(item);
            plan.addCommittedStory(story);
        }
        
        // Identify risks, dependencies, and blockers
        identifyRisksAndBlockers(plan, selectedItems);
        
        // Use ScrumMasterAgent to refine plan
        String scrumMasterPlan = scrumMasterAgent.createSprintPlan(
            sprintNumber, sprintDuration, sprintGoal, sessionId);
        
        // Parse and enhance plan with ScrumMaster insights
        enhancePlanWithScrumMasterInsights(plan, scrumMasterPlan);
        
        log.info("Sprint plan created: {} stories, {} story points", 
            plan.getCommittedStories().size(), plan.getStoryPointTotal());
        
        return plan;
    }
    
    /**
     * Selects items from backlog for sprint.
     */
    private List<BacklogItem> selectSprintItems(int velocityForecast, Map<String, Integer> agentCapacities) {
        log.info("Selecting sprint items. Velocity forecast: {}", velocityForecast);
        
        // Get TODO items, ordered by priority
        List<BacklogItem> todoItems = backlogManager.getItemsByStatus(BacklogItem.Status.TODO);
        todoItems.sort((a, b) -> b.getPriority().compareTo(a.getPriority())); // HIGH first
        
        List<BacklogItem> selected = new ArrayList<>();
        int totalPoints = 0;
        Map<String, Integer> agentPoints = new HashMap<>();
        
        for (BacklogItem item : todoItems) {
            // Check if item is ready
            if (!item.isReady()) {
                log.debug("Skipping item {} - not ready", item.getId());
                continue;
            }
            
            // Check if item is blocked
            if (item.isBlocked(backlogManager.getAllItems())) {
                log.debug("Skipping item {} - blocked", item.getId());
                continue;
            }
            
            // Check velocity limit
            if (item.getStoryPoints() != null) {
                if (totalPoints + item.getStoryPoints() > velocityForecast) {
                    log.debug("Skipping item {} - would exceed velocity", item.getId());
                    continue;
                }
            }
            
            // Check agent capacity
            String agent = item.getAgentOwner();
            if (agent != null && !agent.isEmpty()) {
                int agentCurrentPoints = agentPoints.getOrDefault(agent, 0);
                int agentCapacity = agentCapacities.getOrDefault(agent, Integer.MAX_VALUE);
                
                if (item.getStoryPoints() != null) {
                    if (agentCurrentPoints + item.getStoryPoints() > agentCapacity) {
                        log.debug("Skipping item {} - agent {} at capacity", item.getId(), agent);
                        continue;
                    }
                }
                
                agentPoints.put(agent, agentCurrentPoints + (item.getStoryPoints() != null ? item.getStoryPoints() : 0));
            }
            
            // Add item to sprint
            selected.add(item);
            if (item.getStoryPoints() != null) {
                totalPoints += item.getStoryPoints();
            }
            
            log.debug("Selected item: {} ({} points)", item.getTitle(), item.getStoryPoints());
            
            // Stop if we've reached velocity forecast
            if (totalPoints >= velocityForecast) {
                break;
            }
        }
        
        log.info("Selected {} items with {} total story points", selected.size(), totalPoints);
        return selected;
    }
    
    /**
     * Converts BacklogItem to CommittedStory.
     */
    private SprintPlan.CommittedStory convertToCommittedStory(BacklogItem item) {
        SprintPlan.CommittedStory story = new SprintPlan.CommittedStory();
        story.setStoryId(item.getId());
        story.setTitle(item.getTitle());
        story.setDescription(item.getDescription());
        story.setStoryPoints(item.getStoryPoints());
        story.setAssignedAgent(item.getAgentOwner());
        story.setAcceptanceCriteria(new ArrayList<>(item.getAcceptanceCriteria()));
        story.setPriority(item.getPriority());
        
        // Extract tasks (simplified - in real implementation, would come from breakdown)
        story.setTasks(new ArrayList<>()); // Would be populated from epic breakdown
        
        return story;
    }
    
    /**
     * Identifies risks, dependencies, and blockers.
     */
    private void identifyRisksAndBlockers(SprintPlan plan, List<BacklogItem> items) {
        // Identify dependencies
        for (BacklogItem item : items) {
            if (!item.getDependencies().isEmpty()) {
                plan.addDependency("Item " + item.getTitle() + " depends on: " + 
                    String.join(", ", item.getDependencies()));
            }
        }
        
        // Identify blockers
        for (BacklogItem item : items) {
            if (item.getStatus() == BacklogItem.Status.BLOCKED) {
                plan.addBlocker("Item " + item.getTitle() + " is blocked");
            }
        }
        
        // Identify risks
        if (plan.getStoryPointTotal() > plan.getVelocityForecast()) {
            plan.addRisk("Committed story points exceed velocity forecast");
        }
        
        long unassignedItems = items.stream()
            .filter(item -> item.getAgentOwner() == null || item.getAgentOwner().isEmpty())
            .count();
        if (unassignedItems > 0) {
            plan.addRisk(unassignedItems + " items are not assigned to agents");
        }
    }
    
    /**
     * Enhances plan with ScrumMaster insights.
     */
    private void enhancePlanWithScrumMasterInsights(SprintPlan plan, String scrumMasterPlan) {
        // Parse ScrumMaster plan JSON and extract insights
        // For now, just log it
        log.debug("ScrumMaster plan: {}", scrumMasterPlan);
        
        // In a full implementation, would parse JSON and merge insights
    }
    
    /**
     * Formats capacity summary.
     */
    private String formatCapacitySummary(Map<String, Integer> agentCapacities) {
        StringBuilder summary = new StringBuilder();
        summary.append("Agent Capacities:\n");
        agentCapacities.forEach((agent, capacity) -> {
            summary.append("  ").append(agent).append(": ").append(capacity).append(" story points\n");
        });
        return summary.toString();
    }
}

