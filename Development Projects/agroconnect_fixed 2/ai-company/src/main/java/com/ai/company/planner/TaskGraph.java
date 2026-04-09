package com.ai.company.planner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a Directed Acyclic Graph (DAG) of tasks.
 * 
 * The task graph maintains:
 * - All task nodes
 * - Dependency relationships
 * - Topological ordering for execution
 * - Cycle detection
 * 
 * This is a DAG structure ensuring no circular dependencies.
 */
public class TaskGraph {
    
    private static final Logger log = LoggerFactory.getLogger(TaskGraph.class);
    
    // Task storage: taskId -> TaskNode
    private final Map<String, TaskNode> tasks = new HashMap<>();
    
    // Reverse dependencies: taskId -> tasks that depend on it
    private final Map<String, Set<String>> reverseDependencies = new HashMap<>();
    
    /**
     * Adds a task node to the graph.
     * 
     * @param task The task node to add
     * @throws IllegalArgumentException if task ID already exists
     */
    public void addTask(TaskNode task) {
        if (tasks.containsKey(task.getId())) {
            throw new IllegalArgumentException("Task with ID already exists: " + task.getId());
        }
        
        tasks.put(task.getId(), task);
        
        // Update reverse dependencies
        for (String depId : task.getDependencies()) {
            reverseDependencies.computeIfAbsent(depId, k -> new HashSet<>()).add(task.getId());
        }
        
        log.debug("Added task to graph: {}", task.getId());
    }
    
    /**
     * Gets a task by ID.
     * 
     * @param taskId The task ID
     * @return The task node, or null if not found
     */
    public TaskNode getTask(String taskId) {
        return tasks.get(taskId);
    }
    
    /**
     * Gets all tasks in the graph.
     * 
     * @return Unmodifiable collection of all tasks
     */
    public Collection<TaskNode> getAllTasks() {
        return Collections.unmodifiableCollection(tasks.values());
    }
    
    /**
     * Gets tasks that depend on a given task.
     * 
     * @param taskId The task ID
     * @return Set of task IDs that depend on this task
     */
    public Set<String> getDependents(String taskId) {
        return Collections.unmodifiableSet(
            reverseDependencies.getOrDefault(taskId, Collections.emptySet()));
    }
    
    /**
     * Validates the graph for cycles.
     * 
     * @return true if graph is acyclic (valid DAG)
     */
    public boolean isAcyclic() {
        return findCycle() == null;
    }
    
    /**
     * Finds a cycle in the graph if one exists.
     * 
     * @return List of task IDs forming a cycle, or null if no cycle
     */
    public List<String> findCycle() {
        Set<String> visited = new HashSet<>();
        Set<String> recursionStack = new HashSet<>();
        Map<String, String> parent = new HashMap<>();
        
        for (String taskId : tasks.keySet()) {
            if (!visited.contains(taskId)) {
                List<String> cycle = dfsCycle(taskId, visited, recursionStack, parent);
                if (cycle != null) {
                    return cycle;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Depth-first search for cycle detection.
     */
    private List<String> dfsCycle(String taskId, Set<String> visited, Set<String> recursionStack,
                                  Map<String, String> parent) {
        visited.add(taskId);
        recursionStack.add(taskId);
        
        TaskNode task = tasks.get(taskId);
        if (task != null) {
            for (String depId : task.getDependencies()) {
                if (tasks.containsKey(depId)) {
                    if (!visited.contains(depId)) {
                        parent.put(depId, taskId);
                        List<String> cycle = dfsCycle(depId, visited, recursionStack, parent);
                        if (cycle != null) {
                            return cycle;
                        }
                    } else if (recursionStack.contains(depId)) {
                        // Found cycle
                        List<String> cycle = new ArrayList<>();
                        String current = taskId;
                        while (current != null && !current.equals(depId)) {
                            cycle.add(current);
                            current = parent.get(current);
                        }
                        cycle.add(depId);
                        cycle.add(taskId);
                        Collections.reverse(cycle);
                        return cycle;
                    }
                }
            }
        }
        
        recursionStack.remove(taskId);
        return null;
    }
    
    /**
     * Performs topological sort to get execution order.
     * 
     * @return List of task IDs in topological order
     * @throws IllegalStateException if graph contains cycles
     */
    public List<String> topologicalSort() {
        if (!isAcyclic()) {
            throw new IllegalStateException("Cannot perform topological sort: graph contains cycles");
        }
        
        Map<String, Integer> inDegree = new HashMap<>();
        for (String taskId : tasks.keySet()) {
            inDegree.put(taskId, 0);
        }
        
        // Calculate in-degrees
        for (TaskNode task : tasks.values()) {
            for (String depId : task.getDependencies()) {
                if (tasks.containsKey(depId)) {
                    inDegree.put(task.getId(), inDegree.get(task.getId()) + 1);
                }
            }
        }
        
        // Kahn's algorithm
        Queue<String> queue = new LinkedList<>();
        for (Map.Entry<String, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.offer(entry.getKey());
            }
        }
        
        List<String> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            String taskId = queue.poll();
            result.add(taskId);
            
            Set<String> dependents = reverseDependencies.getOrDefault(taskId, Collections.emptySet());
            for (String dependentId : dependents) {
                inDegree.put(dependentId, inDegree.get(dependentId) - 1);
                if (inDegree.get(dependentId) == 0) {
                    queue.offer(dependentId);
                }
            }
        }
        
        if (result.size() != tasks.size()) {
            throw new IllegalStateException("Topological sort incomplete: graph may have cycles");
        }
        
        return result;
    }
    
    /**
     * Gets tasks that are ready to execute (all dependencies satisfied).
     * 
     * @param completedTasks Set of completed task IDs
     * @return List of ready tasks
     */
    public List<TaskNode> getReadyTasks(Set<String> completedTasks) {
        return tasks.values().stream()
            .filter(task -> task.isReady(completedTasks))
            .sorted(Comparator.comparing((TaskNode t) -> t.getPriority().ordinal())
                .thenComparing(TaskNode::getId))
            .collect(Collectors.toList());
    }
    
    /**
     * Gets the total number of tasks.
     * 
     * @return Task count
     */
    public int size() {
        return tasks.size();
    }
    
    /**
     * Checks if graph is empty.
     * 
     * @return true if no tasks
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }
    
    /**
     * Clears all tasks from the graph.
     */
    public void clear() {
        tasks.clear();
        reverseDependencies.clear();
        log.debug("Cleared task graph");
    }
}



