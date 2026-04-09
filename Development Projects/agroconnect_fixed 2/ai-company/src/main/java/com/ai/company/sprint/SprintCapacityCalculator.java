package com.ai.company.sprint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.*;

/**
 * Sprint Capacity Calculator
 * 
 * Estimates capacity based on:
 * - Agent availability
 * - Complexity
 * - Past performance
 * - Special constraints (holidays, load)
 * 
 * SAFETY:
 * - Read-only calculations
 * - Never modifies code or systems
 */
public class SprintCapacityCalculator {
    
    private static final Logger log = LoggerFactory.getLogger(SprintCapacityCalculator.class);
    
    private static final int DEFAULT_HOURS_PER_DAY = 6; // 6 hours of actual work per day
    private static final int DEFAULT_STORY_POINTS_PER_DAY = 1; // 1 story point per day average
    
    private final Map<String, AgentCapacity> agentCapacities;
    private final Set<LocalDate> holidays;
    
    public SprintCapacityCalculator() {
        this.agentCapacities = new HashMap<>();
        this.holidays = new HashSet<>();
    }
    
    /**
     * Calculates agent capacities for a sprint.
     * 
     * @param sprintDurationWeeks Sprint duration in weeks
     * @return Map of agent names to capacity (story points)
     */
    public Map<String, Integer> calculateAgentCapacities(int sprintDurationWeeks) {
        log.info("Calculating agent capacities for {} week sprint", sprintDurationWeeks);
        
        Map<String, Integer> capacities = new HashMap<>();
        
        // Default agents
        String[] agents = {
            "EngineerAgent",
            "QAAgent",
            "FullStackAgent",
            "DevOpsAgent",
            "ArchitectAgent"
        };
        
        int workingDays = calculateWorkingDays(sprintDurationWeeks);
        
        for (String agent : agents) {
            AgentCapacity capacity = agentCapacities.getOrDefault(agent, createDefaultCapacity(agent));
            int agentCapacity = calculateAgentCapacity(capacity, workingDays);
            capacities.put(agent, agentCapacity);
            
            log.debug("Agent {} capacity: {} story points", agent, agentCapacity);
        }
        
        return capacities;
    }
    
    /**
     * Calculates working days in sprint (excluding weekends and holidays).
     */
    private int calculateWorkingDays(int sprintDurationWeeks) {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusWeeks(sprintDurationWeeks);
        
        int workingDays = 0;
        LocalDate current = start;
        
        while (!current.isAfter(end)) {
            // Check if not weekend
            java.time.DayOfWeek dayOfWeek = current.getDayOfWeek();
            if (dayOfWeek != java.time.DayOfWeek.SATURDAY && 
                dayOfWeek != java.time.DayOfWeek.SUNDAY) {
                // Check if not holiday
                if (!holidays.contains(current)) {
                    workingDays++;
                }
            }
            current = current.plusDays(1);
        }
        
        return workingDays;
    }
    
    /**
     * Calculates capacity for a specific agent.
     */
    private int calculateAgentCapacity(AgentCapacity capacity, int workingDays) {
        // Base capacity: working days * hours per day * story points per hour
        double baseCapacity = workingDays * capacity.getHoursPerDay() * capacity.getStoryPointsPerHour();
        
        // Apply availability factor
        baseCapacity *= capacity.getAvailabilityFactor();
        
        // Apply complexity factor
        baseCapacity *= capacity.getComplexityFactor();
        
        // Apply past performance factor
        baseCapacity *= capacity.getPastPerformanceFactor();
        
        // Round to integer
        return (int) Math.round(baseCapacity);
    }
    
    /**
     * Creates default capacity for an agent.
     */
    private AgentCapacity createDefaultCapacity(String agentName) {
        AgentCapacity capacity = new AgentCapacity();
        capacity.setAgentName(agentName);
        capacity.setHoursPerDay(DEFAULT_HOURS_PER_DAY);
        capacity.setStoryPointsPerHour(1.0 / 6.0); // 1 story point per 6 hours
        capacity.setAvailabilityFactor(1.0);
        capacity.setComplexityFactor(1.0);
        capacity.setPastPerformanceFactor(1.0);
        return capacity;
    }
    
    /**
     * Sets agent capacity configuration.
     */
    public void setAgentCapacity(String agentName, AgentCapacity capacity) {
        agentCapacities.put(agentName, capacity);
        log.info("Set capacity for agent {}: {} hours/day, {} story points/hour", 
            agentName, capacity.getHoursPerDay(), capacity.getStoryPointsPerHour());
    }
    
    /**
     * Adds a holiday.
     */
    public void addHoliday(LocalDate date) {
        holidays.add(date);
        log.info("Added holiday: {}", date);
    }
    
    /**
     * Adds multiple holidays.
     */
    public void addHolidays(List<LocalDate> dates) {
        holidays.addAll(dates);
        log.info("Added {} holidays", dates.size());
    }
    
    /**
     * Gets total team capacity for sprint.
     * 
     * @param sprintDurationWeeks Sprint duration
     * @return Total team capacity in story points
     */
    public int getTotalTeamCapacity(int sprintDurationWeeks) {
        Map<String, Integer> capacities = calculateAgentCapacities(sprintDurationWeeks);
        return capacities.values().stream().mapToInt(Integer::intValue).sum();
    }
    
    /**
     * Agent capacity configuration.
     */
    public static class AgentCapacity {
        private String agentName;
        private double hoursPerDay;
        private double storyPointsPerHour;
        private double availabilityFactor; // 0.0 to 1.0 (1.0 = 100% available)
        private double complexityFactor; // 0.5 to 2.0 (1.0 = normal complexity)
        private double pastPerformanceFactor; // 0.5 to 1.5 (1.0 = average performance)
        
        // Getters and Setters
        public String getAgentName() { return agentName; }
        public void setAgentName(String agentName) { this.agentName = agentName; }
        public double getHoursPerDay() { return hoursPerDay; }
        public void setHoursPerDay(double hoursPerDay) { this.hoursPerDay = hoursPerDay; }
        public double getStoryPointsPerHour() { return storyPointsPerHour; }
        public void setStoryPointsPerHour(double storyPointsPerHour) { this.storyPointsPerHour = storyPointsPerHour; }
        public double getAvailabilityFactor() { return availabilityFactor; }
        public void setAvailabilityFactor(double availabilityFactor) { this.availabilityFactor = availabilityFactor; }
        public double getComplexityFactor() { return complexityFactor; }
        public void setComplexityFactor(double complexityFactor) { this.complexityFactor = complexityFactor; }
        public double getPastPerformanceFactor() { return pastPerformanceFactor; }
        public void setPastPerformanceFactor(double pastPerformanceFactor) { this.pastPerformanceFactor = pastPerformanceFactor; }
    }
}



