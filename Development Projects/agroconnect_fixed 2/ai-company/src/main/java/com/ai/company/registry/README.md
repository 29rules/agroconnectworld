# Agent Registry

## Overview

The AgentRegistry provides centralized management and access to all AI agents in the AgroConnectWorld AI Company system.

## Features

- **Singleton Pattern**: Single instance manages all agents
- **Type-Safe Accessors**: Getter methods for each agent type
- **Metadata Management**: Comprehensive metadata for each agent
- **Zero-Impact Compliance**: All agents operate in zero-impact mode
- **LangChain4j Integration**: All agents use @AiService pattern

## Usage

### Initialization

```java
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import com.ai.company.registry.AgentRegistry;

// Initialize with chat model
ChatLanguageModel chatModel = OpenAiChatModel.builder()
    .apiKey("your-api-key")
    .modelName("gpt-4")
    .build();

AgentRegistry registry = AgentRegistry.getInstance(chatModel);
```

### Accessing Agents

```java
// Get specific agents
CTOAgent cto = registry.getCTOAgent();
ArchitectAgent architect = registry.getArchitectAgent();
EngineerAgent engineer = registry.getEngineerAgent();
DevOpsAgent devops = registry.getDevOpsAgent();
FullStackAgent fullstack = registry.getFullStackAgent();
ProductManagerAgent pm = registry.getProductManagerAgent();
QAAgent qa = registry.getQAAgent();

// Supervisor agent is alias for CTO agent
CTOAgent supervisor = registry.getSupervisorAgent();
```

### Accessing Metadata

```java
// Get metadata for an agent
AgentMetadata metadata = registry.getMetadata("cto_agent");
String role = metadata.getRole();
Set<String> capabilities = metadata.getCapabilities();

// Check capabilities
boolean canDesign = metadata.hasCapability("architecture");

// Get all metadata
Map<String, AgentMetadata> allMetadata = registry.getAllMetadata();
```

### Generating Config File

```java
import com.ai.company.registry.RegistryConfigGenerator;

RegistryConfigGenerator generator = new RegistryConfigGenerator();
generator.generate(registry, "registry_config.json");
```

## Registered Agents

1. **CTO Agent** (`cto_agent`, `supervisor_agent`)
   - Role: Chief Technology Officer
   - Supervises all agents, approves decisions

2. **Architect Agent** (`architect_agent`)
   - Role: AI Architect
   - Designs system architecture and API contracts

3. **Engineer Agent** (`engineer_agent`)
   - Role: AI Engineer
   - Creates implementation specifications

4. **DevOps Agent** (`devops_agent`)
   - Role: DevOps Specialist
   - Analyzes infrastructure and deployment

5. **Full-Stack Agent** (`fullstack_agent`)
   - Role: Full-Stack Developer
   - Suggests UI and API implementations

6. **Product Manager Agent** (`product_manager_agent`)
   - Role: Product Manager
   - Creates epics and user stories

7. **QA Agent** (`qa_agent`)
   - Role: QA Specialist
   - Creates test plans and collections

## Zero-Impact Mode

All agents in the registry operate in **zero-impact mode**:
- ✅ Read-only access to codebase
- ✅ Specification generation only
- ✅ No code modifications
- ✅ No configuration changes
- ✅ Approval required for all decisions

## Agent Capabilities

Each agent has specific capabilities:
- **Architecture**: System design, API contracts
- **Implementation**: Backend, frontend, integration
- **Testing**: Test plans, Postman collections
- **Infrastructure**: Docker, CI/CD, deployment
- **Planning**: Epics, user stories, roadmaps
- **Review**: Approval, risk assessment

## Thread Safety

The AgentRegistry is thread-safe:
- Uses ConcurrentHashMap for agent storage
- Singleton pattern with double-checked locking
- Safe for concurrent access

## Configuration

The registry automatically:
- Initializes all agents on first access
- Creates metadata for each agent
- Provides type-safe accessors
- Ensures zero-impact compliance

## Future Enhancements

1. **Dynamic Agent Loading**: Load agents from configuration
2. **Agent Health Monitoring**: Track agent status
3. **Capability Discovery**: Auto-detect agent capabilities
4. **Agent Versioning**: Support multiple agent versions
5. **Plugin System**: Allow custom agent registration

