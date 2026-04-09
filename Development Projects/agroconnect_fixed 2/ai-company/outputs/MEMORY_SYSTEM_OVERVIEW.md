# Memory System Overview

## Architecture

The memory subsystem provides persistent, searchable memory for AI agents using vector embeddings and semantic search.

## Components

### 1. MemoryEntry
- **Purpose**: Represents a single memory entry
- **Fields**: id, timestamp, agentName, content, sessionId, category
- **Location**: `com.ai.company.memory.MemoryEntry`

### 2. VectorMemoryStore
- **Purpose**: Pluggable vector storage for memories
- **Implementation**: In-memory with cosine similarity search
- **Features**:
  - Vector similarity search
  - Indexed by agent, session, category
  - Thread-safe concurrent access
- **Location**: `com.ai.company.memory.VectorMemoryStore`
- **Future**: Can be replaced with Chroma, Pinecone, or other vector DBs

### 3. EmbeddingService
- **Purpose**: Generates embeddings from text
- **Current**: Hash-based implementation (for testing)
- **Future**: Replace with OpenAI embeddings, sentence-transformers, or Hugging Face
- **Location**: `com.ai.company.memory.EmbeddingService`

### 4. AgentMemoryService
- **Purpose**: High-level service for saving and recalling memories
- **Features**:
  - Save memories with automatic embedding
  - Recall relevant memories via semantic search
  - Filter by agent, session, category
- **Location**: `com.ai.company.memory.AgentMemoryService`

### 5. KnowledgeBaseLoader
- **Purpose**: Loads Markdown files from knowledge base into memory
- **Features**:
  - Scans `/ai-company/knowledge` directory
  - Loads .md files
  - Splits large files into chunks
  - Stores with metadata
- **Location**: `com.ai.company.memory.KnowledgeBaseLoader`

### 6. MemoryPromptBuilder
- **Purpose**: Combines agent prompts with relevant memories
- **Features**:
  - Retrieves relevant memories
  - Enhances system prompts
  - Includes session context
  - Adds knowledge base context
- **Location**: `com.ai.company.memory.MemoryPromptBuilder`

## Data Flow

```
Agent Interaction
    ↓
AgentMemoryService.saveMemory()
    ↓
EmbeddingService.embed()
    ↓
VectorMemoryStore.store()
    ↓
[Memory Stored]

Query/Recall
    ↓
AgentMemoryService.recallMemories()
    ↓
EmbeddingService.embed(query)
    ↓
VectorMemoryStore.searchSimilar()
    ↓
[Relevant Memories Returned]

Prompt Building
    ↓
MemoryPromptBuilder.buildPrompt()
    ↓
AgentMemoryService.recallMemories()
    ↓
[Enhanced Prompt with Memories]
```

## Usage Example

```java
// Initialize components
VectorMemoryStore store = new VectorMemoryStore(384);
EmbeddingService embeddingService = new EmbeddingService(384);
AgentMemoryService memoryService = new AgentMemoryService(store, embeddingService);
MemoryPromptBuilder promptBuilder = new MemoryPromptBuilder(memoryService);

// Save a memory
MemoryEntry memory = memoryService.saveMemory(
    "cto_agent",
    "Approved architecture for new feature X",
    "session-123",
    "approval"
);

// Recall relevant memories
List<MemoryEntry> relevant = memoryService.recallMemories(
    "architecture approval",
    "cto_agent",
    "session-123",
    null,
    5
);

// Build enhanced prompt
String enhancedPrompt = promptBuilder.buildPrompt(
    baseSystemPrompt,
    "cto_agent",
    "session-123",
    "architecture review"
);
```

## Integration Points

### With Agents
- Agents save important decisions and learnings
- Agents recall relevant past interactions
- Prompts enhanced with memory context

### With Knowledge Base
- Markdown files loaded at startup
- Knowledge searchable via semantic search
- Context added to prompts automatically

### With Workflows
- Workflows can save intermediate results
- Previous workflow results can be recalled
- Cross-session learning enabled

## Configuration

- **Embedding Dimension**: Default 384 (configurable)
- **Max Memories per Prompt**: Default 5 (configurable)
- **Knowledge Base Path**: `/ai-company/knowledge` (configurable)
- **Chunk Size**: 2000 characters (configurable)

## Future Enhancements

1. **Production Embeddings**: Replace hash-based with real embedding models
2. **Vector DB Integration**: Add Chroma, Pinecone, or Weaviate support
3. **Memory Persistence**: Save to database or file system
4. **Memory Expiration**: Automatic cleanup of old memories
5. **Memory Compression**: Summarize old memories to save space
6. **Multi-modal Support**: Support for images, code, etc.



