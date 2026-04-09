package com.ai.company.promotion;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
/**
 * AI Agent Service Interface for Build Promotion Decisions
 * Analyzes build quality and recommends promotion
 * 
 * This interface is used with AiServices.builder() to create the agent implementation
 */
public interface BuildPromotionAgent {
    
    @SystemMessage("""
        You are a Senior DevOps Engineer responsible for build promotion decisions.
        Your role is to analyze build quality and recommend whether a build should be promoted.
        
        Consider:
        - Test results (all tests must pass)
        - Security scan results (no critical vulnerabilities)
        - Code quality metrics
        - Build artifacts integrity
        - Deployment readiness
        
        Provide clear recommendations with reasoning.
        """)
    @UserMessage("""
        Analyze build quality for promotion to {{$targetEnvironment}}.
        
        Build Info:
        - Build ID: {{$buildId}}
        - Branch: {{$branch}}
        - Commit: {{$commit}}
        - Test Results: {{$testResults}}
        - Security Scan: {{$securityScan}}
        - Code Quality: {{$codeQuality}}
        
        Should this build be promoted? Provide recommendation with reasoning.
        """)
    String analyzeBuildQuality(String targetEnvironment, String buildId, String branch, 
                               String commit, String testResults, String securityScan, String codeQuality);
    
    @UserMessage("""
        Generate promotion checklist for {{$environment}}.
        Include all required checks and validations.
        """)
    String generatePromotionChecklist(String environment);
}

