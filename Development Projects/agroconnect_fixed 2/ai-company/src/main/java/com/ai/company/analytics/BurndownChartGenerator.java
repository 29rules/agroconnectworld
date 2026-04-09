package com.ai.company.analytics;

import com.ai.company.sprint.SprintPlan;
import com.ai.company.sprint.SprintVelocityTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Burndown Chart Generator
 * 
 * Generates burndown charts from SprintPlan and SprintVelocityTracker.
 * 
 * Outputs:
 * - ASCII burndown graph (console/logs)
 * - PNG chart saved in outputs/analytics (future enhancement)
 * 
 * SAFETY:
 * - Read-only analytics
 * - Never modifies code or systems
 * - Only generates charts and reports
 */
public class BurndownChartGenerator {
    
    private static final Logger log = LoggerFactory.getLogger(BurndownChartGenerator.class);
    
    private static final int CHART_WIDTH = 80;
    private static final int CHART_HEIGHT = 20;
    private static final String OUTPUT_DIR = "ai-company/outputs/analytics";
    
    /**
     * Generates a burndown chart for a sprint.
     * 
     * @param sprintPlan Sprint plan
     * @param velocityTracker Velocity tracker
     * @return ASCII burndown chart
     */
    public String generateBurndownChart(SprintPlan sprintPlan, SprintVelocityTracker velocityTracker) {
        log.info("Generating burndown chart for Sprint {}", sprintPlan.getSprintId());
        
        if (sprintPlan.getStartDate() == null || sprintPlan.getEndDate() == null) {
            return "ERROR: Sprint dates not set";
        }
        
        int totalStoryPoints = sprintPlan.getStoryPointTotal();
        if (totalStoryPoints == 0) {
            return "ERROR: No story points in sprint";
        }
        
        // Calculate sprint duration
        long days = ChronoUnit.DAYS.between(sprintPlan.getStartDate(), sprintPlan.getEndDate()) + 1;
        
        // Generate data points
        List<BurndownDataPoint> dataPoints = generateDataPoints(
            sprintPlan, velocityTracker, totalStoryPoints, (int) days);
        
        // Generate ASCII chart
        String asciiChart = generateAsciiChart(dataPoints, totalStoryPoints, (int) days);
        
        // Save to file
        saveChartToFile(sprintPlan.getSprintId(), asciiChart);
        
        return asciiChart;
    }
    
    /**
     * Generates data points for burndown chart.
     */
    private List<BurndownDataPoint> generateDataPoints(SprintPlan sprintPlan, 
                                                       SprintVelocityTracker velocityTracker,
                                                       int totalStoryPoints, 
                                                       int sprintDays) {
        List<BurndownDataPoint> dataPoints = new ArrayList<>();
        
        LocalDate startDate = sprintPlan.getStartDate();
        LocalDate endDate = sprintPlan.getEndDate();
        
        // Ideal burn (linear)
        double idealBurnPerDay = (double) totalStoryPoints / sprintDays;
        
        // Calculate actual burn from completed stories
        int completedPoints = calculateCompletedPoints(sprintPlan);
        
        // Generate data points for each day
        LocalDate currentDate = startDate;
        int dayNumber = 0;
        
        while (!currentDate.isAfter(endDate)) {
            // Ideal remaining
            double idealRemaining = Math.max(0, totalStoryPoints - (idealBurnPerDay * dayNumber));
            
            // Actual remaining (simplified - in real implementation, would track daily)
            double actualRemaining = Math.max(0, totalStoryPoints - 
                (completedPoints * ((double) dayNumber / sprintDays)));
            
            dataPoints.add(new BurndownDataPoint(
                currentDate, dayNumber, idealRemaining, actualRemaining));
            
            currentDate = currentDate.plusDays(1);
            dayNumber++;
        }
        
        return dataPoints;
    }
    
    /**
     * Calculates completed story points from sprint plan.
     */
    private int calculateCompletedPoints(SprintPlan sprintPlan) {
        return sprintPlan.getCommittedStories().stream()
            .filter(story -> story.getStoryPoints() != null)
            .mapToInt(SprintPlan.CommittedStory::getStoryPoints)
            .sum(); // In real implementation, would check actual completion status
    }
    
    /**
     * Generates ASCII chart.
     */
    private String generateAsciiChart(List<BurndownDataPoint> dataPoints, 
                                      int totalStoryPoints, 
                                      int sprintDays) {
        StringBuilder chart = new StringBuilder();
        
        // Header
        chart.append("=".repeat(CHART_WIDTH)).append("\n");
        chart.append("BURNDOWN CHART\n");
        chart.append("=".repeat(CHART_WIDTH)).append("\n\n");
        
        // Find max value for scaling
        double maxValue = totalStoryPoints;
        for (BurndownDataPoint point : dataPoints) {
            maxValue = Math.max(maxValue, Math.max(point.getIdealRemaining(), point.getActualRemaining()));
        }
        
        // Chart area
        int chartHeight = CHART_HEIGHT;
        int chartWidth = Math.min(CHART_WIDTH - 20, sprintDays);
        
        // Y-axis labels
        String[] yAxisLabels = new String[chartHeight + 1];
        for (int i = 0; i <= chartHeight; i++) {
            double value = maxValue - (maxValue * i / chartHeight);
            yAxisLabels[i] = String.format("%5.0f", value);
        }
        
        // Generate chart lines
        char[][] chartGrid = new char[chartHeight + 1][chartWidth];
        for (int i = 0; i <= chartHeight; i++) {
            for (int j = 0; j < chartWidth; j++) {
                chartGrid[i][j] = ' ';
            }
        }
        
        // Draw ideal line (dashed)
        if (dataPoints.size() > 1) {
            for (int i = 0; i < dataPoints.size() - 1 && i < chartWidth; i++) {
                BurndownDataPoint p1 = dataPoints.get(i);
                BurndownDataPoint p2 = dataPoints.get(i + 1);
                
                int y1 = (int) (chartHeight * (1 - p1.getIdealRemaining() / maxValue));
                int y2 = (int) (chartHeight * (1 - p2.getIdealRemaining() / maxValue));
                
                drawLine(chartGrid, i, y1, i + 1, y2, '-');
            }
        }
        
        // Draw actual line (solid)
        if (dataPoints.size() > 1) {
            for (int i = 0; i < dataPoints.size() - 1 && i < chartWidth; i++) {
                BurndownDataPoint p1 = dataPoints.get(i);
                BurndownDataPoint p2 = dataPoints.get(i + 1);
                
                int y1 = (int) (chartHeight * (1 - p1.getActualRemaining() / maxValue));
                int y2 = (int) (chartHeight * (1 - p2.getActualRemaining() / maxValue));
                
                drawLine(chartGrid, i, y1, i + 1, y2, '*');
            }
        }
        
        // Print chart
        for (int i = 0; i <= chartHeight; i++) {
            chart.append(yAxisLabels[i]).append(" |");
            for (int j = 0; j < chartWidth; j++) {
                chart.append(chartGrid[i][j]);
            }
            chart.append("\n");
        }
        
        // X-axis
        chart.append("      +");
        chart.append("-".repeat(chartWidth)).append("\n");
        chart.append("       ");
        for (int i = 0; i < chartWidth && i < dataPoints.size(); i += Math.max(1, chartWidth / 10)) {
            chart.append(String.format("%-2d", i));
        }
        chart.append("\n\n");
        
        // Legend
        chart.append("Legend:\n");
        chart.append("  - Ideal burn (linear)\n");
        chart.append("  * Actual burn\n");
        chart.append("\n");
        
        // Summary
        if (!dataPoints.isEmpty()) {
            BurndownDataPoint lastPoint = dataPoints.get(dataPoints.size() - 1);
            chart.append("Summary:\n");
            chart.append(String.format("  Total Story Points: %d\n", totalStoryPoints));
            chart.append(String.format("  Ideal Remaining: %.1f\n", lastPoint.getIdealRemaining()));
            chart.append(String.format("  Actual Remaining: %.1f\n", lastPoint.getActualRemaining()));
            chart.append(String.format("  Deviation: %.1f\n", 
                lastPoint.getActualRemaining() - lastPoint.getIdealRemaining()));
        }
        
        chart.append("=".repeat(CHART_WIDTH)).append("\n");
        
        return chart.toString();
    }
    
    /**
     * Draws a line on the chart grid.
     */
    private void drawLine(char[][] grid, int x1, int y1, int x2, int y2, char symbol) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;
        
        int x = x1;
        int y = y1;
        
        while (true) {
            if (x >= 0 && x < grid[0].length && y >= 0 && y < grid.length) {
                if (grid[y][x] == ' ') {
                    grid[y][x] = symbol;
                } else if (grid[y][x] != symbol) {
                    grid[y][x] = '+'; // Intersection
                }
            }
            
            if (x == x2 && y == y2) break;
            
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x += sx;
            }
            if (e2 < dx) {
                err += dx;
                y += sy;
            }
        }
    }
    
    /**
     * Saves chart to file.
     */
    private void saveChartToFile(String sprintId, String chart) {
        try {
            Path outputDir = Paths.get(OUTPUT_DIR);
            if (!Files.exists(outputDir)) {
                Files.createDirectories(outputDir);
            }
            
            String filename = String.format("burndown_%s.txt", sprintId.replaceAll("[^a-zA-Z0-9]", "_"));
            Path filePath = outputDir.resolve(filename);
            
            try (FileWriter writer = new FileWriter(filePath.toFile())) {
                writer.write(chart);
            }
            
            log.info("Burndown chart saved to: {}", filePath);
            
        } catch (IOException e) {
            log.error("Error saving burndown chart to file", e);
        }
    }
    
    /**
     * Burndown data point.
     */
    public static class BurndownDataPoint {
        private final LocalDate date;
        private final int dayNumber;
        private final double idealRemaining;
        private final double actualRemaining;
        
        public BurndownDataPoint(LocalDate date, int dayNumber, 
                               double idealRemaining, double actualRemaining) {
            this.date = date;
            this.dayNumber = dayNumber;
            this.idealRemaining = idealRemaining;
            this.actualRemaining = actualRemaining;
        }
        
        public LocalDate getDate() { return date; }
        public int getDayNumber() { return dayNumber; }
        public double getIdealRemaining() { return idealRemaining; }
        public double getActualRemaining() { return actualRemaining; }
    }
}



