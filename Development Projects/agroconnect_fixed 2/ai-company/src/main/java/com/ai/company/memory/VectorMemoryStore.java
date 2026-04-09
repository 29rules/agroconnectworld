package com.ai.company.memory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Pluggable in-memory vector memory store.
 * 
 * This implementation uses simple cosine similarity for vector search.
 * Can be replaced with Chroma, Pinecone, or other vector databases later.
 * 
 * The store maintains:
 * - Memory entries with embeddings
 * - Index for fast retrieval by agent, session, or category
 * - Vector similarity search capability
 */
public class VectorMemoryStore {
    
    private static final Logger log = LoggerFactory.getLogger(VectorMemoryStore.class);
    
    // Main storage: entry ID -> MemoryEntry
    private final Map<UUID, MemoryEntry> entries = new ConcurrentHashMap<>();
    
    // Embeddings: entry ID -> embedding vector
    private final Map<UUID, float[]> embeddings = new ConcurrentHashMap<>();
    
    // Indexes for fast lookup
    private final Map<String, Set<UUID>> agentIndex = new ConcurrentHashMap<>();
    private final Map<String, Set<UUID>> sessionIndex = new ConcurrentHashMap<>();
    private final Map<String, Set<UUID>> categoryIndex = new ConcurrentHashMap<>();
    
    // Embedding dimension (can be configured)
    private final int embeddingDimension;
    
    /**
     * Creates a new vector memory store with default embedding dimension.
     */
    public VectorMemoryStore() {
        this(384); // Default dimension (sentence-transformers all-MiniLM-L6-v2)
    }
    
    /**
     * Creates a new vector memory store with specified embedding dimension.
     * 
     * @param embeddingDimension The dimension of embedding vectors
     */
    public VectorMemoryStore(int embeddingDimension) {
        this.embeddingDimension = embeddingDimension;
        log.info("Initialized VectorMemoryStore with dimension: {}", embeddingDimension);
    }
    
    /**
     * Stores a memory entry with its embedding.
     * 
     * @param entry The memory entry to store
     * @param embedding The embedding vector for the entry
     */
    public void store(MemoryEntry entry, float[] embedding) {
        if (embedding.length != embeddingDimension) {
            throw new IllegalArgumentException(
                String.format("Embedding dimension mismatch: expected %d, got %d",
                    embeddingDimension, embedding.length));
        }
        
        entries.put(entry.getId(), entry);
        embeddings.put(entry.getId(), embedding);
        
        // Update indexes
        agentIndex.computeIfAbsent(entry.getAgentName(), k -> ConcurrentHashMap.newKeySet()).add(entry.getId());
        sessionIndex.computeIfAbsent(entry.getSessionId(), k -> ConcurrentHashMap.newKeySet()).add(entry.getId());
        categoryIndex.computeIfAbsent(entry.getCategory(), k -> ConcurrentHashMap.newKeySet()).add(entry.getId());
        
        log.debug("Stored memory entry: {}", entry.getId());
    }
    
    /**
     * Retrieves a memory entry by ID.
     * 
     * @param id The UUID of the memory entry
     * @return The memory entry, or null if not found
     */
    public MemoryEntry get(UUID id) {
        return entries.get(id);
    }
    
    /**
     * Searches for similar memories using vector similarity.
     * 
     * @param queryEmbedding The embedding vector of the query
     * @param limit Maximum number of results to return
     * @return List of memory entries sorted by similarity (highest first)
     */
    public List<MemoryEntry> searchSimilar(float[] queryEmbedding, int limit) {
        if (queryEmbedding.length != embeddingDimension) {
            throw new IllegalArgumentException(
                String.format("Query embedding dimension mismatch: expected %d, got %d",
                    embeddingDimension, queryEmbedding.length));
        }
        
        List<ScoredEntry> scoredEntries = new ArrayList<>();
        
        for (Map.Entry<UUID, float[]> entry : embeddings.entrySet()) {
            float similarity = cosineSimilarity(queryEmbedding, entry.getValue());
            MemoryEntry memoryEntry = entries.get(entry.getKey());
            scoredEntries.add(new ScoredEntry(memoryEntry, similarity));
        }
        
        return scoredEntries.stream()
            .sorted((a, b) -> Float.compare(b.score, a.score))
            .limit(limit)
            .map(se -> se.entry)
            .collect(Collectors.toList());
    }
    
    /**
     * Searches for similar memories with filters.
     * 
     * @param queryEmbedding The embedding vector of the query
     * @param agentName Filter by agent name (null to ignore)
     * @param sessionId Filter by session ID (null to ignore)
     * @param category Filter by category (null to ignore)
     * @param limit Maximum number of results
     * @return List of filtered memory entries sorted by similarity
     */
    public List<MemoryEntry> searchSimilar(float[] queryEmbedding, String agentName,
                                          String sessionId, String category, int limit) {
        Set<UUID> candidateIds = new HashSet<>(embeddings.keySet());
        
        // Apply filters
        if (agentName != null && agentIndex.containsKey(agentName)) {
            candidateIds.retainAll(agentIndex.get(agentName));
        }
        if (sessionId != null && sessionIndex.containsKey(sessionId)) {
            candidateIds.retainAll(sessionIndex.get(sessionId));
        }
        if (category != null && categoryIndex.containsKey(category)) {
            candidateIds.retainAll(categoryIndex.get(category));
        }
        
        List<ScoredEntry> scoredEntries = new ArrayList<>();
        
        for (UUID id : candidateIds) {
            float[] embedding = embeddings.get(id);
            if (embedding != null) {
                float similarity = cosineSimilarity(queryEmbedding, embedding);
                MemoryEntry memoryEntry = entries.get(id);
                scoredEntries.add(new ScoredEntry(memoryEntry, similarity));
            }
        }
        
        return scoredEntries.stream()
            .sorted((a, b) -> Float.compare(b.score, a.score))
            .limit(limit)
            .map(se -> se.entry)
            .collect(Collectors.toList());
    }
    
    /**
     * Retrieves all memories for a specific agent.
     * 
     * @param agentName The agent name
     * @return List of memory entries for the agent
     */
    public List<MemoryEntry> getByAgent(String agentName) {
        Set<UUID> ids = agentIndex.getOrDefault(agentName, Collections.emptySet());
        return ids.stream()
            .map(entries::get)
            .filter(Objects::nonNull)
            .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
            .collect(Collectors.toList());
    }
    
    /**
     * Retrieves all memories for a specific session.
     * 
     * @param sessionId The session ID
     * @return List of memory entries for the session
     */
    public List<MemoryEntry> getBySession(String sessionId) {
        Set<UUID> ids = sessionIndex.getOrDefault(sessionId, Collections.emptySet());
        return ids.stream()
            .map(entries::get)
            .filter(Objects::nonNull)
            .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
            .collect(Collectors.toList());
    }
    
    /**
     * Deletes a memory entry.
     * 
     * @param id The UUID of the entry to delete
     */
    public void delete(UUID id) {
        MemoryEntry entry = entries.remove(id);
        if (entry != null) {
            embeddings.remove(id);
            agentIndex.getOrDefault(entry.getAgentName(), Collections.emptySet()).remove(id);
            sessionIndex.getOrDefault(entry.getSessionId(), Collections.emptySet()).remove(id);
            categoryIndex.getOrDefault(entry.getCategory(), Collections.emptySet()).remove(id);
            log.debug("Deleted memory entry: {}", id);
        }
    }
    
    /**
     * Clears all memories.
     */
    public void clear() {
        entries.clear();
        embeddings.clear();
        agentIndex.clear();
        sessionIndex.clear();
        categoryIndex.clear();
        log.info("Cleared all memories");
    }
    
    /**
     * Gets the total number of stored memories.
     * 
     * @return The count of stored memories
     */
    public int size() {
        return entries.size();
    }
    
    /**
     * Calculates cosine similarity between two vectors.
     * 
     * @param a First vector
     * @param b Second vector
     * @return Cosine similarity score (0 to 1)
     */
    private float cosineSimilarity(float[] a, float[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Vectors must have same length");
        }
        
        float dotProduct = 0.0f;
        float normA = 0.0f;
        float normB = 0.0f;
        
        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        
        float denominator = (float) (Math.sqrt(normA) * Math.sqrt(normB));
        if (denominator == 0.0f) {
            return 0.0f;
        }
        
        return dotProduct / denominator;
    }
    
    /**
     * Helper class for storing scored entries during search.
     */
    private static class ScoredEntry {
        final MemoryEntry entry;
        final float score;
        
        ScoredEntry(MemoryEntry entry, float score) {
            this.entry = entry;
            this.score = score;
        }
    }
}



