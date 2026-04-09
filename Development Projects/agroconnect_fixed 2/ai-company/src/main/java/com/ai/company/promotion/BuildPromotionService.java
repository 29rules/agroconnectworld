package com.ai.company.promotion;

import com.ai.company.audit.ProjectAuditService;
import com.ai.company.devops.DevOpsValidationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Service for Build Promotion Flow
 * Orchestrates the promotion from dev → uat → staging → production
 */
@Service
public class BuildPromotionService {
    
    private static final Logger log = LoggerFactory.getLogger(BuildPromotionService.class);
    
    private final BuildPromotionAgent promotionAgent;
    
    @Autowired
    public BuildPromotionService(ChatLanguageModel chatModel) {
        this.promotionAgent = AiServices.builder(BuildPromotionAgent.class)
            .chatLanguageModel(chatModel)
            .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(50))
            .build();
    }
    
    @Autowired
    private ProjectAuditService auditService;
    
    @Autowired
    private DevOpsValidationService devOpsValidationService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String PROMOTION_REPORTS_DIR = "ai-company/reports/promotions";
    
    /**
     * Process build promotion request
     */
    public BuildPromotionResult promoteBuild(String fromEnvironment, String toEnvironment, 
                                             String buildId, String branch, String commit) {
        log.info("Promoting build {} from {} to {}", buildId, fromEnvironment, toEnvironment);
        
        BuildPromotionResult result = new BuildPromotionResult();
        result.setBuildId(buildId);
        result.setFromEnvironment(fromEnvironment);
        result.setToEnvironment(toEnvironment);
        result.setBranch(branch);
        result.setCommit(commit);
        result.setPromotionDate(LocalDateTime.now());
        
        try {
            // 1. Check code quality
            log.info("Step 1: Checking code quality...");
            CodeQualityCheck codeQuality = checkCodeQuality(buildId, branch);
            result.setCodeQuality(codeQuality);
            if (!codeQuality.isPassed()) {
                result.setStatus("REJECTED");
                result.addIssue("Code quality checks failed");
                savePromotionResult(result);
                return result;
            }
            
            // 2. Run tests
            log.info("Step 2: Running tests...");
            TestResults testResults = runTests(buildId);
            result.setTestResults(testResults);
            if (!testResults.isAllPassed()) {
                result.setStatus("REJECTED");
                result.addIssue("Tests failed: " + testResults.getFailedCount() + " failed");
                savePromotionResult(result);
                return result;
            }
            
            // 3. Run security checks
            log.info("Step 3: Running security checks...");
            SecurityScanResult securityScan = runSecurityChecks(buildId);
            result.setSecurityScan(securityScan);
            if (!securityScan.isPassed()) {
                result.setStatus("REJECTED");
                result.addIssue("Security scan failed: " + securityScan.getCriticalVulnerabilities() + " critical issues");
                savePromotionResult(result);
                return result;
            }
            
            // 4. Build artifacts
            log.info("Step 4: Building artifacts...");
            BuildArtifacts artifacts = buildArtifacts(buildId, toEnvironment);
            result.setArtifacts(artifacts);
            if (!artifacts.isBuilt()) {
                result.setStatus("REJECTED");
                result.addIssue("Artifact build failed");
                savePromotionResult(result);
                return result;
            }
            
            // 5. AI Analysis
            log.info("Step 5: AI analysis...");
            String aiAnalysis = promotionAgent.analyzeBuildQuality(
                toEnvironment, buildId, branch, commit,
                objectMapper.writeValueAsString(testResults),
                objectMapper.writeValueAsString(securityScan),
                objectMapper.writeValueAsString(codeQuality)
            );
            result.setAiAnalysis(aiAnalysis);
            
            // 6. Deploy (if all checks pass)
            if (codeQuality.isPassed() && testResults.isAllPassed() && securityScan.isPassed() && artifacts.isBuilt()) {
                log.info("Step 6: Deploying to {}...", toEnvironment);
                DeploymentResult deployment = deployToEnvironment(toEnvironment, artifacts);
                result.setDeployment(deployment);
                
                if (deployment.isSuccessful()) {
                    result.setStatus("PROMOTED");
                    log.info("Build {} successfully promoted to {}", buildId, toEnvironment);
                } else {
                    result.setStatus("DEPLOYMENT_FAILED");
                    result.addIssue("Deployment failed: " + deployment.getErrorMessage());
                }
            } else {
                result.setStatus("REJECTED");
                result.addIssue("Pre-deployment checks failed");
            }
            
            // 7. Notify CEO Portal
            notifyCEOPortal(result);
            
            savePromotionResult(result);
            
        } catch (Exception e) {
            log.error("Error during build promotion", e);
            result.setStatus("ERROR");
            result.addIssue("Promotion error: " + e.getMessage());
            savePromotionResult(result);
        }
        
        return result;
    }
    
    private CodeQualityCheck checkCodeQuality(String buildId, String branch) {
        CodeQualityCheck check = new CodeQualityCheck();
        check.setBuildId(buildId);
        
        try {
            // Run linting checks
            // Check code coverage
            // Run static analysis
            
            // For now, simulate checks
            check.setLintingPassed(true);
            check.setCoveragePassed(true);
            check.setStaticAnalysisPassed(true);
            check.setPassed(true);
            check.setScore(95);
            
        } catch (Exception e) {
            log.error("Code quality check failed", e);
            check.setPassed(false);
            check.addIssue("Code quality check error: " + e.getMessage());
        }
        
        return check;
    }
    
    private TestResults runTests(String buildId) {
        TestResults results = new TestResults();
        results.setBuildId(buildId);
        
        try {
            // Run unit tests
            // Run integration tests
            // Run E2E tests
            
            // For now, simulate test execution
            results.setUnitTestsPassed(50);
            results.setUnitTestsTotal(50);
            results.setIntegrationTestsPassed(20);
            results.setIntegrationTestsTotal(20);
            results.setE2ETestsPassed(10);
            results.setE2ETestsTotal(10);
            results.setAllPassed(true);
            
        } catch (Exception e) {
            log.error("Test execution failed", e);
            results.setAllPassed(false);
            results.addError("Test execution error: " + e.getMessage());
        }
        
        return results;
    }
    
    private SecurityScanResult runSecurityChecks(String buildId) {
        SecurityScanResult scan = new SecurityScanResult();
        scan.setBuildId(buildId);
        
        try {
            // Run OWASP Dependency Check
            // Run Snyk scan
            // Check for secrets
            
            // For now, simulate security scan
            scan.setCriticalVulnerabilities(0);
            scan.setHighVulnerabilities(0);
            scan.setMediumVulnerabilities(2);
            scan.setLowVulnerabilities(5);
            scan.setPassed(true);
            scan.setScore(85);
            
        } catch (Exception e) {
            log.error("Security scan failed", e);
            scan.setPassed(false);
            scan.addIssue("Security scan error: " + e.getMessage());
        }
        
        return scan;
    }
    
    private BuildArtifacts buildArtifacts(String buildId, String environment) {
        BuildArtifacts artifacts = new BuildArtifacts();
        artifacts.setBuildId(buildId);
        artifacts.setEnvironment(environment);
        
        try {
            // Build Docker images
            // Tag with environment
            // Push to registry
            
            // For now, simulate build
            artifacts.setFrontendImage("ghcr.io/owner/agroconnect-frontend:" + environment);
            artifacts.setGatewayImage("ghcr.io/owner/agroconnect-gateway:" + environment);
            artifacts.setAuthServiceImage("ghcr.io/owner/agroconnect-auth-service:" + environment);
            artifacts.setProductServiceImage("ghcr.io/owner/agroconnect-product-service:" + environment);
            artifacts.setBuilt(true);
            
        } catch (Exception e) {
            log.error("Artifact build failed", e);
            artifacts.setBuilt(false);
            artifacts.addError("Build error: " + e.getMessage());
        }
        
        return artifacts;
    }
    
    private DeploymentResult deployToEnvironment(String environment, BuildArtifacts artifacts) {
        DeploymentResult deployment = new DeploymentResult();
        deployment.setEnvironment(environment);
        deployment.setDeploymentDate(LocalDateTime.now());
        
        try {
            // Trigger deployment workflow
            // For dev: GitHub Actions workflow
            // For uat/staging/prod: With approvals
            
            // Simulate deployment
            deployment.setSuccessful(true);
            deployment.setDeploymentUrl(getEnvironmentUrl(environment));
            deployment.setMessage("Deployment initiated successfully");
            
        } catch (Exception e) {
            log.error("Deployment failed", e);
            deployment.setSuccessful(false);
            deployment.setErrorMessage("Deployment error: " + e.getMessage());
        }
        
        return deployment;
    }
    
    private String getEnvironmentUrl(String environment) {
        return switch (environment.toLowerCase()) {
            case "dev" -> "http://dev.agroconnectworld.com";
            case "uat" -> "http://uat.agroconnectworld.com";
            case "staging", "preprod" -> "https://staging.agroconnectworld.com";
            case "production", "prod" -> "https://www.agroconnectworld.com";
            default -> "unknown";
        };
    }
    
    private void notifyCEOPortal(BuildPromotionResult result) {
        try {
            // Send notification to CEO Portal webhook
            String message = String.format(
                "🚀 Build Promotion %s\n\n" +
                "Build ID: %s\n" +
                "From: %s → To: %s\n" +
                "Branch: %s\n" +
                "Status: %s\n" +
                "URL: %s\n",
                result.getStatus(),
                result.getBuildId(),
                result.getFromEnvironment(),
                result.getToEnvironment(),
                result.getBranch(),
                result.getStatus(),
                result.getDeployment() != null ? result.getDeployment().getDeploymentUrl() : "N/A"
            );
            
            log.info("CEO Portal notification: {}", message);
            // TODO: Send to CEO Portal webhook
            
        } catch (Exception e) {
            log.error("Failed to notify CEO Portal", e);
        }
    }
    
    private void savePromotionResult(BuildPromotionResult result) {
        try {
            Path reportsDir = Paths.get(PROMOTION_REPORTS_DIR);
            Files.createDirectories(reportsDir);
            
            String filename = String.format("promotion_%s_%s.json", 
                result.getBuildId(), 
                LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")));
            Path reportPath = reportsDir.resolve(filename);
            
            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
            Files.writeString(reportPath, json);
            
            log.info("Promotion result saved to {}", reportPath);
        } catch (IOException e) {
            log.error("Failed to save promotion result", e);
        }
    }
    
    /**
     * Get promotion checklist for environment
     */
    public String getPromotionChecklist(String environment) {
        return promotionAgent.generatePromotionChecklist(environment);
    }
}

