# AI Agent Hierarchy

## Overview

The AgroConnectWorld AI Company uses a hierarchical agent structure with the CTO Agent as the supervisor of all technical agents.

## Agent Roles and Responsibilities

### 1. CTO Agent (Supervisor)
- **Class**: `com.ai.company.agents.cto.CTOAgent`
- **Supervises**: All technical agents
- **Responsibilities**:
  - Approves all architecture and implementation decisions
  - Generates technical documentation
  - Creates Mermaid diagrams
  - Assesses risks
  - Plans technical sprints
- **Tools**: GitHub Reader, File System Reader, Docker Stats, Log Reader

### 2. AI Architect Agent
- **Class**: `com.ai.company.agents.architect.ArchitectAgent`
- **Reports To**: CTO Agent
- **Responsibilities**:
  - Converts business requirements into technical architecture
  - Designs scalable microservice patterns
  - Generates API contract specifications (OpenAPI)
  - Creates service interaction diagrams
- **Tools**: GitHub Reader, File System Reader

### 3. AI Engineer Agent
- **Class**: `com.ai.company.agents.engineer.EngineerAgent`
- **Reports To**: CTO Agent, AI Architect Agent
- **Responsibilities**:
  - Converts architecture into implementation specifications
  - Generates pseudo-code
  - Creates service layer specifications
  - Designs integration patterns
- **Tools**: GitHub Reader, File System Reader

### 4. DevOps Agent
- **Class**: `com.ai.company.agents.devops.DevOpsAgent`
- **Reports To**: CTO Agent
- **Responsibilities**:
  - Analyzes infrastructure (read-only)
  - Reviews Docker Compose configurations
  - Suggests CI/CD improvements
  - Creates deployment diagrams
- **Tools**: Docker Stats, File System Reader, Log Reader

### 5. Full-Stack Developer Agent
- **Class**: `com.ai.company.agents.fullstack.FullStackAgent`
- **Reports To**: CTO Agent, AI Engineer Agent
- **Responsibilities**:
  - Suggests React component implementations
  - Designs API request flows
  - Creates frontend-backend integration specs
- **Tools**: GitHub Reader, File System Reader

### 6. Product Manager Agent
- **Class**: `com.ai.company.agents.productmanager.ProductManagerAgent`
- **Works With**: CTO Agent, AI Architect Agent
- **Responsibilities**:
  - Converts vision into product roadmap
  - Creates epics and user stories
  - Defines acceptance criteria
  - Prioritizes features
- **Tools**: None (business-focused)

### 7. QA Agent
- **Class**: `com.ai.company.agents.qa.QAAgent`
- **Works With**: Product Manager, AI Engineer
- **Responsibilities**:
  - Creates test plans
  - Designs Postman collections
  - Specifies E2E test flows
- **Tools**: File System Reader, Log Reader

## Communication Flow

```
Product Manager → AI Architect → AI Engineer
                              ↓
                         Full-Stack Developer
                              ↓
                         QA Agent
                              ↓
                         DevOps Agent
                              ↓
                         CTO Agent (Final Approval)
```

## Decision Authority

- **CTO Agent**: Final approval on all technical decisions
- **AI Architect**: Architecture design decisions
- **AI Engineer**: Implementation approach decisions
- **DevOps**: Infrastructure recommendations
- **Product Manager**: Product prioritization
- **QA**: Test strategy decisions



