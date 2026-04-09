package com.ai.company.memory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Builds prompts by combining agent system prompts with relevant memories.
 * 
 * This builder:
 * - Takes an agent's base system prompt
 * - Retrieves relevant memories from the memory system
 * - Combines them into an enhanced prompt
 * - Formats the prompt for use with LangChain4j agents
 * 
 * The enhanced prompt includes:
 * - Original system prompt
 * - Relevant memories from past interactions
 * - Context from knowledge base
 * - Session-specific memories
 */
public class MemoryPromptBuilder {
    
    private static final Logger log = LoggerFactory.getLogger(MemoryPromptBuilder.class);
    
    private final AgentMemoryService memoryService;
    private final int maxMemories;
    
    /**
     * Creates a new memory prompt builder.
     * 
     * @param memoryService The memory service to use for retrieving memories
     * @param maxMemories Maximum number of memories to include in prompt
     */
    public MemoryPromptBuilder(AgentMemoryService memoryService, int maxMemories) {
        this.memoryService = memoryService;
        this.maxMemories = maxMemories;
        log.info("Initialized MemoryPromptBuilder with maxMemories: {}", maxMemories);
    }
    
    /**
     * Creates a new memory prompt builder with default max memories.
     */
    public MemoryPromptBuilder(AgentMemoryService memoryService) {
        this(memoryService, 5);
    }
    
    /**
     * Builds an enhanced prompt by combining base prompt with relevant memories.
     * 
     * @param basePrompt The agent's base system prompt
     * @param agentName The name of the agent
     * @param sessionId The current session ID
     * @param query Optional query to find relevant memories
     * @return Enhanced prompt with memories included
     */
    public String buildPrompt(String basePrompt, String agentName, String sessionId, String query) {
        log.debug("Building prompt for agent: {}, session: {}", agentName, sessionId);
        
        StringBuilder enhancedPrompt = new StringBuilder(basePrompt);
        
        // Add relevant memories
        List<MemoryEntry> relevantMemories = retrieveRelevantMemories(agentName, sessionId, query);
        if (!relevantMemories.isEmpty()) {
            enhancedPrompt.append("\n\n## Relevant Memories from Past Interactions\n\n");
            for (int i = 0; i < relevantMemories.size(); i++) {
                MemoryEntry memory = relevantMemories.get(i);
                enhancedPrompt.append(String.format("Memory %d (from %s, %s):\n%s\n\n",
                    i + 1,
                    memory.getAgentName(),
                    memory.getTimestamp(),
                    memory.getContent()));
            }
        }
        
        // Add session-specific memories
        List<MemoryEntry> sessionMemories = memoryService.getSessionMemories(sessionId);
        if (!sessionMemories.isEmpty() && sessionMemories.size() <= maxMemories) {
            enhancedPrompt.append("\n## Session Context\n\n");
            for (MemoryEntry memory : sessionMemories) {
                if (!relevantMemories.contains(memory)) {
                    enhancedPrompt.append(String.format("- %s: %s\n",
                        memory.getAgentName(),
                        memory.getContent()));
                }
            }
        }
        
        log.debug("Built enhanced prompt with {} memories", relevantMemories.size());
        return enhancedPrompt.toString();
    }
    
    /**
     * Builds an enhanced prompt without a specific query.
     */
    public String buildPrompt(String basePrompt, String agentName, String sessionId) {
        return buildPrompt(basePrompt, agentName, sessionId, null);
    }
    
    /**
     * Retrieves relevant memories based on agent, session, and query.
     * 
     * @param agentName The agent name
     * @param sessionId The session ID
     * @param query Optional query for semantic search
     * @return List of relevant memory entries
     */
    private List<MemoryEntry> retrieveRelevantMemories(String agentName, String sessionId, String query) {
        if (query != null && !query.trim().isEmpty()) {
            // Use semantic search with filters
            return memoryService.recallMemories(query, agentName, sessionId, null, maxMemories);
        } else {
            // Get recent memories for the agent and session
            List<MemoryEntry> agentMemories = memoryService.getAgentMemories(agentName);
            List<MemoryEntry> sessionMemories = memoryService.getSessionMemories(sessionId);
            
            // Combine and deduplicate
            return Stream.concat(agentMemories.stream(), sessionMemories.stream())
                .distinct()
                .limit(maxMemories)
                .collect(Collectors.toList());
        }
    }
    
    /**
     * Builds a prompt with knowledge base context.
     * 
     * @param basePrompt The base prompt
     * @param knowledgeQuery Query to find relevant knowledge base entries
     * @return Enhanced prompt with knowledge base context
     */
    public String buildPromptWithKnowledge(String basePrompt, String knowledgeQuery) {
        log.debug("Building prompt with knowledge base context");
        
        StringBuilder enhancedPrompt = new StringBuilder(basePrompt);
        
        // Retrieve relevant knowledge base entries
        List<MemoryEntry> knowledgeEntries = memoryService.recallMemories(
            knowledgeQuery, "knowledge_base", null, null, 3);
        
        if (!knowledgeEntries.isEmpty()) {
            enhancedPrompt.append("\n\n## Knowledge Base Context\n\n");
            for (int i = 0; i < knowledgeEntries.size(); i++) {
                MemoryEntry entry = knowledgeEntries.get(i);
                enhancedPrompt.append(String.format("Knowledge %d:\n%s\n\n",
                    i + 1,
                    entry.getContent()));
            }
        }
        
        return enhancedPrompt.toString();
    }
}

