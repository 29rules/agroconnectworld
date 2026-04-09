package com.ai.company.standup;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Standup Agent
 * 
 * AI agent that generates daily standup reports based on:
 * - Backlog state
 * - Yesterday's sprint progress
 * - Blockers from agents
 * 
 * Outputs structured JSON with:
 * - yesterday_completed: What was completed yesterday
 * - today_plan: What is planned for today
 * - blockers: Current blockers
 * - risk_flag: Risk indicators
 * 
 * Uses LangChain4j @AiService for AI-powered standup generation.
 */
public class StandupAgent {
    
    private static final Logger log = LoggerFactory.getLogger(StandupAgent.class);
    
    private final StandupService standupService;
    
    public StandupAgent(ChatLanguageModel chatModel) {
        this.standupService = AiServices.builder(StandupService.class)
            .chatLanguageModel(chatModel)
            .chatMemoryProvider(sessionId -> MessageWindowChatMemory.withMaxMessages(20))
            .build();
        
        log.info("StandupAgent initialized");
    }
    
    /**
     * Generates a daily standup report.
     * 
     * @param sessionId Session ID for memory
     * @param backlogState Current backlog state summary
     * @param yesterdayProgress Yesterday's completed work
     * @param blockers List of blockers from agents
     * @return Standup report as JSON string
     */
    public String generateStandupReport(String sessionId, String backlogState, 
                                        String yesterdayProgress, String blockers) {
        log.info("Generating daily standup report");
        
        try {
            String context = String.format(
                "Backlog State:\n%s\n\nYesterday's Progress:\n%s\n\nBlockers:\n%s",
                backlogState, yesterdayProgress, blockers
            );
            
            String report = standupService.generateStandupReport(context, sessionId);
            
            log.info("Daily standup report generated successfully");
            return report;
            
        } catch (Exception e) {
            log.error("Error generating standup report", e);
            return "ERROR: Standup report generation failed: " + e.getMessage();
        }
    }
    
    /**
     * LangChain4j AI Service interface for Standup Agent.
     */
    interface StandupService {
        
        @SystemMessage("""
            You are a Daily Standup Coordinator for an AI development team.
            
            Your role is to generate concise, actionable daily standup reports.
            
            INPUTS:
            - Backlog state: Current state of the product backlog
            - Yesterday's progress: What was completed yesterday
            - Blockers: Current blockers from agents
            
            OUTPUT FORMAT (JSON):
            {
              "yesterday_completed": [
                {
                  "item": "Item title or task",
                  "agent": "Agent name",
                  "status": "completed|in_progress|blocked"
                }
              ],
              "today_plan": [
                {
                  "item": "Item title or task",
                  "agent": "Agent name",
                  "priority": "high|medium|low",
                  "estimated_hours": 4
                }
              ],
              "blockers": [
                {
                  "item": "Blocker description",
                  "agent": "Agent name",
                  "severity": "high|medium|low",
                  "help_needed": "What help is needed"
                }
              ],
              "risk_flag": {
                "level": "none|low|medium|high",
                "issues": [
                  "Risk description"
                ]
              }
            }
            
            RULES:
            1. Be concise and actionable
            2. Focus on what matters (completed work, today's plan, blockers)
            3. Identify risks early
            4. Use clear, professional language
            5. Format JSON strictly (no trailing commas, proper escaping)
            6. If no data provided, return empty arrays/objects
            7. Always include risk_flag (even if level is "none")
            
            ZERO-IMPACT MODE:
            - This is a reporting tool only
            - No code modifications
            - No system changes
            - Only generates reports
            """)
        String generateStandupReport(
            @UserMessage("Generate daily standup report from: {{context}}") 
            String context, 
            @MemoryId String sessionId);
    }
}

