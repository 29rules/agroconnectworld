package com.ai.company.observability;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Service for managing audit trails and execution logs.
 * 
 * Provides:
 * - Logging of agent executions
 * - Logging of workflow executions
 * - Query capabilities
 * - Audit trail persistence
 */
@Service
public class AuditTrailService {
    
    private static final Logger log = LoggerFactory.getLogger(AuditTrailService.class);
    
    // In-memory storage (in production, use database)
    private final Map<UUID, AgentExecutionLog> agentLogs = new ConcurrentHashMap<>();
    private final Map<UUID, WorkflowExecutionLog> workflowLogs = new ConcurrentHashMap<>();
    
    // Indexes for fast lookup
    private final Map<String, List<UUID>> agentNameIndex = new ConcurrentHashMap<>();
    private final Map<String, List<UUID>> sessionIndex = new ConcurrentHashMap<>();
    private final Map<String, List<UUID>> workflowNameIndex = new ConcurrentHashMap<>();
    
    /**
     * Logs an agent execution.
     * 
     * @param executionLog The agent execution log
     * @return The logged execution (with generated UUID if not set)
     */
    public AgentExecutionLog logAgentExecution(AgentExecutionLog executionLog) {
        if (executionLog.getExecutionId() == null) {
            executionLog.setExecutionId(UUID.randomUUID());
        }
        
        agentLogs.put(executionLog.getExecutionId(), executionLog);
        
        // Update indexes
        if (executionLog.getAgentName() != null) {
            agentNameIndex.computeIfAbsent(executionLog.getAgentName(), k -> new ArrayList<>())
                .add(executionLog.getExecutionId());
        }
        
        if (executionLog.getSessionId() != null) {
            sessionIndex.computeIfAbsent(executionLog.getSessionId(), k -> new ArrayList<>())
                .add(executionLog.getExecutionId());
        }
        
        log.debug("Logged agent execution: {}", executionLog.getExecutionId());
        
        return executionLog;
    }
    
    /**
     * Logs a workflow execution.
     * 
     * @param workflowLog The workflow execution log
     * @return The logged workflow (with generated UUID if not set)
     */
    public WorkflowExecutionLog logWorkflowExecution(WorkflowExecutionLog workflowLog) {
        if (workflowLog.getWorkflowExecutionId() == null) {
            workflowLog.setWorkflowExecutionId(UUID.randomUUID());
        }
        
        workflowLogs.put(workflowLog.getWorkflowExecutionId(), workflowLog);
        
        // Update indexes
        if (workflowLog.getWorkflowName() != null) {
            workflowNameIndex.computeIfAbsent(workflowLog.getWorkflowName(), k -> new ArrayList<>())
                .add(workflowLog.getWorkflowExecutionId());
        }
        
        if (workflowLog.getSessionId() != null) {
            sessionIndex.computeIfAbsent(workflowLog.getSessionId(), k -> new ArrayList<>())
                .add(workflowLog.getWorkflowExecutionId());
        }
        
        log.debug("Logged workflow execution: {}", workflowLog.getWorkflowExecutionId());
        
        return workflowLog;
    }
    
    /**
     * Gets an agent execution log by ID.
     * 
     * @param executionId The execution ID
     * @return The agent execution log, or null if not found
     */
    public AgentExecutionLog getAgentExecution(UUID executionId) {
        return agentLogs.get(executionId);
    }
    
    /**
     * Gets a workflow execution log by ID.
     * 
     * @param workflowExecutionId The workflow execution ID
     * @return The workflow execution log, or null if not found
     */
    public WorkflowExecutionLog getWorkflowExecution(UUID workflowExecutionId) {
        return workflowLogs.get(workflowExecutionId);
    }
    
    /**
     * Gets all agent executions for a specific agent.
     * 
     * @param agentName The agent name
     * @return List of agent execution logs
     */
    public List<AgentExecutionLog> getAgentExecutions(String agentName) {
        List<UUID> executionIds = agentNameIndex.getOrDefault(agentName, Collections.emptyList());
        return executionIds.stream()
            .map(agentLogs::get)
            .filter(Objects::nonNull)
            .sorted(Comparator.comparing(AgentExecutionLog::getTimestamp).reversed())
            .collect(Collectors.toList());
    }
    
    /**
     * Gets all executions for a session.
     * 
     * @param sessionId The session ID
     * @return Map of execution type to list of logs
     */
    public Map<String, List<?>> getSessionExecutions(String sessionId) {
        List<UUID> executionIds = sessionIndex.getOrDefault(sessionId, Collections.emptyList());
        
        Map<String, List<?>> result = new HashMap<>();
        List<AgentExecutionLog> agentExecutions = new ArrayList<>();
        List<WorkflowExecutionLog> workflowExecutions = new ArrayList<>();
        
        for (UUID id : executionIds) {
            AgentExecutionLog agentLog = agentLogs.get(id);
            if (agentLog != null) {
                agentExecutions.add(agentLog);
            }
            
            WorkflowExecutionLog workflowLog = workflowLogs.get(id);
            if (workflowLog != null) {
                workflowExecutions.add(workflowLog);
            }
        }
        
        result.put("agent_executions", agentExecutions);
        result.put("workflow_executions", workflowExecutions);
        
        return result;
    }
    
    /**
     * Gets all workflow executions for a specific workflow.
     * 
     * @param workflowName The workflow name
     * @return List of workflow execution logs
     */
    public List<WorkflowExecutionLog> getWorkflowExecutions(String workflowName) {
        List<UUID> executionIds = workflowNameIndex.getOrDefault(workflowName, Collections.emptyList());
        return executionIds.stream()
            .map(workflowLogs::get)
            .filter(Objects::nonNull)
            .sorted(Comparator.comparing(WorkflowExecutionLog::getStartTimestamp).reversed())
            .collect(Collectors.toList());
    }
    
    /**
     * Gets agent executions within a time range.
     * 
     * @param startTime Start time (inclusive)
     * @param endTime End time (inclusive)
     * @return List of agent execution logs
     */
    public List<AgentExecutionLog> getAgentExecutions(Instant startTime, Instant endTime) {
        return agentLogs.values().stream()
            .filter(log -> !log.getTimestamp().isBefore(startTime) && !log.getTimestamp().isAfter(endTime))
            .sorted(Comparator.comparing(AgentExecutionLog::getTimestamp).reversed())
            .collect(Collectors.toList());
    }
    
    /**
     * Gets workflow executions within a time range.
     * 
     * @param startTime Start time (inclusive)
     * @param endTime End time (inclusive)
     * @return List of workflow execution logs
     */
    public List<WorkflowExecutionLog> getWorkflowExecutions(Instant startTime, Instant endTime) {
        return workflowLogs.values().stream()
            .filter(log -> !log.getStartTimestamp().isBefore(startTime) && 
                          (log.getEndTimestamp() == null || !log.getEndTimestamp().isAfter(endTime)))
            .sorted(Comparator.comparing(WorkflowExecutionLog::getStartTimestamp).reversed())
            .collect(Collectors.toList());
    }
    
    /**
     * Gets executions with errors.
     * 
     * @return List of agent execution logs with errors
     */
    public List<AgentExecutionLog> getAgentExecutionsWithErrors() {
        return agentLogs.values().stream()
            .filter(AgentExecutionLog::hasErrors)
            .sorted(Comparator.comparing(AgentExecutionLog::getTimestamp).reversed())
            .collect(Collectors.toList());
    }
    
    /**
     * Gets workflow executions with errors.
     * 
     * @return List of workflow execution logs with errors
     */
    public List<WorkflowExecutionLog> getWorkflowExecutionsWithErrors() {
        return workflowLogs.values().stream()
            .filter(WorkflowExecutionLog::hasErrors)
            .sorted(Comparator.comparing(WorkflowExecutionLog::getStartTimestamp).reversed())
            .collect(Collectors.toList());
    }
    
    /**
     * Gets statistics for an agent.
     * 
     * @param agentName The agent name
     * @return Statistics map
     */
    public Map<String, Object> getAgentStatistics(String agentName) {
        List<AgentExecutionLog> executions = getAgentExecutions(agentName);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("total_executions", executions.size());
        stats.put("successful", executions.stream()
            .filter(e -> e.getStatus() == AgentExecutionLog.ExecutionStatus.SUCCESS)
            .count());
        stats.put("with_warnings", executions.stream()
            .filter(AgentExecutionLog::hasWarnings)
            .count());
        stats.put("with_errors", executions.stream()
            .filter(AgentExecutionLog::hasErrors)
            .count());
        stats.put("average_duration_ms", executions.stream()
            .filter(e -> e.getDurationMs() != null)
            .mapToLong(AgentExecutionLog::getDurationMs)
            .average()
            .orElse(0.0));
        stats.put("total_tokens", executions.stream()
            .filter(e -> e.getTokenCount() != null)
            .mapToInt(AgentExecutionLog::getTokenCount)
            .sum());
        
        return stats;
    }
    
    /**
     * Gets statistics for a workflow.
     * 
     * @param workflowName The workflow name
     * @return Statistics map
     */
    public Map<String, Object> getWorkflowStatistics(String workflowName) {
        List<WorkflowExecutionLog> executions = getWorkflowExecutions(workflowName);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("total_executions", executions.size());
        stats.put("successful", executions.stream()
            .filter(e -> e.getStatus() == WorkflowExecutionLog.WorkflowStatus.SUCCESS)
            .count());
        stats.put("with_warnings", executions.stream()
            .filter(WorkflowExecutionLog::hasWarnings)
            .count());
        stats.put("with_errors", executions.stream()
            .filter(WorkflowExecutionLog::hasErrors)
            .count());
        stats.put("average_duration_ms", executions.stream()
            .filter(e -> e.getTotalDurationMs() != null)
            .mapToLong(WorkflowExecutionLog::getTotalDurationMs)
            .average()
            .orElse(0.0));
        stats.put("average_completion_percentage", executions.stream()
            .mapToDouble(WorkflowExecutionLog::getCompletionPercentage)
            .average()
            .orElse(0.0));
        
        return stats;
    }
    
    /**
     * Clears all logs (for testing/cleanup).
     */
    public void clearAllLogs() {
        agentLogs.clear();
        workflowLogs.clear();
        agentNameIndex.clear();
        sessionIndex.clear();
        workflowNameIndex.clear();
        log.info("Cleared all audit trail logs");
    }
}



