package com.ai.company.agents.productmanager;

import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Product Manager Agent
 * 
 * Responsibilities:
 * - Converts company vision into product roadmap
 * - Breaks down epics into user stories
 * - Defines acceptance criteria
 * - Prioritizes features
 * - Creates release plans
 * 
 * ZERO-IMPACT MODE: Only produces product specifications, never modifies existing features.
 */
public class ProductManagerAgent {
    
    private static final Logger log = LoggerFactory.getLogger(ProductManagerAgent.class);
    
    private final ProductManagerAgentService agentService;
    
    public ProductManagerAgent(ChatLanguageModel chatModel) {
        this.agentService = AiServices.builder(ProductManagerAgentService.class)
            .chatLanguageModel(chatModel)
            .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(50))
            .build();
    }
    
    public String createEpic(String vision, String sessionId) {
        log.info("Product Manager Agent creating epic");
        return agentService.createEpic(vision, sessionId);
    }
    
    public String createUserStory(String epic, String sessionId) {
        log.info("Product Manager Agent creating user story");
        return agentService.createUserStory(epic, sessionId);
    }
    
    public String defineAcceptanceCriteria(String userStory, String sessionId) {
        log.info("Product Manager Agent defining acceptance criteria");
        return agentService.defineAcceptanceCriteria(userStory, sessionId);
    }
    
    public String prioritizeFeatures(String features, String sessionId) {
        log.info("Product Manager Agent prioritizing features");
        return agentService.prioritizeFeatures(features, sessionId);
    }
    
    interface ProductManagerAgentService {
        
        @SystemMessage("""
            You are the Product Manager Agent for AgroConnectWorld.
            
            Your role:
            - Convert company vision into product roadmap
            - Break down epics into user stories
            - Define clear acceptance criteria
            - Prioritize features and tasks
            - Create release plans
            - Coordinate with technical agents
            
            ZERO-IMPACT MODE:
            - MUST NOT modify existing features
            - MUST NOT change existing user flows
            - Can only propose new features and enhancements
            
            User Story Format:
            "As a [user type], I want [goal] so that [benefit]"
            
            Output Format: JSON
            {
              "agent": "product_manager_agent",
              "product_spec": {
                "epic_name": "Epic description",
                "epic_priority": "high|medium|low",
                "user_stories": [{
                  "story_id": "US-001",
                  "title": "User story title",
                  "description": "As a [user type], I want [goal] so that [benefit]",
                  "acceptance_criteria": ["Criterion 1", "Criterion 2"],
                  "priority": "high|medium|low",
                  "story_points": 5,
                  "technical_requirements": "Technical notes"
                }],
                "feature_specifications": {
                  "feature_name": "Feature description",
                  "user_value": "Value proposition",
                  "user_experience": "UX description"
                }
              },
              "roadmap": {
                "sprint_planning": "Sprint breakdown",
                "milestones": ["List of milestones"]
              }
            }
            """)
        String createEpic(@UserMessage String vision, @MemoryId String sessionId);
        
        String createUserStory(@UserMessage("Create user stories for epic: {{epic}}") 
                               String epic, @MemoryId String sessionId);
        
        String defineAcceptanceCriteria(@UserMessage("Define acceptance criteria for: {{userStory}}") 
                                       String userStory, @MemoryId String sessionId);
        
        String prioritizeFeatures(@UserMessage("Prioritize these features: {{features}}") 
                                 String features, @MemoryId String sessionId);
    }
}



