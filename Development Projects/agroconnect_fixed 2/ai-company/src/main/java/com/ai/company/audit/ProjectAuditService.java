package com.ai.company.audit;

import com.ai.company.tools.code.CodeReaderTool;
import com.ai.company.tools.deployment.HealthCheckTool;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Service that orchestrates full project audits
 * Runs every 24 hours and generates comprehensive audit reports
 */
@Service
public class ProjectAuditService {
    
    private static final Logger log = LoggerFactory.getLogger(ProjectAuditService.class);
    
    private final ProjectAuditAgent auditAgent;
    private final CodeReaderTool codeReader;
    private final HealthCheckTool healthCheckTool;
    
    @Autowired
    public ProjectAuditService(ChatLanguageModel chatModel, CodeReaderTool codeReader, HealthCheckTool healthCheckTool) {
        this.auditAgent = AiServices.builder(ProjectAuditAgent.class)
            .chatLanguageModel(chatModel)
            .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(50))
            .build();
        this.codeReader = codeReader;
        this.healthCheckTool = healthCheckTool;
    }
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String AUDIT_REPORTS_DIR = "ai-company/reports/audits";
    
    /**
     * Scheduled audit - runs every 24 hours at 2 AM
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void scheduledFullAudit() {
        log.info("Starting scheduled full project audit...");
        try {
            ProjectAuditReport report = conductFullAudit();
            saveAuditReport(report);
            log.info("Scheduled audit completed successfully");
        } catch (Exception e) {
            log.error("Scheduled audit failed", e);
        }
    }
    
    /**
     * Conduct comprehensive project audit
     */
    public ProjectAuditReport conductFullAudit() {
        log.info("Conducting full project audit...");
        
        ProjectAuditReport report = new ProjectAuditReport();
        report.setAuditDate(LocalDateTime.now());
        report.setAuditId(UUID.randomUUID().toString());
        
        try {
            // 1. Read codebase
            String frontendCode = readCodebase("frontend/src");
            String backendCode = readCodebase("backend");
            String apiSpecs = readAPISpecs();
            
            // 2. Audit Bugs
            log.info("Auditing bugs...");
            String bugsJson = auditAgent.auditBugs(backendCode + "\n" + frontendCode);
            report.setBugs(parseFindings(bugsJson, "bugs"));
            
            // 3. Audit UI Issues
            log.info("Auditing UI issues...");
            String uiJson = auditAgent.auditUI(frontendCode);
            report.setUiIssues(parseFindings(uiJson, "ui_issues"));
            
            // 4. Audit Backend Errors
            log.info("Auditing backend errors...");
            report.setBackendErrors(checkBackendHealth());
            
            // 5. Audit API Links
            log.info("Auditing API links...");
            String apiJson = auditAgent.auditAPIs(apiSpecs);
            report.setBrokenApiLinks(parseFindings(apiJson, "broken_apis"));
            
            // 6. Audit Tests
            log.info("Auditing test coverage...");
            String testCode = readCodebase("backend") + "\n" + readCodebase("frontend/src");
            String testsJson = auditAgent.auditTests(testCode);
            report.setMissingTests(parseFindings(testsJson, "missing_tests"));
            
            // 7. Audit Performance
            log.info("Auditing performance...");
            String perfJson = auditAgent.auditPerformance(backendCode);
            report.setPerformanceProblems(parseFindings(perfJson, "performance"));
            
            // 8. Audit Security
            log.info("Auditing security...");
            String securityJson = auditAgent.auditSecurity(backendCode + "\n" + frontendCode);
            report.setSecurityVulnerabilities(parseFindings(securityJson, "security"));
            
            // 9. Generate summary
            report.generateSummary();
            
            log.info("Full audit completed. Found {} total issues", report.getTotalIssues());
            
        } catch (Exception e) {
            log.error("Error during audit", e);
            report.addError("Audit execution failed: " + e.getMessage());
        }
        
        return report;
    }
    
    private String readCodebase(String path) {
        try {
            Path codePath = Paths.get(path);
            if (!Files.exists(codePath)) {
                return "Path not found: " + path;
            }
            return codeReader.readFolder(path);
        } catch (Exception e) {
            log.warn("Failed to read codebase: {}", path, e);
            return "";
        }
    }
    
    private String readAPISpecs() {
        try {
            // Read OpenAPI specs, gateway configs, etc.
            StringBuilder specs = new StringBuilder();
            
            // Gateway routes
            String gatewayConfig = codeReader.readFile("backend/gateway/src/main/java/com/agroconnectworld/gateway/GatewayConfig.java");
            specs.append("Gateway Routes:\n").append(gatewayConfig).append("\n\n");
            
            // OpenAPI specs if available
            File openapiFile = new File("ai-company/api/openapi.yaml");
            if (openapiFile.exists()) {
                specs.append("OpenAPI Spec:\n").append(Files.readString(openapiFile.toPath())).append("\n");
            }
            
            return specs.toString();
        } catch (Exception e) {
            log.warn("Failed to read API specs", e);
            return "";
        }
    }
    
    private List<AuditFinding> checkBackendHealth() {
        List<AuditFinding> errors = new ArrayList<>();
        
        try {
            // Check health endpoints
            String[] services = {"gateway", "auth-service", "product-service", "supplier-service", 
                               "quote-service", "order-service", "contact-service"};
            
            for (String service : services) {
                try {
                    String health = healthCheckTool.checkServiceHealth("http://localhost:8080/api/" + service + "/health");
                    if (health.contains("DOWN") || health.contains("error")) {
                        errors.add(new AuditFinding(
                            "CRITICAL",
                            "Backend Error",
                            service + " is unhealthy",
                            "Service health check failed: " + health,
                            "backend/" + service
                        ));
                    }
                } catch (Exception e) {
                    errors.add(new AuditFinding(
                        "HIGH",
                        "Backend Error",
                        service + " health check failed",
                        e.getMessage(),
                        "backend/" + service
                    ));
                }
            }
        } catch (Exception e) {
            log.error("Error checking backend health", e);
        }
        
        return errors;
    }
    
    @SuppressWarnings("unchecked")
    private List<AuditFinding> parseFindings(String json, String category) {
        List<AuditFinding> findings = new ArrayList<>();
        
        try {
            Map<String, Object> data = objectMapper.readValue(json, Map.class);
            List<Map<String, Object>> items = (List<Map<String, Object>>) data.get(category);
            
            if (items != null) {
                for (Map<String, Object> item : items) {
                    findings.add(new AuditFinding(
                        (String) item.getOrDefault("severity", "MEDIUM"),
                        (String) item.getOrDefault("category", category),
                        (String) item.getOrDefault("title", "Unknown"),
                        (String) item.getOrDefault("description", ""),
                        (String) item.getOrDefault("location", "")
                    ));
                }
            }
        } catch (Exception e) {
            log.warn("Failed to parse findings for {}", category, e);
            // Try to extract findings from text
            if (json.contains("CRITICAL") || json.contains("HIGH")) {
                findings.add(new AuditFinding(
                    "MEDIUM",
                    category,
                    "Parsing issue",
                    "Could not parse structured findings: " + json.substring(0, Math.min(200, json.length())),
                    ""
                ));
            }
        }
        
        return findings;
    }
    
    private void saveAuditReport(ProjectAuditReport report) {
        try {
            Path reportsDir = Paths.get(AUDIT_REPORTS_DIR);
            Files.createDirectories(reportsDir);
            
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String filename = String.format("audit_%s.json", timestamp);
            Path reportPath = reportsDir.resolve(filename);
            
            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(report);
            Files.writeString(reportPath, json);
            
            // Also save latest
            Path latestPath = reportsDir.resolve("latest_audit.json");
            Files.writeString(latestPath, json);
            
            log.info("Audit report saved to {}", reportPath);
        } catch (IOException e) {
            log.error("Failed to save audit report", e);
        }
    }
    
    /**
     * Get latest audit report
     */
    public ProjectAuditReport getLatestAudit() {
        try {
            Path latestPath = Paths.get(AUDIT_REPORTS_DIR, "latest_audit.json");
            if (Files.exists(latestPath)) {
                String json = Files.readString(latestPath);
                return objectMapper.readValue(json, ProjectAuditReport.class);
            }
        } catch (Exception e) {
            log.error("Failed to read latest audit", e);
        }
        return null;
    }
}

