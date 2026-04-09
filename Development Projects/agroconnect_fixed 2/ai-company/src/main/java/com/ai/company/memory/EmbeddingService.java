package com.ai.company.memory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;

/**
 * Service for generating embeddings from text.
 * 
 * This is a simple implementation using hash-based embeddings for demonstration.
 * In production, this should be replaced with:
 * - OpenAI embeddings API
 * - Sentence transformers (all-MiniLM-L6-v2)
 * - Hugging Face embeddings
 * - Other embedding models
 * 
 * The current implementation creates deterministic embeddings based on text hash,
 * which is suitable for testing but not for semantic similarity.
 */
public class EmbeddingService {
    
    private static final Logger log = LoggerFactory.getLogger(EmbeddingService.class);
    
    private final int dimension;
    
    /**
     * Creates a new embedding service with default dimension.
     */
    public EmbeddingService() {
        this(384); // Default dimension
    }
    
    /**
     * Creates a new embedding service with specified dimension.
     * 
     * @param dimension The dimension of embedding vectors
     */
    public EmbeddingService(int dimension) {
        this.dimension = dimension;
        log.info("Initialized EmbeddingService with dimension: {}", dimension);
    }
    
    /**
     * Generates an embedding vector for the given text.
     * 
     * NOTE: This is a simple hash-based implementation for demonstration.
     * Replace with actual embedding model in production.
     * 
     * @param text The text to embed
     * @return The embedding vector
     */
    public float[] embed(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new float[dimension];
        }
        
        try {
            // Use hash-based approach for simple deterministic embeddings
            // In production, use actual embedding models
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(text.getBytes(StandardCharsets.UTF_8));
            
            float[] embedding = new float[dimension];
            
            // Distribute hash bytes across embedding dimensions
            for (int i = 0; i < dimension; i++) {
                int hashIndex = i % hash.length;
                embedding[i] = (hash[hashIndex] & 0xFF) / 255.0f * 2.0f - 1.0f; // Normalize to [-1, 1]
            }
            
            // Normalize the vector
            normalize(embedding);
            
            return embedding;
        } catch (Exception e) {
            log.error("Failed to generate embedding", e);
            // Return zero vector on error
            return new float[dimension];
        }
    }
    
    /**
     * Normalizes a vector to unit length.
     * 
     * @param vector The vector to normalize
     */
    private void normalize(float[] vector) {
        float sumSquares = 0.0f;
        for (float v : vector) {
            sumSquares += v * v;
        }
        
        float magnitude = (float) Math.sqrt(sumSquares);
        if (magnitude > 0.0f) {
            for (int i = 0; i < vector.length; i++) {
                vector[i] /= magnitude;
            }
        }
    }
    
    /**
     * Gets the embedding dimension.
     * 
     * @return The dimension
     */
    public int getDimension() {
        return dimension;
    }
}



