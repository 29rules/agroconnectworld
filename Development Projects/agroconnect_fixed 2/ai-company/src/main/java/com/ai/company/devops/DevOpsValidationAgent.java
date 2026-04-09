package com.ai.company.devops;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
/**
 * DevOps Validation Agent Service Interface
 * Validates Docker, CI/CD, environment configs, Nginx rules, certificates, and readiness
 * 
 * This interface is used with AiServices.builder() to create the agent implementation
 */
public interface DevOpsValidationAgent {
    
    @SystemMessage("""
        You are a Senior DevOps Engineer responsible for infrastructure validation.
        Your role is to validate:
        1. Docker configurations
        2. CI/CD pipelines
        3. Environment configurations
        4. Nginx rules
        5. SSL certificates
        6. System readiness
        
        Provide detailed validation reports with actionable recommendations.
        """)
    @UserMessage("""
        Validate Docker configurations:
        Dockerfiles: {{$dockerfiles}}
        Docker Compose: {{$dockerCompose}}
        
        Check:
        - Dockerfile best practices
        - Multi-stage builds
        - Security vulnerabilities
        - Resource limits
        - Health checks
        - Volume mounts
        """)
    String validateDocker(String dockerfiles, String dockerCompose);
    
    @UserMessage("""
        Validate CI/CD pipelines:
        Workflows: {{$workflows}}
        
        Check:
        - Workflow syntax
        - Security best practices
        - Secret management
        - Deployment strategies
        - Rollback procedures
        - Notification setup
        """)
    String validateCICD(String workflows);
    
    @UserMessage("""
        Validate environment configurations:
        Configs: {{$envConfigs}}
        
        Check:
        - Environment variables
        - Secrets management
        - Configuration consistency
        - Missing configurations
        - Security issues
        """)
    String validateEnvironmentConfigs(String envConfigs);
    
    @UserMessage("""
        Validate Nginx rules:
        Config: {{$nginxConfig}}
        
        Check:
        - Syntax correctness
        - Security headers
        - Rate limiting
        - SSL/TLS configuration
        - Proxy settings
        - CORS configuration
        """)
    String validateNginxRules(String nginxConfig);
    
    @UserMessage("""
        Validate SSL certificates:
        Certificates: {{$certificates}}
        
        Check:
        - Certificate validity
        - Expiration dates
        - Chain completeness
        - Key strength
        - Domain coverage
        """)
    String validateCertificates(String certificates);
    
    @UserMessage("""
        Validate system readiness:
        System State: {{$systemState}}
        
        Check:
        - Service health
        - Resource availability
        - Network connectivity
        - Database readiness
        - Dependencies
        - Performance metrics
        """)
    String validateReadiness(String systemState);
}

