package com.ai.company.tools.audit;

import com.ai.company.audit.SystemAuditResult;
import com.ai.company.audit.SystemAuditService;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * System Audit Tool
 * 
 * Allows AI agents (especially CTO Agent) to autonomously trigger
 * comprehensive system audits of the AgroConnectWorld platform.
 * 
 * This tool enables:
 * - Autonomous audit execution by AI agents
 * - System-level actions without frontend involvement
 * - Multi-agent architecture with real capabilities
 * - Unified logic in Spring Boot backend
 * 
 * ZERO-IMPACT MODE: This tool only generates reports, never modifies code.
 */
@Component
public class SystemAuditTool {
    
    private static final Logger log = LoggerFactory.getLogger(SystemAuditTool.class);
    
    private final SystemAuditService systemAuditService;
    private final ObjectMapper objectMapper;
    
    /**
     * Constructor with dependency injection
     */
    @Autowired
    public SystemAuditTool(SystemAuditService systemAuditService) {
        this.systemAuditService = systemAuditService;
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Run full system audit
     * 
     * Initiates comprehensive system audit that produces 7 reports:
     * 1. Full System Audit Report
     * 2. Architecture Report
     * 3. Security Report
     * 4. Engineering Readiness Report
     * 5. CI/CD Readiness Report
     * 6. Deployment Plan
     * 7. CEO Action Items
     * 
     * All reports are saved to /ai-company/reports/
     * 
     * @return JSON string with audit result including status, report count, and report filenames
     */
    @Tool("""
        Run a comprehensive system audit of the AgroConnectWorld platform.
        This audit analyzes all microservices, infrastructure, security, code quality,
        CI/CD pipelines, and deployment readiness.
        
        Produces 7 detailed reports:
        - FULL_SYSTEM_AUDIT.md
        - ARCHITECTURE_REPORT.md
        - SECURITY_REPORT.md
        - ENGINEERING_READINESS_REPORT.md
        - CI_CD_READINESS_REPORT.md
        - DEPLOYMENT_PLAN.md
        - CEO_ACTION_ITEMS.md
        
        All reports are saved to /ai-company/reports/
        
        Returns audit result with status, total reports generated, and report filenames.
        """)
    public String runSystemAudit() {
        log.info("🔍 SystemAuditTool: CTO Agent requested full system audit");
        
        try {
            SystemAuditResult result = systemAuditService.initiateFullSystemAudit();
            
            // Format result as JSON for the agent
            String resultJson = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(Map.of(
                    "status", result.getStatus(),
                    "auditId", result.getAuditId(),
                    "totalReports", result.getTotalReports(),
                    "reports", result.getReports(),
                    "startTime", result.getStartTime() != null ? result.getStartTime().toString() : "N/A",
                    "endTime", result.getEndTime() != null ? result.getEndTime().toString() : "N/A",
                    "message", result.getStatus().equals("COMPLETED") 
                        ? String.format("✅ System audit completed successfully. Generated %d reports in /ai-company/reports/", result.getTotalReports())
                        : "❌ System audit failed: " + (result.getErrorMessage() != null ? result.getErrorMessage() : "Unknown error")
                ));
            
            log.info("✅ SystemAuditTool: Audit completed with status: {}", result.getStatus());
            return resultJson;
            
        } catch (Exception e) {
            log.error("❌ SystemAuditTool: Audit execution failed", e);
            try {
                return objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(Map.of(
                        "status", "FAILED",
                        "error", true,
                        "message", "System audit failed: " + e.getMessage()
                    ));
            } catch (Exception jsonError) {
                return "{\"status\":\"FAILED\",\"error\":true,\"message\":\"System audit failed: " + e.getMessage() + "\"}";
            }
        }
    }
    
    /**
     * Get audit status (without running audit)
     * 
     * @return Information about the audit tool and last audit status
     */
    @Tool("""
        Get information about system audit capabilities and status.
        Does not run an audit, just provides information.
        """)
    public String getAuditInfo() {
        return """
            System Audit Tool Information:
            
            Capabilities:
            - Full system audit of AgroConnectWorld platform
            - Generates 7 comprehensive reports
            - Analyzes all microservices, infrastructure, security, code quality
            
            Reports Generated:
            1. FULL_SYSTEM_AUDIT.md
            2. ARCHITECTURE_REPORT.md
            3. SECURITY_REPORT.md
            4. ENGINEERING_READINESS_REPORT.md
            5. CI_CD_READINESS_REPORT.md
            6. DEPLOYMENT_PLAN.md
            7. CEO_ACTION_ITEMS.md
            
            Reports Location: /ai-company/reports/
            
            To run audit: Call runSystemAudit()
            """;
    }
}

