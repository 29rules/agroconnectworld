package com.ai.company.tests;

import com.ai.company.agents.supervisor.SupervisorAgent;
import com.ai.company.planner.PlanningOutput;
import com.ai.company.planner.TaskAssignmentEngine;
import com.ai.company.planner.TaskNode;
import com.ai.company.planner.TaskPlanner;
import com.ai.company.validation.AgentOutputValidator;
import com.ai.company.validation.ValidationResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Planner Layer Test
 * 
 * Tests the TaskPlanner system:
 * 1. Initialize TaskPlanner
 * 2. Input: "Build Order Tracking & Seller Notification System."
 * 3. Expected:
 *    - Several TaskNodes created
 *    - Agents assigned appropriately
 *    - Dependencies created
 *    - Task graph printed
 * 4. Supervisor validates plan
 * 5. Planner must NOT generate code yet (zero-impact rules)
 */
public class Test_PlannerLayer {
    
    private static final Logger log = LoggerFactory.getLogger(Test_PlannerLayer.class);
    
    private final ChatLanguageModel chatModel;
    private final TaskPlanner taskPlanner;
    private final SupervisorAgent supervisor;
    private final AgentOutputValidator validator;
    private final ObjectMapper objectMapper;
    private final Map<String, Boolean> testResults;
    
    public Test_PlannerLayer(ChatLanguageModel chatModel) {
        this.chatModel = chatModel;
        this.taskPlanner = new TaskPlanner(chatModel, new TaskAssignmentEngine());
        this.supervisor = new SupervisorAgent(chatModel);
        this.validator = new AgentOutputValidator();
        this.objectMapper = new ObjectMapper();
        this.testResults = new HashMap<>();
    }
    
    /**
     * Runs all planner tests.
     */
    public void runAllTests() {
        System.out.println("=".repeat(80));
        System.out.println("PLANNER LAYER TEST SUITE");
        System.out.println("=".repeat(80));
        System.out.println();
        
        String sessionId = "planner-test-" + System.currentTimeMillis();
        String request = "Build Order Tracking & Seller Notification System.";
        
        try {
            // Test 1: Initialize and plan
            PlanningOutput planningOutput = testTaskPlanner(request, sessionId);
            
            // Test 2: Validate task nodes
            testTaskNodes(planningOutput);
            
            // Test 3: Validate agent assignments
            testAgentAssignments(planningOutput);
            
            // Test 4: Validate dependencies
            testDependencies(planningOutput);
            
            // Test 5: Print task graph
            printTaskGraph(planningOutput);
            
            // Test 6: Supervisor validation
            testSupervisorValidation(planningOutput, sessionId);
            
            // Test 7: Zero-impact validation
            testZeroImpact(planningOutput);
            
        } catch (Exception e) {
            log.error("Error in planner test", e);
            System.out.println("  ✗ FAIL: Planner test failed with exception: " + e.getMessage());
            testResults.put("PlannerChain", false);
        }
        
        // Print summary
        printSummary();
    }
    
    /**
     * Tests TaskPlanner initialization and planning.
     */
    private PlanningOutput testTaskPlanner(String request, String sessionId) {
        String testName = "TaskPlanner_Initialization";
        System.out.println("Testing " + testName + "...");
        
        try {
            PlanningOutput output = taskPlanner.plan(request, sessionId);
            
            boolean hasPlanId = output.getPlanId() != null && !output.getPlanId().isEmpty();
            boolean hasTasks = output.getTasks() != null && !output.getTasks().isEmpty();
            boolean hasExecutionOrder = output.getExecutionOrder() != null && !output.getExecutionOrder().isEmpty();
            boolean hasAgentAssignments = output.getAgentAssignments() != null && !output.getAgentAssignments().isEmpty();
            
            boolean passed = hasPlanId && hasTasks && hasExecutionOrder && hasAgentAssignments;
            testResults.put(testName, passed);
            
            if (passed) {
                System.out.println("  ✓ PASS: " + testName);
                System.out.println("    Plan ID: " + output.getPlanId());
                System.out.println("    Total Tasks: " + output.getTasks().size());
                System.out.println("    Execution Order: " + output.getExecutionOrder().size() + " tasks");
                System.out.println("    Agent Assignments: " + output.getAgentAssignments().size() + " agents");
            } else {
                System.out.println("  ✗ FAIL: " + testName);
                System.out.println("    Plan ID: " + hasPlanId);
                System.out.println("    Tasks: " + hasTasks);
                System.out.println("    Execution Order: " + hasExecutionOrder);
                System.out.println("    Agent Assignments: " + hasAgentAssignments);
            }
            
            System.out.println();
            return output;
            
        } catch (Exception e) {
            log.error("Error testing " + testName, e);
            testResults.put(testName, false);
            System.out.println("  ✗ FAIL: " + testName + " - " + e.getMessage());
            System.out.println();
            throw new RuntimeException("TaskPlanner test failed", e);
        }
    }
    
    /**
     * Tests task nodes creation.
     */
    private void testTaskNodes(PlanningOutput output) {
        String testName = "TaskNodes_Creation";
        System.out.println("Testing " + testName + "...");
        
        try {
            List<PlanningOutput.TaskInfo> tasks = output.getTasks();
            
            // Assertion: Several TaskNodes created
            boolean hasMultipleTasks = tasks.size() >= 3;
            
            // Validate task structure
            boolean allTasksValid = tasks.stream().allMatch(task ->
                task.getId() != null && !task.getId().isEmpty() &&
                task.getDescription() != null && !task.getDescription().isEmpty() &&
                task.getAssignedAgent() != null && !task.getAssignedAgent().isEmpty()
            );
            
            boolean passed = hasMultipleTasks && allTasksValid;
            testResults.put(testName, passed);
            
            if (passed) {
                System.out.println("  ✓ PASS: " + testName);
                System.out.println("    Task Count: " + tasks.size() + " (expected >= 3)");
                System.out.println("    All Tasks Valid: Yes");
                System.out.println("    Sample Tasks:");
                for (int i = 0; i < Math.min(3, tasks.size()); i++) {
                    PlanningOutput.TaskInfo task = tasks.get(i);
                    System.out.println("      " + (i + 1) + ". " + task.getId() + ": " + 
                        truncate(task.getDescription(), 50) + " (" + task.getAssignedAgent() + ")");
                }
            } else {
                System.out.println("  ✗ FAIL: " + testName);
                System.out.println("    Task Count: " + tasks.size() + " (expected >= 3)");
                System.out.println("    All Tasks Valid: " + allTasksValid);
            }
            
            System.out.println();
            
        } catch (Exception e) {
            log.error("Error testing " + testName, e);
            testResults.put(testName, false);
            System.out.println("  ✗ FAIL: " + testName + " - " + e.getMessage());
            System.out.println();
        }
    }
    
    /**
     * Tests agent assignments.
     */
    private void testAgentAssignments(PlanningOutput output) {
        String testName = "Agent_Assignments";
        System.out.println("Testing " + testName + "...");
        
        try {
            Map<String, List<String>> agentAssignments = output.getAgentAssignments();
            List<PlanningOutput.TaskInfo> tasks = output.getTasks();
            
            // Assertion: Agents assigned appropriately
            boolean hasAssignments = agentAssignments != null && !agentAssignments.isEmpty();
            
            // Check that all tasks have agents assigned
            boolean allTasksAssigned = tasks.stream()
                .allMatch(task -> task.getAssignedAgent() != null && !task.getAssignedAgent().isEmpty());
            
            // Check for valid agent names (should match known agents)
            Set<String> validAgents = Set.of(
                "engineer_agent", "architect_agent", "fullstack_agent",
                "devops_agent", "qa_agent", "product_manager_agent", "cto_agent"
            );
            
            boolean agentsValid = tasks.stream()
                .map(PlanningOutput.TaskInfo::getAssignedAgent)
                .allMatch(agent -> agent != null && 
                    (validAgents.contains(agent.toLowerCase()) || 
                     agent.toLowerCase().contains("agent")));
            
            boolean passed = hasAssignments && allTasksAssigned && agentsValid;
            testResults.put(testName, passed);
            
            if (passed) {
                System.out.println("  ✓ PASS: " + testName);
                System.out.println("    Agent Assignments: " + agentAssignments.size() + " agents");
                System.out.println("    All Tasks Assigned: Yes");
                System.out.println("    Agents Valid: Yes");
                System.out.println("    Agent Distribution:");
                agentAssignments.forEach((agent, taskIds) -> {
                    System.out.println("      " + agent + ": " + taskIds.size() + " tasks");
                });
            } else {
                System.out.println("  ✗ FAIL: " + testName);
                System.out.println("    Has Assignments: " + hasAssignments);
                System.out.println("    All Tasks Assigned: " + allTasksAssigned);
                System.out.println("    Agents Valid: " + agentsValid);
            }
            
            System.out.println();
            
        } catch (Exception e) {
            log.error("Error testing " + testName, e);
            testResults.put(testName, false);
            System.out.println("  ✗ FAIL: " + testName + " - " + e.getMessage());
            System.out.println();
        }
    }
    
    /**
     * Tests dependencies.
     */
    private void testDependencies(PlanningOutput output) {
        String testName = "Dependencies_Creation";
        System.out.println("Testing " + testName + "...");
        
        try {
            Map<String, List<String>> dependencies = output.getDependencies();
            List<PlanningOutput.TaskInfo> tasks = output.getTasks();
            
            // Assertion: Dependencies created
            boolean hasDependencies = dependencies != null && !dependencies.isEmpty();
            
            // Check that at least some tasks have dependencies
            boolean someTasksHaveDeps = tasks.stream()
                .anyMatch(task -> task.getDependencies() != null && !task.getDependencies().isEmpty());
            
            // Validate dependency references (all dependencies should reference existing tasks)
            Set<String> taskIds = new HashSet<>();
            for (PlanningOutput.TaskInfo task : tasks) {
                taskIds.add(task.getId());
            }
            
            boolean dependenciesValid = true;
            for (PlanningOutput.TaskInfo task : tasks) {
                if (task.getDependencies() != null) {
                    for (String depId : task.getDependencies()) {
                        if (!taskIds.contains(depId) && !depId.isEmpty()) {
                            dependenciesValid = false;
                            break;
                        }
                    }
                }
            }
            
            boolean passed = hasDependencies && someTasksHaveDeps && dependenciesValid;
            testResults.put(testName, passed);
            
            if (passed) {
                System.out.println("  ✓ PASS: " + testName);
                System.out.println("    Dependencies Map: " + (dependencies != null ? "Present" : "Missing"));
                System.out.println("    Tasks with Dependencies: " + 
                    tasks.stream().filter(t -> t.getDependencies() != null && !t.getDependencies().isEmpty()).count());
                System.out.println("    Dependencies Valid: Yes");
                
                // Show sample dependencies
                System.out.println("    Sample Dependencies:");
                int count = 0;
                for (PlanningOutput.TaskInfo task : tasks) {
                    if (task.getDependencies() != null && !task.getDependencies().isEmpty() && count < 3) {
                        System.out.println("      " + task.getId() + " depends on: " + task.getDependencies());
                        count++;
                    }
                }
            } else {
                System.out.println("  ✗ FAIL: " + testName);
                System.out.println("    Has Dependencies: " + hasDependencies);
                System.out.println("    Some Tasks Have Deps: " + someTasksHaveDeps);
                System.out.println("    Dependencies Valid: " + dependenciesValid);
            }
            
            System.out.println();
            
        } catch (Exception e) {
            log.error("Error testing " + testName, e);
            testResults.put(testName, false);
            System.out.println("  ✗ FAIL: " + testName + " - " + e.getMessage());
            System.out.println();
        }
    }
    
    /**
     * Prints task graph.
     */
    private void printTaskGraph(PlanningOutput output) {
        String testName = "TaskGraph_Print";
        System.out.println("Testing " + testName + "...");
        
        try {
            System.out.println("  Task Graph:");
            System.out.println("  " + "=".repeat(76));
            
            // Print graph structure
            System.out.println("  Plan ID: " + output.getPlanId());
            System.out.println("  Original Request: " + output.getOriginalRequest());
            System.out.println("  Total Tasks: " + output.getTasks().size());
            System.out.println();
            
            // Print execution order
            System.out.println("  Execution Order:");
            List<String> executionOrder = output.getExecutionOrder();
            for (int i = 0; i < executionOrder.size(); i++) {
                String taskId = executionOrder.get(i);
                PlanningOutput.TaskInfo task = output.getTasks().stream()
                    .filter(t -> t.getId().equals(taskId))
                    .findFirst()
                    .orElse(null);
                
                if (task != null) {
                    System.out.println(String.format("    %d. [%s] %s (%s)", 
                        i + 1, task.getPriority(), truncate(task.getDescription(), 40), task.getAssignedAgent()));
                    if (task.getDependencies() != null && !task.getDependencies().isEmpty()) {
                        System.out.println("       Depends on: " + task.getDependencies());
                    }
                }
            }
            System.out.println();
            
            // Print task details
            System.out.println("  Task Details:");
            for (PlanningOutput.TaskInfo task : output.getTasks()) {
                System.out.println("    Task: " + task.getId());
                System.out.println("      Description: " + truncate(task.getDescription(), 60));
                System.out.println("      Agent: " + task.getAssignedAgent());
                System.out.println("      Priority: " + task.getPriority());
                System.out.println("      Status: " + task.getStatus());
                if (task.getEstimatedEffort() != null) {
                    System.out.println("      Estimated Effort: " + task.getEstimatedEffort() + " points");
                }
                if (task.getDependencies() != null && !task.getDependencies().isEmpty()) {
                    System.out.println("      Dependencies: " + task.getDependencies());
                }
                System.out.println();
            }
            
            // Print timeline
            if (output.getEstimatedTimeline() != null) {
                PlanningOutput.TimelineInfo timeline = output.getEstimatedTimeline();
                System.out.println("  Estimated Timeline:");
                System.out.println("    Total Effort: " + timeline.getEstimatedTotalEffort() + " points");
                System.out.println("    Critical Path Length: " + timeline.getCriticalPathLength() + " tasks");
                System.out.println("    Parallel Execution: " + (timeline.getParallelExecutionPossible() ? "Yes" : "No"));
                System.out.println("    Estimated Duration: " + timeline.getEstimatedDurationHours() + " hours");
            }
            
            System.out.println("  " + "=".repeat(76));
            System.out.println();
            
            testResults.put(testName, true);
            System.out.println("  ✓ PASS: " + testName);
            System.out.println();
            
        } catch (Exception e) {
            log.error("Error printing task graph", e);
            testResults.put(testName, false);
            System.out.println("  ✗ FAIL: " + testName + " - " + e.getMessage());
            System.out.println();
        }
    }
    
    /**
     * Tests supervisor validation.
     */
    private void testSupervisorValidation(PlanningOutput output, String sessionId) {
        String testName = "Supervisor_Validation";
        System.out.println("Testing " + testName + "...");
        
        try {
            // Convert planning output to string for supervisor
            String planSummary = formatPlanSummary(output);
            
            // Supervisor enforces constraints
            var enforcement = supervisor.enforceConstraints(planSummary, sessionId);
            
            // Supervisor reviews decision
            var decision = supervisor.reviewDecision(
                "TaskPlanner",
                planSummary,
                "Task planning output for Order Tracking & Seller Notification System",
                sessionId
            );
            
            boolean isCompliant = enforcement.isCompliant();
            boolean isApproved = decision.getStatus().equals("APPROVED") || 
                               decision.getStatus().equals("APPROVED_WITH_CONDITIONS");
            
            // Supervisor should approve compliant plans
            boolean supervisorWorking = (isCompliant && isApproved) || (!isCompliant && !isApproved);
            
            boolean passed = supervisorWorking;
            testResults.put(testName, passed);
            
            if (passed) {
                System.out.println("  ✓ PASS: " + testName);
                System.out.println("    Compliance: " + (isCompliant ? "COMPLIANT" : "NON-COMPLIANT"));
                System.out.println("    Decision: " + decision.getStatus());
                System.out.println("    Violations: " + enforcement.getViolations().size());
                System.out.println("    Supervisor Working: " + (supervisorWorking ? "Yes" : "No"));
            } else {
                System.out.println("  ✗ FAIL: " + testName);
                System.out.println("    Compliance: " + (isCompliant ? "COMPLIANT" : "NON-COMPLIANT"));
                System.out.println("    Decision: " + decision.getStatus());
                System.out.println("    Violations: " + enforcement.getViolations().size());
                System.out.println("    Supervisor not working correctly");
            }
            
            System.out.println();
            
        } catch (Exception e) {
            log.error("Error testing " + testName, e);
            testResults.put(testName, false);
            System.out.println("  ✗ FAIL: " + testName + " - " + e.getMessage());
            System.out.println();
        }
    }
    
    /**
     * Tests zero-impact rules.
     */
    private void testZeroImpact(PlanningOutput output) {
        String testName = "Zero_Impact_Validation";
        System.out.println("Testing " + testName + "...");
        
        try {
            // Convert planning output to JSON string
            String planJson = objectMapper.writeValueAsString(output);
            
            // Validate no code modification attempts
            ValidationResult validation = validator.validate(planJson, "TaskPlanner");
            boolean noCodeMod = validation.getComplianceCheck() != null && 
                               validation.getComplianceCheck().isNoCodeModifications();
            
            // Check that plan doesn't contain code modification keywords
            String planText = planJson.toLowerCase();
            boolean noModificationKeywords = !planText.contains("modify") ||
                (!planText.contains("change existing") && !planText.contains("update code") &&
                 !planText.contains("edit file") && !planText.contains("delete code"));
            
            // Assertion: Planner must NOT generate code yet
            boolean isPlanOnly = planText.contains("plan") || planText.contains("spec") || 
                                planText.contains("task") || planText.contains("design");
            
            boolean passed = noCodeMod && isPlanOnly;
            testResults.put(testName, passed);
            
            if (passed) {
                System.out.println("  ✓ PASS: " + testName);
                System.out.println("    No Code Modification: Yes");
                System.out.println("    Plan Only: Yes");
                System.out.println("    Zero-Impact Compliant: Yes");
            } else {
                System.out.println("  ✗ FAIL: " + testName);
                System.out.println("    No Code Modification: " + noCodeMod);
                System.out.println("    Plan Only: " + isPlanOnly);
                System.out.println("    Validation Errors: " + validation.getErrors());
            }
            
            System.out.println();
            
        } catch (Exception e) {
            log.error("Error testing " + testName, e);
            testResults.put(testName, false);
            System.out.println("  ✗ FAIL: " + testName + " - " + e.getMessage());
            System.out.println();
        }
    }
    
    /**
     * Formats plan summary for supervisor.
     */
    private String formatPlanSummary(PlanningOutput output) {
        StringBuilder summary = new StringBuilder();
        summary.append("Task Planning Output:\n");
        summary.append("Plan ID: ").append(output.getPlanId()).append("\n");
        summary.append("Original Request: ").append(output.getOriginalRequest()).append("\n");
        summary.append("Total Tasks: ").append(output.getTasks().size()).append("\n");
        summary.append("Execution Order: ").append(output.getExecutionOrder().size()).append(" tasks\n\n");
        
        summary.append("Tasks:\n");
        for (PlanningOutput.TaskInfo task : output.getTasks()) {
            summary.append("  - ").append(task.getId()).append(": ").append(task.getDescription())
                   .append(" (").append(task.getAssignedAgent()).append(")\n");
            if (task.getDependencies() != null && !task.getDependencies().isEmpty()) {
                summary.append("    Dependencies: ").append(task.getDependencies()).append("\n");
            }
        }
        
        return summary.toString();
    }
    
    /**
     * Truncates string to specified length.
     */
    private String truncate(String str, int maxLength) {
        if (str == null) {
            return "null";
        }
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength) + "...";
    }
    
    /**
     * Prints test summary.
     */
    private void printSummary() {
        System.out.println("=".repeat(80));
        System.out.println("TEST SUMMARY");
        System.out.println("=".repeat(80));
        System.out.println();
        
        int total = testResults.size();
        int passed = (int) testResults.values().stream().filter(b -> b).count();
        int failed = total - passed;
        
        System.out.println("Total Tests: " + total);
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        System.out.println();
        
        if (failed > 0) {
            System.out.println("Failed Tests:");
            for (Map.Entry<String, Boolean> entry : testResults.entrySet()) {
                if (!entry.getValue()) {
                    System.out.println("  ✗ " + entry.getKey());
                }
            }
        }
        
        System.out.println();
        System.out.println("=".repeat(80));
        
        if (failed > 0) {
            System.out.println("RESULT: FAIL - " + failed + " test(s) failed");
            if (System.getProperty("runAllTests.noExit") == null) {
                System.exit(1);
            }
        } else {
            System.out.println("RESULT: PASS - All tests passed");
            if (System.getProperty("runAllTests.noExit") == null) {
                System.exit(0);
            }
        }
    }
    
    /**
     * Main method.
     */
    public static void main(String[] args) {
        // Get API key from environment
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("ERROR: OPENAI_API_KEY environment variable not set");
            System.exit(1);
        }
        
        // Initialize chat model
        ChatLanguageModel chatModel = OpenAiChatModel.builder()
            .apiKey(apiKey)
            .modelName("gpt-4o-mini")
            .temperature(0.7)
            .build();
        
        // Run tests
        Test_PlannerLayer test = new Test_PlannerLayer(chatModel);
        test.runAllTests();
    }
}

