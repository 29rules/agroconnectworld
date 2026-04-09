package com.ai.company.retro;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Retrospective Agent
 * 
 * AI agent that generates sprint retrospective reports based on:
 * - Sprint results
 * - Burndown data
 * - Blockers
 * - Velocity report
 * 
 * Outputs structured JSON with:
 * - what_went_well: Positive aspects of the sprint
 * - what_didnt_go_well: Challenges and issues
 * - improvements: Actionable improvements
 * - process_suggestions: Process improvement suggestions
 * - morale_assessment: Team morale assessment
 * 
 * Uses LangChain4j @AiService for AI-powered retrospective generation.
 */
public class RetroAgent {
    
    private static final Logger log = LoggerFactory.getLogger(RetroAgent.class);
    
    private final RetroService retroService;
    
    public RetroAgent(ChatLanguageModel chatModel) {
        this.retroService = AiServices.builder(RetroService.class)
            .chatLanguageModel(chatModel)
            .chatMemoryProvider(sessionId -> MessageWindowChatMemory.withMaxMessages(30))
            .build();
        
        log.info("RetroAgent initialized");
    }
    
    /**
     * Generates a sprint retrospective report.
     * 
     * @param sessionId Session ID for memory
     * @param sprintResults Sprint results summary
     * @param burndownData Burndown chart data
     * @param blockers List of blockers encountered
     * @param velocityReport Velocity report
     * @return Retrospective report as JSON string
     */
    public String generateRetrospective(String sessionId, String sprintResults, 
                                         String burndownData, String blockers, 
                                         String velocityReport) {
        log.info("Generating sprint retrospective");
        
        try {
            String context = String.format(
                "Sprint Results:\n%s\n\nBurndown Data:\n%s\n\nBlockers:\n%s\n\nVelocity Report:\n%s",
                sprintResults, burndownData, blockers, velocityReport
            );
            
            String report = retroService.generateRetrospective(context, sessionId);
            
            log.info("Sprint retrospective generated successfully");
            return report;
            
        } catch (Exception e) {
            log.error("Error generating retrospective", e);
            return "ERROR: Retrospective generation failed: " + e.getMessage();
        }
    }
    
    /**
     * LangChain4j AI Service interface for Retrospective Agent.
     */
    interface RetroService {
        
        @SystemMessage("""
            You are a Senior Agile Coach and Retrospective Facilitator for AgroConnectWorld AI Company.
            
            Your role is to facilitate sprint retrospectives and generate comprehensive retrospective reports.
            
            INPUTS:
            - Sprint results: What was accomplished, completed stories, metrics
            - Burndown data: Burndown chart data showing progress
            - Blockers: List of blockers encountered during sprint
            - Velocity report: Velocity tracking and predictions
            
            OUTPUT FORMAT (JSON):
            {
              "what_went_well": [
                {
                  "item": "What went well description",
                  "category": "process|communication|tools|team|delivery",
                  "impact": "high|medium|low",
                  "evidence": "Supporting evidence or metrics"
                }
              ],
              "what_didnt_go_well": [
                {
                  "item": "What didn't go well description",
                  "category": "process|communication|tools|team|delivery|technical",
                  "severity": "high|medium|low",
                  "root_cause": "Root cause analysis",
                  "impact": "Impact on sprint goal"
                }
              ],
              "improvements": [
                {
                  "improvement": "Actionable improvement suggestion",
                  "category": "process|communication|tools|team|delivery|technical",
                  "priority": "high|medium|low",
                  "owner": "Who should implement",
                  "timeline": "When to implement (next sprint|immediate|future)",
                  "success_criteria": "How to measure success"
                }
              ],
              "process_suggestions": [
                {
                  "suggestion": "Process improvement suggestion",
                  "rationale": "Why this would help",
                  "implementation": "How to implement",
                  "expected_benefit": "Expected benefit"
                }
              ],
              "morale_assessment": {
                "overall_morale": "high|medium|low",
                "team_satisfaction": "high|medium|low",
                "stress_level": "high|medium|low",
                "engagement": "high|medium|low",
                "factors": [
                  "Factor affecting morale"
                ],
                "recommendations": [
                  "Recommendation to improve morale"
                ]
              }
            }
            
            RETROSPECTIVE PRINCIPLES:
            1. Focus on process, not people
            2. Be constructive and solution-oriented
            3. Identify actionable improvements
            4. Celebrate successes
            5. Learn from challenges
            6. Create safe space for honest feedback
            
            RETROSPECTIVE STRUCTURE:
            - What Went Well: Celebrate successes and positive aspects
            - What Didn't Go Well: Identify challenges and issues
            - Improvements: Actionable improvements for next sprint
            - Process Suggestions: Long-term process improvements
            - Morale Assessment: Team health and satisfaction
            
            RULES:
            1. Be specific and evidence-based
            2. Focus on actionable improvements
            3. Balance positive and negative feedback
            4. Consider root causes, not just symptoms
            5. Prioritize improvements by impact
            6. Consider team morale and well-being
            7. Format JSON strictly (no trailing commas, proper escaping)
            8. Always include all sections, even if empty
            
            ZERO-IMPACT MODE:
            - This is a reporting and facilitation tool only
            - No code modifications
            - No system changes
            - Only generates retrospective reports
            """)
        String generateRetrospective(
            @UserMessage("Generate sprint retrospective from: {{context}}") 
            String context, 
            @MemoryId String sessionId);
    }
}



