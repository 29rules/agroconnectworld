package com.ai.company.api;

import com.ai.company.agents.cto.CTOAgent;
import com.ai.company.agents.devops.DevOpsAgent;
import com.ai.company.agents.engineer.EngineerAgent;
import com.ai.company.agents.productmanager.ProductManagerAgent;
import com.ai.company.agents.qa.QAAgent;
import com.ai.company.agents.scrummaster.ScrumMasterAgent;
import com.ai.company.agents.scrummaster.ScrumMasterAgent;
import com.ai.company.audit.SystemAuditResult;
import com.ai.company.audit.SystemAuditService;
import com.ai.company.registry.AgentRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * CTO Chat Controller
 * 
 * Provides chat endpoints for interacting with AI agents:
 * - POST /ai/ctochat - Chat with CTO agent
 * - POST /ai/chat/{agent} - Chat with specific agent (cto, product, engineer, qa, scrum, devops)
 * 
 * All endpoints return structured JSON responses.
 */
@RestController
@RequestMapping("/ai")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:3000", "http://localhost:8080"})
public class CTOChatController {
    
    private static final Logger log = LoggerFactory.getLogger(CTOChatController.class);
    
    private final AgentRegistry agentRegistry;
    private final SystemAuditService systemAuditService;
    private final ObjectMapper objectMapper;
    
    public CTOChatController(AgentRegistry agentRegistry, SystemAuditService systemAuditService) {
        this.agentRegistry = agentRegistry;
        this.systemAuditService = systemAuditService;
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * CTO Chat Endpoint
     * 
     * @param request Chat request with message and optional sessionId
     * @return Structured chat response
     */
    @PostMapping("/ctochat")
    public ResponseEntity<Map<String, Object>> ctoChat(@RequestBody Map<String, String> request) {
        log.info("CTO chat request received: {}", request.get("message"));
        
        try {
            String message = request.get("message");
            String sessionId = request.getOrDefault("sessionId", UUID.randomUUID().toString());
            
            if (message == null || message.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Message is required"));
            }
            
            // Check if this is a system audit request
            boolean isSystemAudit = message != null && 
                (message.toLowerCase().contains("system audit") || 
                 message.toLowerCase().contains("full audit") ||
                 message.toLowerCase().contains("initiate the full"));
            
            if (isSystemAudit) {
                log.info("System audit request detected - this may take 10-15 minutes");
                // Return immediate acknowledgment
                Map<String, Object> ackResponse = new HashMap<>();
                ackResponse.put("sessionId", sessionId);
                ackResponse.put("message", message);
                ackResponse.put("response", "✅ System audit initiated. This process will take approximately 10-15 minutes. " +
                    "The audit is running in the background. Reports will be generated in /ai-company/reports/ with date-based folders. " +
                    "You will receive a confirmation message once all reports are generated and verified.");
                ackResponse.put("timestamp", LocalDateTime.now().toString());
                ackResponse.put("agent", "CTO");
                ackResponse.put("status", "initiated");
                ackResponse.put("async", true);
                ackResponse.put("estimatedDuration", "10-15 minutes");
                
                // Start audit in background thread with verification and confirmation
                new Thread(() -> {
                    try {
                        log.info("🚀 Starting background system audit...");
                        log.info("📋 This may take 10-15 minutes. Please wait...");
                        
                        SystemAuditResult auditResult = systemAuditService.initiateFullSystemAudit();
                        
                        // Generate confirmation message only if all reports are verified
                        if ("COMPLETED".equals(auditResult.getStatus())) {
                            String confirmationMessage = generateConfirmationMessage(auditResult);
                            log.info("✅✅✅ SYSTEM AUDIT CONFIRMATION ✅✅✅");
                            log.info(confirmationMessage);
                            log.info("✅✅✅ All reports verified and saved successfully! ✅✅✅");
                        } else if ("FAILED".equals(auditResult.getStatus())) {
                            log.error("❌❌❌ SYSTEM AUDIT FAILED ❌❌❌");
                            log.error("Error: {}", auditResult.getErrorMessage());
                            log.error("Please check the API key configuration and try again.");
                        } else {
                            log.warn("⚠️ System audit completed with status: {}. Some reports may not be verified.", 
                                auditResult.getStatus());
                            if (auditResult.getErrorMessage() != null) {
                                log.error("Error: {}", auditResult.getErrorMessage());
                            }
                        }
                    } catch (Exception e) {
                        log.error("❌❌❌ Background system audit failed with exception ❌❌❌");
                        log.error("Exception type: {}", e.getClass().getName());
                        log.error("Exception message: {}", e.getMessage());
                        log.error("Full stack trace:", e);
                        
                        // Check if it's an API key issue
                        if (e.getMessage() != null && 
                            (e.getMessage().contains("401") || 
                             e.getMessage().contains("No cookie auth") ||
                             e.getMessage().contains("api key"))) {
                            log.error("🔑 API KEY ERROR: Please check your OPENAI_API_KEY or OPENROUTER_API_KEY environment variable.");
                            log.error("🔑 Set it with: export OPENROUTER_API_KEY=your-key-here");
                        }
                    }
                }).start();
                
                return ResponseEntity.ok(ackResponse);
            }
            
            // Regular chat request - process synchronously
            CTOAgent cto = agentRegistry.getCTOAgent();
            String response = cto.reviewArchitecture(message, sessionId);
            
            Map<String, Object> chatResponse = new HashMap<>();
            chatResponse.put("sessionId", sessionId);
            chatResponse.put("message", message);
            chatResponse.put("response", response);
            chatResponse.put("timestamp", LocalDateTime.now().toString());
            chatResponse.put("agent", "CTO");
            chatResponse.put("status", "success");
            
            return ResponseEntity.ok(chatResponse);
            
        } catch (Exception e) {
            log.error("Error in CTO chat", e);
            return ResponseEntity.status(500).body(createErrorResponse("Error in CTO chat: " + e.getMessage()));
        }
    }
    
    /**
     * Generic Agent Chat Endpoint
     * 
     * Supports: cto, product, engineer, qa, scrum, devops
     * 
     * @param agentName Agent name
     * @param request Chat request with message and optional sessionId
     * @return Structured chat response
     */
    @PostMapping("/chat/{agent}")
    public ResponseEntity<Map<String, Object>> chatWithAgent(
            @PathVariable String agentName,
            @RequestBody Map<String, String> request) {
        
        log.info("Chat request for agent: {}", agentName);
        
        try {
            String message = request.get("message");
            String sessionId = request.getOrDefault("sessionId", UUID.randomUUID().toString());
            
            if (message == null || message.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Message is required"));
            }
            
            String response;
            String normalizedAgent = agentName.toLowerCase();
            
            switch (normalizedAgent) {
                case "cto":
                    CTOAgent cto = agentRegistry.getCTOAgent();
                    response = cto.reviewArchitecture(message, sessionId);
                    break;
                    
                case "product":
                case "productmanager":
                    ProductManagerAgent pm = agentRegistry.getProductManagerAgent();
                    response = pm.createEpic(message, sessionId);
                    break;
                    
                case "engineer":
                case "engineering":
                    EngineerAgent engineer = agentRegistry.getEngineerAgent();
                    response = engineer.createImplementationSpec(message, sessionId);
                    break;
                    
                case "qa":
                case "quality":
                    QAAgent qa = agentRegistry.getQAAgent();
                    response = qa.createTestPlan(message, sessionId);
                    break;
                    
                case "scrum":
                case "scrummaster":
                    ScrumMasterAgent scrum = agentRegistry.getScrumMasterAgent();
                    response = scrum.coordinateAgentsForSprint(message, sessionId);
                    break;
                    
                case "devops":
                    DevOpsAgent devops = agentRegistry.getDevOpsAgent();
                    response = devops.analyzeInfrastructure(sessionId);
                    break;
                    
                default:
                    return ResponseEntity.badRequest().body(createErrorResponse(
                        "Unknown agent: " + agentName + ". Supported agents: cto, product, engineer, qa, scrum, devops"));
            }
            
            Map<String, Object> chatResponse = new HashMap<>();
            chatResponse.put("sessionId", sessionId);
            chatResponse.put("message", message);
            chatResponse.put("response", response);
            chatResponse.put("timestamp", LocalDateTime.now().toString());
            chatResponse.put("agent", normalizedAgent);
            chatResponse.put("status", "success");
            
            return ResponseEntity.ok(chatResponse);
            
        } catch (Exception e) {
            log.error("Error in agent chat for: {}", agentName, e);
            return ResponseEntity.status(500).body(createErrorResponse(
                "Error in agent chat: " + e.getMessage()));
        }
    }
    
    /**
     * Get available agents
     * 
     * @return List of available agents
     */
    @GetMapping("/agents")
    public ResponseEntity<Map<String, Object>> getAvailableAgents() {
        Map<String, Object> response = new HashMap<>();
        response.put("agents", agentRegistry.getAgentNames());
        response.put("availableAgents", new String[]{
            "cto", "product", "engineer", "qa", "scrum", "devops"
        });
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Generate confirmation message after all reports are verified
     * 
     * @param auditResult System audit result with report paths
     * @return Formatted confirmation message
     */
    private String generateConfirmationMessage(SystemAuditResult auditResult) {
        StringBuilder confirmation = new StringBuilder();
        confirmation.append("\n");
        confirmation.append("═══════════════════════════════════════════════════════════════\n");
        confirmation.append("✅ SYSTEM AUDIT COMPLETED SUCCESSFULLY ✅\n");
        confirmation.append("═══════════════════════════════════════════════════════════════\n");
        confirmation.append("\n");
        confirmation.append("📊 Audit Summary:\n");
        confirmation.append("   • Audit ID: ").append(auditResult.getAuditId()).append("\n");
        confirmation.append("   • Status: ").append(auditResult.getStatus()).append("\n");
        confirmation.append("   • Total Reports: ").append(auditResult.getTotalReports()).append("\n");
        
        if (auditResult.getStartTime() != null) {
            confirmation.append("   • Start Time: ").append(auditResult.getStartTime()).append("\n");
        }
        if (auditResult.getEndTime() != null) {
            confirmation.append("   • End Time: ").append(auditResult.getEndTime()).append("\n");
        }
        
        confirmation.append("\n");
        confirmation.append("📁 Generated Reports (All Verified):\n");
        
        int reportNumber = 1;
        for (String reportPath : auditResult.getReports()) {
            confirmation.append("   ").append(reportNumber++).append(". ").append(reportPath).append("\n");
        }
        
        confirmation.append("\n");
        confirmation.append("📂 Report Location:\n");
        if (!auditResult.getReports().isEmpty()) {
            // Extract folder from first report path (e.g., "2025/11/28/full-system-audit-25-11-28.md")
            String firstReport = auditResult.getReports().get(0);
            String folderPath = firstReport.substring(0, firstReport.lastIndexOf('/'));
            confirmation.append("   • Folder: ai-company/reports/").append(folderPath).append("/\n");
        }
        
        confirmation.append("\n");
        confirmation.append("✅ All reports have been verified and saved to the file system.\n");
        confirmation.append("✅ You can now access the reports in the date-based folder structure.\n");
        confirmation.append("\n");
        confirmation.append("═══════════════════════════════════════════════════════════════\n");
        
        return confirmation.toString();
    }
    
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", true);
        error.put("message", message);
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("status", "error");
        return error;
    }
}

