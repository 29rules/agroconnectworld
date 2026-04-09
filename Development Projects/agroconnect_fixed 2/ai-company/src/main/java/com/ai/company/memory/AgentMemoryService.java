package com.ai.company.memory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

/**
 * Service for saving and recalling agent memories.
 * 
 * This service provides a high-level interface for:
 * - Storing agent memories with automatic embedding generation
 * - Retrieving relevant memories based on queries
 * - Managing memory lifecycle (save, recall, delete)
 * 
 * The service uses a VectorMemoryStore for storage and an EmbeddingService
 * for generating embeddings from text.
 */
public class AgentMemoryService {
    
    private static final Logger log = LoggerFactory.getLogger(AgentMemoryService.class);
    
    private final VectorMemoryStore memoryStore;
    private final EmbeddingService embeddingService;
    
    /**
     * Creates a new agent memory service.
     * 
     * @param memoryStore The vector memory store to use
     * @param embeddingService The embedding service for generating embeddings
     */
    public AgentMemoryService(VectorMemoryStore memoryStore, EmbeddingService embeddingService) {
        this.memoryStore = memoryStore;
        this.embeddingService = embeddingService;
        log.info("Initialized AgentMemoryService");
    }
    
    /**
     * Saves a memory entry for an agent.
     * 
     * The content is automatically embedded and stored in the vector store.
     * 
     * @param agentName The name of the agent
     * @param content The content to remember
     * @param sessionId The session ID
     * @param category Optional category for the memory
     * @return The created memory entry
     */
    public MemoryEntry saveMemory(String agentName, String content, String sessionId, String category) {
        log.debug("Saving memory for agent: {}, session: {}", agentName, sessionId);
        
        MemoryEntry entry = new MemoryEntry(agentName, content, sessionId, category);
        
        try {
            float[] embedding = embeddingService.embed(content);
            memoryStore.store(entry, embedding);
            log.debug("Saved memory entry: {}", entry.getId());
        } catch (Exception e) {
            log.error("Failed to save memory for agent: {}", agentName, e);
            throw new RuntimeException("Failed to save memory", e);
        }
        
        return entry;
    }
    
    /**
     * Saves a memory entry with default category.
     */
    public MemoryEntry saveMemory(String agentName, String content, String sessionId) {
        return saveMemory(agentName, content, sessionId, "general");
    }
    
    /**
     * Recalls relevant memories based on a query.
     * 
     * The query is embedded and used to search for similar memories.
     * 
     * @param query The query text
     * @param limit Maximum number of memories to return
     * @return List of relevant memory entries sorted by similarity
     */
    public List<MemoryEntry> recallMemories(String query, int limit) {
        log.debug("Recalling memories for query: {}", query);
        
        try {
            float[] queryEmbedding = embeddingService.embed(query);
            List<MemoryEntry> memories = memoryStore.searchSimilar(queryEmbedding, limit);
            log.debug("Recalled {} memories", memories.size());
            return memories;
        } catch (Exception e) {
            log.error("Failed to recall memories for query: {}", query, e);
            throw new RuntimeException("Failed to recall memories", e);
        }
    }
    
    /**
     * Recalls relevant memories with filters.
     * 
     * @param query The query text
     * @param agentName Filter by agent name (null to ignore)
     * @param sessionId Filter by session ID (null to ignore)
     * @param category Filter by category (null to ignore)
     * @param limit Maximum number of memories to return
     * @return List of filtered relevant memory entries
     */
    public List<MemoryEntry> recallMemories(String query, String agentName, String sessionId,
                                           String category, int limit) {
        log.debug("Recalling memories for query: {}, filters: agent={}, session={}, category={}",
            query, agentName, sessionId, category);
        
        try {
            float[] queryEmbedding = embeddingService.embed(query);
            List<MemoryEntry> memories = memoryStore.searchSimilar(
                queryEmbedding, agentName, sessionId, category, limit);
            log.debug("Recalled {} memories with filters", memories.size());
            return memories;
        } catch (Exception e) {
            log.error("Failed to recall memories with filters", e);
            throw new RuntimeException("Failed to recall memories", e);
        }
    }
    
    /**
     * Retrieves all memories for a specific agent.
     * 
     * @param agentName The agent name
     * @return List of all memories for the agent, sorted by timestamp (newest first)
     */
    public List<MemoryEntry> getAgentMemories(String agentName) {
        log.debug("Retrieving all memories for agent: {}", agentName);
        return memoryStore.getByAgent(agentName);
    }
    
    /**
     * Retrieves all memories for a specific session.
     * 
     * @param sessionId The session ID
     * @return List of all memories for the session, sorted by timestamp (newest first)
     */
    public List<MemoryEntry> getSessionMemories(String sessionId) {
        log.debug("Retrieving all memories for session: {}", sessionId);
        return memoryStore.getBySession(sessionId);
    }
    
    /**
     * Deletes a memory entry.
     * 
     * @param id The UUID of the memory entry to delete
     */
    public void deleteMemory(UUID id) {
        log.debug("Deleting memory entry: {}", id);
        memoryStore.delete(id);
    }
    
    /**
     * Gets a memory entry by ID.
     * 
     * @param id The UUID of the memory entry
     * @return The memory entry, or null if not found
     */
    public MemoryEntry getMemory(UUID id) {
        return memoryStore.get(id);
    }
    
    /**
     * Gets the total number of stored memories.
     * 
     * @return The count of stored memories
     */
    public int getMemoryCount() {
        return memoryStore.size();
    }
}



