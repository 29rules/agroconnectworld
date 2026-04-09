package com.ai.company.analytics;

import com.ai.company.sprint.SprintPlan;
import com.ai.company.sprint.SprintVelocityTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * Story Point Analytics
 * 
 * Calculates and visualizes story point burn analytics:
 * - Ideal burn (linear)
 * - Actual burn (from completed work)
 * - Deviation graph
 * 
 * SAFETY:
 * - Read-only analytics
 * - Never modifies code or systems
 * - Only generates analytics and reports
 */
public class StoryPointAnalytics {
    
    private static final Logger log = LoggerFactory.getLogger(StoryPointAnalytics.class);
    
    private static final int CHART_WIDTH = 80;
    private static final int CHART_HEIGHT = 20;
    private static final String OUTPUT_DIR = "ai-company/outputs/analytics";
    
    /**
     * Calculates ideal burn for a sprint.
     * 
     * @param sprintPlan Sprint plan
     * @return List of ideal burn data points
     */
    public List<BurnDataPoint> calculateIdealBurn(SprintPlan sprintPlan) {
        if (sprintPlan.getStartDate() == null || sprintPlan.getEndDate() == null) {
            return new ArrayList<>();
        }
        
        int totalStoryPoints = sprintPlan.getStoryPointTotal();
        long days = ChronoUnit.DAYS.between(sprintPlan.getStartDate(), sprintPlan.getEndDate()) + 1;
        
        List<BurnDataPoint> dataPoints = new ArrayList<>();
        LocalDate currentDate = sprintPlan.getStartDate();
        double burnPerDay = (double) totalStoryPoints / days;
        
        for (int day = 0; day <= days; day++) {
            double remaining = Math.max(0, totalStoryPoints - (burnPerDay * day));
            double burned = totalStoryPoints - remaining;
            
            dataPoints.add(new BurnDataPoint(currentDate, day, burned, remaining, true));
            currentDate = currentDate.plusDays(1);
        }
        
        return dataPoints;
    }
    
    /**
     * Calculates actual burn for a sprint.
     * 
     * @param sprintPlan Sprint plan
     * @param completedStoryPoints Completed story points (from tracking)
     * @return List of actual burn data points
     */
    public List<BurnDataPoint> calculateActualBurn(SprintPlan sprintPlan, int completedStoryPoints) {
        if (sprintPlan.getStartDate() == null || sprintPlan.getEndDate() == null) {
            return new ArrayList<>();
        }
        
        int totalStoryPoints = sprintPlan.getStoryPointTotal();
        long days = ChronoUnit.DAYS.between(sprintPlan.getStartDate(), sprintPlan.getEndDate()) + 1;
        
        List<BurnDataPoint> dataPoints = new ArrayList<>();
        LocalDate currentDate = sprintPlan.getStartDate();
        
        // Simplified: distribute completed points evenly over sprint
        // In real implementation, would track daily completion
        double burnPerDay = days > 0 ? (double) completedStoryPoints / days : 0;
        
        for (int day = 0; day <= days; day++) {
            double burned = Math.min(completedStoryPoints, burnPerDay * day);
            double remaining = totalStoryPoints - burned;
            
            dataPoints.add(new BurnDataPoint(currentDate, day, burned, remaining, false));
            currentDate = currentDate.plusDays(1);
        }
        
        return dataPoints;
    }
    
    /**
     * Calculates deviation between ideal and actual burn.
     * 
     * @param idealBurn Ideal burn data points
     * @param actualBurn Actual burn data points
     * @return List of deviation data points
     */
    public List<DeviationDataPoint> calculateDeviation(List<BurnDataPoint> idealBurn, 
                                                       List<BurnDataPoint> actualBurn) {
        List<DeviationDataPoint> deviations = new ArrayList<>();
        
        int minSize = Math.min(idealBurn.size(), actualBurn.size());
        
        for (int i = 0; i < minSize; i++) {
            BurnDataPoint ideal = idealBurn.get(i);
            BurnDataPoint actual = actualBurn.get(i);
            
            double deviation = actual.getRemaining() - ideal.getRemaining();
            deviations.add(new DeviationDataPoint(
                ideal.getDate(), ideal.getDayNumber(), deviation));
        }
        
        return deviations;
    }
    
    /**
     * Generates deviation graph.
     * 
     * @param deviations Deviation data points
     * @param sprintId Sprint ID
     * @return ASCII deviation graph
     */
    public String generateDeviationGraph(List<DeviationDataPoint> deviations, String sprintId) {
        if (deviations.isEmpty()) {
            return "ERROR: No deviation data available";
        }
        
        StringBuilder graph = new StringBuilder();
        
        // Header
        graph.append("=".repeat(CHART_WIDTH)).append("\n");
        graph.append("STORY POINT DEVIATION GRAPH\n");
        graph.append("=".repeat(CHART_WIDTH)).append("\n\n");
        
        // Find min/max deviation for scaling
        double minDeviation = deviations.stream()
            .mapToDouble(DeviationDataPoint::getDeviation)
            .min().orElse(0);
        double maxDeviation = deviations.stream()
            .mapToDouble(DeviationDataPoint::getDeviation)
            .max().orElse(0);
        
        double range = Math.max(Math.abs(minDeviation), Math.abs(maxDeviation));
        if (range == 0) range = 1; // Avoid division by zero
        
        // Chart area
        int chartHeight = CHART_HEIGHT;
        int chartWidth = Math.min(CHART_WIDTH - 20, deviations.size());
        
        // Y-axis labels
        String[] yAxisLabels = new String[chartHeight + 1];
        for (int i = 0; i <= chartHeight; i++) {
            double value = range - (2 * range * i / chartHeight);
            yAxisLabels[i] = String.format("%6.1f", value);
        }
        
        // Generate chart grid
        char[][] chartGrid = new char[chartHeight + 1][chartWidth];
        for (int i = 0; i <= chartHeight; i++) {
            for (int j = 0; j < chartWidth; j++) {
                chartGrid[i][j] = ' ';
            }
        }
        
        // Draw zero line
        int zeroLine = chartHeight / 2;
        for (int j = 0; j < chartWidth; j++) {
            chartGrid[zeroLine][j] = '-';
        }
        
        // Draw deviation line
        for (int i = 0; i < deviations.size() - 1 && i < chartWidth - 1; i++) {
            DeviationDataPoint p1 = deviations.get(i);
            DeviationDataPoint p2 = deviations.get(i + 1);
            
            int y1 = (int) (zeroLine - (p1.getDeviation() / range) * (chartHeight / 2));
            int y2 = (int) (zeroLine - (p2.getDeviation() / range) * (chartHeight / 2));
            
            // Clamp to chart bounds
            y1 = Math.max(0, Math.min(chartHeight, y1));
            y2 = Math.max(0, Math.min(chartHeight, y2));
            
            drawLine(chartGrid, i, y1, i + 1, y2, '*');
        }
        
        // Print chart
        for (int i = 0; i <= chartHeight; i++) {
            graph.append(yAxisLabels[i]).append(" |");
            for (int j = 0; j < chartWidth; j++) {
                graph.append(chartGrid[i][j]);
            }
            graph.append("\n");
        }
        
        // X-axis
        graph.append("        +");
        graph.append("-".repeat(chartWidth)).append("\n");
        graph.append("         ");
        for (int i = 0; i < chartWidth && i < deviations.size(); i += Math.max(1, chartWidth / 10)) {
            graph.append(String.format("%-2d", i));
        }
        graph.append("\n\n");
        
        // Legend
        graph.append("Legend:\n");
        graph.append("  - Zero line (on track)\n");
        graph.append("  * Deviation from ideal\n");
        graph.append("  Positive = ahead of schedule\n");
        graph.append("  Negative = behind schedule\n");
        graph.append("\n");
        
        // Summary
        if (!deviations.isEmpty()) {
            DeviationDataPoint lastPoint = deviations.get(deviations.size() - 1);
            double avgDeviation = deviations.stream()
                .mapToDouble(DeviationDataPoint::getDeviation)
                .average().orElse(0);
            
            graph.append("Summary:\n");
            graph.append(String.format("  Current Deviation: %.1f story points\n", lastPoint.getDeviation()));
            graph.append(String.format("  Average Deviation: %.1f story points\n", avgDeviation));
            graph.append(String.format("  Status: %s\n", 
                lastPoint.getDeviation() > 0 ? "AHEAD" : 
                lastPoint.getDeviation() < 0 ? "BEHIND" : "ON TRACK"));
        }
        
        graph.append("=".repeat(CHART_WIDTH)).append("\n");
        
        // Save to file
        saveDeviationGraphToFile(sprintId, graph.toString());
        
        return graph.toString();
    }
    
    /**
     * Generates comprehensive analytics report.
     * 
     * @param sprintPlan Sprint plan
     * @param completedStoryPoints Completed story points
     * @return Analytics report
     */
    public String generateAnalyticsReport(SprintPlan sprintPlan, int completedStoryPoints) {
        log.info("Generating story point analytics for Sprint {}", sprintPlan.getSprintId());
        
        StringBuilder report = new StringBuilder();
        
        report.append("=".repeat(CHART_WIDTH)).append("\n");
        report.append("STORY POINT ANALYTICS REPORT\n");
        report.append("=".repeat(CHART_WIDTH)).append("\n\n");
        
        // Calculate burns
        List<BurnDataPoint> idealBurn = calculateIdealBurn(sprintPlan);
        List<BurnDataPoint> actualBurn = calculateActualBurn(sprintPlan, completedStoryPoints);
        List<DeviationDataPoint> deviations = calculateDeviation(idealBurn, actualBurn);
        
        // Summary
        report.append("SUMMARY:\n");
        report.append("-".repeat(CHART_WIDTH)).append("\n");
        report.append(String.format("Sprint: %s\n", sprintPlan.getSprintId()));
        report.append(String.format("Total Story Points: %d\n", sprintPlan.getStoryPointTotal()));
        report.append(String.format("Completed Story Points: %d\n", completedStoryPoints));
        report.append(String.format("Remaining Story Points: %d\n", 
            sprintPlan.getStoryPointTotal() - completedStoryPoints));
        
        if (!idealBurn.isEmpty() && !actualBurn.isEmpty()) {
            BurnDataPoint lastIdeal = idealBurn.get(idealBurn.size() - 1);
            BurnDataPoint lastActual = actualBurn.get(actualBurn.size() - 1);
            
            report.append(String.format("Ideal Remaining: %.1f\n", lastIdeal.getRemaining()));
            report.append(String.format("Actual Remaining: %.1f\n", lastActual.getRemaining()));
            
            if (!deviations.isEmpty()) {
                DeviationDataPoint lastDeviation = deviations.get(deviations.size() - 1);
                report.append(String.format("Current Deviation: %.1f\n", lastDeviation.getDeviation()));
            }
        }
        
        report.append("\n");
        
        // Deviation graph
        if (!deviations.isEmpty()) {
            report.append(generateDeviationGraph(deviations, sprintPlan.getSprintId()));
            report.append("\n");
        }
        
        report.append("=".repeat(CHART_WIDTH)).append("\n");
        
        // Save report
        saveReportToFile(sprintPlan.getSprintId(), report.toString());
        
        return report.toString();
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
                if (grid[y][x] == ' ' || grid[y][x] == '-') {
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
     * Saves deviation graph to file.
     */
    private void saveDeviationGraphToFile(String sprintId, String graph) {
        try {
            Path outputDir = Paths.get(OUTPUT_DIR);
            if (!Files.exists(outputDir)) {
                Files.createDirectories(outputDir);
            }
            
            String filename = String.format("deviation_%s.txt", sprintId.replaceAll("[^a-zA-Z0-9]", "_"));
            Path filePath = outputDir.resolve(filename);
            
            try (FileWriter writer = new FileWriter(filePath.toFile())) {
                writer.write(graph);
            }
            
            log.info("Deviation graph saved to: {}", filePath);
            
        } catch (IOException e) {
            log.error("Error saving deviation graph to file", e);
        }
    }
    
    /**
     * Saves analytics report to file.
     */
    private void saveReportToFile(String sprintId, String report) {
        try {
            Path outputDir = Paths.get(OUTPUT_DIR);
            if (!Files.exists(outputDir)) {
                Files.createDirectories(outputDir);
            }
            
            String filename = String.format("analytics_%s.txt", sprintId.replaceAll("[^a-zA-Z0-9]", "_"));
            Path filePath = outputDir.resolve(filename);
            
            try (FileWriter writer = new FileWriter(filePath.toFile())) {
                writer.write(report);
            }
            
            log.info("Analytics report saved to: {}", filePath);
            
        } catch (IOException e) {
            log.error("Error saving analytics report to file", e);
        }
    }
    
    /**
     * Burn data point.
     */
    public static class BurnDataPoint {
        private final LocalDate date;
        private final int dayNumber;
        private final double burned;
        private final double remaining;
        private final boolean isIdeal;
        
        public BurnDataPoint(LocalDate date, int dayNumber, double burned, 
                           double remaining, boolean isIdeal) {
            this.date = date;
            this.dayNumber = dayNumber;
            this.burned = burned;
            this.remaining = remaining;
            this.isIdeal = isIdeal;
        }
        
        public LocalDate getDate() { return date; }
        public int getDayNumber() { return dayNumber; }
        public double getBurned() { return burned; }
        public double getRemaining() { return remaining; }
        public boolean isIdeal() { return isIdeal; }
    }
    
    /**
     * Deviation data point.
     */
    public static class DeviationDataPoint {
        private final LocalDate date;
        private final int dayNumber;
        private final double deviation;
        
        public DeviationDataPoint(LocalDate date, int dayNumber, double deviation) {
            this.date = date;
            this.dayNumber = dayNumber;
            this.deviation = deviation;
        }
        
        public LocalDate getDate() { return date; }
        public int getDayNumber() { return dayNumber; }
        public double getDeviation() { return deviation; }
    }
}



