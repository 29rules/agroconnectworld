package com.ai.company.sprint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Sprint Velocity Tracker
 * 
 * Tracks completed story points and predicts velocity for future sprints.
 * 
 * Uses simple linear prediction for velocity forecasting.
 * 
 * SAFETY:
 * - Read-only tracking and prediction
 * - Never modifies code or systems
 */
public class SprintVelocityTracker {
    
    private static final Logger log = LoggerFactory.getLogger(SprintVelocityTracker.class);
    
    private final Map<Integer, Integer> sprintVelocities; // Sprint number -> completed story points
    private final List<VelocityDataPoint> velocityHistory;
    
    public SprintVelocityTracker() {
        this.sprintVelocities = new HashMap<>();
        this.velocityHistory = new ArrayList<>();
    }
    
    /**
     * Records completed story points for a sprint.
     * 
     * @param sprintNumber Sprint number
     * @param completedStoryPoints Completed story points
     */
    public void recordSprintVelocity(int sprintNumber, int completedStoryPoints) {
        log.info("Recording velocity for Sprint {}: {} story points", sprintNumber, completedStoryPoints);
        
        sprintVelocities.put(sprintNumber, completedStoryPoints);
        velocityHistory.add(new VelocityDataPoint(sprintNumber, completedStoryPoints));
        
        // Keep only last 10 sprints for prediction
        if (velocityHistory.size() > 10) {
            velocityHistory.remove(0);
        }
    }
    
    /**
     * Predicts velocity for a future sprint using simple linear prediction.
     * 
     * @param sprintNumber Sprint number to predict
     * @return Predicted velocity in story points
     */
    public int predictVelocity(int sprintNumber) {
        if (velocityHistory.isEmpty()) {
            log.info("No velocity history - using default prediction: 20 story points");
            return 20; // Default prediction
        }
        
        if (velocityHistory.size() == 1) {
            // Only one data point - use it as prediction
            int velocity = velocityHistory.get(0).getStoryPoints();
            log.info("Single velocity data point - using: {} story points", velocity);
            return velocity;
        }
        
        // Simple linear prediction: average of last 3 sprints
        int recentSprints = Math.min(3, velocityHistory.size());
        List<VelocityDataPoint> recent = velocityHistory.subList(
            velocityHistory.size() - recentSprints, velocityHistory.size());
        
        double average = recent.stream()
            .mapToInt(VelocityDataPoint::getStoryPoints)
            .average()
            .orElse(20.0);
        
        int prediction = (int) Math.round(average);
        
        log.info("Predicted velocity for Sprint {}: {} story points (based on last {} sprints)", 
            sprintNumber, prediction, recentSprints);
        
        return prediction;
    }
    
    /**
     * Gets velocity trend (increasing, decreasing, stable).
     * 
     * @return Trend description
     */
    public String getVelocityTrend() {
        if (velocityHistory.size() < 2) {
            return "INSUFFICIENT_DATA";
        }
        
        List<VelocityDataPoint> recent = velocityHistory.subList(
            Math.max(0, velocityHistory.size() - 3), velocityHistory.size());
        
        boolean increasing = true;
        boolean decreasing = true;
        
        for (int i = 1; i < recent.size(); i++) {
            int prev = recent.get(i - 1).getStoryPoints();
            int curr = recent.get(i).getStoryPoints();
            
            if (curr <= prev) {
                increasing = false;
            }
            if (curr >= prev) {
                decreasing = false;
            }
        }
        
        if (increasing) {
            return "INCREASING";
        } else if (decreasing) {
            return "DECREASING";
        } else {
            return "STABLE";
        }
    }
    
    /**
     * Gets average velocity over last N sprints.
     * 
     * @param numberOfSprints Number of sprints to average
     * @return Average velocity
     */
    public double getAverageVelocity(int numberOfSprints) {
        if (velocityHistory.isEmpty()) {
            return 0.0;
        }
        
        int sprints = Math.min(numberOfSprints, velocityHistory.size());
        List<VelocityDataPoint> recent = velocityHistory.subList(
            velocityHistory.size() - sprints, velocityHistory.size());
        
        return recent.stream()
            .mapToInt(VelocityDataPoint::getStoryPoints)
            .average()
            .orElse(0.0);
    }
    
    /**
     * Gets velocity history.
     * 
     * @return List of velocity data points
     */
    public List<VelocityDataPoint> getVelocityHistory() {
        return new ArrayList<>(velocityHistory);
    }
    
    /**
     * Gets velocity for a specific sprint.
     * 
     * @param sprintNumber Sprint number
     * @return Velocity or null if not recorded
     */
    public Integer getSprintVelocity(int sprintNumber) {
        return sprintVelocities.get(sprintNumber);
    }
    
    /**
     * Velocity data point.
     */
    public static class VelocityDataPoint {
        private final int sprintNumber;
        private final int storyPoints;
        private final Date recordedAt;
        
        public VelocityDataPoint(int sprintNumber, int storyPoints) {
            this.sprintNumber = sprintNumber;
            this.storyPoints = storyPoints;
            this.recordedAt = new Date();
        }
        
        public int getSprintNumber() { return sprintNumber; }
        public int getStoryPoints() { return storyPoints; }
        public Date getRecordedAt() { return recordedAt; }
    }
}



