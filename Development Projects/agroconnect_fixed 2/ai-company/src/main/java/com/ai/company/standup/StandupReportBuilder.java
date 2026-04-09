package com.ai.company.standup;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Standup Report Builder
 * 
 * Converts JSON standup report into formatted Slack-style summary.
 * 
 * Formats:
 * - Slack-style markdown
 * - Emoji indicators
 * - Color-coded sections
 * - Clear structure
 */
public class StandupReportBuilder {
    
    private static final Logger log = LoggerFactory.getLogger(StandupReportBuilder.class);
    
    private final ObjectMapper objectMapper;
    
    public StandupReportBuilder() {
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Builds a formatted Slack-style standup report from JSON.
     * 
     * @param jsonReport JSON standup report from StandupAgent
     * @return Formatted Slack-style report
     */
    public String buildSlackStyleReport(String jsonReport) {
        try {
            JsonNode root = objectMapper.readTree(jsonReport);
            
            StringBuilder report = new StringBuilder();
            
            // Header
            report.append(":calendar: *Daily Standup Report*\n");
            report.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
            
            // Yesterday Completed
            report.append(":white_check_mark: *Yesterday Completed*\n");
            JsonNode yesterdayCompleted = root.get("yesterday_completed");
            if (yesterdayCompleted != null && yesterdayCompleted.isArray()) {
                if (yesterdayCompleted.size() == 0) {
                    report.append("_No items completed yesterday._\n");
                } else {
                    for (JsonNode item : yesterdayCompleted) {
                        String itemText = item.has("item") ? item.get("item").asText() : "Unknown";
                        String agent = item.has("agent") ? item.get("agent").asText() : "Unknown";
                        String status = item.has("status") ? item.get("status").asText() : "unknown";
                        
                        String statusEmoji = getStatusEmoji(status);
                        report.append(String.format("%s *%s* (%s) - %s\n", 
                            statusEmoji, itemText, agent, status));
                    }
                }
            } else {
                report.append("_No data available._\n");
            }
            report.append("\n");
            
            // Today's Plan
            report.append(":rocket: *Today's Plan*\n");
            JsonNode todayPlan = root.get("today_plan");
            if (todayPlan != null && todayPlan.isArray()) {
                if (todayPlan.size() == 0) {
                    report.append("_No items planned for today._\n");
                } else {
                    for (JsonNode item : todayPlan) {
                        String itemText = item.has("item") ? item.get("item").asText() : "Unknown";
                        String agent = item.has("agent") ? item.get("agent").asText() : "Unknown";
                        String priority = item.has("priority") ? item.get("priority").asText() : "medium";
                        int estimatedHours = item.has("estimated_hours") ? item.get("estimated_hours").asInt() : 0;
                        
                        String priorityEmoji = getPriorityEmoji(priority);
                        report.append(String.format("%s *%s* (%s) - Priority: %s", 
                            priorityEmoji, itemText, agent, priority.toUpperCase()));
                        if (estimatedHours > 0) {
                            report.append(String.format(" - Est: %dh", estimatedHours));
                        }
                        report.append("\n");
                    }
                }
            } else {
                report.append("_No data available._\n");
            }
            report.append("\n");
            
            // Blockers
            report.append(":warning: *Blockers*\n");
            JsonNode blockers = root.get("blockers");
            if (blockers != null && blockers.isArray()) {
                if (blockers.size() == 0) {
                    report.append("_No blockers reported._ :tada:\n");
                } else {
                    for (JsonNode blocker : blockers) {
                        String itemText = blocker.has("item") ? blocker.get("item").asText() : "Unknown";
                        String agent = blocker.has("agent") ? blocker.get("agent").asText() : "Unknown";
                        String severity = blocker.has("severity") ? blocker.get("severity").asText() : "medium";
                        String helpNeeded = blocker.has("help_needed") ? blocker.get("help_needed").asText() : "";
                        
                        String severityEmoji = getSeverityEmoji(severity);
                        report.append(String.format("%s *%s* (%s) - Severity: %s\n", 
                            severityEmoji, itemText, agent, severity.toUpperCase()));
                        if (!helpNeeded.isEmpty()) {
                            report.append(String.format("   _Help needed: %s_\n", helpNeeded));
                        }
                    }
                }
            } else {
                report.append("_No data available._\n");
            }
            report.append("\n");
            
            // Risk Flag
            report.append(":chart_with_upwards_trend: *Risk Assessment*\n");
            JsonNode riskFlag = root.get("risk_flag");
            if (riskFlag != null) {
                String level = riskFlag.has("level") ? riskFlag.get("level").asText() : "none";
                String levelEmoji = getRiskLevelEmoji(level);
                report.append(String.format("%s Risk Level: *%s*\n", levelEmoji, level.toUpperCase()));
                
                JsonNode issues = riskFlag.get("issues");
                if (issues != null && issues.isArray() && issues.size() > 0) {
                    report.append("Issues:\n");
                    for (JsonNode issue : issues) {
                        report.append(String.format("  • %s\n", issue.asText()));
                    }
                } else {
                    report.append("_No risk issues identified._\n");
                }
            } else {
                report.append("_No risk data available._\n");
            }
            report.append("\n");
            
            // Footer
            report.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            report.append("_Generated by StandupAgent_");
            
            return report.toString();
            
        } catch (Exception e) {
            log.error("Error building Slack-style report", e);
            return buildErrorReport(e.getMessage());
        }
    }
    
    /**
     * Builds a plain text standup report from JSON.
     * 
     * @param jsonReport JSON standup report
     * @return Plain text report
     */
    public String buildPlainTextReport(String jsonReport) {
        try {
            JsonNode root = objectMapper.readTree(jsonReport);
            
            StringBuilder report = new StringBuilder();
            
            report.append("=".repeat(80)).append("\n");
            report.append("DAILY STANDUP REPORT\n");
            report.append("=".repeat(80)).append("\n\n");
            
            // Yesterday Completed
            report.append("YESTERDAY COMPLETED:\n");
            report.append("-".repeat(80)).append("\n");
            JsonNode yesterdayCompleted = root.get("yesterday_completed");
            if (yesterdayCompleted != null && yesterdayCompleted.isArray()) {
                if (yesterdayCompleted.size() == 0) {
                    report.append("No items completed yesterday.\n");
                } else {
                    for (JsonNode item : yesterdayCompleted) {
                        String itemText = item.has("item") ? item.get("item").asText() : "Unknown";
                        String agent = item.has("agent") ? item.get("agent").asText() : "Unknown";
                        String status = item.has("status") ? item.get("status").asText() : "unknown";
                        report.append(String.format("  • %s (%s) - %s\n", itemText, agent, status));
                    }
                }
            }
            report.append("\n");
            
            // Today's Plan
            report.append("TODAY'S PLAN:\n");
            report.append("-".repeat(80)).append("\n");
            JsonNode todayPlan = root.get("today_plan");
            if (todayPlan != null && todayPlan.isArray()) {
                if (todayPlan.size() == 0) {
                    report.append("No items planned for today.\n");
                } else {
                    for (JsonNode item : todayPlan) {
                        String itemText = item.has("item") ? item.get("item").asText() : "Unknown";
                        String agent = item.has("agent") ? item.get("agent").asText() : "Unknown";
                        String priority = item.has("priority") ? item.get("priority").asText() : "medium";
                        int estimatedHours = item.has("estimated_hours") ? item.get("estimated_hours").asInt() : 0;
                        report.append(String.format("  • %s (%s) - Priority: %s", 
                            itemText, agent, priority.toUpperCase()));
                        if (estimatedHours > 0) {
                            report.append(String.format(" - Est: %dh", estimatedHours));
                        }
                        report.append("\n");
                    }
                }
            }
            report.append("\n");
            
            // Blockers
            report.append("BLOCKERS:\n");
            report.append("-".repeat(80)).append("\n");
            JsonNode blockers = root.get("blockers");
            if (blockers != null && blockers.isArray()) {
                if (blockers.size() == 0) {
                    report.append("No blockers reported.\n");
                } else {
                    for (JsonNode blocker : blockers) {
                        String itemText = blocker.has("item") ? blocker.get("item").asText() : "Unknown";
                        String agent = blocker.has("agent") ? blocker.get("agent").asText() : "Unknown";
                        String severity = blocker.has("severity") ? blocker.get("severity").asText() : "medium";
                        String helpNeeded = blocker.has("help_needed") ? blocker.get("help_needed").asText() : "";
                        report.append(String.format("  • %s (%s) - Severity: %s\n", 
                            itemText, agent, severity.toUpperCase()));
                        if (!helpNeeded.isEmpty()) {
                            report.append(String.format("    Help needed: %s\n", helpNeeded));
                        }
                    }
                }
            }
            report.append("\n");
            
            // Risk Flag
            report.append("RISK ASSESSMENT:\n");
            report.append("-".repeat(80)).append("\n");
            JsonNode riskFlag = root.get("risk_flag");
            if (riskFlag != null) {
                String level = riskFlag.has("level") ? riskFlag.get("level").asText() : "none";
                report.append(String.format("Risk Level: %s\n", level.toUpperCase()));
                
                JsonNode issues = riskFlag.get("issues");
                if (issues != null && issues.isArray() && issues.size() > 0) {
                    report.append("Issues:\n");
                    for (JsonNode issue : issues) {
                        report.append(String.format("  • %s\n", issue.asText()));
                    }
                } else {
                    report.append("No risk issues identified.\n");
                }
            }
            report.append("\n");
            
            report.append("=".repeat(80)).append("\n");
            
            return report.toString();
            
        } catch (Exception e) {
            log.error("Error building plain text report", e);
            return buildErrorReport(e.getMessage());
        }
    }
    
    /**
     * Gets emoji for status.
     */
    private String getStatusEmoji(String status) {
        switch (status.toLowerCase()) {
            case "completed": return ":white_check_mark:";
            case "in_progress": return ":hourglass_flowing_sand:";
            case "blocked": return ":no_entry:";
            default: return ":question:";
        }
    }
    
    /**
     * Gets emoji for priority.
     */
    private String getPriorityEmoji(String priority) {
        switch (priority.toLowerCase()) {
            case "high": return ":red_circle:";
            case "medium": return ":yellow_circle:";
            case "low": return ":green_circle:";
            default: return ":white_circle:";
        }
    }
    
    /**
     * Gets emoji for severity.
     */
    private String getSeverityEmoji(String severity) {
        switch (severity.toLowerCase()) {
            case "high": return ":rotating_light:";
            case "medium": return ":warning:";
            case "low": return ":information_source:";
            default: return ":question:";
        }
    }
    
    /**
     * Gets emoji for risk level.
     */
    private String getRiskLevelEmoji(String level) {
        switch (level.toLowerCase()) {
            case "high": return ":red_circle:";
            case "medium": return ":yellow_circle:";
            case "low": return ":green_circle:";
            case "none": return ":white_check_mark:";
            default: return ":question:";
        }
    }
    
    /**
     * Builds error report.
     */
    private String buildErrorReport(String errorMessage) {
        return String.format(
            ":x: *Error generating standup report*\n\n" +
            "Error: %s\n\n" +
            "Please check the JSON format and try again.",
            errorMessage
        );
    }
}



