package com.ai.company.agents.scrummaster;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Scrum Master Agent
 * 
 * Coordinates Agile development processes and sprint planning.
 * 
 * Responsibilities:
 * - Create sprint plans
 * - Break epics → stories → tasks
 * - Maintain backlog health
 * - Generate burndown charts
 * - Create daily standups & weekly summaries
 * - Coordinate all other agents
 * - Predict team velocity & capacity
 * - Identify blockers & dependencies
 * 
 * ZERO-IMPACT MODE: This agent only plans and coordinates, never modifies code.
 */
public class ScrumMasterAgent {
    
    private static final Logger log = LoggerFactory.getLogger(ScrumMasterAgent.class);
    
    private final ScrumMasterService scrumMasterService;
    
    public ScrumMasterAgent(ChatLanguageModel chatModel) {
        this.scrumMasterService = AiServices.builder(ScrumMasterService.class)
            .chatLanguageModel(chatModel)
            .chatMemoryProvider(sessionId -> MessageWindowChatMemory.withMaxMessages(100))
            .build();
        
        log.info("ScrumMasterAgent initialized");
    }
    
    /**
     * Creates a sprint plan.
     * 
     * @param sprintNumber Sprint number
     * @param sprintDuration Duration in weeks (default: 2)
     * @param sprintGoal High-level sprint goal
     * @param sessionId Session ID for tracking
     * @return Sprint plan in JSON format
     */
    public String createSprintPlan(int sprintNumber, int sprintDuration, String sprintGoal, String sessionId) {
        log.info("Creating sprint plan for Sprint {}", sprintNumber);
        
        try {
            String plan = scrumMasterService.createSprintPlan(
                sprintNumber, sprintDuration, sprintGoal, sessionId);
            
            log.info("Sprint plan created successfully");
            return plan;
            
        } catch (Exception e) {
            log.error("Error creating sprint plan", e);
            return "ERROR: Sprint plan creation failed: " + e.getMessage();
        }
    }
    
    /**
     * Breaks down epics into user stories and tasks.
     * 
     * @param epicDescription Epic description
     * @param sessionId Session ID
     * @return Breakdown in JSON format
     */
    public String breakDownEpic(String epicDescription, String sessionId) {
        log.info("Breaking down epic into stories and tasks");
        
        try {
            String breakdown = scrumMasterService.breakDownEpic(epicDescription, sessionId);
            
            log.info("Epic breakdown completed");
            return breakdown;
            
        } catch (Exception e) {
            log.error("Error breaking down epic", e);
            return "ERROR: Epic breakdown failed: " + e.getMessage();
        }
    }
    
    /**
     * Maintains backlog health.
     * 
     * @param backlogItems List of backlog items
     * @param sessionId Session ID
     * @return Backlog health report
     */
    public String maintainBacklogHealth(List<String> backlogItems, String sessionId) {
        log.info("Maintaining backlog health");
        
        try {
            String backlogContext = String.join("\n", backlogItems);
            String healthReport = scrumMasterService.maintainBacklogHealth(backlogContext, sessionId);
            
            log.info("Backlog health maintenance completed");
            return healthReport;
            
        } catch (Exception e) {
            log.error("Error maintaining backlog health", e);
            return "ERROR: Backlog health maintenance failed: " + e.getMessage();
        }
    }
    
    /**
     * Generates a burndown chart data.
     * 
     * @param sprintNumber Sprint number
     * @param completedTasks List of completed tasks
     * @param totalTasks Total tasks in sprint
     * @param sessionId Session ID
     * @return Burndown chart data in JSON format
     */
    public String generateBurndownChart(int sprintNumber, List<String> completedTasks, 
                                        int totalTasks, String sessionId) {
        log.info("Generating burndown chart for Sprint {}", sprintNumber);
        
        try {
            String context = String.format(
                "Sprint: %d\nCompleted Tasks: %d/%d\nCompleted: %s",
                sprintNumber, completedTasks.size(), totalTasks, String.join(", ", completedTasks)
            );
            
            String burndownData = scrumMasterService.generateBurndownChart(context, sessionId);
            
            log.info("Burndown chart generated successfully");
            return burndownData;
            
        } catch (Exception e) {
            log.error("Error generating burndown chart", e);
            return "ERROR: Burndown chart generation failed: " + e.getMessage();
        }
    }
    
    /**
     * Creates a daily standup summary.
     * 
     * @param date Date for standup
     * @param teamUpdates List of team member updates
     * @param sessionId Session ID
     * @return Daily standup summary
     */
    public String createDailyStandup(LocalDate date, List<String> teamUpdates, String sessionId) {
        log.info("Creating daily standup for {}", date);
        
        try {
            String updatesContext = String.join("\n---\n", teamUpdates);
            String standup = scrumMasterService.createDailyStandup(
                date.format(DateTimeFormatter.ISO_DATE), updatesContext, sessionId);
            
            log.info("Daily standup created successfully");
            return standup;
            
        } catch (Exception e) {
            log.error("Error creating daily standup", e);
            return "ERROR: Daily standup creation failed: " + e.getMessage();
        }
    }
    
    /**
     * Creates a weekly summary.
     * 
     * @param weekStartDate Week start date
     * @param weekProgress Week progress data
     * @param sessionId Session ID
     * @return Weekly summary
     */
    public String createWeeklySummary(LocalDate weekStartDate, String weekProgress, String sessionId) {
        log.info("Creating weekly summary for week starting {}", weekStartDate);
        
        try {
            String summary = scrumMasterService.createWeeklySummary(
                weekStartDate.format(DateTimeFormatter.ISO_DATE), weekProgress, sessionId);
            
            log.info("Weekly summary created successfully");
            return summary;
            
        } catch (Exception e) {
            log.error("Error creating weekly summary", e);
            return "ERROR: Weekly summary creation failed: " + e.getMessage();
        }
    }
    
    /**
     * Predicts team velocity and capacity.
     * 
     * @param historicalVelocity Historical velocity data
     * @param teamCapacity Team capacity information
     * @param sessionId Session ID
     * @return Velocity and capacity prediction
     */
    public String predictVelocityAndCapacity(String historicalVelocity, String teamCapacity, String sessionId) {
        log.info("Predicting team velocity and capacity");
        
        try {
            String prediction = scrumMasterService.predictVelocityAndCapacity(
                historicalVelocity, teamCapacity, sessionId);
            
            log.info("Velocity and capacity prediction completed");
            return prediction;
            
        } catch (Exception e) {
            log.error("Error predicting velocity and capacity", e);
            return "ERROR: Velocity prediction failed: " + e.getMessage();
        }
    }
    
    /**
     * Identifies blockers and dependencies.
     * 
     * @param sprintContext Sprint context and tasks
     * @param sessionId Session ID
     * @return Blockers and dependencies report
     */
    public String identifyBlockersAndDependencies(String sprintContext, String sessionId) {
        log.info("Identifying blockers and dependencies");
        
        try {
            String blockersReport = scrumMasterService.identifyBlockersAndDependencies(
                sprintContext, sessionId);
            
            log.info("Blockers and dependencies identified");
            return blockersReport;
            
        } catch (Exception e) {
            log.error("Error identifying blockers and dependencies", e);
            return "ERROR: Blocker identification failed: " + e.getMessage();
        }
    }
    
    /**
     * Coordinates all agents for sprint planning.
     * 
     * @param sprintGoal Sprint goal
     * @param sessionId Session ID
     * @return Coordination plan
     */
    public String coordinateAgentsForSprint(String sprintGoal, String sessionId) {
        log.info("Coordinating agents for sprint planning");
        
        try {
            String coordinationPlan = scrumMasterService.coordinateAgentsForSprint(
                sprintGoal, sessionId);
            
            log.info("Agent coordination completed");
            return coordinationPlan;
            
        } catch (Exception e) {
            log.error("Error coordinating agents", e);
            return "ERROR: Agent coordination failed: " + e.getMessage();
        }
    }
    
    /**
     * LangChain4j AI Service interface for Scrum Master.
     */
    interface ScrumMasterService {
        
        @SystemMessage("""
            You are a Senior Scrum Master and Agile Coach for AgroConnectWorld AI Company.
            
            Your role is to facilitate Agile development processes and coordinate the AI agent team.
            
            RESPONSIBILITIES:
            1. Sprint Planning:
               - Create comprehensive sprint plans
               - Break epics into user stories
               - Break user stories into tasks
               - Define acceptance criteria
               - Estimate effort (story points or hours)
               - Plan sprint capacity
            
            2. Backlog Management:
               - Maintain backlog health
               - Prioritize backlog items
               - Refine user stories
               - Ensure stories are INVEST-compliant
               - Remove obsolete items
            
            3. Burndown Charts:
               - Generate burndown chart data
               - Track sprint progress
               - Identify velocity trends
               - Predict sprint completion
            
            4. Daily Standups:
               - Create daily standup summaries
               - Track: What did I do yesterday? What will I do today? Any blockers?
               - Identify impediments
               - Coordinate team activities
            
            5. Weekly Summaries:
               - Create weekly progress summaries
               - Highlight achievements
               - Identify risks and issues
               - Plan next week
            
            6. Velocity & Capacity:
               - Predict team velocity based on historical data
               - Calculate team capacity
               - Adjust estimates based on velocity
               - Plan sprint scope accordingly
            
            7. Blocker & Dependency Management:
               - Identify blockers early
               - Track dependencies between tasks
               - Escalate blockers to appropriate agents
               - Coordinate resolution
            
            8. Agent Coordination:
               - Coordinate ProductManagerAgent for requirements
               - Coordinate ArchitectAgent for technical design
               - Coordinate CTOAgent for approvals
               - Coordinate EngineerAgent for implementation
               - Coordinate QAAgent for testing
               - Coordinate DevOpsAgent for deployment
            
            AGILE PRINCIPLES:
            - User stories must be INVEST (Independent, Negotiable, Valuable, Estimable, Small, Testable)
            - Tasks should be small (1-8 hours ideally)
            - Sprint goals should be clear and achievable
            - Velocity is based on completed story points, not planned
            - Capacity accounts for meetings, overhead, and unplanned work
            - Blockers must be identified and resolved quickly
            
            SCRUM CEREMONIES:
            - Sprint Planning: Plan sprint goal, select backlog items, break into tasks
            - Daily Standup: 15-minute sync, track progress, identify blockers
            - Sprint Review: Demo completed work, gather feedback
            - Sprint Retrospective: Reflect on process, identify improvements
            
            ZERO-IMPACT MODE:
            - You only plan and coordinate, never modify code
            - All outputs are planning documents and summaries
            - No actual code changes or deployments
            
            OUTPUT FORMAT: JSON with structure:
            {
              "sprint_goal": "Clear, achievable sprint goal",
              "epics": [
                {
                  "epic_id": "EPIC-001",
                  "epic_name": "Epic name",
                  "description": "Epic description",
                  "priority": "HIGH|MEDIUM|LOW",
                  "status": "PLANNED|IN_PROGRESS|COMPLETED"
                }
              ],
              "user_stories": [
                {
                  "story_id": "US-001",
                  "epic_id": "EPIC-001",
                  "title": "As a [user], I want [feature] so that [benefit]",
                  "description": "Detailed description",
                  "acceptance_criteria": [
                    "Criterion 1",
                    "Criterion 2"
                  ],
                  "story_points": 5,
                  "priority": "HIGH|MEDIUM|LOW",
                  "status": "BACKLOG|PLANNED|IN_PROGRESS|DONE"
                }
              ],
              "tasks": [
                {
                  "task_id": "TASK-001",
                  "story_id": "US-001",
                  "title": "Task title",
                  "description": "Task description",
                  "estimated_hours": 4,
                  "assigned_agent": "EngineerAgent|QAAgent|DevOpsAgent",
                  "status": "TODO|IN_PROGRESS|DONE|BLOCKED",
                  "dependencies": ["TASK-002"]
                }
              ],
              "acceptance_criteria": {
                "US-001": [
                  "Criterion 1",
                  "Criterion 2"
                ]
              },
              "risks": [
                {
                  "risk_id": "RISK-001",
                  "description": "Risk description",
                  "probability": "HIGH|MEDIUM|LOW",
                  "impact": "HIGH|MEDIUM|LOW",
                  "mitigation": "Mitigation strategy"
                }
              ],
              "capacity_plan": {
                "sprint_duration_weeks": 2,
                "team_capacity_hours": 160,
                "allocated_hours": 140,
                "buffer_hours": 20,
                "velocity_prediction": 25
              },
              "predicted_velocity": {
                "story_points": 25,
                "confidence": "HIGH|MEDIUM|LOW",
                "based_on": "Historical data from last 3 sprints"
              },
              "dependencies": [
                {
                  "from_task": "TASK-001",
                  "to_task": "TASK-002",
                  "type": "BLOCKS|REQUIRES",
                  "description": "Dependency description"
                }
              ],
              "blockers": [
                {
                  "blocker_id": "BLOCKER-001",
                  "description": "Blocker description",
                  "affected_tasks": ["TASK-001"],
                  "severity": "CRITICAL|HIGH|MEDIUM",
                  "owner": "Agent or person responsible",
                  "resolution_plan": "How to resolve"
                }
              ],
              "deliverables": [
                {
                  "deliverable_id": "DEL-001",
                  "name": "Deliverable name",
                  "description": "What will be delivered",
                  "story_ids": ["US-001", "US-002"],
                  "due_date": "ISO8601 date",
                  "status": "PLANNED|IN_PROGRESS|COMPLETED"
                }
              ]
            }
            
            Be thorough, realistic, and focused on delivering value.
            """)
        String createSprintPlan(
            @UserMessage("Create sprint plan for Sprint {{sprintNumber}}, duration {{sprintDuration}} weeks, goal: {{sprintGoal}}") 
            int sprintNumber, int sprintDuration, String sprintGoal, @MemoryId String sessionId);
        
        String breakDownEpic(
            @UserMessage("Break down this epic into user stories and tasks: {{epicDescription}}") 
            String epicDescription, @MemoryId String sessionId);
        
        String maintainBacklogHealth(
            @UserMessage("Maintain backlog health for these items: {{backlogItems}}") 
            String backlogItems, @MemoryId String sessionId);
        
        String generateBurndownChart(
            @UserMessage("Generate burndown chart data: {{context}}") 
            String context, @MemoryId String sessionId);
        
        String createDailyStandup(
            @UserMessage("Create daily standup for {{date}}. Team updates: {{teamUpdates}}") 
            String date, String teamUpdates, @MemoryId String sessionId);
        
        String createWeeklySummary(
            @UserMessage("Create weekly summary for week starting {{weekStartDate}}. Progress: {{weekProgress}}") 
            String weekStartDate, String weekProgress, @MemoryId String sessionId);
        
        String predictVelocityAndCapacity(
            @UserMessage("Predict velocity and capacity. Historical: {{historicalVelocity}}. Capacity: {{teamCapacity}}") 
            String historicalVelocity, String teamCapacity, @MemoryId String sessionId);
        
        String identifyBlockersAndDependencies(
            @UserMessage("Identify blockers and dependencies for: {{sprintContext}}") 
            String sprintContext, @MemoryId String sessionId);
        
        String coordinateAgentsForSprint(
            @UserMessage("Coordinate all agents for sprint with goal: {{sprintGoal}}") 
            String sprintGoal, @MemoryId String sessionId);
    }
}



