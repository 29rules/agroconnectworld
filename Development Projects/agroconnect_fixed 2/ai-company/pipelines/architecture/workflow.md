# Architecture Workflow

## Overview
The Architecture workflow converts product specifications into technical architecture designs, API contracts, and system interaction diagrams.

## Responsibilities
- Design system architecture
- Generate API contracts
- Create service interaction diagrams
- Propose integration patterns
- Ensure scalability
- Maintain compatibility with existing systems

## Input → Output Mapping

### Input
- Product specifications from Planning workflow
- User stories with acceptance criteria
- Existing system architecture (read-only)

### Output
- Architecture specifications
- API contract definitions (OpenAPI)
- Service interaction diagrams (Mermaid)
- Integration flow diagrams
- Technology recommendations

## Participating Agents

### Primary Agent
- **AI Architect Agent**: Leads architecture design

### Supporting Agents
- **CTO Agent**: Reviews and approves architecture
- **DevOps Agent**: Provides infrastructure input
- **AI Engineer Agent**: Reviews for implementation feasibility

## Agent Handoff Process

1. **AI Architect Agent** receives product specs from Planning
2. **AI Architect Agent** analyzes existing architecture (read-only)
3. **AI Architect Agent** designs new architecture extensions
4. **AI Architect Agent** generates API contracts
5. **DevOps Agent** reviews deployment architecture
6. **AI Engineer Agent** reviews implementation feasibility
7. **CTO Agent** reviews and approves architecture
8. Approved architecture moves to Development workflow

## Zero-Impact Mode
- **MUST NOT** modify existing microservices
- **MUST NOT** change existing API contracts
- **MUST NOT** modify database schemas
- Can only propose new designs and extensions
- All outputs are specifications
- Requires explicit approval for any changes

## When Real Code Changes Begin
Real code changes begin only after:
1. Architecture workflow completes
2. CTO Agent approves architecture
3. Development workflow creates implementation specs
4. Review workflow validates specifications
5. Explicit "implementation mode" is activated

## Workflow Diagram
```
Product Specifications
    ↓
AI Architect Agent
    ↓
Architecture Design
    ↓
API Contract Generation
    ↓
DevOps Agent (Infrastructure Review)
    ↓
AI Engineer Agent (Implementation Feasibility)
    ↓
CTO Agent (Architecture Review)
    ↓
Approved Architecture
    ↓
Development Workflow
```

## Artifacts Produced
- Architecture specifications (Markdown)
- API contracts (OpenAPI/Swagger JSON)
- Service interaction diagrams (Mermaid)
- Integration flow diagrams (Mermaid)
- Technology stack recommendations (Markdown)



