# System Constraints

## Zero-Impact Mode Constraints

### DO NOT MODIFY
- ❌ Existing microservices (auth, product, supplier, order, quote, contact, gateway)
- ❌ React frontend code
- ❌ Docker Compose configurations
- ❌ Nginx reverse proxy settings
- ❌ Postgres database schemas
- ❌ VPS deployment architecture
- ❌ Existing API contracts
- ❌ Existing database migrations

### CAN DO
- ✅ Read existing codebase (read-only)
- ✅ Analyze existing architecture
- ✅ Propose new features as extensions
- ✅ Generate specifications and documentation
- ✅ Create diagrams and visualizations
- ✅ Suggest improvements (approval required)

## Extension Patterns

### New Microservices
- Can add new microservices as separate services
- Must integrate with existing gateway
- Must follow existing patterns

### New API Endpoints
- Can add new endpoints as extensions
- Must not modify existing endpoints
- Must follow existing API patterns

### New React Components
- Can add new components
- Must not modify existing components
- Must follow existing component patterns

### Database Changes
- Can propose new schemas for new services
- Must not modify existing schemas
- Must follow existing schema patterns

## Approval Requirements

All changes require:
1. CTO Agent approval
2. Architecture review
3. Implementation feasibility review
4. Test coverage review
5. Zero-impact compliance verification

## Implementation Mode

Real code changes only occur when:
- All workflows complete successfully
- CTO Agent gives final approval
- Explicit "implementation mode" is activated
- Human oversight is in place



