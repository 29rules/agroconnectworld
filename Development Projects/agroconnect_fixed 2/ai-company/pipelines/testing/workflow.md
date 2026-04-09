# Testing Workflow

## Overview
The Testing workflow creates comprehensive test plans, Postman collections, and E2E testing flows to ensure quality standards.

## Responsibilities
- Create test plans for features
- Design Postman test collections
- Specify E2E testing flows
- Define unit test requirements
- Ensure test coverage
- Validate acceptance criteria

## Input → Output Mapping

### Input
- Approved implementation specifications
- User stories with acceptance criteria
- API contracts
- Component specifications

### Output
- Test plans
- Postman collections (JSON)
- E2E test specifications
- Unit test requirements
- Integration test specs
- Test coverage reports

## Participating Agents

### Primary Agent
- **QA Agent**: Creates test plans and collections

### Supporting Agents
- **Product Manager Agent**: Validates acceptance criteria coverage
- **AI Engineer Agent**: Reviews test requirements
- **Full-Stack Developer Agent**: Reviews component test specs
- **CTO Agent**: Approves test strategy

## Agent Handoff Process

1. **QA Agent** receives implementation specs
2. **QA Agent** creates test plans
3. **QA Agent** designs Postman collections
4. **Product Manager Agent** validates acceptance criteria coverage
5. **AI Engineer Agent** reviews backend test requirements
6. **Full-Stack Developer Agent** reviews frontend test specs
7. **CTO Agent** approves test strategy
8. Test artifacts ready for implementation

## Zero-Impact Mode
- **MUST NOT** modify existing tests
- **MUST NOT** run tests against production
- **MUST NOT** change test infrastructure
- Can only produce test specifications
- All test plans require review

## When Real Code Changes Begin
Real code changes begin only after:
1. Testing workflow completes
2. Test plans are approved
3. Review workflow validates all specs
4. CTO Agent gives final approval
5. Explicit "implementation mode" is activated

## Workflow Diagram
```
Implementation Specs
    ↓
QA Agent
    ↓
Test Plan Creation
    ↓
Postman Collection Design
    ↓
Product Manager (Acceptance Criteria Check)
    ↓
AI Engineer (Backend Test Review)
    ↓
Full-Stack Developer (Frontend Test Review)
    ↓
CTO Agent (Test Strategy Approval)
    ↓
Approved Test Plans
```

## Artifacts Produced
- Test plans (Markdown)
- Postman collections (JSON)
- E2E test specifications (Markdown)
- Unit test requirements (Markdown)
- Integration test specs (Markdown)
- Test coverage reports (Markdown)



