package com.ai.company.engineering;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * AI Agent Service Interface for Engineering Pipeline Automation
 * Generates stories, tasks, PR reviews, architecture warnings, and refactor proposals
 * 
 * This interface is used with AiServices.builder() to create the agent implementation
 */
public interface EngineeringPipelineAgent {
    
    @SystemMessage("""
        You are a Senior Engineering Lead responsible for:
        1. Generating user stories from requirements
        2. Creating technical tasks
        3. Reviewing pull requests
        4. Identifying architecture warnings
        5. Proposing refactoring opportunities
        
        Always provide structured, actionable output in JSON format.
        """)
    @UserMessage("""
        Generate user stories for the following requirement: {{$requirement}}
        
        Format as JSON with:
        - story_id
        - title
        - description
        - acceptance_criteria
        - story_points
        - priority
        - technical_tasks
        """)
    String generateUserStories(String requirement);
    
    @UserMessage("""
        Generate technical tasks for implementing: {{$feature}}
        
        Break down into:
        - Backend tasks
        - Frontend tasks
        - Database tasks
        - Testing tasks
        - DevOps tasks
        
        Format as JSON array of tasks with:
        - task_id
        - title
        - description
        - estimated_hours
        - dependencies
        - assigned_service
        """)
    String generateTechnicalTasks(String feature);
    
    @UserMessage("""
        Review this pull request:
        Title: {{$prTitle}}
        Description: {{$prDescription}}
        Changed Files: {{$changedFiles}}
        Diff: {{$diff}}
        
        Provide:
        - Code quality assessment
        - Security concerns
        - Performance implications
        - Test coverage review
        - Architecture impact
        - Suggestions for improvement
        
        Format as JSON with approval status and detailed comments.
        """)
    String reviewPullRequest(String prTitle, String prDescription, String changedFiles, String diff);
    
    @UserMessage("""
        Analyze the architecture for warnings:
        Codebase: {{$codebase}}
        Current Architecture: {{$architecture}}
        
        Identify:
        - Architecture violations
        - Design pattern issues
        - Coupling problems
        - Scalability concerns
        - Maintainability issues
        
        Format as JSON with severity and recommendations.
        """)
    String analyzeArchitectureWarnings(String codebase, String architecture);
    
    @UserMessage("""
        Propose refactoring opportunities for: {{$code}}
        
        Identify:
        - Code smells
        - Duplication
        - Complex methods
        - Dead code
        - Performance improvements
        
        For each, provide:
        - Current issue
        - Proposed refactoring
        - Expected benefits
        - Risk level
        - Estimated effort
        
        Format as JSON array.
        """)
    String proposeRefactoring(String code);
}

