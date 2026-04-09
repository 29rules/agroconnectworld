package com.ai.company.devops;

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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Service for DevOps Validation
 * Validates all infrastructure components
 */
@Service
public class DevOpsValidationService {
    
    private static final Logger log = LoggerFactory.getLogger(DevOpsValidationService.class);
    
    private final DevOpsValidationAgent validationAgent;
    
    @Autowired
    public DevOpsValidationService(ChatLanguageModel chatModel) {
        this.validationAgent = AiServices.builder(DevOpsValidationAgent.class)
            .chatLanguageModel(chatModel)
            .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(50))
            .build();
    }
    
    @Autowired
    private CodeReaderTool codeReader;
    
    @Autowired
    private HealthCheckTool healthCheckTool;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String VALIDATION_REPORTS_DIR = "ai-company/reports/devops-validation";
    
    /**
     * Scheduled validation - runs every 6 hours
     */
    @Scheduled(cron = "0 0 */6 * * *")
    public void scheduledValidation() {
        log.info("Starting scheduled DevOps validation...");
        try {
            DevOpsValidationReport report = conductFullValidation();
            saveValidationReport(report);
            log.info("Scheduled validation completed");
        } catch (Exception e) {
            log.error("Scheduled validation failed", e);
        }
    }
    
    /**
     * Conduct full DevOps validation
     */
    public DevOpsValidationReport conductFullValidation() {
        log.info("Conducting full DevOps validation...");
        
        DevOpsValidationReport report = new DevOpsValidationReport();
        report.setValidationDate(LocalDateTime.now());
        report.setValidationId(UUID.randomUUID().toString());
        
        try {
            // 1. Validate Docker
            log.info("Validating Docker...");
            String dockerfiles = readDockerfiles();
            String dockerCompose = readDockerCompose();
            String dockerValidation = validationAgent.validateDocker(dockerfiles, dockerCompose);
            report.setDockerValidation(parseValidation(dockerValidation));
            
            // 2. Validate CI/CD
            log.info("Validating CI/CD...");
            String workflows = readCIWorkflows();
            String cicdValidation = validationAgent.validateCICD(workflows);
            report.setCicdValidation(parseValidation(cicdValidation));
            
            // 3. Validate Environment Configs
            log.info("Validating environment configs...");
            String envConfigs = readEnvironmentConfigs();
            String envValidation = validationAgent.validateEnvironmentConfigs(envConfigs);
            report.setEnvironmentValidation(parseValidation(envValidation));
            
            // 4. Validate Nginx
            log.info("Validating Nginx...");
            String nginxConfig = readNginxConfigs();
            String nginxValidation = validationAgent.validateNginxRules(nginxConfig);
            report.setNginxValidation(parseValidation(nginxValidation));
            
            // 5. Validate Certificates
            log.info("Validating certificates...");
            String certificates = readCertificates();
            String certValidation = validationAgent.validateCertificates(certificates);
            report.setCertificateValidation(parseValidation(certValidation));
            
            // 6. Validate Readiness
            log.info("Validating system readiness...");
            String systemState = checkSystemState();
            String readinessValidation = validationAgent.validateReadiness(systemState);
            report.setReadinessValidation(parseValidation(readinessValidation));
            
            // Generate summary
            report.generateSummary();
            
            log.info("Full DevOps validation completed");
            
        } catch (Exception e) {
            log.error("Error during validation", e);
            report.addError("Validation execution failed: " + e.getMessage());
        }
        
        return report;
    }
    
    private String readDockerfiles() {
        try {
            StringBuilder dockerfiles = new StringBuilder();
            String[] services = {"frontend", "gateway", "auth-service", "product-service", 
                              "supplier-service", "quote-service", "order-service", "contact-service"};
            
            for (String service : services) {
                String path = service.equals("frontend") ? 
                    "frontend/Dockerfile" : 
                    "backend/" + service + "/Dockerfile";
                try {
                    dockerfiles.append(path).append(":\n");
                    dockerfiles.append(codeReader.readFile(path)).append("\n\n");
                } catch (Exception e) {
                    log.warn("Failed to read Dockerfile: {}", path, e);
                }
            }
            return dockerfiles.toString();
        } catch (Exception e) {
            log.warn("Failed to read Dockerfiles", e);
            return "";
        }
    }
    
    private String readDockerCompose() {
        try {
            StringBuilder compose = new StringBuilder();
            String[] composeFiles = {
                "ops/docker-compose.yml",
                "ops/environments/dev/docker-compose.dev.yml",
                "ops/environments/uat/docker-compose.uat.yml",
                "ops/environments/preprod/docker-compose.preprod.yml",
                "ops/environments/production/docker-compose.prod.yml"
            };
            
            for (String file : composeFiles) {
                try {
                    compose.append(file).append(":\n");
                    compose.append(codeReader.readFile(file)).append("\n\n");
                } catch (Exception e) {
                    log.warn("Failed to read: {}", file, e);
                }
            }
            return compose.toString();
        } catch (Exception e) {
            log.warn("Failed to read Docker Compose files", e);
            return "";
        }
    }
    
    private String readCIWorkflows() {
        try {
            StringBuilder workflows = new StringBuilder();
            Path workflowsDir = Paths.get(".github/workflows");
            
            if (Files.exists(workflowsDir)) {
                Files.list(workflowsDir)
                    .filter(p -> p.toString().endsWith(".yml") || p.toString().endsWith(".yaml"))
                    .forEach(p -> {
                        try {
                            workflows.append(p.getFileName()).append(":\n");
                            workflows.append(Files.readString(p)).append("\n\n");
                        } catch (IOException e) {
                            log.warn("Failed to read workflow: {}", p, e);
                        }
                    });
            }
            return workflows.toString();
        } catch (Exception e) {
            log.warn("Failed to read CI workflows", e);
            return "";
        }
    }
    
    private String readEnvironmentConfigs() {
        try {
            StringBuilder configs = new StringBuilder();
            configs.append("Environment Variables Guide:\n");
            configs.append(codeReader.readFile("ops/ENV_VARIABLES_GUIDE.md")).append("\n\n");
            return configs.toString();
        } catch (Exception e) {
            log.warn("Failed to read environment configs", e);
            return "";
        }
    }
    
    private String readNginxConfigs() {
        try {
            StringBuilder nginx = new StringBuilder();
            Path nginxDir = Paths.get("ops/nginx");
            
            if (Files.exists(nginxDir)) {
                Files.list(nginxDir)
                    .filter(p -> p.toString().endsWith(".conf"))
                    .forEach(p -> {
                        try {
                            nginx.append(p.getFileName()).append(":\n");
                            nginx.append(Files.readString(p)).append("\n\n");
                        } catch (IOException e) {
                            log.warn("Failed to read nginx config: {}", p, e);
                        }
                    });
            }
            return nginx.toString();
        } catch (Exception e) {
            log.warn("Failed to read Nginx configs", e);
            return "";
        }
    }
    
    private String readCertificates() {
        try {
            StringBuilder certs = new StringBuilder();
            // Read certificate info (not the actual keys)
            Path stagingCert = Paths.get("ops/environments/preprod/ssl/manual-setup.md");
            Path prodCert = Paths.get("ops/environments/production/ssl/manual-setup.md");
            
            if (Files.exists(stagingCert)) {
                certs.append("Staging Certificates:\n").append(Files.readString(stagingCert)).append("\n\n");
            }
            if (Files.exists(prodCert)) {
                certs.append("Production Certificates:\n").append(Files.readString(prodCert)).append("\n\n");
            }
            return certs.toString();
        } catch (Exception e) {
            log.warn("Failed to read certificates", e);
            return "";
        }
    }
    
    private String checkSystemState() {
        try {
            StringBuilder state = new StringBuilder();
            state.append("System Health Checks:\n");
            
            // Check service health
            String[] services = {"gateway", "auth-service", "product-service"};
            for (String service : services) {
                try {
                    String health = healthCheckTool.checkServiceHealth("http://localhost:8080/api/" + service + "/health");
                    state.append(service).append(": ").append(health).append("\n");
                } catch (Exception e) {
                    state.append(service).append(": UNKNOWN\n");
                }
            }
            
            return state.toString();
        } catch (Exception e) {
            log.warn("Failed to check system state", e);
            return "";
        }
    }
    
    @SuppressWarnings("unchecked")
    private ValidationResult parseValidation(String json) {
        try {
            Map<String, Object> data = objectMapper.readValue(json, Map.class);
            ValidationResult result = new ValidationResult();
            result.setValid((Boolean) data.getOrDefault("valid", false));
            result.setIssues((List<String>) data.getOrDefault("issues", new ArrayList<>()));
            result.setRecommendations((List<String>) data.getOrDefault("recommendations", new ArrayList<>()));
            result.setScore((Integer) data.getOrDefault("score", 0));
            return result;
        } catch (Exception e) {
            log.warn("Failed to parse validation", e);
            ValidationResult result = new ValidationResult();
            result.setValid(false);
            result.addIssue("Failed to parse validation result");
            return result;
        }
    }
    
    private void saveValidationReport(DevOpsValidationReport report) {
        try {
            Path reportsDir = Paths.get(VALIDATION_REPORTS_DIR);
            Files.createDirectories(reportsDir);
            
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String filename = String.format("devops_validation_%s.json", timestamp);
            Path reportPath = reportsDir.resolve(filename);
            
            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(report);
            Files.writeString(reportPath, json);
            
            // Also save latest
            Path latestPath = reportsDir.resolve("latest_validation.json");
            Files.writeString(latestPath, json);
            
            log.info("Validation report saved to {}", reportPath);
        } catch (IOException e) {
            log.error("Failed to save validation report", e);
        }
    }
}

