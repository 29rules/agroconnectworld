# Memory System Integration Notes

## Integration with Agents

### CTO Agent

```java
// In CTOAgent constructor
private final AgentMemoryService memoryService;
private final MemoryPromptBuilder promptBuilder;

public CTOAgent(ChatLanguageModel chatModel, AgentMemoryService memoryService) {
    this.memoryService = memoryService;
    this.promptBuilder = new MemoryPromptBuilder(memoryService);
    
    // Build enhanced prompt
    String basePrompt = loadSystemPrompt();
    String enhancedPrompt = promptBuilder.buildPrompt(
        basePrompt, "cto_agent", sessionId);
    
    // Use enhanced prompt in agent service
    this.agentService = AiServices.builder(CTOAgentService.class)
        .chatLanguageModel(chatModel)
        .systemMessage(enhancedPrompt)
        .build();
}

// Save important decisions
public String approveDecision(String decision, String sessionId) {
    String result = agentService.approveDecision(decision, sessionId);
    
    // Save to memory
    memoryService.saveMemory(
        "cto_agent",
        "Approved decision: " + decision,
        sessionId,
        "approval"
    );
    
    return result;
}
```

### AI Architect Agent

```java
// Save architecture designs
public String designArchitecture(String requirements, String sessionId) {
    String architecture = agentService.designArchitecture(requirements, sessionId);
    
    // Save architecture to memory
    memoryService.saveMemory(
        "architect_agent",
        "Designed architecture: " + architecture,
        sessionId,
        "architecture"
    );
    
    return architecture;
}

// Recall past architectures
public String designArchitecture(String requirements, String sessionId) {
    // Recall similar past architectures
    List<MemoryEntry> pastArchitectures = memoryService.recallMemories(
        requirements, "architect_agent", sessionId, "architecture", 3);
    
    // Use past architectures as context
    String context = pastArchitectures.stream()
        .map(MemoryEntry::getContent)
        .collect(Collectors.joining("\n"));
    
    return agentService.designArchitecture(requirements + "\n\nPast designs:\n" + context, sessionId);
}
```

### AI Engineer Agent

```java
// Save implementation patterns
public String createImplementationSpec(String architecture, String sessionId) {
    String spec = agentService.createImplementationSpec(architecture, sessionId);
    
    // Save implementation pattern
    memoryService.saveMemory(
        "engineer_agent",
        "Implementation spec: " + spec,
        sessionId,
        "implementation"
    );
    
    return spec;
}

// Recall similar implementations
public String createImplementationSpec(String architecture, String sessionId) {
    List<MemoryEntry> similar = memoryService.recallMemories(
        architecture, "engineer_agent", null, "implementation", 5);
    
    // Use similar implementations as reference
    // ... build context and call agent
}
```

### DevOps Agent

```java
// Save infrastructure insights
public String analyzeInfrastructure(String sessionId) {
    String analysis = agentService.analyzeInfrastructure(sessionId);
    
    // Save infrastructure analysis
    memoryService.saveMemory(
        "devops_agent",
        "Infrastructure analysis: " + analysis,
        sessionId,
        "infrastructure"
    );
    
    return analysis;
}
```

### Full-Stack Developer Agent

```java
// Save component patterns
public String suggestComponent(String requirement, String sessionId) {
    String suggestion = agentService.suggestComponent(requirement, sessionId);
    
    // Save component pattern
    memoryService.saveMemory(
        "fullstack_agent",
        "Component suggestion: " + suggestion,
        sessionId,
        "component"
    );
    
    return suggestion;
}
```

### Product Manager Agent

```java
// Save user stories and epics
public String createEpic(String vision, String sessionId) {
    String epic = agentService.createEpic(vision, sessionId);
    
    // Save epic
    memoryService.saveMemory(
        "product_manager_agent",
        "Created epic: " + epic,
        sessionId,
        "epic"
    );
    
    return epic;
}
```

### QA Agent

```java
// Save test patterns
public String createTestPlan(String feature, String sessionId) {
    String testPlan = agentService.createTestPlan(feature, sessionId);
    
    // Save test plan
    memoryService.saveMemory(
        "qa_agent",
        "Test plan: " + testPlan,
        sessionId,
        "test_plan"
    );
    
    return testPlan;
}
```

## Integration with Workflows

### Planning Workflow

```java
public PlanningResult execute(String vision, String businessRequirements, String sessionId) {
    // Recall similar past planning sessions
    List<MemoryEntry> pastPlans = memoryService.recallMemories(
        vision, "product_manager_agent", null, "epic", 3);
    
    // Use past plans as context
    // ... execute workflow with context
    
    // Save workflow results
    memoryService.saveMemory(
        "planning_workflow",
        "Planning result: " + result,
        sessionId,
        "workflow"
    );
    
    return result;
}
```

### Architecture Workflow

```java
public ArchitectureResult execute(String productSpecs, String sessionId) {
    // Recall past architectures
    List<MemoryEntry> pastArchitectures = memoryService.recallMemories(
        productSpecs, "architect_agent", null, "architecture", 5);
    
    // Use as context
    // ... execute workflow
    
    // Save architecture
    memoryService.saveMemory(
        "architecture_workflow",
        "Architecture: " + architecture,
        sessionId,
        "workflow"
    );
    
    return result;
}
```

## Knowledge Base Integration

### Loading Knowledge Base

```java
// At application startup
AgentMemoryService memoryService = // ... initialize
KnowledgeBaseLoader loader = new KnowledgeBaseLoader(memoryService);
List<MemoryEntry> loaded = loader.loadAll();
log.info("Loaded {} knowledge entries", loaded.size());
```

### Using Knowledge in Prompts

```java
// In agent initialization
MemoryPromptBuilder promptBuilder = new MemoryPromptBuilder(memoryService);

// Build prompt with knowledge context
String prompt = promptBuilder.buildPromptWithKnowledge(
    basePrompt,
    "architecture patterns"
);
```

## Best Practices

1. **Save Important Decisions**: Always save approvals, architecture decisions, and key learnings
2. **Use Categories**: Organize memories with meaningful categories
3. **Session Context**: Use session IDs to maintain conversation context
4. **Recall Before Acting**: Check past memories before making decisions
5. **Knowledge Base**: Load knowledge base at startup for all agents
6. **Prompt Enhancement**: Always use MemoryPromptBuilder for enhanced prompts

## Configuration

Add to `application.properties`:

```properties
# Memory Configuration
memory.embedding.dimension=384
memory.max.memories.per.prompt=5
memory.knowledge.base.path=knowledge
memory.chunk.size=2000
```

## Performance Considerations

- **Embedding Generation**: Cache embeddings for frequently accessed content
- **Vector Search**: Limit search results to reasonable numbers (5-10)
- **Memory Cleanup**: Implement expiration for old memories
- **Batch Operations**: Load knowledge base in batches

## Future Enhancements

1. **Persistent Storage**: Save memories to database
2. **Memory Compression**: Summarize old memories
3. **Multi-modal**: Support code, images, etc.
4. **Memory Clustering**: Group related memories
5. **Temporal Context**: Time-aware memory retrieval



