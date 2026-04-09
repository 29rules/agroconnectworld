package com.ai.company.retro;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Retrospective Report Builder
 * 
 * Converts JSON retrospective report into formatted written summary.
 * 
 * Formats:
 * - Markdown summary
 * - Plain text summary
 * - Structured sections
 * - Action items
 */
public class RetroReportBuilder {
    
    private static final Logger log = LoggerFactory.getLogger(RetroReportBuilder.class);
    
    private final ObjectMapper objectMapper;
    
    public RetroReportBuilder() {
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Builds a formatted markdown retrospective report from JSON.
     * 
     * @param jsonReport JSON retrospective report from RetroAgent
     * @return Formatted markdown report
     */
    public String buildMarkdownReport(String jsonReport) {
        try {
            JsonNode root = objectMapper.readTree(jsonReport);
            
            StringBuilder report = new StringBuilder();
            
            // Header
            report.append("# Sprint Retrospective Report\n\n");
            report.append("---\n\n");
            
            // What Went Well
            report.append("## ✅ What Went Well\n\n");
            JsonNode whatWentWell = root.get("what_went_well");
            if (whatWentWell != null && whatWentWell.isArray()) {
                if (whatWentWell.size() == 0) {
                    report.append("_No items recorded._\n\n");
                } else {
                    for (JsonNode item : whatWentWell) {
                        String itemText = item.has("item") ? item.get("item").asText() : "Unknown";
                        String category = item.has("category") ? item.get("category").asText() : "general";
                        String impact = item.has("impact") ? item.get("impact").asText() : "medium";
                        String evidence = item.has("evidence") ? item.get("evidence").asText() : "";
                        
                        report.append(String.format("### %s\n", itemText));
                        report.append(String.format("- **Category:** %s\n", capitalize(category)));
                        report.append(String.format("- **Impact:** %s\n", capitalize(impact)));
                        if (!evidence.isEmpty()) {
                            report.append(String.format("- **Evidence:** %s\n", evidence));
                        }
                        report.append("\n");
                    }
                }
            } else {
                report.append("_No data available._\n\n");
            }
            
            // What Didn't Go Well
            report.append("## ❌ What Didn't Go Well\n\n");
            JsonNode whatDidntGoWell = root.get("what_didnt_go_well");
            if (whatDidntGoWell != null && whatDidntGoWell.isArray()) {
                if (whatDidntGoWell.size() == 0) {
                    report.append("_No issues recorded._\n\n");
                } else {
                    for (JsonNode item : whatDidntGoWell) {
                        String itemText = item.has("item") ? item.get("item").asText() : "Unknown";
                        String category = item.has("category") ? item.get("category").asText() : "general";
                        String severity = item.has("severity") ? item.get("severity").asText() : "medium";
                        String rootCause = item.has("root_cause") ? item.get("root_cause").asText() : "";
                        String impact = item.has("impact") ? item.get("impact").asText() : "";
                        
                        report.append(String.format("### %s\n", itemText));
                        report.append(String.format("- **Category:** %s\n", capitalize(category)));
                        report.append(String.format("- **Severity:** %s\n", capitalize(severity)));
                        if (!rootCause.isEmpty()) {
                            report.append(String.format("- **Root Cause:** %s\n", rootCause));
                        }
                        if (!impact.isEmpty()) {
                            report.append(String.format("- **Impact:** %s\n", impact));
                        }
                        report.append("\n");
                    }
                }
            } else {
                report.append("_No data available._\n\n");
            }
            
            // Improvements
            report.append("## 🚀 Improvements\n\n");
            JsonNode improvements = root.get("improvements");
            if (improvements != null && improvements.isArray()) {
                if (improvements.size() == 0) {
                    report.append("_No improvements suggested._\n\n");
                } else {
                    for (JsonNode improvement : improvements) {
                        String improvementText = improvement.has("improvement") ? 
                            improvement.get("improvement").asText() : "Unknown";
                        String category = improvement.has("category") ? 
                            improvement.get("category").asText() : "general";
                        String priority = improvement.has("priority") ? 
                            improvement.get("priority").asText() : "medium";
                        String owner = improvement.has("owner") ? 
                            improvement.get("owner").asText() : "TBD";
                        String timeline = improvement.has("timeline") ? 
                            improvement.get("timeline").asText() : "TBD";
                        String successCriteria = improvement.has("success_criteria") ? 
                            improvement.get("success_criteria").asText() : "";
                        
                        report.append(String.format("### %s\n", improvementText));
                        report.append(String.format("- **Category:** %s\n", capitalize(category)));
                        report.append(String.format("- **Priority:** %s\n", capitalize(priority)));
                        report.append(String.format("- **Owner:** %s\n", owner));
                        report.append(String.format("- **Timeline:** %s\n", timeline));
                        if (!successCriteria.isEmpty()) {
                            report.append(String.format("- **Success Criteria:** %s\n", successCriteria));
                        }
                        report.append("\n");
                    }
                }
            } else {
                report.append("_No data available._\n\n");
            }
            
            // Process Suggestions
            report.append("## 💡 Process Suggestions\n\n");
            JsonNode processSuggestions = root.get("process_suggestions");
            if (processSuggestions != null && processSuggestions.isArray()) {
                if (processSuggestions.size() == 0) {
                    report.append("_No process suggestions._\n\n");
                } else {
                    for (JsonNode suggestion : processSuggestions) {
                        String suggestionText = suggestion.has("suggestion") ? 
                            suggestion.get("suggestion").asText() : "Unknown";
                        String rationale = suggestion.has("rationale") ? 
                            suggestion.get("rationale").asText() : "";
                        String implementation = suggestion.has("implementation") ? 
                            suggestion.get("implementation").asText() : "";
                        String expectedBenefit = suggestion.has("expected_benefit") ? 
                            suggestion.get("expected_benefit").asText() : "";
                        
                        report.append(String.format("### %s\n", suggestionText));
                        if (!rationale.isEmpty()) {
                            report.append(String.format("- **Rationale:** %s\n", rationale));
                        }
                        if (!implementation.isEmpty()) {
                            report.append(String.format("- **Implementation:** %s\n", implementation));
                        }
                        if (!expectedBenefit.isEmpty()) {
                            report.append(String.format("- **Expected Benefit:** %s\n", expectedBenefit));
                        }
                        report.append("\n");
                    }
                }
            } else {
                report.append("_No data available._\n\n");
            }
            
            // Morale Assessment
            report.append("## 😊 Team Morale Assessment\n\n");
            JsonNode moraleAssessment = root.get("morale_assessment");
            if (moraleAssessment != null) {
                String overallMorale = moraleAssessment.has("overall_morale") ? 
                    moraleAssessment.get("overall_morale").asText() : "unknown";
                String teamSatisfaction = moraleAssessment.has("team_satisfaction") ? 
                    moraleAssessment.get("team_satisfaction").asText() : "unknown";
                String stressLevel = moraleAssessment.has("stress_level") ? 
                    moraleAssessment.get("stress_level").asText() : "unknown";
                String engagement = moraleAssessment.has("engagement") ? 
                    moraleAssessment.get("engagement").asText() : "unknown";
                
                report.append("### Overall Assessment\n\n");
                report.append(String.format("- **Overall Morale:** %s\n", capitalize(overallMorale)));
                report.append(String.format("- **Team Satisfaction:** %s\n", capitalize(teamSatisfaction)));
                report.append(String.format("- **Stress Level:** %s\n", capitalize(stressLevel)));
                report.append(String.format("- **Engagement:** %s\n", capitalize(engagement)));
                report.append("\n");
                
                JsonNode factors = moraleAssessment.get("factors");
                if (factors != null && factors.isArray() && factors.size() > 0) {
                    report.append("### Factors Affecting Morale\n\n");
                    for (JsonNode factor : factors) {
                        report.append(String.format("- %s\n", factor.asText()));
                    }
                    report.append("\n");
                }
                
                JsonNode recommendations = moraleAssessment.get("recommendations");
                if (recommendations != null && recommendations.isArray() && recommendations.size() > 0) {
                    report.append("### Recommendations\n\n");
                    for (JsonNode recommendation : recommendations) {
                        report.append(String.format("- %s\n", recommendation.asText()));
                    }
                    report.append("\n");
                }
            } else {
                report.append("_No morale assessment data available._\n\n");
            }
            
            // Footer
            report.append("---\n\n");
            report.append("*Generated by RetroAgent*\n");
            
            return report.toString();
            
        } catch (Exception e) {
            log.error("Error building markdown report", e);
            return buildErrorReport(e.getMessage());
        }
    }
    
    /**
     * Builds a plain text retrospective report from JSON.
     * 
     * @param jsonReport JSON retrospective report
     * @return Plain text report
     */
    public String buildPlainTextReport(String jsonReport) {
        try {
            JsonNode root = objectMapper.readTree(jsonReport);
            
            StringBuilder report = new StringBuilder();
            
            report.append("=".repeat(80)).append("\n");
            report.append("SPRINT RETROSPECTIVE REPORT\n");
            report.append("=".repeat(80)).append("\n\n");
            
            // What Went Well
            report.append("WHAT WENT WELL:\n");
            report.append("-".repeat(80)).append("\n");
            JsonNode whatWentWell = root.get("what_went_well");
            if (whatWentWell != null && whatWentWell.isArray()) {
                if (whatWentWell.size() == 0) {
                    report.append("No items recorded.\n");
                } else {
                    int index = 1;
                    for (JsonNode item : whatWentWell) {
                        String itemText = item.has("item") ? item.get("item").asText() : "Unknown";
                        String category = item.has("category") ? item.get("category").asText() : "general";
                        String impact = item.has("impact") ? item.get("impact").asText() : "medium";
                        String evidence = item.has("evidence") ? item.get("evidence").asText() : "";
                        
                        report.append(String.format("%d. %s\n", index++, itemText));
                        report.append(String.format("   Category: %s | Impact: %s\n", 
                            capitalize(category), capitalize(impact)));
                        if (!evidence.isEmpty()) {
                            report.append(String.format("   Evidence: %s\n", evidence));
                        }
                        report.append("\n");
                    }
                }
            }
            report.append("\n");
            
            // What Didn't Go Well
            report.append("WHAT DIDN'T GO WELL:\n");
            report.append("-".repeat(80)).append("\n");
            JsonNode whatDidntGoWell = root.get("what_didnt_go_well");
            if (whatDidntGoWell != null && whatDidntGoWell.isArray()) {
                if (whatDidntGoWell.size() == 0) {
                    report.append("No issues recorded.\n");
                } else {
                    int index = 1;
                    for (JsonNode item : whatDidntGoWell) {
                        String itemText = item.has("item") ? item.get("item").asText() : "Unknown";
                        String category = item.has("category") ? item.get("category").asText() : "general";
                        String severity = item.has("severity") ? item.get("severity").asText() : "medium";
                        String rootCause = item.has("root_cause") ? item.get("root_cause").asText() : "";
                        String impact = item.has("impact") ? item.get("impact").asText() : "";
                        
                        report.append(String.format("%d. %s\n", index++, itemText));
                        report.append(String.format("   Category: %s | Severity: %s\n", 
                            capitalize(category), capitalize(severity)));
                        if (!rootCause.isEmpty()) {
                            report.append(String.format("   Root Cause: %s\n", rootCause));
                        }
                        if (!impact.isEmpty()) {
                            report.append(String.format("   Impact: %s\n", impact));
                        }
                        report.append("\n");
                    }
                }
            }
            report.append("\n");
            
            // Improvements
            report.append("IMPROVEMENTS:\n");
            report.append("-".repeat(80)).append("\n");
            JsonNode improvements = root.get("improvements");
            if (improvements != null && improvements.isArray()) {
                if (improvements.size() == 0) {
                    report.append("No improvements suggested.\n");
                } else {
                    int index = 1;
                    for (JsonNode improvement : improvements) {
                        String improvementText = improvement.has("improvement") ? 
                            improvement.get("improvement").asText() : "Unknown";
                        String category = improvement.has("category") ? 
                            improvement.get("category").asText() : "general";
                        String priority = improvement.has("priority") ? 
                            improvement.get("priority").asText() : "medium";
                        String owner = improvement.has("owner") ? 
                            improvement.get("owner").asText() : "TBD";
                        String timeline = improvement.has("timeline") ? 
                            improvement.get("timeline").asText() : "TBD";
                        String successCriteria = improvement.has("success_criteria") ? 
                            improvement.get("success_criteria").asText() : "";
                        
                        report.append(String.format("%d. %s\n", index++, improvementText));
                        report.append(String.format("   Category: %s | Priority: %s | Owner: %s | Timeline: %s\n", 
                            capitalize(category), capitalize(priority), owner, timeline));
                        if (!successCriteria.isEmpty()) {
                            report.append(String.format("   Success Criteria: %s\n", successCriteria));
                        }
                        report.append("\n");
                    }
                }
            }
            report.append("\n");
            
            // Process Suggestions
            report.append("PROCESS SUGGESTIONS:\n");
            report.append("-".repeat(80)).append("\n");
            JsonNode processSuggestions = root.get("process_suggestions");
            if (processSuggestions != null && processSuggestions.isArray()) {
                if (processSuggestions.size() == 0) {
                    report.append("No process suggestions.\n");
                } else {
                    int index = 1;
                    for (JsonNode suggestion : processSuggestions) {
                        String suggestionText = suggestion.has("suggestion") ? 
                            suggestion.get("suggestion").asText() : "Unknown";
                        String rationale = suggestion.has("rationale") ? 
                            suggestion.get("rationale").asText() : "";
                        String implementation = suggestion.has("implementation") ? 
                            suggestion.get("implementation").asText() : "";
                        String expectedBenefit = suggestion.has("expected_benefit") ? 
                            suggestion.get("expected_benefit").asText() : "";
                        
                        report.append(String.format("%d. %s\n", index++, suggestionText));
                        if (!rationale.isEmpty()) {
                            report.append(String.format("   Rationale: %s\n", rationale));
                        }
                        if (!implementation.isEmpty()) {
                            report.append(String.format("   Implementation: %s\n", implementation));
                        }
                        if (!expectedBenefit.isEmpty()) {
                            report.append(String.format("   Expected Benefit: %s\n", expectedBenefit));
                        }
                        report.append("\n");
                    }
                }
            }
            report.append("\n");
            
            // Morale Assessment
            report.append("TEAM MORALE ASSESSMENT:\n");
            report.append("-".repeat(80)).append("\n");
            JsonNode moraleAssessment = root.get("morale_assessment");
            if (moraleAssessment != null) {
                String overallMorale = moraleAssessment.has("overall_morale") ? 
                    moraleAssessment.get("overall_morale").asText() : "unknown";
                String teamSatisfaction = moraleAssessment.has("team_satisfaction") ? 
                    moraleAssessment.get("team_satisfaction").asText() : "unknown";
                String stressLevel = moraleAssessment.has("stress_level") ? 
                    moraleAssessment.get("stress_level").asText() : "unknown";
                String engagement = moraleAssessment.has("engagement") ? 
                    moraleAssessment.get("engagement").asText() : "unknown";
                
                report.append(String.format("Overall Morale: %s\n", capitalize(overallMorale)));
                report.append(String.format("Team Satisfaction: %s\n", capitalize(teamSatisfaction)));
                report.append(String.format("Stress Level: %s\n", capitalize(stressLevel)));
                report.append(String.format("Engagement: %s\n", capitalize(engagement)));
                report.append("\n");
                
                JsonNode factors = moraleAssessment.get("factors");
                if (factors != null && factors.isArray() && factors.size() > 0) {
                    report.append("Factors Affecting Morale:\n");
                    for (JsonNode factor : factors) {
                        report.append(String.format("  • %s\n", factor.asText()));
                    }
                    report.append("\n");
                }
                
                JsonNode recommendations = moraleAssessment.get("recommendations");
                if (recommendations != null && recommendations.isArray() && recommendations.size() > 0) {
                    report.append("Recommendations:\n");
                    for (JsonNode recommendation : recommendations) {
                        report.append(String.format("  • %s\n", recommendation.asText()));
                    }
                    report.append("\n");
                }
            }
            
            report.append("=".repeat(80)).append("\n");
            
            return report.toString();
            
        } catch (Exception e) {
            log.error("Error building plain text report", e);
            return buildErrorReport(e.getMessage());
        }
    }
    
    /**
     * Capitalizes first letter of string.
     */
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
    
    /**
     * Builds error report.
     */
    private String buildErrorReport(String errorMessage) {
        return String.format(
            "# Error Generating Retrospective Report\n\n" +
            "Error: %s\n\n" +
            "Please check the JSON format and try again.",
            errorMessage
        );
    }
}



