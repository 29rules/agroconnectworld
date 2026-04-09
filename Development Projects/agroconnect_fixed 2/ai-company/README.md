# AgroConnectWorld AI Company

## Overview

This is a complete multi-agent AI company skeleton built entirely in **Java + LangChain4j** on top of AgroConnectWorld's existing microservices architecture. The system operates in **zero-impact mode**, ensuring no modifications to existing production code.

## Technology Stack

- **Language**: Java 21
- **AI Framework**: LangChain4j 0.29.1
- **LLM Providers**: OpenAI, Anthropic (Claude), Ollama (local)
- **Build Tool**: Maven
- **Logging**: SLF4J + Logback

## Project Structure

```
ai-company/
├── src/main/java/com/ai/company/
│   ├── agents/          # AI agent implementations
│   │   ├── cto/
│   │   ├── architect/
│   │   ├── engineer/
│   │   ├── devops/
│   │   ├── productmanager/
│   │   ├── fullstack/
│   │   └── qa/
│   ├── tools/           # Read-only tool interfaces
│   │   ├── github/
│   │   ├── filesystem/
│   │   ├── docker/
│   │   └── logs/
│   ├── pipelines/        # Workflow orchestration
│   │   ├── planning/
│   │   ├── architecture/
│   │   ├── development/
│   │   ├── review/
│   │   └── testing/
│   └── config/          # Configuration
├── src/main/resources/
│   ├── application.properties
│   ├── prompts/
│   ├── diagrams/
│   └── templates/
├── outputs/             # Generated artifacts
│   ├── architecture/
│   ├── diagrams/
│   ├── sprint_plans/
│   ├── tech_docs/
│   └── code_specs/
└── pom.xml
```

## Quick Start

### 1. Build the Project

```bash
cd ai-company
mvn clean install
```

### 2. Configure LLM Provider

Edit `src/main/resources/application.properties`:

```properties
# For OpenAI
ai.model.provider=openai
openai.api.key=your-api-key-here

# For Anthropic
ai.model.provider=anthropic
anthropic.api.key=your-api-key-here

# For Ollama (local)
ai.model.provider=ollama
ollama.base.url=http://localhost:11434
```

### 3. Initialize Agents

```java
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import com.ai.company.agents.cto.CTOAgent;
import com.ai.company.agents.architect.ArchitectAgent;

// Initialize chat model
ChatLanguageModel chatModel = OpenAiChatModel.builder()
    .apiKey("your-api-key")
    .modelName("gpt-4")
    .temperature(0.7)
    .build();

// Create agents
CTOAgent cto = new CTOAgent(chatModel);
ArchitectAgent architect = new ArchitectAgent(chatModel);
```

### 4. Execute Workflow

```java
import com.ai.company.pipelines.planning.PlanningWorkflow;
import com.ai.company.agents.productmanager.ProductManagerAgent;

ProductManagerAgent pm = new ProductManagerAgent(chatModel);
PlanningWorkflow planning = new PlanningWorkflow(pm, cto);

PlanningWorkflow.PlanningResult result = planning.execute(
    "Company vision here",
    "Business requirements here",
    "session-123"
);
```

## Agents

- **CTOAgent**: Supervises all agents, approves decisions
- **ArchitectAgent**: Designs system architecture
- **EngineerAgent**: Creates implementation specs
- **DevOpsAgent**: Analyzes infrastructure
- **FullStackAgent**: Suggests UI/API implementations
- **ProductManagerAgent**: Creates epics and user stories
- **QAAgent**: Creates test plans

## Tools

All tools are read-only:

- **GitHubReaderTool**: Read repository files
- **FileSystemReaderTool**: Read filesystem files
- **DockerStatsTool**: Monitor Docker containers
- **LogReaderTool**: Read application logs

## Workflows

1. **PlanningWorkflow**: Vision → Epics → User Stories
2. **ArchitectureWorkflow**: Requirements → Architecture → API Contracts
3. **DevelopmentWorkflow**: Architecture → Implementation Specs
4. **ReviewWorkflow**: Specs → Review → Approval
5. **TestingWorkflow**: Specs → Test Plans → Postman Collections

## Zero-Impact Mode

**IMPORTANT**: All agents operate in zero-impact mode:
- ✅ Read-only access to codebase
- ✅ Specification generation only
- ✅ No code modifications
- ✅ No configuration changes
- ✅ Approval required for all decisions

## Documentation

- [Company Blueprint](./outputs/COMPANY_BLUEPRINT.md)
- [AI Agent Hierarchy](./outputs/AI_AGENT_HIERARCHY.md)
- [System Constraints](./outputs/SYSTEM_CONSTRAINTS.md)
- [Engineering Standards](./outputs/ENGINEERING_STANDARDS.md)
- [Tools Catalog](./outputs/TOOLS_CATALOG.json)

## Diagrams

- [AI Workflow Graph](./outputs/diagrams/AI_WORKFLOW_GRAPH.mmd)
- [System Architecture](./outputs/diagrams/SYSTEM_ARCHITECTURE.mmd)

## License

This AI company skeleton is part of AgroConnectWorld project.
