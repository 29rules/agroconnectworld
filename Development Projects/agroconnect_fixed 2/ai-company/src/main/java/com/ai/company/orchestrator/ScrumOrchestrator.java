package com.ai.company.orchestrator;

import com.ai.company.agents.architect.ArchitectAgent;
import com.ai.company.agents.cto.CTOAgent;
import com.ai.company.analytics.BurndownChartGenerator;
import com.ai.company.analytics.StoryPointAnalytics;
import com.ai.company.backlog.BacklogItem;
import com.ai.company.backlog.BacklogManager;
import com.ai.company.backlog.BacklogReporter;
import com.ai.company.planner.TaskPlanner;
import com.ai.company.retro.RetroAgent;
import com.ai.company.retro.RetroReportBuilder;
import com.ai.company.standup.StandupAgent;
import com.ai.company.standup.StandupReportBuilder;
import com.ai.company.sprint.SprintCapacityCalculator;
import com.ai.company.sprint.SprintPlan;
import com.ai.company.sprint.SprintPlanner;
import com.ai.company.sprint.SprintVelocityTracker;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

/**
 * Scrum Orchestrator
 * 
 * Coordinates all Scrum-related activities:
 * - Backlog grooming
 * - Sprint planning
 * - Standup generation
 * - Burndown chart updates
 * - Retro reports
 * - Sync with Task Planner + Architect + CTO
 * - Schedule notifications
 * 
 * SAFETY:
 * - Coordinates activities only
 * - Never modifies code or systems
 * - Only orchestrates planning and reporting
 */
public class ScrumOrchestrator {
    
    private static final Logger log = LoggerFactory.getLogger(ScrumOrchestrator.class);
    
    private final BacklogManager backlogManager;
    private final BacklogReporter backlogReporter;
    private final SprintPlanner sprintPlanner;
    private final SprintVelocityTracker velocityTracker;
    private final SprintCapacityCalculator capacityCalculator;
    private final StandupAgent standupAgent;
    private final StandupReportBuilder standupReportBuilder;
    private final BurndownChartGenerator burndownChartGenerator;
    private final StoryPointAnalytics storyPointAnalytics;
    private final RetroAgent retroAgent;
    private final RetroReportBuilder retroReportBuilder;
    private final TaskPlanner taskPlanner;
    private final ArchitectAgent architectAgent;
    private final CTOAgent ctoAgent;
    private final ScrumSchedule scrumSchedule;
    
    public ScrumOrchestrator(ChatLanguageModel chatModel, BacklogManager backlogManager) {
        this.backlogManager = backlogManager;
        this.backlogReporter = new BacklogReporter(backlogManager);
        this.sprintPlanner = new SprintPlanner(chatModel, backlogManager);
        this.velocityTracker = new SprintVelocityTracker();
        this.capacityCalculator = new SprintCapacityCalculator();
        this.standupAgent = new StandupAgent(chatModel);
        this.standupReportBuilder = new StandupReportBuilder();
        this.burndownChartGenerator = new BurndownChartGenerator();
        this.storyPointAnalytics = new StoryPointAnalytics();
        this.retroAgent = new RetroAgent(chatModel);
        this.retroReportBuilder = new RetroReportBuilder();
        this.taskPlanner = new TaskPlanner(chatModel, new com.ai.company.planner.TaskAssignmentEngine());
        this.architectAgent = new ArchitectAgent(chatModel);
        this.ctoAgent = new CTOAgent(chatModel);
        this.scrumSchedule = new ScrumSchedule(this);
        
        log.info("ScrumOrchestrator initialized");
    }
    
    /**
     * Performs backlog grooming.
     * 
     * @param sessionId Session ID
     * @return Grooming report
     */
    public String groomBacklog(String sessionId) {
        log.info("Starting backlog grooming");
        
        try {
            // Get backlog health report
            String healthReport = backlogReporter.generateBacklogHealthReport();
            
            // Get items that need grooming
            List<BacklogItem> todoItems = backlogManager.getItemsByStatus(BacklogItem.Status.TODO);
            List<BacklogItem> itemsNeedingGrooming = todoItems.stream()
                .filter(item -> !item.isReady())
                .toList();
            
            // Generate grooming report
            StringBuilder report = new StringBuilder();
            report.append("BACKLOG GROOMING REPORT\n");
            report.append("=".repeat(80)).append("\n\n");
            report.append(healthReport).append("\n");
            
            report.append("Items Needing Grooming: ").append(itemsNeedingGrooming.size()).append("\n");
            for (BacklogItem item : itemsNeedingGrooming) {
                report.append(String.format("  - %s (ID: %s)\n", item.getTitle(), item.getId()));
                if (item.getAcceptanceCriteria().isEmpty()) {
                    report.append("    Missing: Acceptance criteria\n");
                }
                if (item.getStoryPoints() == null || item.getStoryPoints() == 0) {
                    report.append("    Missing: Story point estimate\n");
                }
            }
            
            log.info("Backlog grooming completed");
            return report.toString();
            
        } catch (Exception e) {
            log.error("Error during backlog grooming", e);
            return "ERROR: Backlog grooming failed: " + e.getMessage();
        }
    }
    
    /**
     * Creates a sprint plan.
     * 
     * @param sprintNumber Sprint number
     * @param sprintDuration Duration in weeks
     * @param sprintGoal Sprint goal
     * @param sessionId Session ID
     * @return Sprint plan
     */
    public SprintPlan planSprint(int sprintNumber, int sprintDuration, String sprintGoal, String sessionId) {
        log.info("Starting sprint planning for Sprint {}", sprintNumber);
        
        try {
            // Sync with Task Planner for task breakdown
            var planningOutput = taskPlanner.plan(sprintGoal, sessionId);
            String taskBreakdown = planningOutput != null ? planningOutput.toString() : "No task breakdown";
            log.debug("Task breakdown from TaskPlanner: {}", taskBreakdown);
            
            // Sync with Architect for technical design
            String technicalDesign = architectAgent.designArchitecture(sprintGoal, sessionId);
            log.debug("Technical design from Architect: {}", technicalDesign);
            
            // Sync with CTO for approval
            String ctoApproval = ctoAgent.reviewArchitecture(technicalDesign, sessionId);
            log.debug("CTO approval: {}", ctoApproval);
            
            // Create sprint plan
            SprintPlan sprintPlan = sprintPlanner.createSprintPlan(
                sprintNumber, sprintDuration, sprintGoal, sessionId);
            
            log.info("Sprint planning completed for Sprint {}", sprintNumber);
            return sprintPlan;
            
        } catch (Exception e) {
            log.error("Error during sprint planning", e);
            throw new RuntimeException("Sprint planning failed", e);
        }
    }
    
    /**
     * Generates daily standup report.
     * 
     * @param sprintPlan Current sprint plan
     * @param sessionId Session ID
     * @return Standup report
     */
    public String generateStandup(SprintPlan sprintPlan, String sessionId) {
        log.info("Generating daily standup");
        
        try {
            // Get backlog state
            String backlogState = backlogReporter.generateStatusDistribution();
            
            // Get yesterday's progress (simplified - in real implementation, would track daily)
            String yesterdayProgress = formatYesterdayProgress(sprintPlan);
            
            // Get blockers
            String blockers = formatBlockers(sprintPlan);
            
            // Get velocity report
            String velocityReport = formatVelocityReport();
            
            // Generate standup
            String jsonStandup = standupAgent.generateStandupReport(
                sessionId, backlogState, yesterdayProgress, blockers);
            
            // Format report
            String formattedStandup = standupReportBuilder.buildSlackStyleReport(jsonStandup);
            
            log.info("Daily standup generated successfully");
            return formattedStandup;
            
        } catch (Exception e) {
            log.error("Error generating standup", e);
            return "ERROR: Standup generation failed: " + e.getMessage();
        }
    }
    
    /**
     * Updates burndown chart for sprint.
     * 
     * @param sprintPlan Sprint plan
     * @return Burndown chart
     */
    public String updateBurndownChart(SprintPlan sprintPlan) {
        log.info("Updating burndown chart for Sprint {}", sprintPlan.getSprintId());
        
        try {
            String chart = burndownChartGenerator.generateBurndownChart(sprintPlan, velocityTracker);
            
            log.info("Burndown chart updated successfully");
            return chart;
            
        } catch (Exception e) {
            log.error("Error updating burndown chart", e);
            return "ERROR: Burndown chart update failed: " + e.getMessage();
        }
    }
    
    /**
     * Generates retrospective report.
     * 
     * @param sprintPlan Completed sprint plan
     * @param completedStoryPoints Completed story points
     * @param sessionId Session ID
     * @return Retrospective report
     */
    public String generateRetrospective(SprintPlan sprintPlan, int completedStoryPoints, String sessionId) {
        log.info("Generating retrospective for Sprint {}", sprintPlan.getSprintId());
        
        try {
            // Format sprint results
            String sprintResults = formatSprintResults(sprintPlan, completedStoryPoints);
            
            // Get burndown data
            String burndownData = burndownChartGenerator.generateBurndownChart(sprintPlan, velocityTracker);
            
            // Get blockers
            String blockers = formatBlockers(sprintPlan);
            
            // Get velocity report
            String velocityReport = formatVelocityReport();
            
            // Generate retrospective
            String jsonRetro = retroAgent.generateRetrospective(
                sessionId, sprintResults, burndownData, blockers, velocityReport);
            
            // Format report
            String formattedRetro = retroReportBuilder.buildMarkdownReport(jsonRetro);
            
            // Record velocity for future predictions
            velocityTracker.recordSprintVelocity(
                Integer.parseInt(sprintPlan.getSprintId().replace("SPRINT-", "")),
                completedStoryPoints);
            
            log.info("Retrospective generated successfully");
            return formattedRetro;
            
        } catch (Exception e) {
            log.error("Error generating retrospective", e);
            return "ERROR: Retrospective generation failed: " + e.getMessage();
        }
    }
    
    /**
     * Syncs with Task Planner, Architect, and CTO for sprint planning.
     * 
     * @param sprintGoal Sprint goal
     * @param sessionId Session ID
     * @return Sync report
     */
    public String syncWithAgents(String sprintGoal, String sessionId) {
        log.info("Syncing with Task Planner, Architect, and CTO");
        
        try {
            StringBuilder syncReport = new StringBuilder();
            syncReport.append("AGENT SYNC REPORT\n");
            syncReport.append("=".repeat(80)).append("\n\n");
            
            // Task Planner
            syncReport.append("Task Planner:\n");
            var planningOutput = taskPlanner.plan(sprintGoal, sessionId);
            String taskBreakdown = planningOutput != null ? planningOutput.toString() : "No task breakdown";
            syncReport.append(taskBreakdown).append("\n\n");
            
            // Architect
            syncReport.append("Architect:\n");
            String technicalDesign = architectAgent.designArchitecture(sprintGoal, sessionId);
            syncReport.append(technicalDesign).append("\n\n");
            
            // CTO
            syncReport.append("CTO:\n");
            String ctoReview = ctoAgent.reviewArchitecture(technicalDesign, sessionId);
            syncReport.append(ctoReview).append("\n\n");
            
            log.info("Agent sync completed");
            return syncReport.toString();
            
        } catch (Exception e) {
            log.error("Error syncing with agents", e);
            return "ERROR: Agent sync failed: " + e.getMessage();
        }
    }
    
    /**
     * Formats yesterday's progress for standup.
     */
    private String formatYesterdayProgress(SprintPlan sprintPlan) {
        StringBuilder progress = new StringBuilder();
        progress.append("Yesterday's Progress:\n");
        
        // In real implementation, would track daily completion
        // For now, use committed stories
        int completedCount = 0;
        for (SprintPlan.CommittedStory story : sprintPlan.getCommittedStories()) {
            // Simplified - in real implementation, would check actual completion status
            progress.append(String.format("- %s (%s)\n", story.getTitle(), story.getAssignedAgent()));
            completedCount++;
        }
        
        if (completedCount == 0) {
            progress.append("No items completed yesterday.");
        }
        
        return progress.toString();
    }
    
    /**
     * Formats blockers for standup.
     */
    private String formatBlockers(SprintPlan sprintPlan) {
        StringBuilder blockers = new StringBuilder();
        blockers.append("Blockers:\n");
        
        if (sprintPlan.getBlockers() != null && !sprintPlan.getBlockers().isEmpty()) {
            for (String blocker : sprintPlan.getBlockers()) {
                blockers.append("- ").append(blocker).append("\n");
            }
        } else {
            blockers.append("No blockers reported.");
        }
        
        return blockers.toString();
    }
    
    /**
     * Formats velocity report.
     */
    private String formatVelocityReport() {
        StringBuilder report = new StringBuilder();
        report.append("Velocity Report:\n");
        
        String trend = velocityTracker.getVelocityTrend();
        double avgVelocity = velocityTracker.getAverageVelocity(3);
        
        report.append(String.format("Trend: %s\n", trend));
        report.append(String.format("Average Velocity (last 3 sprints): %.1f story points\n", avgVelocity));
        
        return report.toString();
    }
    
    /**
     * Formats sprint results for retrospective.
     */
    private String formatSprintResults(SprintPlan sprintPlan, int completedStoryPoints) {
        StringBuilder results = new StringBuilder();
        results.append("Sprint Results:\n");
        results.append(String.format("Sprint: %s\n", sprintPlan.getSprintId()));
        results.append(String.format("Sprint Goal: %s\n", sprintPlan.getSprintGoal()));
        results.append(String.format("Committed Story Points: %d\n", sprintPlan.getStoryPointTotal()));
        results.append(String.format("Completed Story Points: %d\n", completedStoryPoints));
        results.append(String.format("Completion Rate: %.1f%%\n", 
            sprintPlan.getStoryPointTotal() > 0 ? 
                (completedStoryPoints * 100.0 / sprintPlan.getStoryPointTotal()) : 0));
        results.append(String.format("Committed Stories: %d\n", sprintPlan.getCommittedStories().size()));
        
        return results.toString();
    }
    
    /**
     * Gets the Scrum schedule.
     */
    public ScrumSchedule getSchedule() {
        return scrumSchedule;
    }
}

