# Review Workflow

## Overview
The Review workflow validates all specifications, ensures compliance with zero-impact mode, and prepares artifacts for implementation.

## Responsibilities
- Review all specifications for completeness
- Validate zero-impact mode compliance
- Check technical feasibility
- Ensure alignment with existing architecture
- Approve or request modifications
- Prepare final artifacts for implementation

## Input → Output Mapping

### Input
- Implementation specifications from Development workflow
- Architecture designs
- API contracts
- Code structure outlines

### Output
- Review reports
- Approval decisions
- Modification requests
- Final approved specifications
- Implementation readiness assessment

## Participating Agents

### Primary Agent
- **CTO Agent**: Leads review process, makes final decisions

### Supporting Agents
- **AI Architect Agent**: Reviews architecture compliance
- **AI Engineer Agent**: Reviews implementation approach
- **DevOps Agent**: Reviews infrastructure impact
- **QA Agent**: Reviews test coverage

## Agent Handoff Process

1. **CTO Agent** receives all specifications
2. **CTO Agent** performs initial compliance check
3. **AI Architect Agent** reviews architecture alignment
4. **AI Engineer Agent** reviews implementation approach
5. **DevOps Agent** reviews infrastructure impact
6. **QA Agent** reviews test coverage
7. **CTO Agent** makes final approval decision
8. Approved artifacts move to Testing workflow or Implementation

## Zero-Impact Mode
- **MUST NOT** approve breaking changes
- **MUST NOT** approve modifications to existing code
- **MUST** validate zero-impact compliance
- **MUST** ensure all changes are extensions
- All approvals are conditional on zero-impact mode

## When Real Code Changes Begin
Real code changes begin only after:
1. Review workflow completes
2. All agents approve their respective areas
3. CTO Agent gives final approval
4. Testing workflow validates test coverage
5. Explicit "implementation mode" is activated

## Workflow Diagram
```
Implementation Specs
    ↓
CTO Agent (Initial Review)
    ↓
AI Architect Agent (Architecture Check)
    ↓
AI Engineer Agent (Implementation Check)
    ↓
DevOps Agent (Infrastructure Check)
    ↓
QA Agent (Test Coverage Check)
    ↓
CTO Agent (Final Approval)
    ↓
Approved for Implementation
```

## Artifacts Produced
- Review reports (Markdown)
- Approval decisions (JSON)
- Modification requests (Markdown)
- Final specifications (Markdown/JSON)
- Implementation readiness report (Markdown)



