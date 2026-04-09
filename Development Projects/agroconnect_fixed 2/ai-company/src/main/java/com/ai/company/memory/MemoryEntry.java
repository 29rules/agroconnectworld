package com.ai.company.memory;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents a single memory entry stored in the memory system.
 * 
 * Each memory entry contains:
 * - Unique identifier
 * - Timestamp of creation
 * - Agent name that created the memory
 * - Content (text content of the memory)
 * - Optional metadata for filtering and retrieval
 */
public class MemoryEntry {
    
    private final UUID id;
    private final Instant timestamp;
    private final String agentName;
    private final String content;
    private final String sessionId;
    private final String category;
    
    /**
     * Creates a new memory entry.
     * 
     * @param agentName The name of the agent that created this memory
     * @param content The content of the memory
     * @param sessionId The session ID associated with this memory
     * @param category Optional category for organizing memories
     */
    public MemoryEntry(String agentName, String content, String sessionId, String category) {
        this.id = UUID.randomUUID();
        this.timestamp = Instant.now();
        this.agentName = agentName;
        this.content = content;
        this.sessionId = sessionId;
        this.category = category != null ? category : "general";
    }
    
    /**
     * Creates a new memory entry with default category.
     */
    public MemoryEntry(String agentName, String content, String sessionId) {
        this(agentName, content, sessionId, "general");
    }
    
    // Getters
    public UUID getId() {
        return id;
    }
    
    public Instant getTimestamp() {
        return timestamp;
    }
    
    public String getAgentName() {
        return agentName;
    }
    
    public String getContent() {
        return content;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public String getCategory() {
        return category;
    }
    
    @Override
    public String toString() {
        return String.format("MemoryEntry[id=%s, agent=%s, timestamp=%s, category=%s]",
            id, agentName, timestamp, category);
    }
}



