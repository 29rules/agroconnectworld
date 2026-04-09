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
 * Architecture Drift Detector
 * 
 * Detects when the actual implementation diverges from the planned architecture.
 * 
 * This agent:
 * - Compares current codebase structure with documented architecture
 * - Identifies architectural violations
 * - Detects service coupling issues
 * - Finds missing abstractions
 * - Suggests corrections to align with planned architecture
 * 
 * ZERO-IMPACT MODE: This agent only analyzes and suggests, never modifies code.
 */
public class ArchitectureDriftDetector {
    
    private static final Logger log = LoggerFactory.getLogger(ArchitectureDriftDetector.class);
    
    private final ArchitectureDriftService driftService;
    private final CodeReaderTool codeReader;
    
    public ArchitectureDriftDetector(ChatLanguageModel chatModel) {
        this.codeReader = new CodeReaderTool();
        this.driftService = AiServices.builder(ArchitectureDriftService.class)
            .chatLanguageModel(chatModel)
            .chatMemoryProvider(sessionId -> MessageWindowChatMemory.withMaxMessages(50))
            .build();
    }
    
    /**
     * Detects architecture drift by comparing implementation with planned architecture.
     * 
     * @param sessionId Session ID for tracking
     * @return Drift detection report with violations and suggestions
     */
    public String detectDrift(String sessionId) {
        log.info("ArchitectureDriftDetector starting drift analysis");
        
        try {
            // Read architecture documentation
            String architectureDoc = readArchitectureDocumentation();
            
            // Read current implementation structure
            String implementationStructure = readImplementationStructure();
            
            String driftReport = driftService.detectDrift(architectureDoc, implementationStructure, sessionId);
            
            log.info("ArchitectureDriftDetector completed drift analysis");
            return driftReport;
            
        } catch (Exception e) {
            log.error("Error during drift detection", e);
            return "ERROR: Drift detection failed: " + e.getMessage();
        }
    }
    
    /**
     * Reads architecture documentation from knowledge base.
     */
    private String readArchitectureDocumentation() {
        StringBuilder doc = new StringBuilder();
        
        try {
            // Try to read architecture docs
            String[] docFiles = {
                "ai-company/knowledge/architecture_principles.md",
                "ai-company/knowledge/service_catalog.md",
                "ai-company/knowledge/api_reference.md"
            };
            
            for (String file : docFiles) {
                String content = codeReader.readFile(file);
                if (!content.startsWith("ERROR") && !content.startsWith("WARNING")) {
                    doc.append("--- ").append(file).append(" ---\n");
                    doc.append(content).append("\n\n");
                }
            }
            
            if (doc.length() == 0) {
                doc.append("Architecture documentation not found. Using default microservice architecture principles.");
            }
            
        } catch (Exception e) {
            log.warn("Error reading architecture documentation", e);
            doc.append("Could not read architecture docs. Using default principles.");
        }
        
        return doc.toString();
    }
    
    /**
     * Reads current implementation structure.
     */
    private String readImplementationStructure() {
        StringBuilder structure = new StringBuilder();
        structure.append("=== CURRENT IMPLEMENTATION STRUCTURE ===\n\n");
        
        // Read key configuration files
        String[] structureFiles = {
            "ops/docker-compose.yml",
            "backend/gateway/pom.xml",
            "backend/auth-service/pom.xml",
            "backend/product-service/pom.xml"
        };
        
        for (String file : structureFiles) {
            try {
                String content = codeReader.readFile(file);
                if (!content.startsWith("ERROR") && !content.startsWith("WARNING")) {
                    structure.append("--- ").append(file).append(" ---\n");
                    // Limit content
                    String preview = content.length() > 1000 ? content.substring(0, 1000) + "..." : content;
                    structure.append(preview).append("\n\n");
                }
            } catch (Exception e) {
                log.warn("Error reading structure file: {}", file, e);
            }
        }
        
        return structure.toString();
    }
    
    /**
     * LangChain4j AI Service interface for drift detection.
     */
    interface ArchitectureDriftService {
        
        @SystemMessage("""
            You are an Architecture Compliance Analyst specializing in detecting architectural drift.
            
            Your role:
            - Compare planned architecture with actual implementation
            - Detect violations of architectural principles
            - Identify service coupling issues
            - Find missing abstractions or layers
            - Detect anti-patterns in microservice architecture
            - Suggest corrections to align with planned architecture
            
            ARCHITECTURAL PRINCIPLES TO CHECK:
            1. Service Independence:
               - Services should not directly call each other (use API Gateway)
               - Services should have separate database schemas
               - Services should not share code libraries (at MVP stage)
            
            2. API Gateway Pattern:
               - All external requests should go through Gateway
               - Gateway should not contain business logic
               - Gateway should only route requests
            
            3. Database Per Service:
               - Each service should have its own schema
               - No shared database access between services
               - Services should not access other services' data directly
            
            4. Microservice Boundaries:
               - Clear separation of concerns
               - No circular dependencies
               - Proper service boundaries
            
            5. Zero-Impact Constraints:
               - No modifications to existing microservices without approval
               - New features should not break existing functionality
               - Backward compatibility maintained
            
            OUTPUT FORMAT: JSON with structure:
            {
              "detection_timestamp": "ISO8601",
              "drift_summary": "High-level summary of detected drift",
              "violations": [
                {
                  "type": "service_coupling|missing_abstraction|boundary_violation|principle_violation",
                  "severity": "low|medium|high|critical",
                  "location": "service or component name",
                  "description": "Detailed description of violation",
                  "planned_architecture": "What was planned",
                  "actual_implementation": "What exists",
                  "impact": "Impact on system",
                  "correction": "Suggested correction"
                }
              ],
              "drift_score": 0-100,
              "recommendations": [
                "Prioritized list of architectural corrections"
              ],
              "alignment_actions": [
                {
                  "priority": 1,
                  "action": "Specific action to align with architecture",
                  "affected_components": ["list of components"],
                  "estimated_effort": "hours"
                }
              ]
            }
            
            Be thorough in identifying drift and provide actionable corrections.
            """)
        String detectDrift(@UserMessage("""
            Compare the planned architecture with actual implementation:
            
            PLANNED ARCHITECTURE:
            {{architectureDoc}}
            
            ACTUAL IMPLEMENTATION:
            {{implementationStructure}}
            
            Detect any drift or violations.
            """) String architectureDoc, String implementationStructure, @MemoryId String sessionId);
    }
}



