package com.ai.company.api;

import com.ai.company.api.dto.WorkflowRequest;
import com.ai.company.api.dto.WorkflowResponse;
import com.ai.company.pipelines.architecture.ArchitectureWorkflow;
import com.ai.company.pipelines.development.DevelopmentWorkflow;
import com.ai.company.pipelines.planning.PlanningWorkflow;
import com.ai.company.pipelines.review.ReviewWorkflow;
import com.ai.company.pipelines.testing.TestingWorkflow;
import com.ai.company.registry.AgentRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for workflow execution.
 * 
 * Provides endpoints to run multi-agent workflows:
 * - Planning
 * - Architecture
 * - Development
 * - Review
 * - Testing
 * 
 * ZERO-IMPACT MODE: All workflows produce specifications only.
 * This API does NOT modify any AgroConnectWorld backend code.
 */
@RestController
@RequestMapping("/api/workflows")
public class WorkflowController {
    
    private static final Logger log = LoggerFactory.getLogger(WorkflowController.class);
    
    private final AgentRegistry agentRegistry;
    private final PlanningWorkflow planningWorkflow;
    private final ArchitectureWorkflow architectureWorkflow;
    private final DevelopmentWorkflow developmentWorkflow;
    private final ReviewWorkflow reviewWorkflow;
    private final TestingWorkflow testingWorkflow;
    
    public WorkflowController(AgentRegistry agentRegistry) {
        this.agentRegistry = agentRegistry;
        
        // Initialize workflows
        this.planningWorkflow = new PlanningWorkflow(
            agentRegistry.getProductManagerAgent(),
            agentRegistry.getCTOAgent()
        );
        
        this.architectureWorkflow = new ArchitectureWorkflow(
            agentRegistry.getArchitectAgent(),
            agentRegistry.getDevOpsAgent(),
            agentRegistry.getCTOAgent()
        );
        
        this.developmentWorkflow = new DevelopmentWorkflow(
            agentRegistry.getEngineerAgent(),
            agentRegistry.getFullStackAgent(),
            agentRegistry.getCTOAgent()
        );
        
        this.reviewWorkflow = new ReviewWorkflow(
            agentRegistry.getCTOAgent(),
            agentRegistry.getArchitectAgent(),
            agentRegistry.getEngineerAgent(),
            agentRegistry.getDevOpsAgent(),
            agentRegistry.getQAAgent()
        );
        
        this.testingWorkflow = new TestingWorkflow(
            agentRegistry.getQAAgent(),
            agentRegistry.getProductManagerAgent(),
            agentRegistry.getEngineerAgent(),
            agentRegistry.getFullStackAgent(),
            agentRegistry.getCTOAgent()
        );
    }
    
    /**
     * Executes a workflow by name.
     * 
     * @param workflowName The name of the workflow
     * @param request The workflow request
     * @return Workflow response
     */
    @PostMapping("/{workflowName}/run")
    public ResponseEntity<WorkflowResponse> runWorkflow(
            @PathVariable String workflowName,
            @RequestBody WorkflowRequest request) {
        
        log.info("Executing workflow: {} with session: {}", workflowName, request.getSessionId());
        
        try {
            WorkflowResponse response = executeWorkflow(workflowName, request);
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.error("Invalid workflow name: {}", workflowName, e);
            WorkflowResponse errorResponse = new WorkflowResponse(workflowName, request.getSessionId());
            errorResponse.setStatus("error");
            errorResponse.setResults(Map.of("error", "Invalid workflow name: " + workflowName));
            return ResponseEntity.badRequest().body(errorResponse);
            
        } catch (Exception e) {
            log.error("Error executing workflow: {}", workflowName, e);
            WorkflowResponse errorResponse = new WorkflowResponse(workflowName, request.getSessionId());
            errorResponse.setStatus("error");
            errorResponse.setResults(Map.of("error", e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Executes the appropriate workflow based on workflow name.
     */
    private WorkflowResponse executeWorkflow(String workflowName, WorkflowRequest request) {
        String normalizedName = workflowName.toLowerCase();
        String input = request.getInput();
        String sessionId = request.getSessionId() != null ? request.getSessionId() : "default-session";
        
        WorkflowResponse response = new WorkflowResponse(normalizedName, sessionId);
        List<WorkflowResponse.ExecutionStep> steps = new ArrayList<>();
        Map<String, Object> results = new HashMap<>();
        
        switch (normalizedName) {
            case "planning":
                PlanningWorkflow.PlanningResult planningResult = planningWorkflow.execute(input, "", sessionId);
                results.put("epics", planningResult.getEpics());
                results.put("user_stories", planningResult.getUserStories());
                results.put("acceptance_criteria", planningResult.getAcceptanceCriteria());
                results.put("cto_review", planningResult.getCtoReview());
                addStep(steps, "create_epics", "product_manager_agent", "completed", planningResult.getEpics());
                addStep(steps, "create_user_stories", "product_manager_agent", "completed", planningResult.getUserStories());
                addStep(steps, "cto_review", "cto_agent", "completed", planningResult.getCtoReview());
                break;
                
            case "architecture":
                ArchitectureWorkflow.ArchitectureResult archResult = architectureWorkflow.execute(input, sessionId);
                results.put("architecture_design", archResult.getArchitectureDesign());
                results.put("api_contracts", archResult.getApiContracts());
                results.put("service_diagrams", archResult.getServiceDiagrams());
                results.put("cto_approval", archResult.getCtoApproval());
                addStep(steps, "design_architecture", "architect_agent", "completed", archResult.getArchitectureDesign());
                addStep(steps, "infrastructure_analysis", "devops_agent", "completed", archResult.getInfrastructureAnalysis());
                addStep(steps, "cto_approval", "cto_agent", "completed", archResult.getCtoApproval());
                break;
                
            case "development":
                DevelopmentWorkflow.DevelopmentResult devResult = developmentWorkflow.execute(input, sessionId);
                results.put("backend_specs", devResult.getBackendSpecs());
                results.put("frontend_specs", devResult.getFrontendSpecs());
                results.put("pseudo_code", devResult.getPseudoCode());
                results.put("integration_specs", devResult.getIntegrationSpecs());
                addStep(steps, "backend_specs", "engineer_agent", "completed", devResult.getBackendSpecs());
                addStep(steps, "frontend_specs", "fullstack_agent", "completed", devResult.getFrontendSpecs());
                addStep(steps, "cto_review", "cto_agent", "completed", devResult.getCtoReview());
                break;
                
            case "review":
                ReviewWorkflow.ReviewResult reviewResult = reviewWorkflow.execute(input, sessionId);
                results.put("compliance_check", reviewResult.getComplianceCheck());
                results.put("architecture_review", reviewResult.getArchitectureReview());
                results.put("implementation_review", reviewResult.getImplementationReview());
                results.put("final_approval", reviewResult.getFinalApproval());
                addStep(steps, "compliance_check", "cto_agent", "completed", reviewResult.getComplianceCheck());
                addStep(steps, "architecture_review", "architect_agent", "completed", reviewResult.getArchitectureReview());
                addStep(steps, "final_approval", "cto_agent", "completed", reviewResult.getFinalApproval());
                break;
                
            case "testing":
                TestingWorkflow.TestingResult testResult = testingWorkflow.execute(input, "", sessionId);
                results.put("test_plans", testResult.getTestPlans());
                results.put("postman_collections", testResult.getPostmanCollections());
                results.put("e2e_flows", testResult.getE2eFlows());
                results.put("cto_approval", testResult.getCtoApproval());
                addStep(steps, "create_test_plans", "qa_agent", "completed", testResult.getTestPlans());
                addStep(steps, "design_postman_collections", "qa_agent", "completed", testResult.getPostmanCollections());
                addStep(steps, "cto_approval", "cto_agent", "completed", testResult.getCtoApproval());
                break;
                
            default:
                throw new IllegalArgumentException("Unknown workflow: " + workflowName);
        }
        
        response.setResults(results);
        response.setExecutionSteps(steps);
        return response;
    }
    
    private void addStep(List<WorkflowResponse.ExecutionStep> steps, String stepName,
                        String agent, String status, String output) {
        WorkflowResponse.ExecutionStep step = new WorkflowResponse.ExecutionStep();
        step.setStepName(stepName);
        step.setAgent(agent);
        step.setStatus(status);
        step.setOutput(output);
        steps.add(step);
    }
    
    /**
     * Gets list of available workflows.
     * 
     * @return List of workflow names
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> listWorkflows() {
        Map<String, Object> response = new HashMap<>();
        response.put("workflows", List.of("planning", "architecture", "development", "review", "testing"));
        response.put("total", 5);
        return ResponseEntity.ok(response);
    }
}



