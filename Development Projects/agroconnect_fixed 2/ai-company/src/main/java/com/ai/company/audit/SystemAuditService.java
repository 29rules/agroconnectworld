package com.ai.company.audit;

import com.ai.company.agents.cto.CTOAgent;
import com.ai.company.registry.AgentRegistry;
import com.ai.company.tools.code.CodeReaderTool;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Comprehensive System Audit Service
 * Orchestrates full system audit using CTO Agent and generates multiple reports
 */
@Service
public class SystemAuditService {
    
    private static final Logger log = LoggerFactory.getLogger(SystemAuditService.class);

    @Autowired
    private CodeReaderTool codeReader;
    
    @Autowired
    private ChatLanguageModel chatLanguageModel;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String REPORTS_DIR = "ai-company/reports";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yy-MM-dd");
    private static final DateTimeFormatter FOLDER_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    
    /**
     * Initiate full system audit
     * Produces 5-7 comprehensive reports
     */
    public SystemAuditResult initiateFullSystemAudit() {
        log.info("🚀 Initiating Full AgroConnectWorld System Audit...");
        
        SystemAuditResult result = new SystemAuditResult();
        result.setAuditId(UUID.randomUUID().toString());
        result.setStartTime(LocalDateTime.now());
        
        try {
            // Get date-based folder structure
            LocalDateTime now = LocalDateTime.now();
            String dateFolder = now.format(FOLDER_DATE_FORMATTER); // yyyy/MM/dd
            String dateSuffix = now.format(DATE_FORMATTER); // yy-MM-dd
            
            // 1. Full System Audit Report
            log.info("📋 Generating Full System Audit Report...");
            String systemAuditReport = generateSystemAuditReport();
            String fullSystemAuditPath = saveReportWithTracking("full-system-audit", dateSuffix, dateFolder, systemAuditReport);
            result.addReport(fullSystemAuditPath);
            
            // 2. Architecture Report
            log.info("🏗️ Generating Architecture Report...");
            String architectureReport = generateArchitectureReport();
            String architecturePath = saveReportWithTracking("architecture-report", dateSuffix, dateFolder, architectureReport);
            result.addReport(architecturePath);
            
            // 3. Security Report
            log.info("🔒 Generating Security Report...");
            String securityReport = generateSecurityReport();
            String securityPath = saveReportWithTracking("security-report", dateSuffix, dateFolder, securityReport);
            result.addReport(securityPath);
            
            // 4. Engineering Readiness Report
            log.info("⚙️ Generating Engineering Readiness Report...");
            String engineeringReport = generateEngineeringReadinessReport();
            String engineeringPath = saveReportWithTracking("engineering-readiness-report", dateSuffix, dateFolder, engineeringReport);
            result.addReport(engineeringPath);
            
            // 5. CI/CD Readiness Report
            log.info("🔄 Generating CI/CD Readiness Report...");
            String cicdReport = generateCICDReadinessReport();
            String cicdPath = saveReportWithTracking("cicd-readiness-report", dateSuffix, dateFolder, cicdReport);
            result.addReport(cicdPath);
            
            // 6. Deployment Plan
            log.info("🚀 Generating Deployment Plan...");
            String deploymentPlan = generateDeploymentPlan();
            String deploymentPath = saveReportWithTracking("deployment-plan", dateSuffix, dateFolder, deploymentPlan);
            result.addReport(deploymentPath);
            
            // 7. CEO Action Items
            log.info("📝 Generating CEO Action Items...");
            String ceoActionItems = generateCEOActionItems();
            String ceoActionPath = saveReportWithTracking("ceo-action-items", dateSuffix, dateFolder, ceoActionItems);
            result.addReport(ceoActionPath);
            
            // Verify all reports are saved before marking as completed
            boolean allReportsVerified = verifyAllReportsSaved(result.getReports(), dateFolder);
            
            if (allReportsVerified) {
                result.setEndTime(LocalDateTime.now());
                result.setStatus("COMPLETED");
                result.setTotalReports(7);
                
                log.info("✅ Full System Audit completed successfully. Generated {} reports.", result.getTotalReports());
                log.info("✅ All reports verified and saved in: ai-company/reports/{}", dateFolder);
            } else {
                result.setEndTime(LocalDateTime.now());
                result.setStatus("PARTIAL");
                result.setErrorMessage("Some reports may not have been saved correctly. Please check the logs.");
                log.warn("⚠️ System audit completed but some reports may not be verified.");
            }
            
        } catch (Exception e) {
            log.error("❌ System audit failed", e);
            result.setStatus("FAILED");
            result.setErrorMessage(e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Verify that all reports are actually saved in the file system
     * 
     * @param reportPaths List of relative report paths (e.g., "2025/11/28/full-system-audit-25-11-28.md")
     * @param dateFolder Date folder (e.g., "2025/11/28")
     * @return true if all reports exist, false otherwise
     */
    private boolean verifyAllReportsSaved(List<String> reportPaths, String dateFolder) {
        if (reportPaths == null || reportPaths.isEmpty()) {
            log.warn("No report paths to verify");
            return false;
        }
        
        Path reportsBaseDir = Paths.get(REPORTS_DIR);
        Path dateDir = reportsBaseDir.resolve(dateFolder);
        
        int verifiedCount = 0;
        int totalReports = reportPaths.size();
        
        log.info("🔍 Verifying {} reports are saved in: {}", totalReports, dateDir);
        
        for (String reportPath : reportPaths) {
            try {
                // reportPath is like "2025/11/28/full-system-audit-25-11-28.md"
                // We need to check if the file exists
                Path fullPath = reportsBaseDir.resolve(reportPath);
                
                if (Files.exists(fullPath) && Files.isRegularFile(fullPath)) {
                    long fileSize = Files.size(fullPath);
                    if (fileSize > 0) {
                        verifiedCount++;
                        log.info("✅ Verified: {} ({} bytes)", reportPath, fileSize);
                    } else {
                        log.warn("⚠️ Report exists but is empty: {}", reportPath);
                    }
                } else {
                    log.error("❌ Report not found: {}", reportPath);
                }
            } catch (Exception e) {
                log.error("❌ Error verifying report: {}", reportPath, e);
            }
        }
        
        boolean allVerified = verifiedCount == totalReports;
        
        if (allVerified) {
            log.info("✅✅✅ All {} reports verified and saved successfully! ✅✅✅", verifiedCount);
        } else {
            log.warn("⚠️ Only {}/{} reports verified. Some reports may be missing.", verifiedCount, totalReports);
        }
        
        return allVerified;
    }
    
    /**
     * Get CTO Agent from registry WITHOUT SystemAuditTool to prevent infinite loops
     * 
     * When SystemAuditService calls CTO Agent, we don't want the CTO Agent to call
     * runSystemAudit tool again, which would create an infinite loop.
     */
    private CTOAgent getCTOAgent() {
        // Create a new CTO Agent instance WITHOUT SystemAuditTool
        // This prevents the recursive loop when generating reports
        return new CTOAgent(chatLanguageModel);
    }
    
    /**
     * Generate Full System Audit Report
     */
    private String generateSystemAuditReport() throws Exception {
        String sessionId = UUID.randomUUID().toString();
        CTOAgent ctoAgent = getCTOAgent();
        
        // Read key files
        String dockerCompose = readFile("ops/docker-compose.yml");
        String gatewayConfig = readFile("backend/gateway/src/main/resources/application.yml");
        String frontendRoutes = readFile("frontend/src/App.jsx");
        String nginxConfig = readFile("ops/nginx/default.conf");
        
        String prompt = String.format("""
            Conduct a comprehensive Full System Audit for AgroConnectWorld.
            
            Analyze:
            1. All microservices (auth-service, product-service, supplier-service, quote-service, order-service, contact-service, gateway)
            2. Gateway and routing configuration
            3. Frontend routes and components
            4. All configuration files (application.yml, docker-compose, nginx)
            5. All environment variables
            6. Docker and Docker Compose setup
            7. Nginx and reverse proxy configuration
            8. Database schema and connections
            9. Auth service implementation
            10. CEO Portal functionality
            11. AI Company tools and agents
            12. GitHub workflows
            13. Logs and error patterns
            14. File structure and organization
            15. Code quality metrics
            16. Security issues
            17. Deployment readiness
            
            Docker Compose:
            %s
            
            Gateway Config:
            %s
            
            Frontend Routes:
            %s
            
            Nginx Config:
            %s
            
            Provide a comprehensive audit report with:
            - Executive Summary
            - System Overview
            - Microservices Analysis
            - Infrastructure Analysis
            - Configuration Analysis
            - Security Assessment
            - Code Quality Assessment
            - Deployment Readiness
            - Critical Issues
            - Recommendations
            - Risk Assessment
            """, 
            truncate(dockerCompose, 2000),
            truncate(gatewayConfig, 2000),
            truncate(frontendRoutes, 2000),
            truncate(nginxConfig, 2000)
        );
        
        try {
            String auditResult = getCTOAgent().reviewArchitecture(prompt, sessionId);
            return formatReport("Full System Audit Report", auditResult);
        } catch (Exception e) {
            log.error("❌ Failed to generate system audit report via CTO Agent", e);
            // Return a fallback report with error information
            return formatReport("Full System Audit Report", 
                "## Error Generating Report\n\n" +
                "The system audit report could not be generated due to an error:\n\n" +
                "**Error:** " + e.getMessage() + "\n\n" +
                "**Possible Causes:**\n" +
                "- API key not configured (check OPENROUTER_API_KEY environment variable)\n" +
                "- Network connectivity issues\n" +
                "- API service unavailable\n\n" +
                "Please check the API key configuration and try again.");
        }
    }
    
    /**
     * Generate Architecture Report
     */
    private String generateArchitectureReport() throws Exception {
        String sessionId = UUID.randomUUID().toString();
        CTOAgent ctoAgent = getCTOAgent();
        
        String prompt = """
            Analyze the complete AgroConnectWorld architecture.
            
            Review:
            - Microservices architecture
            - Service communication patterns
            - API Gateway design
            - Database architecture (PostgreSQL schemas per service)
            - Frontend architecture (React, Vite, routing)
            - AI Company layer architecture
            - Deployment architecture (Docker, Docker Compose, Nginx)
            - Environment architecture (Dev, UAT, Staging, Production)
            
            Provide:
            - Architecture Overview
            - Service Dependencies
            - Data Flow Diagrams (textual)
            - Communication Patterns
            - Scalability Analysis
            - Performance Considerations
            - Architecture Strengths
            - Architecture Weaknesses
            - Recommendations for Improvement
            """;
        
        String architectureResult = getCTOAgent().reviewArchitecture(prompt, sessionId);
        
        return formatReport("Architecture Report", architectureResult);
    }
    
    /**
     * Generate Security Report
     */
    private String generateSecurityReport() throws Exception {
        String sessionId = UUID.randomUUID().toString();
        CTOAgent ctoAgent = getCTOAgent();
        
        String authServiceCode = readFile("backend/auth-service/src/main/java/com/agroconnectworld/auth");
        String gatewaySecurity = readFile("backend/gateway/src/main/java/com/agroconnectworld/gateway");
        String aiSecurityFilter = readFile("ai-company/src/main/java/com/ai/company/api/SecurityFilter.java");
        
        String prompt = String.format("""
            Conduct a comprehensive Security Audit for AgroConnectWorld.
            
            Analyze:
            - Authentication and authorization mechanisms
            - JWT token implementation and security
            - API security (CORS, rate limiting, input validation)
            - Database security (SQL injection prevention, connection security)
            - Docker security (image security, container security)
            - Environment variable security (secrets management)
            - SSL/TLS configuration
            - CEO Portal security
            - AI Company API security
            - Security vulnerabilities
            - Compliance considerations
            
            Auth Service Code:
            %s
            
            Gateway Security:
            %s
            
            AI Security Filter:
            %s
            
            Provide:
            - Security Overview
            - Authentication & Authorization Analysis
            - API Security Assessment
            - Infrastructure Security
            - Data Security
            - Security Vulnerabilities (Critical, High, Medium, Low)
            - Security Recommendations
            - Compliance Status
            - Security Best Practices
            """,
            truncate(authServiceCode, 2000),
            truncate(gatewaySecurity, 2000),
            truncate(aiSecurityFilter, 2000)
        );
        
        String securityResult = getCTOAgent().reviewArchitecture(prompt, sessionId);
        
        return formatReport("Security Report", securityResult);
    }
    
    /**
     * Generate Engineering Readiness Report
     */
    private String generateEngineeringReadinessReport() throws Exception {
        String sessionId = UUID.randomUUID().toString();
        CTOAgent ctoAgent = getCTOAgent();
        
        String prompt = """
            Assess Engineering Readiness for AgroConnectWorld.
            
            Evaluate:
            - Code quality and maintainability
            - Test coverage and quality
            - Documentation completeness
            - Code organization and structure
            - Development workflow
            - Code review process
            - Technical debt
            - Performance optimization
            - Error handling
            - Logging and monitoring
            - Developer experience
            - Build and deployment processes
            
            Provide:
            - Engineering Readiness Score (0-100)
            - Code Quality Assessment
            - Test Coverage Analysis
            - Documentation Status
            - Technical Debt Analysis
            - Performance Metrics
            - Developer Experience Assessment
            - Readiness for Production
            - Improvement Recommendations
            - Priority Actions
            """;
        
        String engineeringResult = getCTOAgent().reviewArchitecture(prompt, sessionId);
        
        return formatReport("Engineering Readiness Report", engineeringResult);
    }
    
    /**
     * Generate CI/CD Readiness Report
     */
    private String generateCICDReadinessReport() throws Exception {
        String sessionId = UUID.randomUUID().toString();
        CTOAgent ctoAgent = getCTOAgent();
        
        String ciYml = readFile(".github/workflows/ci.yml");
        String devYml = readFile(".github/workflows/dev.yml");
        String uatYml = readFile(".github/workflows/uat.yml");
        String stagingYml = readFile(".github/workflows/staging.yml");
        String prodYml = readFile(".github/workflows/prod.yml");
        
        String prompt = String.format("""
            Assess CI/CD Readiness for AgroConnectWorld.
            
            Review:
            - GitHub Actions workflows
            - Build pipelines
            - Test automation
            - Deployment automation
            - Environment promotion flow
            - Security scanning integration
            - Code quality checks
            - Artifact management
            - Rollback strategies
            - Monitoring and alerting
            
            CI Workflow:
            %s
            
            Dev Deployment:
            %s
            
            UAT Deployment:
            %s
            
            Staging Deployment:
            %s
            
            Production Deployment:
            %s
            
            Provide:
            - CI/CD Readiness Score (0-100)
            - Pipeline Analysis
            - Automation Coverage
            - Testing Integration
            - Deployment Strategy Assessment
            - Security Integration
            - Monitoring & Alerting
            - Gaps and Missing Components
            - Recommendations
            - Priority Improvements
            """,
            truncate(ciYml, 2000),
            truncate(devYml, 2000),
            truncate(uatYml, 2000),
            truncate(stagingYml, 2000),
            truncate(prodYml, 2000)
        );
        
        String cicdResult = getCTOAgent().reviewArchitecture(prompt, sessionId);
        
        return formatReport("CI/CD Readiness Report", cicdResult);
    }
    
    /**
     * Generate Deployment Plan
     */
    private String generateDeploymentPlan() throws Exception {
        String sessionId = UUID.randomUUID().toString();
        CTOAgent ctoAgent = getCTOAgent();
        
        String prompt = """
            Create a comprehensive Deployment Plan for AgroConnectWorld.
            
            Plan deployment flow:
            - Dev → UAT → Staging → Production
            
            For each environment, provide:
            - Pre-deployment checklist
            - Deployment steps
            - Health check procedures
            - Rollback procedures
            - Validation criteria
            - Success metrics
            - Risk mitigation
            
            Include:
            - Environment-specific configurations
            - Database migration strategy
            - Service deployment order
            - Health check endpoints
            - Monitoring setup
            - Rollback procedures
            - Communication plan
            - Timeline estimates
            - Risk assessment
            - Approval gates
            """;
        
        String deploymentResult = getCTOAgent().reviewArchitecture(prompt, sessionId);
        
        return formatReport("Deployment Plan", deploymentResult);
    }
    
    /**
     * Generate CEO Action Items
     */
    private String generateCEOActionItems() throws Exception {
        String sessionId = UUID.randomUUID().toString();
        CTOAgent ctoAgent = getCTOAgent();
        
        String prompt = """
            Generate CEO Action Items based on the full system audit.
            
            Create prioritized action items for:
            - Critical security issues requiring immediate attention
            - Architecture decisions requiring CEO approval
            - Resource allocation needs
            - Strategic technical decisions
            - Risk mitigation actions
            - Compliance requirements
            - Production readiness blockers
            - Budget considerations
            - Timeline decisions
            
            Format each action item with:
            - Priority (CRITICAL, HIGH, MEDIUM, LOW)
            - Category
            - Description
            - Impact
            - Required Actions
            - Timeline
            - Owner/Responsible Party
            - Dependencies
            - Estimated Effort
            """;
        
        String actionItemsResult = getCTOAgent().reviewArchitecture(prompt, sessionId);
        
        return formatReport("CEO Action Items", actionItemsResult);
    }
    
    // Helper methods
    private String readFile(String filePath) {
        try {
            // Use CodeReaderTool for safe file reading
            String content = codeReader.readFile(filePath);
            if (content != null && !content.isEmpty() && !content.contains("Error")) {
                return content;
            }
        } catch (Exception e) {
            log.warn("Could not read file: {}", filePath, e);
        }
        
        // Fallback to direct file reading
        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                return Files.readString(path);
            }
        } catch (Exception e) {
            log.warn("Fallback file read failed: {}", filePath, e);
        }
        
        return "File not found or could not be read";
    }
    
    private String truncate(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text != null ? text : "";
        }
        return text.substring(0, maxLength) + "... [truncated]";
    }
    
    private String formatReport(String title, String content) {
        StringBuilder report = new StringBuilder();
        report.append("# ").append(title).append("\n\n");
        report.append("**Generated:** ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n\n");
        report.append("---\n\n");
        report.append(content);
        return report.toString();
    }
    
    /**
     * Save report with date-based folder structure and tracking number
     * 
     * Pattern:
     * - Folder: ai-company/reports/yyyy/MM/dd/
     * - Filename: {baseName}-{yy-MM-dd}.md (if first report of the day)
     * - Filename: {baseName}-{yy-MM-dd}-{trackNumber}.md (if multiple reports per day)
     * 
     * @param baseName Base name for the report (e.g., "full-system-audit")
     * @param dateSuffix Date suffix in format yy-MM-dd
     * @param dateFolder Date folder in format yyyy/MM/dd
     * @param content Report content
     * @return Relative path to the saved report
     */
    private String saveReportWithTracking(String baseName, String dateSuffix, String dateFolder, String content) throws IOException {
        // Create date-based folder structure: ai-company/reports/yyyy/MM/dd/
        Path reportsBaseDir = Paths.get(REPORTS_DIR);
        Path dateDir = reportsBaseDir.resolve(dateFolder);
        Files.createDirectories(dateDir);
        
        // Determine filename with tracking number
        String filename = determineReportFilename(baseName, dateSuffix, dateDir);
        
        // Save report
        Path reportPath = dateDir.resolve(filename);
        Files.writeString(reportPath, content);
        
        // Return relative path for the result
        String relativePath = dateFolder + "/" + filename;
        log.info("✅ Report saved: {}", reportPath);
        log.info("📁 Report path: {}", relativePath);
        
        return relativePath;
    }
    
    /**
     * Determine the filename for a report, checking if reports already exist for today
     * 
     * @param baseName Base name (e.g., "full-system-audit")
     * @param dateSuffix Date suffix (e.g., "25-11-28")
     * @param dateDir Directory to check for existing reports
     * @return Filename with appropriate tracking number
     */
    private String determineReportFilename(String baseName, String dateSuffix, Path dateDir) {
        // Base filename without tracking number
        String baseFilename = baseName + "-" + dateSuffix + ".md";
        
        // Check if base filename already exists
        Path basePath = dateDir.resolve(baseFilename);
        if (!Files.exists(basePath)) {
            // First report of the day - use base name without number
            return baseFilename;
        }
        
        // Multiple reports today - find the next available tracking number
        int trackNumber = 1;
        String filename;
        Path filePath;
        
        do {
            filename = baseName + "-" + dateSuffix + "-" + trackNumber + ".md";
            filePath = dateDir.resolve(filename);
            trackNumber++;
        } while (Files.exists(filePath) && trackNumber < 1000); // Safety limit
        
        log.info("📊 Multiple reports detected for today. Using tracking number: {}", trackNumber - 1);
        return filename;
    }
    
    /**
     * Legacy method for backward compatibility (kept for other services that might use it)
     */
    private void saveReport(String filename, String content) throws IOException {
        Path reportsDir = Paths.get(REPORTS_DIR);
        Files.createDirectories(reportsDir);
        
        Path reportPath = reportsDir.resolve(filename);
        Files.writeString(reportPath, content);
        
        log.info("✅ Report saved: {}", reportPath);
    }
}

