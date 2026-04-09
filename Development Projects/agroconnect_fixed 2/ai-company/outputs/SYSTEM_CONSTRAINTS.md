# System Constraints

## Zero-Impact Mode

### DO NOT MODIFY
- ❌ Existing Spring Boot microservices (auth, product, supplier, order, quote, contact, gateway)
- ❌ React frontend code
- ❌ Docker Compose configurations
- ❌ Nginx reverse proxy settings
- ❌ Postgres database schemas
- ❌ VPS deployment architecture
- ❌ Existing API contracts
- ❌ Existing database migrations

### CAN DO
- ✅ Read existing codebase (read-only tools)
- ✅ Analyze existing architecture
- ✅ Propose new features as extensions
- ✅ Generate specifications and documentation
- ✅ Create diagrams and visualizations
- ✅ Suggest improvements (approval required)

## Tool Constraints

### GitHub Reader Tool
- **Read-Only**: Can only read files and repository structure
- **No Writes**: Cannot create, modify, or delete files
- **No Commits**: Cannot commit changes to Git

### File System Reader Tool
- **Read-Only**: Can only read files
- **Path Validation**: Ensures paths are within allowed boundaries
- **No Writes**: Cannot modify filesystem

### Docker Stats Tool
- **Read-Only**: Can only read container stats and configurations
- **No Control**: Cannot start, stop, or modify containers
- **No Deployments**: Cannot deploy or update services

### Log Reader Tool
- **Read-Only**: Can only read logs
- **No Modifications**: Cannot modify or delete logs

## Agent Constraints

### All Agents
- **No Code Commits**: Cannot commit code to repository
- **No Production Changes**: Cannot modify production systems
- **Specification Only**: Can only produce specifications
- **Approval Required**: All outputs require CTO approval

### CTO Agent
- **Final Authority**: Makes final approval decisions
- **Compliance Check**: Validates zero-impact compliance
- **Risk Assessment**: Assesses risks of all proposals

## Extension Patterns

### New Microservices
- Can add new microservices as separate services
- Must integrate with existing gateway
- Must follow existing Spring Boot patterns

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

## Implementation Mode

Real code changes only occur when:
1. All workflows complete successfully
2. CTO Agent gives final approval
3. Explicit "implementation mode" is activated
4. Human oversight is in place



