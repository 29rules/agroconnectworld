package com.ai.company.deployment.schedule;

import com.ai.company.deployment.DeploymentWorkflow;
import com.ai.company.deployment.staging.StagingSimulator;
import com.ai.company.self.SelfImprovementEngine;
import com.ai.company.registry.AgentRegistry;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Deployment Scheduler
 * 
 * Schedules automated deployment-related tasks.
 * 
 * Scheduled Tasks:
 * - Daily build & test at 2 AM
 * - Weekly staging deploy simulation on Sunday
 * - Monthly self-improvement deploy proposal
 * 
 * SAFETY:
 * - All scheduled tasks run in ZERO_IMPACT mode by default
 * - Only simulations and reports, no actual deployments
 * - All results logged
 */
public class DeploymentScheduler {
    
    private static final Logger log = LoggerFactory.getLogger(DeploymentScheduler.class);
    
    private final ScheduledExecutorService scheduler;
    private final ChatLanguageModel chatModel;
    private final AgentRegistry agentRegistry;
    private final NightlyDeploymentAgent nightlyAgent;
    private final DeploymentWorkflow deploymentWorkflow;
    private final StagingSimulator stagingSimulator;
    private final SelfImprovementEngine selfImprovementEngine;
    
    private boolean isRunning = false;
    
    public DeploymentScheduler(ChatLanguageModel chatModel, AgentRegistry agentRegistry) {
        this.chatModel = chatModel;
        this.agentRegistry = agentRegistry;
        this.scheduler = Executors.newScheduledThreadPool(3);
        this.nightlyAgent = new NightlyDeploymentAgent(chatModel);
        this.deploymentWorkflow = new DeploymentWorkflow(chatModel);
        this.stagingSimulator = new StagingSimulator();
        this.selfImprovementEngine = new SelfImprovementEngine(agentRegistry, chatModel);
    }
    
    /**
     * Starts the deployment scheduler.
     */
    public void start() {
        if (isRunning) {
            log.warn("Deployment scheduler is already running");
            return;
        }
        
        log.info("Starting Deployment Scheduler");
        isRunning = true;
        
        // Schedule daily build & test at 2 AM
        scheduleDailyBuildAndTest();
        
        // Schedule weekly staging deploy simulation on Sunday
        scheduleWeeklyStagingSimulation();
        
        // Schedule monthly self-improvement deploy proposal
        scheduleMonthlySelfImprovement();
        
        log.info("Deployment Scheduler started successfully");
    }
    
    /**
     * Stops the deployment scheduler.
     */
    public void stop() {
        if (!isRunning) {
            log.warn("Deployment scheduler is not running");
            return;
        }
        
        log.info("Stopping Deployment Scheduler");
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        isRunning = false;
        log.info("Deployment Scheduler stopped");
    }
    
    /**
     * Schedules daily build & test at 2 AM.
     */
    private void scheduleDailyBuildAndTest() {
        log.info("Scheduling daily build & test at 2:00 AM");
        
        Runnable dailyTask = () -> {
            log.info("=".repeat(80));
            log.info("DAILY BUILD & TEST TASK STARTED");
            log.info("Time: {}", LocalDateTime.now());
            log.info("=".repeat(80));
            
            try {
                // Run nightly deployment agent
                String nightlyReport = nightlyAgent.runNightlyBuildTestHealthCheck("daily-" + LocalDateTime.now().toString());
                log.info("Nightly deployment agent completed");
                log.info("Report: {}", nightlyReport);
                
            } catch (Exception e) {
                log.error("Error during daily build & test task", e);
            }
        };
        
        // Calculate initial delay to next 2 AM
        long initialDelay = calculateDelayToNextTime(LocalTime.of(2, 0));
        
        // Schedule daily at 2 AM
        scheduler.scheduleAtFixedRate(dailyTask, initialDelay, 24, TimeUnit.HOURS);
        
        log.info("Daily build & test scheduled. Initial delay: {} hours", initialDelay / 3600);
    }
    
    /**
     * Schedules weekly staging deploy simulation on Sunday.
     */
    private void scheduleWeeklyStagingSimulation() {
        log.info("Scheduling weekly staging deploy simulation on Sunday");
        
        Runnable weeklyTask = () -> {
            log.info("=".repeat(80));
            log.info("WEEKLY STAGING DEPLOY SIMULATION STARTED");
            log.info("Time: {}", LocalDateTime.now());
            log.info("=".repeat(80));
            
            try {
                // Run staging simulation
                StagingSimulator.StagingSimulationResult result = stagingSimulator.simulateStaging(null);
                log.info("Staging simulation completed. Status: {}", result.getStatus());
                
                if (!result.getErrors().isEmpty()) {
                    log.warn("Staging simulation errors: {}", result.getErrors());
                }
                
                if (!result.getWarnings().isEmpty()) {
                    log.warn("Staging simulation warnings: {}", result.getWarnings());
                }
                
            } catch (Exception e) {
                log.error("Error during weekly staging simulation", e);
            }
        };
        
        // Calculate initial delay to next Sunday
        long initialDelay = calculateDelayToNextSunday();
        
        // Schedule weekly on Sunday
        scheduler.scheduleAtFixedRate(weeklyTask, initialDelay, 7, TimeUnit.DAYS);
        
        log.info("Weekly staging simulation scheduled. Initial delay: {} days", initialDelay / (24 * 3600));
    }
    
    /**
     * Schedules monthly self-improvement deploy proposal.
     */
    private void scheduleMonthlySelfImprovement() {
        log.info("Scheduling monthly self-improvement deploy proposal");
        
        Runnable monthlyTask = () -> {
            log.info("=".repeat(80));
            log.info("MONTHLY SELF-IMPROVEMENT DEPLOY PROPOSAL STARTED");
            log.info("Time: {}", LocalDateTime.now());
            log.info("=".repeat(80));
            
            try {
                // Run self-improvement cycle
                SelfImprovementEngine.ImprovementCycleResult result = 
                    selfImprovementEngine.executeCycle("monthly-" + LocalDateTime.now().toString());
                
                log.info("Self-improvement cycle completed. Status: {}", result.getStatus());
                log.info("Backend Health Score: {}", result.getBackendHealthScore());
                log.info("Frontend Health Score: {}", result.getFrontendHealthScore());
                
                // Generate proposal report
                String proposal = generateSelfImprovementProposal(result);
                log.info("Self-improvement proposal generated");
                
            } catch (Exception e) {
                log.error("Error during monthly self-improvement", e);
            }
        };
        
        // Calculate initial delay to first day of next month
        long initialDelay = calculateDelayToFirstOfMonth();
        
        // Schedule monthly (approximately 30 days)
        scheduler.scheduleAtFixedRate(monthlyTask, initialDelay, 30, TimeUnit.DAYS);
        
        log.info("Monthly self-improvement scheduled. Initial delay: {} days", initialDelay / (24 * 3600));
    }
    
    /**
     * Calculates delay to next occurrence of a specific time.
     */
    private long calculateDelayToNextTime(LocalTime targetTime) {
        LocalTime now = LocalTime.now();
        long delaySeconds;
        
        if (now.isBefore(targetTime)) {
            // Today
            delaySeconds = java.time.Duration.between(now, targetTime).getSeconds();
        } else {
            // Tomorrow
            delaySeconds = java.time.Duration.between(now, LocalTime.MAX).getSeconds() + 1 +
                          java.time.Duration.between(LocalTime.MIN, targetTime).getSeconds();
        }
        
        return delaySeconds;
    }
    
    /**
     * Calculates delay to next Sunday.
     */
    private long calculateDelayToNextSunday() {
        java.time.DayOfWeek today = LocalDateTime.now().getDayOfWeek();
        int daysUntilSunday = 7 - today.getValue(); // Sunday is 7
        
        if (daysUntilSunday == 7) {
            daysUntilSunday = 0; // Today is Sunday, schedule for next Sunday
        }
        
        return daysUntilSunday * 24 * 3600; // Convert to seconds
    }
    
    /**
     * Calculates delay to first day of next month.
     */
    private long calculateDelayToFirstOfMonth() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime firstOfNextMonth = now.plusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        
        return java.time.Duration.between(now, firstOfNextMonth).getSeconds();
    }
    
    /**
     * Generates self-improvement proposal report.
     */
    private String generateSelfImprovementProposal(SelfImprovementEngine.ImprovementCycleResult result) {
        StringBuilder proposal = new StringBuilder();
        proposal.append("=== SELF-IMPROVEMENT DEPLOY PROPOSAL ===\n");
        proposal.append("Generated: ").append(LocalDateTime.now()).append("\n\n");
        
        proposal.append("Status: ").append(result.getStatus()).append("\n");
        proposal.append("Backend Health: ").append(result.getBackendHealthScore()).append("/100\n");
        proposal.append("Frontend Health: ").append(result.getFrontendHealthScore()).append("/100\n\n");
        
        if (result.getAnalysisReport() != null) {
            proposal.append("Analysis Report:\n");
            String preview = result.getAnalysisReport().length() > 500 
                ? result.getAnalysisReport().substring(0, 500) + "..." 
                : result.getAnalysisReport();
            proposal.append(preview).append("\n\n");
        }
        
        proposal.append("Note: This is a proposal. Review and approve before deploying.\n");
        
        return proposal.toString();
    }
    
    /**
     * Checks if scheduler is running.
     */
    public boolean isRunning() {
        return isRunning;
    }
}



