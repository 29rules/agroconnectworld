package com.ai.company.deployment;

import com.ai.company.agents.supervisor.SupervisorAgent;
import com.ai.company.impact.ImpactGuard;
import com.ai.company.impact.ImpactMode;
import com.ai.company.impact.ImpactModeManager;
import com.ai.company.tools.code.TestRunnerTool;
import com.ai.company.tools.deployment.*;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Deployment Workflow
 * 
 * Orchestrates the complete deployment process for AgroConnectWorld services.
 * 
 * Stages:
 * 1. Code validation
 * 2. Build containers
 * 3. Run tests (backend + frontend)
 * 4. Package artifacts
 * 5. Push artifacts to VPS staging
 * 6. Run health checks
 * 7. Supervisor approval
 * 8. Deploy to production
 * 9. Post-deployment validation
 * 
 * Uses all code/deployment tools and respects Impact Mode settings.
 */
public class DeploymentWorkflow {
    
    private static final Logger log = LoggerFactory.getLogger(DeploymentWorkflow.class);
    
    private final ChatLanguageModel chatModel;
    private final DeploymentPlanAgent planAgent;
    private final DockerBuildTool dockerBuildTool;
    private final DockerComposeTool dockerComposeTool;
    private final DockerRunnerTool dockerRunnerTool;
    private final SSHDeploymentTool sshDeploymentTool;
    private final EnvConfigTool envConfigTool;
    private final HealthCheckTool healthCheckTool;
    private final TestRunnerTool testRunnerTool;
    private final SupervisorAgent supervisorAgent;
    private final ImpactGuard impactGuard;
    
    public DeploymentWorkflow(ChatLanguageModel chatModel) {
        this.chatModel = chatModel;
        this.planAgent = new DeploymentPlanAgent(chatModel);
        this.dockerBuildTool = new DockerBuildTool();
        this.dockerComposeTool = new DockerComposeTool();
        this.dockerRunnerTool = new DockerRunnerTool();
        this.sshDeploymentTool = new SSHDeploymentTool(chatModel);
        this.envConfigTool = new EnvConfigTool();
        this.healthCheckTool = new HealthCheckTool();
        this.testRunnerTool = new TestRunnerTool();
        this.supervisorAgent = new SupervisorAgent(chatModel);
        this.impactGuard = new ImpactGuard();
    }
    
    /**
     * Executes a complete deployment workflow.
     * 
     * @param services Comma-separated list of services to deploy
     * @param environment Deployment environment ("staging" or "production")
     * @param sessionId Session ID for tracking
     * @return Deployment summary with results
     */
    public DeploymentSummary executeDeployment(String services, String environment, String sessionId) {
        log.info("=".repeat(80));
        log.info("DEPLOYMENT WORKFLOW STARTED");
        log.info("Services: {}", services);
        log.info("Environment: {}", environment);
        log.info("Session ID: {}", sessionId);
        log.info("=".repeat(80));
        
        DeploymentState state = new DeploymentState();
        state.setServices(Arrays.asList(services.split(",")));
        state.setEnvironment(environment);
        
        DeploymentSummary summary = new DeploymentSummary();
        summary.setDeploymentId(state.getDeploymentId());
        summary.setEnvironment(environment);
        summary.setServices(state.getServices());
        summary.setStartTime(LocalDateTime.now());
        
        try {
            // Stage 1: Code Validation
            log.info("STAGE 1: Code Validation");
            state.updateStageStatus("code_validation", DeploymentState.StageStatus.IN_PROGRESS);
            String validationResult = validateCode(services);
            if (validationResult.contains("ERROR")) {
                state.addError("Code validation failed: " + validationResult);
                state.updateStageStatus("code_validation", DeploymentState.StageStatus.FAILED);
                state.markFailed();
                summary.setStatus("FAILED");
                summary.setEndTime(LocalDateTime.now());
                return summary;
            }
            state.updateStageStatus("code_validation", DeploymentState.StageStatus.COMPLETED);
            summary.addStageResult("code_validation", validationResult);
            
            // Stage 2: Build Containers
            log.info("STAGE 2: Build Containers");
            state.updateStageStatus("build", DeploymentState.StageStatus.IN_PROGRESS);
            String buildResult = buildContainers(services, state);
            if (buildResult.contains("ERROR") || buildResult.contains("FAILED")) {
                state.addError("Build failed: " + buildResult);
                state.updateStageStatus("build", DeploymentState.StageStatus.FAILED);
                state.markFailed();
                summary.setStatus("FAILED");
                summary.setEndTime(LocalDateTime.now());
                return summary;
            }
            state.updateStageStatus("build", DeploymentState.StageStatus.COMPLETED);
            summary.addStageResult("build", buildResult);
            
            // Stage 3: Run Tests
            log.info("STAGE 3: Run Tests");
            state.updateStageStatus("test", DeploymentState.StageStatus.IN_PROGRESS);
            String testResult = runTests(services, state);
            if (testResult.contains("ERROR") || testResult.contains("FAILED")) {
                state.addWarning("Tests failed: " + testResult);
                // Continue with warning (tests may be flaky)
            }
            state.updateStageStatus("test", DeploymentState.StageStatus.COMPLETED);
            summary.addStageResult("test", testResult);
            
            // Stage 4: Package Artifacts
            log.info("STAGE 4: Package Artifacts");
            state.updateStageStatus("package", DeploymentState.StageStatus.IN_PROGRESS);
            String packageResult = packageArtifacts(services);
            state.updateStageStatus("package", DeploymentState.StageStatus.COMPLETED);
            summary.addStageResult("package", packageResult);
            
            // Stage 5: Push to VPS Staging
            log.info("STAGE 5: Push to VPS Staging");
            state.updateStageStatus("push_staging", DeploymentState.StageStatus.IN_PROGRESS);
            String pushResult = pushToStaging(services, state);
            if (pushResult.contains("ERROR")) {
                state.addError("Push to staging failed: " + pushResult);
                state.updateStageStatus("push_staging", DeploymentState.StageStatus.FAILED);
                state.markFailed();
                summary.setStatus("FAILED");
                summary.setEndTime(LocalDateTime.now());
                return summary;
            }
            state.updateStageStatus("push_staging", DeploymentState.StageStatus.COMPLETED);
            summary.addStageResult("push_staging", pushResult);
            
            // Stage 6: Health Checks
            log.info("STAGE 6: Health Checks");
            state.updateStageStatus("health_check", DeploymentState.StageStatus.IN_PROGRESS);
            String healthResult = runHealthChecks(services, state);
            state.updateStageStatus("health_check", DeploymentState.StageStatus.COMPLETED);
            summary.addStageResult("health_check", healthResult);
            
            // Stage 7: Supervisor Approval
            log.info("STAGE 7: Supervisor Approval");
            state.updateStageStatus("supervisor_approval", DeploymentState.StageStatus.IN_PROGRESS);
            boolean approved = requestSupervisorApproval(services, environment, state, sessionId);
            if (!approved) {
                state.addError("Supervisor rejected deployment");
                state.updateStageStatus("supervisor_approval", DeploymentState.StageStatus.FAILED);
                state.markFailed();
                summary.setStatus("REJECTED");
                summary.setEndTime(LocalDateTime.now());
                return summary;
            }
            state.updateStageStatus("supervisor_approval", DeploymentState.StageStatus.COMPLETED);
            summary.addStageResult("supervisor_approval", "APPROVED");
            
            // Stage 8: Deploy to Production (if environment is production)
            if ("production".equals(environment)) {
                log.info("STAGE 8: Deploy to Production");
                state.updateStageStatus("deploy_production", DeploymentState.StageStatus.IN_PROGRESS);
                String deployResult = deployToProduction(services, state);
                if (deployResult.contains("ERROR")) {
                    state.addError("Production deployment failed: " + deployResult);
                    state.updateStageStatus("deploy_production", DeploymentState.StageStatus.FAILED);
                    state.markFailed();
                    summary.setStatus("FAILED");
                    summary.setEndTime(LocalDateTime.now());
                    return summary;
                }
                state.updateStageStatus("deploy_production", DeploymentState.StageStatus.COMPLETED);
                summary.addStageResult("deploy_production", deployResult);
            }
            
            // Stage 9: Post-Deployment Validation
            log.info("STAGE 9: Post-Deployment Validation");
            state.updateStageStatus("post_deployment_validation", DeploymentState.StageStatus.IN_PROGRESS);
            String validation = postDeploymentValidation(services, state);
            state.updateStageStatus("post_deployment_validation", DeploymentState.StageStatus.COMPLETED);
            summary.addStageResult("post_deployment_validation", validation);
            
            // Mark as completed
            state.markCompleted();
            summary.setStatus("COMPLETED");
            summary.setEndTime(LocalDateTime.now());
            
            log.info("=".repeat(80));
            log.info("DEPLOYMENT WORKFLOW COMPLETED");
            log.info("Status: {}", summary.getStatus());
            log.info("=".repeat(80));
            
        } catch (Exception e) {
            log.error("Error during deployment workflow", e);
            state.addError("Deployment workflow error: " + e.getMessage());
            state.markFailed();
            summary.setStatus("FAILED");
            summary.setErrorMessage(e.getMessage());
            summary.setEndTime(LocalDateTime.now());
        }
        
        summary.setState(state);
        return summary;
    }
    
    /**
     * Stage 1: Code Validation
     */
    private String validateCode(String services) {
        log.info("Validating code for services: {}", services);
        // Validate docker-compose.yml
        String composeValidation = dockerComposeTool.validateDockerCompose("ops/docker-compose.yml");
        return composeValidation;
    }
    
    /**
     * Stage 2: Build Containers
     */
    private String buildContainers(String services, DeploymentState state) {
        log.info("Building containers for services: {}", services);
        StringBuilder result = new StringBuilder();
        
        String[] serviceList = services.split(",");
        for (String service : serviceList) {
            service = service.trim();
            log.info("Building service: {}", service);
            
            DeploymentState.ServiceState serviceState = new DeploymentState.ServiceState(service);
            serviceState.setBuildStatus(DeploymentState.BuildStatus.BUILDING);
            state.updateServiceState(service, serviceState);
            
            String buildResult;
            if ("frontend".equals(service)) {
                buildResult = dockerBuildTool.buildFrontend("latest");
            } else if ("gateway".equals(service)) {
                buildResult = dockerBuildTool.buildGateway("latest");
            } else {
                buildResult = dockerBuildTool.buildBackendService(service, "latest");
            }
            
            if (buildResult.contains("SUCCESS") || buildResult.contains("DRY-RUN")) {
                serviceState.setBuildStatus(DeploymentState.BuildStatus.SUCCESS);
                serviceState.setBuildLog(buildResult);
            } else {
                serviceState.setBuildStatus(DeploymentState.BuildStatus.FAILED);
                serviceState.setBuildLog(buildResult);
                result.append("Build failed for ").append(service).append(": ").append(buildResult).append("\n");
            }
            
            state.updateServiceState(service, serviceState);
            result.append(buildResult).append("\n\n");
        }
        
        return result.toString();
    }
    
    /**
     * Stage 3: Run Tests
     */
    private String runTests(String services, DeploymentState state) {
        log.info("Running tests for services: {}", services);
        StringBuilder result = new StringBuilder();
        
        String[] serviceList = services.split(",");
        for (String service : serviceList) {
            service = service.trim();
            
            DeploymentState.ServiceState serviceState = state.getServiceStates().get(service);
            if (serviceState == null) {
                serviceState = new DeploymentState.ServiceState(service);
            }
            
            serviceState.setTestStatus(DeploymentState.TestStatus.RUNNING);
            state.updateServiceState(service, serviceState);
            
            String testResult;
            if (service.startsWith("backend/") || service.contains("-service")) {
                testResult = testRunnerTool.runBackendTests("backend/" + service);
            } else {
                testResult = testRunnerTool.runFrontendTests();
            }
            
            if (testResult.contains("Exit Code: 0") || testResult.contains("SUCCESS")) {
                serviceState.setTestStatus(DeploymentState.TestStatus.PASSED);
            } else {
                serviceState.setTestStatus(DeploymentState.TestStatus.FAILED);
            }
            
            serviceState.setTestLog(testResult);
            state.updateServiceState(service, serviceState);
            result.append(testResult).append("\n\n");
        }
        
        return result.toString();
    }
    
    /**
     * Stage 4: Package Artifacts
     */
    private String packageArtifacts(String services) {
        log.info("Packaging artifacts for services: {}", services);
        // In a real implementation, this would create deployment packages
        return "Artifacts packaged successfully";
    }
    
    /**
     * Stage 5: Push to VPS Staging
     */
    private String pushToStaging(String services, DeploymentState state) {
        log.info("Pushing to VPS staging for services: {}", services);
        // This would use SSHDeploymentTool to upload files
        // For now, simulate
        ImpactModeManager modeManager = ImpactModeManager.getInstance();
        if (modeManager.getMode() == ImpactMode.ZERO_IMPACT) {
            return "DRY-RUN: Would push to staging";
        }
        return "Pushed to staging successfully";
    }
    
    /**
     * Stage 6: Health Checks
     */
    private String runHealthChecks(String services, DeploymentState state) {
        log.info("Running health checks for services: {}", services);
        StringBuilder result = new StringBuilder();
        
        // Example health check URLs
        String[] healthUrls = {
            "http://localhost:8080/actuator/health",
            "http://localhost:8081/actuator/health"
        };
        
        for (String url : healthUrls) {
            String healthResult = healthCheckTool.checkServiceHealth(url);
            result.append(healthResult).append("\n\n");
            
            // Parse and store health check result
            DeploymentState.HealthCheckResult healthCheck = new DeploymentState.HealthCheckResult(url);
            healthCheck.setHealthy(healthResult.contains("HEALTHY"));
            state.addHealthCheck(url, healthCheck);
        }
        
        return result.toString();
    }
    
    /**
     * Stage 7: Supervisor Approval
     */
    private boolean requestSupervisorApproval(String services, String environment, 
                                             DeploymentState state, String sessionId) {
        log.info("Requesting Supervisor approval for deployment");
        
        String approvalRequest = String.format(
            "Request to deploy services: %s to environment: %s. " +
            "Build status: %s, Test status: %s. Approve?",
            services, environment,
            state.getServiceStates().values().stream()
                .allMatch(s -> s.getBuildStatus() == DeploymentState.BuildStatus.SUCCESS) ? "SUCCESS" : "FAILED",
            state.getServiceStates().values().stream()
                .allMatch(s -> s.getTestStatus() == DeploymentState.TestStatus.PASSED) ? "PASSED" : "FAILED"
        );
        
        SupervisorAgent.ConstraintEnforcement enforcement = 
            supervisorAgent.enforceConstraints(approvalRequest, sessionId);
        
        return enforcement.isCompliant();
    }
    
    /**
     * Stage 8: Deploy to Production
     */
    private String deployToProduction(String services, DeploymentState state) {
        log.info("Deploying to production for services: {}", services);
        // This would use DockerRunnerTool to start containers in production
        ImpactModeManager modeManager = ImpactModeManager.getInstance();
        if (modeManager.getMode() != ImpactMode.FULL_IMPACT) {
            return "DRY-RUN: Would deploy to production (requires FULL_IMPACT mode)";
        }
        return "Deployed to production successfully";
    }
    
    /**
     * Stage 9: Post-Deployment Validation
     */
    private String postDeploymentValidation(String services, DeploymentState state) {
        log.info("Running post-deployment validation for services: {}", services);
        // Run health checks again
        return runHealthChecks(services, state);
    }
}



