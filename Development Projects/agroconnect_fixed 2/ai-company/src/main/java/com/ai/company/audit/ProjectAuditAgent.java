package com.ai.company.audit;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

import java.util.List;

/**
 * AI Agent Service Interface for comprehensive project auditing
 * Runs automated audits to identify bugs, issues, and vulnerabilities
 * 
 * This interface is used with AiServices.builder() to create the agent implementation
 */
public interface ProjectAuditAgent {
    
    @SystemMessage("""
        You are a Senior Software Engineer and Security Expert conducting a comprehensive project audit.
        Your role is to identify:
        1. Bugs and code issues
        2. UI/UX problems
        3. Backend errors and exceptions
        4. Broken API links and endpoints
        5. Missing or inadequate tests
        6. Performance bottlenecks
        7. Security vulnerabilities
        
        Provide detailed, actionable findings with severity levels (CRITICAL, HIGH, MEDIUM, LOW).
        Format your response as structured JSON.
        """)
    @UserMessage("""
        Conduct a full project audit for AgroConnectWorld.
        Analyze the codebase, APIs, frontend, backend, and infrastructure.
        
        Focus on:
        - Code quality and bugs
        - API endpoint availability and correctness
        - Frontend UI/UX issues
        - Backend service health
        - Test coverage gaps
        - Performance issues
        - Security vulnerabilities
        
        Return a comprehensive audit report in JSON format.
        """)
    String conductFullAudit();
    
    @UserMessage("""
        Check for bugs in the {{$codebase}}.
        Identify:
        - Null pointer exceptions
        - Logic errors
        - Type mismatches
        - Resource leaks
        - Race conditions
        """)
    String auditBugs(String codebase);
    
    @UserMessage("""
        Analyze UI/UX issues in the frontend.
        Check for:
        - Broken links
        - Layout problems
        - Accessibility issues
        - Responsive design problems
        - User experience issues
        """)
    String auditUI(String frontendCode);
    
    @UserMessage("""
        Validate all API endpoints.
        Check:
        - Endpoint availability
        - Request/response formats
        - Authentication/authorization
        - Error handling
        - Rate limiting
        """)
    String auditAPIs(String apiSpecs);
    
    @UserMessage("""
        Analyze test coverage.
        Identify:
        - Missing unit tests
        - Missing integration tests
        - Inadequate test coverage
        - Test quality issues
        """)
    String auditTests(String testCode);
    
    @UserMessage("""
        Identify performance problems.
        Check:
        - Slow database queries
        - N+1 query problems
        - Memory leaks
        - CPU bottlenecks
        - Network latency issues
        """)
    String auditPerformance(String codebase);
    
    @UserMessage("""
        Scan for security vulnerabilities.
        Check:
        - SQL injection risks
        - XSS vulnerabilities
        - CSRF protection
        - Authentication flaws
        - Authorization bypasses
        - Sensitive data exposure
        """)
    String auditSecurity(String codebase);
}

