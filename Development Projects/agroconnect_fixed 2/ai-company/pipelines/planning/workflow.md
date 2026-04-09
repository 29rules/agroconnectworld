# Planning Workflow

## Overview
The Planning workflow converts company vision and business goals into actionable product specifications, epics, and user stories.

## Responsibilities
- Convert vision into product roadmap
- Break down epics into user stories
- Define acceptance criteria
- Prioritize features
- Coordinate with technical agents

## Input → Output Mapping

### Input
- Company vision and goals
- Business requirements
- Market research
- User feedback

### Output
- Product roadmap
- Epic definitions
- User stories with acceptance criteria
- Feature prioritization
- Sprint planning suggestions

## Participating Agents

### Primary Agent
- **Product Manager Agent**: Leads planning, creates epics and user stories

### Supporting Agents
- **CTO Agent**: Reviews technical feasibility
- **AI Architect Agent**: Provides architecture input for feasibility
- **QA Agent**: Reviews acceptance criteria for testability

## Agent Handoff Process

1. **Product Manager** receives business requirements
2. **Product Manager** creates epics and user stories
3. **CTO Agent** reviews for technical feasibility
4. **AI Architect Agent** provides architecture input
5. **Product Manager** refines based on feedback
6. **QA Agent** reviews acceptance criteria
7. **CTO Agent** approves final specifications
8. Specifications move to Architecture workflow

## Zero-Impact Mode
- **MUST NOT** modify existing features
- **MUST NOT** change existing user flows
- Can only propose new features
- All outputs are specifications
- No code changes at this stage

## When Real Code Changes Begin
Real code changes begin only after:
1. Planning workflow completes
2. Architecture workflow approves design
3. Development workflow creates implementation specs
4. CTO Agent gives final approval
5. Explicit "implementation mode" is activated

## Workflow Diagram
```
Business Requirements
    ↓
Product Manager Agent
    ↓
Epic & User Story Creation
    ↓
CTO Agent (Feasibility Review)
    ↓
AI Architect Agent (Architecture Input)
    ↓
Product Manager Agent (Refinement)
    ↓
QA Agent (Acceptance Criteria Review)
    ↓
CTO Agent (Final Approval)
    ↓
Architecture Workflow
```

## Artifacts Produced
- Product roadmap (Markdown)
- Epic definitions (JSON)
- User stories (JSON)
- Acceptance criteria (Markdown)
- Sprint planning suggestions (Markdown)



