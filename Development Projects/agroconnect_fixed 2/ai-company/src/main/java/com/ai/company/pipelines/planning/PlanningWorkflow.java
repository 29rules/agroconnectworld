package com.ai.company.pipelines.planning;

import com.ai.company.agents.cto.CTOAgent;
import com.ai.company.agents.productmanager.ProductManagerAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Planning Workflow
 * 
 * Converts company vision and business goals into actionable product specifications,
 * epics, and user stories.
 * 
 * Participating Agents:
 * - Product Manager Agent (primary)
 * - CTO Agent (feasibility review)
 */
public class PlanningWorkflow {
    
    private static final Logger log = LoggerFactory.getLogger(PlanningWorkflow.class);
    
    private final ProductManagerAgent productManager;
    private final CTOAgent cto;
    
    public PlanningWorkflow(ProductManagerAgent productManager, CTOAgent cto) {
        this.productManager = productManager;
        this.cto = cto;
    }
    
    /**
     * Execute planning workflow
     * 
     * Input: Company vision, business requirements
     * Output: Product roadmap, epics, user stories with acceptance criteria
     */
    public PlanningResult execute(String vision, String businessRequirements, String sessionId) {
        log.info("Starting Planning Workflow for session: {}", sessionId);
        
        // Step 1: Product Manager creates epics
        log.info("Step 1: Creating epics from vision");
        String epicsJson = productManager.createEpic(vision, sessionId);
        
        // Step 2: Product Manager creates user stories
        log.info("Step 2: Creating user stories from epics");
        String userStoriesJson = productManager.createUserStory(epicsJson, sessionId);
        
        // Step 3: Define acceptance criteria
        log.info("Step 3: Defining acceptance criteria");
        String acceptanceCriteriaJson = productManager.defineAcceptanceCriteria(userStoriesJson, sessionId);
        
        // Step 4: Prioritize features
        log.info("Step 4: Prioritizing features");
        String prioritizedFeaturesJson = productManager.prioritizeFeatures(acceptanceCriteriaJson, sessionId);
        
        // Step 5: CTO reviews for technical feasibility
        log.info("Step 5: CTO reviewing for technical feasibility");
        String ctoReview = cto.reviewArchitecture(prioritizedFeaturesJson, sessionId);
        
        log.info("Planning Workflow completed for session: {}", sessionId);
        
        return new PlanningResult(
            epicsJson,
            userStoriesJson,
            acceptanceCriteriaJson,
            prioritizedFeaturesJson,
            ctoReview
        );
    }
    
    /**
     * Result of planning workflow
     */
    public static class PlanningResult {
        private final String epics;
        private final String userStories;
        private final String acceptanceCriteria;
        private final String prioritizedFeatures;
        private final String ctoReview;
        
        public PlanningResult(String epics, String userStories, String acceptanceCriteria,
                             String prioritizedFeatures, String ctoReview) {
            this.epics = epics;
            this.userStories = userStories;
            this.acceptanceCriteria = acceptanceCriteria;
            this.prioritizedFeatures = prioritizedFeatures;
            this.ctoReview = ctoReview;
        }
        
        // Getters
        public String getEpics() { return epics; }
        public String getUserStories() { return userStories; }
        public String getAcceptanceCriteria() { return acceptanceCriteria; }
        public String getPrioritizedFeatures() { return prioritizedFeatures; }
        public String getCtoReview() { return ctoReview; }
    }
}



