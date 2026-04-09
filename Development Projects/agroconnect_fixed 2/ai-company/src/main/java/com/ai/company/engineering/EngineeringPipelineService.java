package com.ai.company.engineering;

import com.ai.company.tools.code.CodeReaderTool;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Service for Engineering Pipeline Automation
 */
@Service
public class EngineeringPipelineService {
    
    private static final Logger log = LoggerFactory.getLogger(EngineeringPipelineService.class);
    
    private final EngineeringPipelineAgent pipelineAgent;
    
    @Autowired
    public EngineeringPipelineService(ChatLanguageModel chatModel) {
        this.pipelineAgent = AiServices.builder(EngineeringPipelineAgent.class)
            .chatLanguageModel(chatModel)
            .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(50))
            .build();
    }
    
    @Autowired
    private CodeReaderTool codeReader;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String ENGINEERING_OUTPUT_DIR = "ai-company/outputs/engineering";
    
    /**
     * Generate user stories from requirement
     */
    public List<UserStory> generateUserStories(String requirement) {
        log.info("Generating user stories for: {}", requirement);
        
        try {
            String storiesJson = pipelineAgent.generateUserStories(requirement);
            return parseUserStories(storiesJson);
        } catch (Exception e) {
            log.error("Failed to generate user stories", e);
            return Collections.emptyList();
        }
    }
    
    /**
     * Generate technical tasks for a feature
     */
    public List<TechnicalTask> generateTechnicalTasks(String feature) {
        log.info("Generating technical tasks for: {}", feature);
        
        try {
            String tasksJson = pipelineAgent.generateTechnicalTasks(feature);
            return parseTechnicalTasks(tasksJson);
        } catch (Exception e) {
            log.error("Failed to generate technical tasks", e);
            return Collections.emptyList();
        }
    }
    
    /**
     * Review a pull request
     */
    public PRReview reviewPullRequest(String prTitle, String prDescription, String changedFiles, String diff) {
        log.info("Reviewing PR: {}", prTitle);
        
        try {
            String reviewJson = pipelineAgent.reviewPullRequest(prTitle, prDescription, changedFiles, diff);
            return parsePRReview(reviewJson);
        } catch (Exception e) {
            log.error("Failed to review PR", e);
            return new PRReview();
        }
    }
    
    /**
     * Analyze architecture warnings
     */
    public List<ArchitectureWarning> analyzeArchitectureWarnings() {
        log.info("Analyzing architecture warnings...");
        
        try {
            String codebase = codeReader.readFolder("backend") + "\n" + codeReader.readFolder("frontend/src");
            String architecture = readArchitectureDoc();
            
            String warningsJson = pipelineAgent.analyzeArchitectureWarnings(codebase, architecture);
            return parseArchitectureWarnings(warningsJson);
        } catch (Exception e) {
            log.error("Failed to analyze architecture", e);
            return Collections.emptyList();
        }
    }
    
    /**
     * Propose refactoring opportunities
     */
    public List<RefactorProposal> proposeRefactoring(String filePath) {
        log.info("Proposing refactoring for: {}", filePath);
        
        try {
            String code = codeReader.readFile(filePath);
            String proposalsJson = pipelineAgent.proposeRefactoring(code);
            return parseRefactorProposals(proposalsJson);
        } catch (Exception e) {
            log.error("Failed to propose refactoring", e);
            return Collections.emptyList();
        }
    }
    
    private String readArchitectureDoc() {
        try {
            Path archDoc = Paths.get("ai-company/reports/SYSTEM_OVERVIEW.md");
            if (Files.exists(archDoc)) {
                return Files.readString(archDoc);
            }
        } catch (Exception e) {
            log.warn("Failed to read architecture doc", e);
        }
        return "";
    }
    
    @SuppressWarnings("unchecked")
    private List<UserStory> parseUserStories(String json) {
        try {
            Map<String, Object> data = objectMapper.readValue(json, Map.class);
            List<Map<String, Object>> stories = (List<Map<String, Object>>) data.get("stories");
            
            List<UserStory> result = new ArrayList<>();
            if (stories != null) {
                for (Map<String, Object> story : stories) {
                    UserStory us = new UserStory();
                    us.setStoryId((String) story.get("story_id"));
                    us.setTitle((String) story.get("title"));
                    us.setDescription((String) story.get("description"));
                    us.setAcceptanceCriteria((List<String>) story.get("acceptance_criteria"));
                    us.setStoryPoints((Integer) story.getOrDefault("story_points", 3));
                    us.setPriority((String) story.getOrDefault("priority", "MEDIUM"));
                    result.add(us);
                }
            }
            return result;
        } catch (Exception e) {
            log.error("Failed to parse user stories", e);
            return Collections.emptyList();
        }
    }
    
    @SuppressWarnings("unchecked")
    private List<TechnicalTask> parseTechnicalTasks(String json) {
        try {
            List<Map<String, Object>> tasks = objectMapper.readValue(json, List.class);
            List<TechnicalTask> result = new ArrayList<>();
            
            for (Map<String, Object> task : tasks) {
                TechnicalTask tt = new TechnicalTask();
                tt.setTaskId((String) task.get("task_id"));
                tt.setTitle((String) task.get("title"));
                tt.setDescription((String) task.get("description"));
                tt.setEstimatedHours((Integer) task.getOrDefault("estimated_hours", 4));
                tt.setDependencies((List<String>) task.get("dependencies"));
                tt.setAssignedService((String) task.get("assigned_service"));
                result.add(tt);
            }
            return result;
        } catch (Exception e) {
            log.error("Failed to parse technical tasks", e);
            return Collections.emptyList();
        }
    }
    
    @SuppressWarnings("unchecked")
    private PRReview parsePRReview(String json) {
        try {
            Map<String, Object> data = objectMapper.readValue(json, Map.class);
            PRReview review = new PRReview();
            review.setApproved((Boolean) data.getOrDefault("approved", false));
            review.setComments((List<String>) data.get("comments"));
            review.setCodeQuality((String) data.get("code_quality"));
            review.setSecurityConcerns((List<String>) data.get("security_concerns"));
            review.setPerformanceImplications((List<String>) data.get("performance_implications"));
            review.setTestCoverage((String) data.get("test_coverage"));
            review.setArchitectureImpact((String) data.get("architecture_impact"));
            review.setSuggestions((List<String>) data.get("suggestions"));
            return review;
        } catch (Exception e) {
            log.error("Failed to parse PR review", e);
            return new PRReview();
        }
    }
    
    @SuppressWarnings("unchecked")
    private List<ArchitectureWarning> parseArchitectureWarnings(String json) {
        try {
            List<Map<String, Object>> warnings = objectMapper.readValue(json, List.class);
            List<ArchitectureWarning> result = new ArrayList<>();
            
            for (Map<String, Object> warning : warnings) {
                ArchitectureWarning aw = new ArchitectureWarning();
                aw.setSeverity((String) warning.get("severity"));
                aw.setCategory((String) warning.get("category"));
                aw.setDescription((String) warning.get("description"));
                aw.setRecommendation((String) warning.get("recommendation"));
                result.add(aw);
            }
            return result;
        } catch (Exception e) {
            log.error("Failed to parse architecture warnings", e);
            return Collections.emptyList();
        }
    }
    
    @SuppressWarnings("unchecked")
    private List<RefactorProposal> parseRefactorProposals(String json) {
        try {
            List<Map<String, Object>> proposals = objectMapper.readValue(json, List.class);
            List<RefactorProposal> result = new ArrayList<>();
            
            for (Map<String, Object> proposal : proposals) {
                RefactorProposal rp = new RefactorProposal();
                rp.setIssue((String) proposal.get("current_issue"));
                rp.setProposedRefactoring((String) proposal.get("proposed_refactoring"));
                rp.setExpectedBenefits((String) proposal.get("expected_benefits"));
                rp.setRiskLevel((String) proposal.get("risk_level"));
                rp.setEstimatedEffort((String) proposal.get("estimated_effort"));
                result.add(rp);
            }
            return result;
        } catch (Exception e) {
            log.error("Failed to parse refactor proposals", e);
            return Collections.emptyList();
        }
    }
}

