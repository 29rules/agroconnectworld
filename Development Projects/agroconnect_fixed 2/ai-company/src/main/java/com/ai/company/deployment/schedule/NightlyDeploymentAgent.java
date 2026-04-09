package com.ai.company.deployment.schedule;

import com.ai.company.agents.cto.CTOAgent;
import com.ai.company.agents.supervisor.SupervisorAgent;
import com.ai.company.registry.AgentRegistry;
import com.ai.company.tools.code.TestRunnerTool;
import com.ai.company.tools.deployment.DockerBuildTool;
import com.ai.company.tools.deployment.HealthCheckTool;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Nightly Deployment Agent
 * 
 * Runs nightly build → test → health check pipeline.
 * 
 * This agent:
 * - Runs nightly builds for all services
 * - Executes test suites
 * - Performs health checks
 * - Reports issues to CTOAgent and SupervisorAgent
 * - Generates comprehensive nightly reports
 * 
 * ZERO-IMPACT MODE: This agent only builds, tests, and checks - never deploys.
 */
public class NightlyDeploymentAgent {
    
    private static final Logger log = LoggerFactory.getLogger(NightlyDeploymentAgent.class);
    
    private final NightlyDeploymentService nightlyService;
    private final DockerBuildTool dockerBuildTool;
    private final TestRunnerTool testRunnerTool;
    private final HealthCheckTool healthCheckTool;
    private final CTOAgent ctoAgent;
    private final SupervisorAgent supervisorAgent;
    
    public NightlyDeploymentAgent(ChatLanguageModel chatModel) {
        this.nightlyService = AiServices.builder(NightlyDeploymentService.class)
            .chatLanguageModel(chatModel)
            .chatMemoryProvider(sessionId -> MessageWindowChatMemory.withMaxMessages(50))
            .build();
        this.dockerBuildTool = new DockerBuildTool();
        this.testRunnerTool = new TestRunnerTool();
        this.healthCheckTool = new HealthCheckTool();
        this.ctoAgent = new CTOAgent(chatModel);
        this.supervisorAgent = new SupervisorAgent(chatModel);
    }
    
    /**
     * Runs nightly build → test → health check pipeline.
     * 
     * @param sessionId Session ID for tracking
     * @return Nightly report with all results
     */
    public String runNightlyBuildTestHealthCheck(String sessionId) {
        log.info("=".repeat(80));
        log.info("NIGHTLY DEPLOYMENT AGENT STARTED");
        log.info("Session ID: {}", sessionId);
        log.info("Time: {}", LocalDateTime.now());
        log.info("=".repeat(80));
        
        NightlyReport report = new NightlyReport();
        report.setSessionId(sessionId);
        report.setStartTime(LocalDateTime.now());
        
        try {
            // Step 1: Build all services
            log.info("STEP 1: Building all services");
            String buildResults = buildAllServices(report);
            report.setBuildResults(buildResults);
            
            // Step 2: Run tests
            log.info("STEP 2: Running tests");
            String testResults = runAllTests(report);
            report.setTestResults(testResults);
            
            // Step 3: Health checks
            log.info("STEP 3: Running health checks");
            String healthResults = runHealthChecks(report);
            report.setHealthResults(healthResults);
            
            // Step 4: Generate AI analysis
            log.info("STEP 4: Generating AI analysis");
            String aiAnalysis = generateAIAnalysis(report, sessionId);
            report.setAiAnalysis(aiAnalysis);
            
            // Step 5: Report issues to CTO and Supervisor
            log.info("STEP 5: Reporting issues to CTO and Supervisor");
            reportIssues(report, sessionId);
            
            report.setEndTime(LocalDateTime.now());
            report.setStatus("COMPLETED");
            
            log.info("=".repeat(80));
            log.info("NIGHTLY DEPLOYMENT AGENT COMPLETED");
            log.info("Status: {}", report.getStatus());
            log.info("Issues Found: {}", report.getIssues().size());
            log.info("=".repeat(80));
            
        } catch (Exception e) {
            log.error("Error during nightly deployment agent execution", e);
            report.setStatus("FAILED");
            report.addError("Nightly agent error: " + e.getMessage());
            report.setEndTime(LocalDateTime.now());
        }
        
        return formatReport(report);
    }
    
    /**
     * Builds all services.
     */
    private String buildAllServices(NightlyReport report) {
        StringBuilder results = new StringBuilder();
        results.append("=== BUILD RESULTS ===\n\n");
        
        String[] services = {"auth-service", "product-service", "gateway", "frontend"};
        
        for (String service : services) {
            log.info("Building service: {}", service);
            String buildResult;
            
            if ("frontend".equals(service)) {
                buildResult = dockerBuildTool.buildFrontend("nightly");
            } else if ("gateway".equals(service)) {
                buildResult = dockerBuildTool.buildGateway("nightly");
            } else {
                buildResult = dockerBuildTool.buildBackendService(service, "nightly");
            }
            
            results.append("Service: ").append(service).append("\n");
            results.append(buildResult).append("\n\n");
            
            if (buildResult.contains("ERROR") || buildResult.contains("FAILED")) {
                report.addIssue("Build failed for service: " + service);
            }
        }
        
        return results.toString();
    }
    
    /**
     * Runs all tests.
     */
    private String runAllTests(NightlyReport report) {
        StringBuilder results = new StringBuilder();
        results.append("=== TEST RESULTS ===\n\n");
        
        // Run backend tests
        log.info("Running backend tests");
        String backendTests = testRunnerTool.runBackendTests("backend");
        results.append("Backend Tests:\n").append(backendTests).append("\n\n");
        
        if (backendTests.contains("Exit Code: 1") || backendTests.contains("FAILED")) {
            report.addIssue("Backend tests failed");
        }
        
        // Run frontend tests
        log.info("Running frontend tests");
        String frontendTests = testRunnerTool.runFrontendTests();
        results.append("Frontend Tests:\n").append(frontendTests).append("\n\n");
        
        if (frontendTests.contains("Exit Code: 1") || frontendTests.contains("FAILED")) {
            report.addIssue("Frontend tests failed");
        }
        
        return results.toString();
    }
    
    /**
     * Runs health checks.
     */
    private String runHealthChecks(NightlyReport report) {
        StringBuilder results = new StringBuilder();
        results.append("=== HEALTH CHECK RESULTS ===\n\n");
        
        String[] healthUrls = {
            "http://localhost:8080/actuator/health",  // Gateway
            "http://localhost:8081/actuator/health", // Auth Service
            "http://localhost:8082/actuator/health"  // Product Service
        };
        
        int healthyCount = 0;
        int unhealthyCount = 0;
        
        for (String url : healthUrls) {
            String healthResult = healthCheckTool.checkServiceHealth(url);
            results.append(healthResult).append("\n\n");
            
            if (healthResult.contains("HEALTHY") || healthResult.contains("200")) {
                healthyCount++;
            } else {
                unhealthyCount++;
                report.addIssue("Service unhealthy: " + url);
            }
        }
        
        results.append("Summary: ").append(healthyCount).append(" healthy, ")
              .append(unhealthyCount).append(" unhealthy\n");
        
        return results.toString();
    }
    
    /**
     * Generates AI analysis of nightly results.
     */
    private String generateAIAnalysis(NightlyReport report, String sessionId) {
        String context = formatReportContext(report);
        return nightlyService.analyzeNightlyResults(context, sessionId);
    }
    
    /**
     * Reports issues to CTO and Supervisor.
     */
    private void reportIssues(NightlyReport report, String sessionId) {
        if (report.getIssues().isEmpty()) {
            log.info("No issues found - no reporting needed");
            return;
        }
        
        String issueReport = "Nightly deployment agent found " + report.getIssues().size() + " issues:\n" +
            String.join("\n", report.getIssues());
        
        // Report to CTO
        log.info("Reporting issues to CTO");
        String ctoReport = ctoAgent.reviewArchitecture(issueReport, sessionId);
        report.setCtoReport(ctoReport);
        
        // Report to Supervisor
        log.info("Reporting issues to Supervisor");
        SupervisorAgent.ConstraintEnforcement enforcement = 
            supervisorAgent.enforceConstraints(issueReport, sessionId);
        report.setSupervisorReport(enforcement.getReport());
    }
    
    /**
     * Formats report context for AI analysis.
     */
    private String formatReportContext(NightlyReport report) {
        StringBuilder context = new StringBuilder();
        context.append("=== NIGHTLY DEPLOYMENT REPORT ===\n\n");
        context.append("Session ID: ").append(report.getSessionId()).append("\n");
        context.append("Start Time: ").append(report.getStartTime()).append("\n");
        context.append("End Time: ").append(report.getEndTime()).append("\n");
        context.append("Status: ").append(report.getStatus()).append("\n\n");
        
        context.append("--- Build Results ---\n");
        context.append(report.getBuildResults()).append("\n");
        
        context.append("--- Test Results ---\n");
        context.append(report.getTestResults()).append("\n");
        
        context.append("--- Health Check Results ---\n");
        context.append(report.getHealthResults()).append("\n");
        
        if (!report.getIssues().isEmpty()) {
            context.append("--- Issues Found ---\n");
            report.getIssues().forEach(issue -> context.append("- ").append(issue).append("\n"));
            context.append("\n");
        }
        
        return context.toString();
    }
    
    /**
     * Formats final report.
     */
    private String formatReport(NightlyReport report) {
        StringBuilder formatted = new StringBuilder();
        formatted.append("=".repeat(80)).append("\n");
        formatted.append("NIGHTLY DEPLOYMENT REPORT\n");
        formatted.append("=".repeat(80)).append("\n\n");
        
        formatted.append("Session ID: ").append(report.getSessionId()).append("\n");
        formatted.append("Start Time: ").append(report.getStartTime()).append("\n");
        formatted.append("End Time: ").append(report.getEndTime()).append("\n");
        formatted.append("Status: ").append(report.getStatus()).append("\n\n");
        
        formatted.append("--- Build Results ---\n");
        formatted.append(report.getBuildResults()).append("\n");
        
        formatted.append("--- Test Results ---\n");
        formatted.append(report.getTestResults()).append("\n");
        
        formatted.append("--- Health Check Results ---\n");
        formatted.append(report.getHealthResults()).append("\n");
        
        if (!report.getIssues().isEmpty()) {
            formatted.append("--- Issues Found ---\n");
            report.getIssues().forEach(issue -> formatted.append("- ").append(issue).append("\n"));
            formatted.append("\n");
        }
        
        if (report.getAiAnalysis() != null) {
            formatted.append("--- AI Analysis ---\n");
            formatted.append(report.getAiAnalysis()).append("\n\n");
        }
        
        if (report.getCtoReport() != null) {
            formatted.append("--- CTO Report ---\n");
            formatted.append(report.getCtoReport()).append("\n\n");
        }
        
        if (report.getSupervisorReport() != null) {
            formatted.append("--- Supervisor Report ---\n");
            formatted.append(report.getSupervisorReport()).append("\n\n");
        }
        
        return formatted.toString();
    }
    
    /**
     * Nightly report data structure.
     */
    public static class NightlyReport {
        private String sessionId;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String status;
        private String buildResults;
        private String testResults;
        private String healthResults;
        private String aiAnalysis;
        private String ctoReport;
        private String supervisorReport;
        private List<String> issues;
        private List<String> errors;
        
        public NightlyReport() {
            this.issues = new ArrayList<>();
            this.errors = new ArrayList<>();
            this.status = "PENDING";
        }
        
        public void addIssue(String issue) {
            this.issues.add(issue);
        }
        
        public void addError(String error) {
            this.errors.add(error);
        }
        
        // Getters and Setters
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        public LocalDateTime getStartTime() { return startTime; }
        public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
        public LocalDateTime getEndTime() { return endTime; }
        public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getBuildResults() { return buildResults; }
        public void setBuildResults(String buildResults) { this.buildResults = buildResults; }
        public String getTestResults() { return testResults; }
        public void setTestResults(String testResults) { this.testResults = testResults; }
        public String getHealthResults() { return healthResults; }
        public void setHealthResults(String healthResults) { this.healthResults = healthResults; }
        public String getAiAnalysis() { return aiAnalysis; }
        public void setAiAnalysis(String aiAnalysis) { this.aiAnalysis = aiAnalysis; }
        public String getCtoReport() { return ctoReport; }
        public void setCtoReport(String ctoReport) { this.ctoReport = ctoReport; }
        public String getSupervisorReport() { return supervisorReport; }
        public void setSupervisorReport(String supervisorReport) { this.supervisorReport = supervisorReport; }
        public List<String> getIssues() { return issues; }
        public List<String> getErrors() { return errors; }
    }
    
    /**
     * LangChain4j AI Service interface for nightly deployment analysis.
     */
    interface NightlyDeploymentService {
        
        @SystemMessage("""
            You are a Nightly Deployment Agent responsible for analyzing nightly build, test, and health check results.
            
            Your role:
            - Analyze build results for failures and warnings
            - Analyze test results for failures and coverage issues
            - Analyze health check results for unhealthy services
            - Identify patterns and trends
            - Provide actionable recommendations
            - Prioritize issues by severity
            
            ANALYSIS AREAS:
            1. Build Failures:
               - Identify which services failed to build
               - Analyze build errors and warnings
               - Suggest fixes
            
            2. Test Failures:
               - Identify failing tests
               - Analyze test coverage
               - Suggest test improvements
            
            3. Health Check Issues:
               - Identify unhealthy services
               - Analyze health check failures
               - Suggest remediation steps
            
            4. Patterns and Trends:
               - Identify recurring issues
               - Track improvements or regressions
               - Highlight critical issues
            
            OUTPUT FORMAT: JSON with structure:
            {
              "analysis_timestamp": "ISO8601",
              "overall_status": "HEALTHY|WARNING|CRITICAL",
              "summary": "High-level summary",
              "build_analysis": {
                "status": "SUCCESS|FAILED|PARTIAL",
                "failed_services": ["list of services"],
                "warnings": ["list of warnings"],
                "recommendations": ["fix recommendations"]
              },
              "test_analysis": {
                "status": "PASSED|FAILED|PARTIAL",
                "failed_tests": ["list of tests"],
                "coverage_issues": ["coverage problems"],
                "recommendations": ["test improvements"]
              },
              "health_analysis": {
                "status": "HEALTHY|UNHEALTHY|PARTIAL",
                "unhealthy_services": ["list of services"],
                "health_issues": ["health problems"],
                "recommendations": ["health fixes"]
              },
              "critical_issues": [
                {
                  "priority": 1,
                  "issue": "Issue description",
                  "impact": "Impact description",
                  "recommendation": "How to fix"
                }
              ],
              "recommendations": [
                "Prioritized list of recommendations"
              ]
            }
            
            Be thorough and provide actionable insights.
            """)
        String analyzeNightlyResults(@UserMessage("""
            Analyze the following nightly deployment results:
            
            {{context}}
            
            Provide comprehensive analysis with recommendations.
            """) String context, @MemoryId String sessionId);
    }
}



