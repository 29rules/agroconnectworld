# Development Workflow

## Overview
The Development workflow converts architecture designs into detailed implementation specifications, pseudo-code, and code structure outlines.

## Responsibilities
- Convert architecture to implementation specs
- Generate pseudo-code for complex logic
- Create code structure outlines
- Specify service implementations
- Design integration patterns
- Propose testing strategies

## Input → Output Mapping

### Input
- Approved architecture from Architecture workflow
- API contracts
- Service interaction diagrams

### Output
- Implementation specifications
- Pseudo-code
- Code structure outlines
- Service layer specifications
- Integration specifications
- Testing requirements

## Participating Agents

### Primary Agent
- **AI Engineer Agent**: Creates backend implementation specs

### Supporting Agents
- **Full-Stack Developer Agent**: Creates frontend implementation specs
- **CTO Agent**: Reviews implementation approach
- **QA Agent**: Reviews testing requirements

## Agent Handoff Process

1. **AI Engineer Agent** receives approved architecture
2. **AI Engineer Agent** creates backend implementation specs
3. **Full-Stack Developer Agent** creates frontend specs
4. **AI Engineer Agent** and **Full-Stack Developer Agent** coordinate integration
5. **QA Agent** reviews testing requirements
6. **CTO Agent** reviews implementation approach
7. Approved specs move to Review workflow

## Zero-Impact Mode
- **MUST NOT** commit code to repository
- **MUST NOT** modify existing production code
- **MUST NOT** run database migrations
- Can only produce specifications and pseudo-code
- All outputs are suggestions for review
- No actual code changes at this stage

## When Real Code Changes Begin
Real code changes begin only after:
1. Development workflow completes
2. Review workflow validates specifications
3. Testing workflow creates test plans
4. CTO Agent gives final approval
5. Explicit "implementation mode" is activated

## Workflow Diagram
```
Approved Architecture
    ↓
AI Engineer Agent (Backend Specs)
    ↓
Full-Stack Developer Agent (Frontend Specs)
    ↓
Integration Coordination
    ↓
QA Agent (Testing Requirements)
    ↓
CTO Agent (Implementation Review)
    ↓
Approved Implementation Specs
    ↓
Review Workflow
```

## Artifacts Produced
- Implementation specifications (Markdown)
- Pseudo-code (Markdown)
- Code structure outlines (Markdown)
- Service specifications (Markdown)
- Integration specs (Markdown)
- Testing requirements (Markdown)



