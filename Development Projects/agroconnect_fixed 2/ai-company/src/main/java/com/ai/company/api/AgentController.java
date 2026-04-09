package com.ai.company.api;

import com.ai.company.api.dto.AgentRequest;
import com.ai.company.api.dto.AgentResponse;
import com.ai.company.agents.architect.ArchitectAgent;
import com.ai.company.agents.cto.CTOAgent;
import com.ai.company.agents.devops.DevOpsAgent;
import com.ai.company.agents.engineer.EngineerAgent;
import com.ai.company.agents.fullstack.FullStackAgent;
import com.ai.company.agents.productmanager.ProductManagerAgent;
import com.ai.company.agents.qa.QAAgent;
import com.ai.company.registry.AgentRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for agent execution.
 * 
 * Provides endpoints to run individual AI agents.
 * 
 * ZERO-IMPACT MODE: All agents operate in read-only/specification-only mode.
 * This API does NOT modify any AgroConnectWorld backend code.
 */
@RestController
@RequestMapping("/api/agents")
public class AgentController {
    
    private static final Logger log = LoggerFactory.getLogger(AgentController.class);
    
    private final AgentRegistry agentRegistry;
    
    public AgentController(AgentRegistry agentRegistry) {
        this.agentRegistry = agentRegistry;
    }
    
    /**
     * Executes an agent with the given input.
     * 
     * @param agentName The name of the agent to execute
     * @param request The agent request
     * @return Agent response
     */
    @PostMapping("/{agentName}/run")
    public ResponseEntity<AgentResponse> runAgent(
            @PathVariable String agentName,
            @RequestBody AgentRequest request) {
        
        log.info("Executing agent: {} with session: {}", agentName, request.getSessionId());
        
        try {
            String response = executeAgent(agentName, request);
            
            AgentResponse agentResponse = new AgentResponse(
                agentName,
                response,
                request.getSessionId()
            );
            
            return ResponseEntity.ok(agentResponse);
            
        } catch (IllegalArgumentException e) {
            log.error("Invalid agent name: {}", agentName, e);
            AgentResponse errorResponse = new AgentResponse();
            errorResponse.setAgentName(agentName);
            errorResponse.setStatus("error");
            errorResponse.setResponse("Invalid agent name: " + agentName);
            return ResponseEntity.badRequest().body(errorResponse);
            
        } catch (Exception e) {
            log.error("Error executing agent: {}", agentName, e);
            AgentResponse errorResponse = new AgentResponse();
            errorResponse.setAgentName(agentName);
            errorResponse.setStatus("error");
            errorResponse.setResponse("Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Executes the appropriate agent method based on agent name.
     */
    private String executeAgent(String agentName, AgentRequest request) {
        String normalizedName = agentName.toLowerCase();
        String input = request.getInput();
        String sessionId = request.getSessionId() != null ? request.getSessionId() : "default-session";
        
        switch (normalizedName) {
            case "cto_agent":
            case "supervisor_agent":
                CTOAgent cto = agentRegistry.getCTOAgent();
                return cto.reviewArchitecture(input, sessionId);
                
            case "architect_agent":
                ArchitectAgent architect = agentRegistry.getArchitectAgent();
                return architect.designArchitecture(input, sessionId);
                
            case "engineer_agent":
                EngineerAgent engineer = agentRegistry.getEngineerAgent();
                return engineer.createImplementationSpec(input, sessionId);
                
            case "devops_agent":
                DevOpsAgent devops = agentRegistry.getDevOpsAgent();
                return devops.analyzeInfrastructure(sessionId);
                
            case "fullstack_agent":
                FullStackAgent fullstack = agentRegistry.getFullStackAgent();
                return fullstack.suggestComponent(input, sessionId);
                
            case "product_manager_agent":
                ProductManagerAgent pm = agentRegistry.getProductManagerAgent();
                return pm.createEpic(input, sessionId);
                
            case "qa_agent":
                QAAgent qa = agentRegistry.getQAAgent();
                return qa.createTestPlan(input, sessionId);
                
            default:
                throw new IllegalArgumentException("Unknown agent: " + agentName);
        }
    }
    
    /**
     * Gets list of available agents.
     * 
     * @return List of agent names
     */
    @GetMapping
    public ResponseEntity<java.util.Map<String, Object>> listAgents() {
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("agents", agentRegistry.getAgentNames());
        response.put("total", agentRegistry.getAgentNames().size());
        return ResponseEntity.ok(response);
    }
}



