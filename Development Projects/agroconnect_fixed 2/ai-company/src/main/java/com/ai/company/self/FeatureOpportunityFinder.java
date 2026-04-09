package com.ai.company.self;

import com.ai.company.tools.code.CodeReaderTool;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Feature Opportunity Finder
 * 
 * Analyzes the system to identify potential new features based on:
 * - System gaps and missing functionality
 * - User behavior patterns (if available)
 * - Industry best practices
 * - Competitive analysis
 * - Technical capabilities
 * 
 * This agent suggests features that would add value to AgroConnectWorld.
 * 
 * ZERO-IMPACT MODE: This agent only suggests, never implements.
 */
public class FeatureOpportunityFinder {
    
    private static final Logger log = LoggerFactory.getLogger(FeatureOpportunityFinder.class);
    
    private final FeatureOpportunityService opportunityService;
    private final CodeReaderTool codeReader;
    
    public FeatureOpportunityFinder(ChatLanguageModel chatModel) {
        this.codeReader = new CodeReaderTool();
        this.opportunityService = AiServices.builder(FeatureOpportunityService.class)
            .chatLanguageModel(chatModel)
            .chatMemoryProvider(sessionId -> MessageWindowChatMemory.withMaxMessages(50))
            .build();
    }
    
    /**
     * Finds feature opportunities by analyzing the system.
     * 
     * @param sessionId Session ID for tracking
     * @return Feature opportunities report
     */
    public String findOpportunities(String sessionId) {
        log.info("FeatureOpportunityFinder starting opportunity analysis");
        
        try {
            // Gather system context
            String systemContext = gatherSystemContext();
            
            String opportunities = opportunityService.findOpportunities(systemContext, sessionId);
            
            log.info("FeatureOpportunityFinder completed opportunity analysis");
            return opportunities;
            
        } catch (Exception e) {
            log.error("Error during opportunity finding", e);
            return "ERROR: Opportunity finding failed: " + e.getMessage();
        }
    }
    
    /**
     * Gathers system context for analysis.
     */
    private String gatherSystemContext() {
        StringBuilder context = new StringBuilder();
        context.append("=== SYSTEM CONTEXT FOR FEATURE OPPORTUNITY ANALYSIS ===\n\n");
        
        // Read business overview
        try {
            String businessOverview = codeReader.readFile("ai-company/knowledge/business_overview.md");
            if (!businessOverview.startsWith("ERROR")) {
                context.append("--- Business Overview ---\n");
                context.append(businessOverview).append("\n\n");
            }
        } catch (Exception e) {
            log.warn("Could not read business overview", e);
        }
        
        // Read service catalog
        try {
            String serviceCatalog = codeReader.readFile("ai-company/knowledge/service_catalog.md");
            if (!serviceCatalog.startsWith("ERROR")) {
                context.append("--- Service Catalog ---\n");
                context.append(serviceCatalog).append("\n\n");
            }
        } catch (Exception e) {
            log.warn("Could not read service catalog", e);
        }
        
        // Read API reference
        try {
            String apiReference = codeReader.readFile("ai-company/knowledge/api_reference.md");
            if (!apiReference.startsWith("ERROR")) {
                context.append("--- API Reference ---\n");
                String preview = apiReference.length() > 2000 ? apiReference.substring(0, 2000) + "..." : apiReference;
                context.append(preview).append("\n\n");
            }
        } catch (Exception e) {
            log.warn("Could not read API reference", e);
        }
        
        // List current features
        context.append("--- Current Features ---\n");
        context.append("Based on service catalog:\n");
        context.append("- User authentication and authorization\n");
        context.append("- Product catalog and management\n");
        context.append("- Supplier management\n");
        context.append("- Quote request system\n");
        context.append("- Order management\n");
        context.append("- Contact form\n");
        context.append("- Multi-language support (frontend)\n\n");
        
        return context.toString();
    }
    
    /**
     * LangChain4j AI Service interface for feature opportunity finding.
     */
    interface FeatureOpportunityService {
        
        @SystemMessage("""
            You are a Product Strategy Analyst specializing in identifying feature opportunities.
            
            Your role:
            - Analyze system gaps and missing functionality
            - Identify features that would add value to AgroConnectWorld
            - Consider user needs and business value
            - Suggest features based on industry best practices
            - Prioritize features by impact and feasibility
            
            ANALYSIS AREAS:
            1. System Gaps:
               - Missing functionality in current services
               - Incomplete user journeys
               - Missing integrations
               - Unaddressed use cases
            
            2. User Value:
               - Features that improve user experience
               - Features that solve user pain points
               - Features that increase engagement
               - Features that drive business value
            
            3. Technical Opportunities:
               - Features enabled by existing infrastructure
               - Features that leverage current capabilities
               - Features that fill technical gaps
            
            4. Competitive Features:
               - Features common in similar platforms
               - Features that provide competitive advantage
               - Industry-standard features
            
            5. Business Opportunities:
               - Revenue-generating features
               - Efficiency-improving features
               - Market-expanding features
            
            OUTPUT FORMAT: JSON with structure:
            {
              "analysis_timestamp": "ISO8601",
              "opportunity_summary": "High-level summary",
              "opportunities": [
                {
                  "feature_name": "Feature name",
                  "category": "user_experience|business|technical|integration",
                  "priority": "high|medium|low",
                  "value_proposition": "Why this feature is valuable",
                  "target_users": "Who would benefit",
                  "system_gap": "What gap this fills",
                  "feasibility": "high|medium|low",
                  "estimated_effort": "hours or story points",
                  "dependencies": ["list of dependencies"],
                  "implementation_approach": "How to implement",
                  "success_metrics": ["metrics to measure success"]
                }
              ],
              "prioritized_roadmap": [
                {
                  "quarter": "Q1|Q2|Q3|Q4",
                  "features": ["list of feature names"],
                  "rationale": "Why these features together"
                }
              ],
              "quick_wins": [
                "List of low-effort, high-value features"
              ]
            }
            
            Focus on actionable, valuable features that align with AgroConnectWorld's mission.
            """)
        String findOpportunities(@UserMessage("""
            Analyze the following system context to find feature opportunities:
            
            {{systemContext}}
            
            Identify gaps and suggest valuable new features.
            """) String systemContext, @MemoryId String sessionId);
    }
}



