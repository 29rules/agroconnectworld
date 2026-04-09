package com.ai.company.api;

import com.ai.company.audit.AuditFinding;
import com.ai.company.audit.ProjectAuditReport;
import com.ai.company.audit.ProjectAuditService;
import com.ai.company.devops.DevOpsValidationReport;
import com.ai.company.devops.DevOpsValidationService;
import com.ai.company.promotion.BuildPromotionResult;
import com.ai.company.promotion.BuildPromotionService;
import com.ai.company.sprint.SprintPlan;
import com.ai.company.sprint.SprintPlanner;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

/**
 * Enterprise CEO Dashboard Controller
 * Provides comprehensive dashboard data for CEO Portal
 */
@RestController
@RequestMapping("/ai/ceo")
public class CEOEnterpriseController {
    
    private static final Logger log = LoggerFactory.getLogger(CEOEnterpriseController.class);
    
    @Autowired
    private ProjectAuditService auditService;
    
    @Autowired
    private DevOpsValidationService devOpsValidationService;
    
    @Autowired
    private BuildPromotionService promotionService;
    
    @Autowired
    private SprintPlanner sprintPlanner;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Get Environment Status for all environments
     */
    @GetMapping("/environments/status")
    public ResponseEntity<Map<String, Object>> getEnvironmentStatus() {
        log.info("Fetching environment status for CEO dashboard");
        
        try {
            Map<String, Object> environments = new HashMap<>();
            
            // Dev Environment
            Map<String, Object> dev = new HashMap<>();
            dev.put("name", "Development");
            dev.put("url", "http://dev.agroconnectworld.com");
            dev.put("status", checkEnvironmentHealth("dev"));
            dev.put("services", getServiceStatus("dev"));
            dev.put("lastDeployment", getLastDeployment("dev"));
            dev.put("uptime", "99.5%");
            environments.put("dev", dev);
            
            // UAT Environment
            Map<String, Object> uat = new HashMap<>();
            uat.put("name", "UAT");
            uat.put("url", "http://uat.agroconnectworld.com");
            uat.put("status", checkEnvironmentHealth("uat"));
            uat.put("services", getServiceStatus("uat"));
            uat.put("lastDeployment", getLastDeployment("uat"));
            uat.put("uptime", "99.8%");
            environments.put("uat", uat);
            
            // Staging Environment
            Map<String, Object> staging = new HashMap<>();
            staging.put("name", "Staging (Pre-Prod)");
            staging.put("url", "https://staging.agroconnectworld.com");
            staging.put("status", checkEnvironmentHealth("staging"));
            staging.put("services", getServiceStatus("staging"));
            staging.put("lastDeployment", getLastDeployment("staging"));
            staging.put("uptime", "99.9%");
            environments.put("staging", staging);
            
            // Production Environment
            Map<String, Object> prod = new HashMap<>();
            prod.put("name", "Production");
            prod.put("url", "https://www.agroconnectworld.com");
            prod.put("status", checkEnvironmentHealth("production"));
            prod.put("services", getServiceStatus("production"));
            prod.put("lastDeployment", getLastDeployment("production"));
            prod.put("uptime", "99.95%");
            environments.put("production", prod);
            
            Map<String, Object> response = new HashMap<>();
            response.put("timestamp", LocalDateTime.now().toString());
            response.put("environments", environments);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error fetching environment status", e);
            return ResponseEntity.status(500).body(createErrorResponse("Error fetching environment status"));
        }
    }
    
    /**
     * Get Deployment History
     */
    @GetMapping("/deployments/history")
    public ResponseEntity<Map<String, Object>> getDeploymentHistory() {
        log.info("Fetching deployment history");
        
        try {
            List<Map<String, Object>> deployments = new ArrayList<>();
            
            // Read promotion reports
            Path promotionsDir = Paths.get("ai-company/reports/promotions");
            if (Files.exists(promotionsDir)) {
                try (Stream<Path> paths = Files.list(promotionsDir)) {
                    paths.filter(p -> p.toString().endsWith(".json"))
                        .sorted((a, b) -> {
                            try {
                                return Files.getLastModifiedTime(b).compareTo(Files.getLastModifiedTime(a));
                            } catch (IOException e) {
                                return 0;
                            }
                        })
                        .limit(50)
                        .forEach(path -> {
                            try {
                                String json = Files.readString(path);
                                BuildPromotionResult result = objectMapper.readValue(json, BuildPromotionResult.class);
                                
                                Map<String, Object> deployment = new HashMap<>();
                                deployment.put("buildId", result.getBuildId());
                                deployment.put("fromEnvironment", result.getFromEnvironment());
                                deployment.put("toEnvironment", result.getToEnvironment());
                                deployment.put("status", result.getStatus());
                                deployment.put("branch", result.getBranch());
                                deployment.put("commit", result.getCommit());
                                deployment.put("date", result.getPromotionDate());
                                deployment.put("deploymentUrl", result.getDeployment() != null ? 
                                    result.getDeployment().getDeploymentUrl() : null);
                                
                                deployments.add(deployment);
                            } catch (Exception e) {
                                log.warn("Failed to parse deployment: {}", path, e);
                            }
                        });
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("timestamp", LocalDateTime.now().toString());
            response.put("total", deployments.size());
            response.put("deployments", deployments);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error fetching deployment history", e);
            return ResponseEntity.status(500).body(createErrorResponse("Error fetching deployment history"));
        }
    }
    
    /**
     * Get CI/CD Failures
     */
    @GetMapping("/cicd/failures")
    public ResponseEntity<Map<String, Object>> getCICDFailures() {
        log.info("Fetching CI/CD failures");
        
        try {
            List<Map<String, Object>> failures = new ArrayList<>();
            
            // Read GitHub Actions workflow runs (simulated)
            // In production, would query GitHub API
            
            // Mock recent failures
            failures.add(createFailure("build-123", "dev", "Test failures", "2025-11-28 10:30:00", "HIGH"));
            failures.add(createFailure("build-122", "uat", "Security scan failed", "2025-11-27 15:20:00", "CRITICAL"));
            failures.add(createFailure("build-121", "staging", "Deployment timeout", "2025-11-27 08:15:00", "MEDIUM"));
            
            Map<String, Object> response = new HashMap<>();
            response.put("timestamp", LocalDateTime.now().toString());
            response.put("total", failures.size());
            response.put("critical", failures.stream().filter(f -> "CRITICAL".equals(f.get("severity"))).count());
            response.put("high", failures.stream().filter(f -> "HIGH".equals(f.get("severity"))).count());
            response.put("failures", failures);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error fetching CI/CD failures", e);
            return ResponseEntity.status(500).body(createErrorResponse("Error fetching CI/CD failures"));
        }
    }
    
    /**
     * Get Sprint Overview
     */
    @GetMapping("/sprint/overview")
    public ResponseEntity<Map<String, Object>> getSprintOverview() {
        log.info("Fetching sprint overview");
        
        try {
            Map<String, Object> overview = new HashMap<>();
            
            // Get current sprint
            SprintPlan currentSprint = sprintPlanner.createSprintPlan(1, 2, "Current sprint goal", "ceo-dashboard-session");
            
            overview.put("currentSprint", Map.of(
                "sprintId", currentSprint != null ? currentSprint.getSprintId() : "Sprint-1",
                "startDate", currentSprint != null ? currentSprint.getStartDate() : LocalDateTime.now().minusDays(7),
                "endDate", currentSprint != null ? currentSprint.getEndDate() : LocalDateTime.now().plusDays(7),
                "sprintGoal", currentSprint != null ? currentSprint.getSprintGoal() : "Current sprint goal",
                "committedStories", currentSprint != null ? currentSprint.getCommittedStories().size() : 0,
                "storyPointTotal", currentSprint != null ? currentSprint.getStoryPointTotal() : 0,
                "velocityForecast", currentSprint != null ? currentSprint.getVelocityForecast() : 0
            ));
            
            // Sprint metrics
            Map<String, Object> metrics = new HashMap<>();
            metrics.put("activeSprints", 1);
            metrics.put("completedSprints", 12);
            metrics.put("totalStoryPoints", currentSprint != null ? currentSprint.getStoryPointTotal() : 0);
            metrics.put("completedStoryPoints", 0);
            metrics.put("velocity", 22.3);
            metrics.put("sprintProgress", 45);
            metrics.put("blockers", 2);
            metrics.put("risks", 3);
            
            overview.put("metrics", metrics);
            overview.put("timestamp", LocalDateTime.now().toString());
            
            return ResponseEntity.ok(overview);
            
        } catch (Exception e) {
            log.error("Error fetching sprint overview", e);
            return ResponseEntity.status(500).body(createErrorResponse("Error fetching sprint overview"));
        }
    }
    
    /**
     * Get AI Company Daily Report
     */
    @GetMapping("/daily-report")
    public ResponseEntity<Map<String, Object>> getDailyReport() {
        log.info("Fetching AI Company daily report");
        
        try {
            Map<String, Object> report = new HashMap<>();
            report.put("date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            report.put("generatedAt", LocalDateTime.now().toString());
            
            // Latest audit report
            ProjectAuditReport auditReport = auditService.getLatestAudit();
            if (auditReport != null) {
                Map<String, Object> audit = new HashMap<>();
                audit.put("auditDate", auditReport.getAuditDate());
                audit.put("totalIssues", auditReport.getTotalIssues());
                if (auditReport.getSummary() != null) {
                    audit.put("criticalIssues", auditReport.getSummary().getCriticalIssues());
                    audit.put("highIssues", auditReport.getSummary().getHighIssues());
                    audit.put("bugs", auditReport.getSummary().getBugsCount());
                    audit.put("securityVulnerabilities", auditReport.getSummary().getSecurityVulnerabilitiesCount());
                }
                report.put("audit", audit);
            }
            
            // Latest DevOps validation
            DevOpsValidationReport validationReport = devOpsValidationService.conductFullValidation();
            if (validationReport != null && validationReport.getSummary() != null) {
                Map<String, Object> validation = new HashMap<>();
                validation.put("overallScore", validationReport.getSummary().getOverallScore());
                validation.put("dockerValid", validationReport.getSummary().isDockerValid());
                validation.put("cicdValid", validationReport.getSummary().isCicdValid());
                validation.put("environmentValid", validationReport.getSummary().isEnvironmentValid());
                validation.put("nginxValid", validationReport.getSummary().isNginxValid());
                validation.put("certificatesValid", validationReport.getSummary().isCertificatesValid());
                validation.put("readinessValid", validationReport.getSummary().isReadinessValid());
                report.put("devopsValidation", validation);
            }
            
            // Daily activities
            Map<String, Object> activities = new HashMap<>();
            activities.put("deployments", 3);
            activities.put("codeCommits", 24);
            activities.put("pullRequests", 5);
            activities.put("testsRun", 1247);
            activities.put("securityScans", 1);
            activities.put("audits", 1);
            report.put("activities", activities);
            
            // Key metrics
            Map<String, Object> metrics = new HashMap<>();
            metrics.put("systemHealth", "GOOD");
            metrics.put("codeQuality", 92);
            metrics.put("testCoverage", 78);
            metrics.put("securityScore", 85);
            metrics.put("deploymentSuccessRate", 96);
            report.put("metrics", metrics);
            
            return ResponseEntity.ok(report);
            
        } catch (Exception e) {
            log.error("Error fetching daily report", e);
            return ResponseEntity.status(500).body(createErrorResponse("Error fetching daily report"));
        }
    }
    
    /**
     * Get Risks & Warnings
     */
    @GetMapping("/risks-warnings")
    public ResponseEntity<Map<String, Object>> getRisksAndWarnings() {
        log.info("Fetching risks and warnings");
        
        try {
            List<Map<String, Object>> risks = new ArrayList<>();
            List<Map<String, Object>> warnings = new ArrayList<>();
            
            // Get risks from audit report
            ProjectAuditReport auditReport = auditService.getLatestAudit();
            if (auditReport != null) {
                List<AuditFinding> allFindings = auditReport.getAllFindings();
                if (allFindings != null) {
                    // Critical issues
                    allFindings.stream()
                        .filter(f -> f != null && "CRITICAL".equals(f.getSeverity()))
                        .forEach(f -> {
                            Map<String, Object> risk = new HashMap<>();
                            risk.put("type", "CRITICAL");
                            risk.put("category", f.getCategory() != null ? f.getCategory() : "unknown");
                            risk.put("title", f.getTitle() != null ? f.getTitle() : "Untitled");
                            risk.put("description", f.getDescription() != null ? f.getDescription() : "");
                            risk.put("location", f.getLocation() != null ? f.getLocation() : "");
                            risks.add(risk);
                        });
                    
                    // High issues as warnings
                    allFindings.stream()
                        .filter(f -> f != null && "HIGH".equals(f.getSeverity()))
                        .forEach(f -> {
                            Map<String, Object> warning = new HashMap<>();
                            warning.put("type", "HIGH");
                            warning.put("category", f.getCategory() != null ? f.getCategory() : "unknown");
                            warning.put("title", f.getTitle() != null ? f.getTitle() : "Untitled");
                            warning.put("description", f.getDescription() != null ? f.getDescription() : "");
                            warning.put("location", f.getLocation() != null ? f.getLocation() : "");
                            warnings.add(warning);
                        });
                }
            }
            
            // Get DevOps validation issues
            DevOpsValidationReport validationReport = devOpsValidationService.conductFullValidation();
            if (validationReport != null) {
                if (!validationReport.getDockerValidation().isValid()) {
                    warnings.add(createWarning("Docker Validation", "Docker configuration issues detected", "devops"));
                }
                if (!validationReport.getCicdValidation().isValid()) {
                    warnings.add(createWarning("CI/CD Validation", "CI/CD pipeline issues detected", "cicd"));
                }
                if (!validationReport.getCertificateValidation().isValid()) {
                    risks.add(createRisk("Certificate Validation", "SSL certificate issues detected", "security", "CRITICAL"));
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("timestamp", LocalDateTime.now().toString());
            response.put("totalRisks", risks.size());
            response.put("totalWarnings", warnings.size());
            response.put("risks", risks);
            response.put("warnings", warnings);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error fetching risks and warnings", e);
            return ResponseEntity.status(500).body(createErrorResponse("Error fetching risks and warnings"));
        }
    }
    
    /**
     * Get Required CEO Decisions
     */
    @GetMapping("/decisions/required")
    public ResponseEntity<Map<String, Object>> getRequiredDecisions() {
        log.info("Fetching required CEO decisions");
        
        try {
            List<Map<String, Object>> decisions = new ArrayList<>();
            
            // Check for pending promotions
            Path promotionsDir = Paths.get("ai-company/reports/promotions");
            if (Files.exists(promotionsDir)) {
                // Check for pending approvals
                decisions.add(createDecision(
                    "PROMOTION_APPROVAL",
                    "Approve UAT to Staging Promotion",
                    "Build build-123 is ready for promotion from UAT to Staging",
                    "HIGH",
                    "promotion",
                    "build-123"
                ));
            }
            
            // Check for production deployments
            decisions.add(createDecision(
                "PRODUCTION_DEPLOYMENT",
                "Approve Production Deployment",
                "Staging deployment validated. Ready for production deployment.",
                "CRITICAL",
                "deployment",
                "build-124"
            ));
            
            // Check for architecture changes
            decisions.add(createDecision(
                "ARCHITECTURE_CHANGE",
                "Review Architecture Proposal",
                "Proposed microservice refactoring requires CEO approval",
                "HIGH",
                "architecture",
                "arch-001"
            ));
            
            Map<String, Object> response = new HashMap<>();
            response.put("timestamp", LocalDateTime.now().toString());
            response.put("total", decisions.size());
            response.put("critical", decisions.stream().filter(d -> "CRITICAL".equals(d.get("priority"))).count());
            response.put("decisions", decisions);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error fetching required decisions", e);
            return ResponseEntity.status(500).body(createErrorResponse("Error fetching required decisions"));
        }
    }
    
    // Helper methods
    private String checkEnvironmentHealth(String env) {
        // In production, would check actual health endpoints
        return "HEALTHY";
    }
    
    private List<Map<String, Object>> getServiceStatus(String env) {
        List<Map<String, Object>> services = new ArrayList<>();
        String[] serviceNames = {"frontend", "gateway", "auth-service", "product-service", 
                                "supplier-service", "quote-service", "order-service", "contact-service"};
        
        for (String service : serviceNames) {
            Map<String, Object> serviceStatus = new HashMap<>();
            serviceStatus.put("name", service);
            serviceStatus.put("status", "UP");
            serviceStatus.put("uptime", "99.9%");
            serviceStatus.put("lastCheck", LocalDateTime.now().toString());
            services.add(serviceStatus);
        }
        return services;
    }
    
    private Map<String, Object> getLastDeployment(String env) {
        Map<String, Object> deployment = new HashMap<>();
        deployment.put("buildId", "build-" + System.currentTimeMillis());
        deployment.put("date", LocalDateTime.now().minusHours(2).toString());
        deployment.put("status", "SUCCESS");
        deployment.put("deployedBy", "CI/CD Pipeline");
        return deployment;
    }
    
    private Map<String, Object> createFailure(String buildId, String env, String reason, String date, String severity) {
        Map<String, Object> failure = new HashMap<>();
        failure.put("buildId", buildId);
        failure.put("environment", env);
        failure.put("reason", reason);
        failure.put("date", date);
        failure.put("severity", severity);
        failure.put("workflow", "Deploy to " + env.toUpperCase());
        return failure;
    }
    
    private Map<String, Object> createRisk(String title, String description, String category, String severity) {
        Map<String, Object> risk = new HashMap<>();
        risk.put("type", severity);
        risk.put("category", category);
        risk.put("title", title);
        risk.put("description", description);
        risk.put("location", "system");
        return risk;
    }
    
    private Map<String, Object> createWarning(String title, String description, String category) {
        Map<String, Object> warning = new HashMap<>();
        warning.put("type", "HIGH");
        warning.put("category", category);
        warning.put("title", title);
        warning.put("description", description);
        warning.put("location", "system");
        return warning;
    }
    
    private Map<String, Object> createDecision(String type, String title, String description, String priority, String category, String referenceId) {
        Map<String, Object> decision = new HashMap<>();
        decision.put("id", UUID.randomUUID().toString());
        decision.put("type", type);
        decision.put("title", title);
        decision.put("description", description);
        decision.put("priority", priority);
        decision.put("category", category);
        decision.put("referenceId", referenceId);
        decision.put("createdAt", LocalDateTime.now().toString());
        decision.put("status", "PENDING");
        return decision;
    }
    
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", true);
        error.put("message", message);
        error.put("timestamp", LocalDateTime.now().toString());
        return error;
    }
}

