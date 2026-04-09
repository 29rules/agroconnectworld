package com.ai.company.observability;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;

/**
 * Formats execution logs for output.
 * 
 * Supports:
 * - JSON formatting
 * - Human-readable text formatting
 * - Summary formatting
 * - Structured logging
 */
public class LogFormatter {
    
    private static final Logger log = LoggerFactory.getLogger(LogFormatter.class);
    
    private final ObjectMapper objectMapper;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    public LogFormatter() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }
    
    /**
     * Formats agent execution log as JSON.
     * 
     * @param executionLog The agent execution log
     * @return JSON string
     */
    public String formatAsJson(AgentExecutionLog executionLog) {
        try {
            return objectMapper.writeValueAsString(executionLog);
        } catch (Exception e) {
            log.error("Error formatting agent execution log as JSON", e);
            return "{\"error\": \"Failed to format log\"}";
        }
    }
    
    /**
     * Formats workflow execution log as JSON.
     * 
     * @param workflowLog The workflow execution log
     * @return JSON string
     */
    public String formatAsJson(WorkflowExecutionLog workflowLog) {
        try {
            return objectMapper.writeValueAsString(workflowLog);
        } catch (Exception e) {
            log.error("Error formatting workflow execution log as JSON", e);
            return "{\"error\": \"Failed to format log\"}";
        }
    }
    
    /**
     * Formats agent execution log as human-readable text.
     * 
     * @param executionLog The agent execution log
     * @return Formatted text string
     */
    public String formatAsText(AgentExecutionLog executionLog) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("=== Agent Execution Log ===\n");
        sb.append(String.format("Execution ID: %s\n", executionLog.getExecutionId()));
        sb.append(String.format("Timestamp: %s\n", 
            executionLog.getTimestamp().toString()));
        sb.append(String.format("Agent: %s\n", executionLog.getAgentName()));
        sb.append(String.format("Session ID: %s\n", executionLog.getSessionId()));
        sb.append(String.format("Status: %s\n", executionLog.getStatus()));
        
        if (executionLog.getWorkflowName() != null) {
            sb.append(String.format("Workflow: %s\n", executionLog.getWorkflowName()));
        }
        
        if (executionLog.getTaskId() != null) {
            sb.append(String.format("Task ID: %s\n", executionLog.getTaskId()));
        }
        
        sb.append("\n--- Input ---\n");
        sb.append(truncate(executionLog.getPromptSummary(), 500));
        sb.append(String.format("\n(Prompt length: %d chars)\n", executionLog.getPromptLength()));
        
        sb.append("\n--- Output ---\n");
        sb.append(truncate(executionLog.getResponseSummary(), 500));
        sb.append(String.format("\n(Response length: %d chars)\n", executionLog.getResponseLength()));
        
        if (executionLog.getDurationMs() != null) {
            sb.append(String.format("\nDuration: %d ms\n", executionLog.getDurationMs()));
        }
        
        if (executionLog.getTokenCount() != null) {
            sb.append(String.format("Token count: %d\n", executionLog.getTokenCount()));
        }
        
        if (executionLog.getModelUsed() != null) {
            sb.append(String.format("Model: %s\n", executionLog.getModelUsed()));
        }
        
        if (executionLog.hasWarnings()) {
            sb.append("\n--- Warnings ---\n");
            for (String warning : executionLog.getWarnings()) {
                sb.append(String.format("- %s\n", warning));
            }
        }
        
        if (executionLog.hasErrors()) {
            sb.append("\n--- Errors ---\n");
            for (String error : executionLog.getErrors()) {
                sb.append(String.format("- %s\n", error));
            }
        }
        
        return sb.toString();
    }
    
    /**
     * Formats workflow execution log as human-readable text.
     * 
     * @param workflowLog The workflow execution log
     * @return Formatted text string
     */
    public String formatAsText(WorkflowExecutionLog workflowLog) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("=== Workflow Execution Log ===\n");
        sb.append(String.format("Workflow Execution ID: %s\n", workflowLog.getWorkflowExecutionId()));
        sb.append(String.format("Workflow: %s\n", workflowLog.getWorkflowName()));
        sb.append(String.format("Session ID: %s\n", workflowLog.getSessionId()));
        sb.append(String.format("Status: %s\n", workflowLog.getStatus()));
        sb.append(String.format("Start: %s\n", workflowLog.getStartTimestamp().toString()));
        
        if (workflowLog.getEndTimestamp() != null) {
            sb.append(String.format("End: %s\n", workflowLog.getEndTimestamp().toString()));
        }
        
        if (workflowLog.getTotalDurationMs() != null) {
            sb.append(String.format("Duration: %d ms\n", workflowLog.getTotalDurationMs()));
        }
        
        sb.append(String.format("\nSteps: %d/%d completed (%.1f%%)\n",
            workflowLog.getCompletedSteps(),
            workflowLog.getTotalSteps(),
            workflowLog.getCompletionPercentage()));
        
        sb.append(String.format("Failed steps: %d\n", workflowLog.getFailedSteps()));
        
        if (workflowLog.getTotalTokenCount() != null) {
            sb.append(String.format("Total tokens: %d\n", workflowLog.getTotalTokenCount()));
        }
        
        if (workflowLog.getInputSummary() != null) {
            sb.append("\n--- Input ---\n");
            sb.append(truncate(workflowLog.getInputSummary(), 500));
        }
        
        if (workflowLog.getOutputSummary() != null) {
            sb.append("\n--- Output ---\n");
            sb.append(truncate(workflowLog.getOutputSummary(), 500));
        }
        
        if (workflowLog.hasWarnings()) {
            sb.append("\n--- Warnings ---\n");
            for (String warning : workflowLog.getWarnings()) {
                sb.append(String.format("- %s\n", warning));
            }
        }
        
        if (workflowLog.hasErrors()) {
            sb.append("\n--- Errors ---\n");
            for (String error : workflowLog.getErrors()) {
                sb.append(String.format("- %s\n", error));
            }
        }
        
        sb.append("\n--- Agent Executions ---\n");
        for (AgentExecutionLog agentLog : workflowLog.getAgentExecutions()) {
            sb.append(String.format("- %s: %s (%s)\n",
                agentLog.getAgentName(),
                agentLog.getStatus(),
                agentLog.getExecutionId()));
        }
        
        return sb.toString();
    }
    
    /**
     * Formats agent execution log as summary.
     * 
     * @param executionLog The agent execution log
     * @return Summary string
     */
    public String formatSummary(AgentExecutionLog executionLog) {
        return String.format("[%s] %s - %s - %s (ID: %s)",
            executionLog.getTimestamp().toString(),
            executionLog.getAgentName(),
            executionLog.getStatus(),
            executionLog.getWorkflowName() != null ? executionLog.getWorkflowName() : "standalone",
            executionLog.getExecutionId());
    }
    
    /**
     * Formats workflow execution log as summary.
     * 
     * @param workflowLog The workflow execution log
     * @return Summary string
     */
    public String formatSummary(WorkflowExecutionLog workflowLog) {
        return String.format("[%s] %s - %s - %d/%d steps (ID: %s)",
            workflowLog.getStartTimestamp().toString(),
            workflowLog.getWorkflowName(),
            workflowLog.getStatus(),
            workflowLog.getCompletedSteps(),
            workflowLog.getTotalSteps(),
            workflowLog.getWorkflowExecutionId());
    }
    
    /**
     * Truncates text to specified length.
     */
    private String truncate(String text, int maxLength) {
        if (text == null) {
            return "(null)";
        }
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength) + "... (truncated)";
    }
}



