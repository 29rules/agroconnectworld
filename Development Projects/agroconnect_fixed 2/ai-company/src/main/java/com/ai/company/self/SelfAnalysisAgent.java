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
 * Self Analysis Agent
 * 
 * Analyzes the entire codebase to detect:
 * - Code smells
 * - Inefficiencies
 * - Code duplication
 * - Refactoring opportunities
 * - Best practice violations
 * 
 * This agent uses LangChain4j @AiService to perform intelligent code analysis
 * and provides actionable suggestions for improvement.
 * 
 * ZERO-IMPACT MODE: This agent only analyzes and suggests, never modifies code.
 */
public class SelfAnalysisAgent {
    
    private static final Logger log = LoggerFactory.getLogger(SelfAnalysisAgent.class);
    
    private final SelfAnalysisService analysisService;
    private final CodeReaderTool codeReader;
    
    public SelfAnalysisAgent(ChatLanguageModel chatModel) {
        this.codeReader = new CodeReaderTool();
        this.analysisService = AiServices.builder(SelfAnalysisService.class)
            .chatLanguageModel(chatModel)
            .chatMemoryProvider(sessionId -> MessageWindowChatMemory.withMaxMessages(50))
            .build();
    }
    
    /**
     * Analyzes the entire codebase and returns analysis report.
     * 
     * @param sessionId Session ID for tracking
     * @return Analysis report with findings and suggestions
     */
    public String analyzeCodebase(String sessionId) {
        log.info("SelfAnalysisAgent starting codebase analysis");
        
        try {
            // Sample key files for analysis
            List<String> sampleFiles = getSampleFiles();
            StringBuilder codebaseContext = new StringBuilder();
            
            codebaseContext.append("=== CODEBASE ANALYSIS CONTEXT ===\n\n");
            for (String file : sampleFiles) {
                try {
                    String content = codeReader.readFile(file);
                    if (!content.startsWith("ERROR") && !content.startsWith("WARNING")) {
                        codebaseContext.append("--- File: ").append(file).append(" ---\n");
                        // Limit content to prevent token overflow
                        String preview = content.length() > 2000 ? content.substring(0, 2000) + "..." : content;
                        codebaseContext.append(preview).append("\n\n");
                    }
                } catch (Exception e) {
                    log.warn("Could not read file for analysis: {}", file, e);
                }
            }
            
            String analysis = analysisService.analyzeCodebase(codebaseContext.toString(), sessionId);
            log.info("SelfAnalysisAgent completed analysis");
            return analysis;
            
        } catch (Exception e) {
            log.error("Error during codebase analysis", e);
            return "ERROR: Analysis failed: " + e.getMessage();
        }
    }
    
    /**
     * Analyzes a specific file or directory.
     * 
     * @param filePath Path to file or directory
     * @param sessionId Session ID
     * @return Analysis report
     */
    public String analyzeFile(String filePath, String sessionId) {
        log.info("SelfAnalysisAgent analyzing file: {}", filePath);
        
        try {
            String content = codeReader.readFile(filePath);
            if (content.startsWith("ERROR") || content.startsWith("WARNING")) {
                return content;
            }
            
            String analysis = analysisService.analyzeFile(filePath, content, sessionId);
            return analysis;
            
        } catch (Exception e) {
            log.error("Error analyzing file: {}", filePath, e);
            return "ERROR: " + e.getMessage();
        }
    }
    
    /**
     * Gets a sample of key files for analysis.
     */
    private List<String> getSampleFiles() {
        List<String> files = new ArrayList<>();
        
        // Backend samples
        files.add("backend/gateway/pom.xml");
        files.add("backend/auth-service/pom.xml");
        files.add("backend/product-service/pom.xml");
        
        // Frontend samples
        files.add("frontend/package.json");
        
        // AI Company samples
        files.add("ai-company/pom.xml");
        
        return files;
    }
    
    /**
     * LangChain4j AI Service interface for code analysis.
     */
    interface SelfAnalysisService {
        
        @SystemMessage("""
            You are a Senior Code Analyst specializing in code quality, architecture, and best practices.
            
            Your role:
            - Analyze codebases for code smells, inefficiencies, and duplication
            - Identify refactoring opportunities
            - Detect anti-patterns and technical debt
            - Suggest improvements following industry best practices
            - Focus on maintainability, performance, and scalability
            
            ANALYSIS AREAS:
            1. Code Smells:
               - Long methods/classes
               - Duplicate code
               - Magic numbers/strings
               - Dead code
               - Tight coupling
               - God objects
            
            2. Inefficiencies:
               - Performance bottlenecks
               - Memory leaks
               - Inefficient algorithms
               - Unnecessary complexity
               - Missing caching opportunities
            
            3. Refactoring Opportunities:
               - Extract methods/classes
               - Simplify conditionals
               - Replace inheritance with composition
               - Introduce design patterns
               - Improve naming and structure
            
            4. Best Practice Violations:
               - SOLID principles violations
               - Missing error handling
               - Security vulnerabilities
               - Missing documentation
               - Inconsistent coding style
            
            OUTPUT FORMAT: JSON with structure:
            {
              "analysis_timestamp": "ISO8601",
              "codebase_summary": "High-level summary",
              "findings": [
                {
                  "type": "code_smell|inefficiency|duplication|refactoring_opportunity",
                  "severity": "low|medium|high|critical",
                  "location": "file:line or file pattern",
                  "description": "Detailed description",
                  "impact": "Impact on system",
                  "suggestion": "Recommended fix"
                }
              ],
              "metrics": {
                "total_files_analyzed": 0,
                "code_smells_count": 0,
                "duplication_percentage": 0.0,
                "complexity_score": 0.0
              },
              "recommendations": [
                "Prioritized list of recommended actions"
              ],
              "refactoring_priorities": [
                {
                  "priority": 1,
                  "description": "High-priority refactoring",
                  "estimated_effort": "hours",
                  "expected_benefit": "Description of benefit"
                }
              ]
            }
            
            Be thorough but practical. Focus on actionable insights.
            """)
        String analyzeCodebase(@UserMessage("Analyze the following codebase context:\n{{codebaseContext}}") 
                              String codebaseContext, @MemoryId String sessionId);
        
        String analyzeFile(@UserMessage("Analyze file {{filePath}} with content:\n{{content}}") 
                           String filePath, String content, @MemoryId String sessionId);
    }
}



