# AgroConnectWorld Compliance Rules

## Zero-Impact Mode

### Definition
Zero-impact mode means that AI agents and automated systems **MUST NOT** modify any existing code, configuration, or production systems. All outputs must be specification-only.

### Mandatory Rules

#### 1. No Code Modifications
- **FORBIDDEN**: Modifying existing Java files
- **FORBIDDEN**: Modifying existing React components
- **FORBIDDEN**: Creating new files in existing service directories
- **FORBIDDEN**: Deleting existing files
- **ALLOWED**: Creating specification documents
- **ALLOWED**: Creating design documents
- **ALLOWED**: Creating test plans

#### 2. No Configuration Changes
- **FORBIDDEN**: Modifying Docker Compose files
- **FORBIDDEN**: Modifying Nginx configuration
- **FORBIDDEN**: Modifying database schemas
- **FORBIDDEN**: Modifying environment variables
- **FORBIDDEN**: Modifying application.properties files
- **ALLOWED**: Documenting configuration changes
- **ALLOWED**: Proposing configuration improvements

#### 3. No Production Impact
- **FORBIDDEN**: Deploying to production
- **FORBIDDEN**: Running migrations
- **FORBIDDEN**: Restarting services
- **FORBIDDEN**: Modifying running containers
- **ALLOWED**: Analyzing production logs (read-only)
- **ALLOWED**: Monitoring system health (read-only)

#### 4. Specification Only
- **REQUIRED**: All outputs must be specifications
- **REQUIRED**: All outputs must be documentation
- **REQUIRED**: All outputs must be design proposals
- **REQUIRED**: All outputs must be test plans
- **FORBIDDEN**: Executable code changes
- **FORBIDDEN**: Direct system modifications

## AI Agent Constraints

### Agent Behavior Rules

#### 1. Read-Only Operations
- Agents can READ existing code
- Agents can READ existing configurations
- Agents can READ logs and metrics
- Agents CANNOT write to files
- Agents CANNOT modify configurations
- Agents CANNOT execute commands that modify system state

#### 2. Output Restrictions
- All agent outputs must be text/JSON specifications
- No agent can generate code that modifies existing files
- No agent can propose breaking changes
- No agent can suggest production deployments

#### 3. Approval Requirements
- All agent outputs require Supervisor Agent approval
- Supervisor Agent enforces zero-impact compliance
- Supervisor Agent can reject non-compliant outputs
- Supervisor Agent can require modifications

### Agent-Specific Rules

#### CTO Agent
- Can approve/reject architecture proposals
- Can review technical decisions
- Must ensure zero-impact compliance
- Cannot approve code modifications

#### Architect Agent
- Can propose architecture designs
- Can create API contracts (specifications only)
- Cannot modify existing architecture
- Cannot change service boundaries

#### Engineer Agent
- Can create implementation specifications
- Can generate pseudo-code
- Cannot modify existing code
- Cannot create new service files

#### DevOps Agent
- Can analyze infrastructure (read-only)
- Can propose improvements (specifications only)
- Cannot modify Docker configurations
- Cannot modify Nginx configurations

#### Full-Stack Agent
- Can propose UI designs
- Can create component specifications
- Cannot modify existing React components
- Cannot change existing frontend code

#### Product Manager Agent
- Can create epics and user stories
- Can define requirements
- Cannot modify existing features
- Cannot change business rules

#### QA Agent
- Can create test plans
- Can design test cases
- Cannot modify existing tests
- Cannot change test infrastructure

## Workflow Compliance

### Workflow Rules

#### 1. Planning Workflow
- Must produce specifications only
- Must not create actual code
- Must not modify existing plans
- Must be approved by Supervisor

#### 2. Architecture Workflow
- Must produce design documents only
- Must not modify existing architecture
- Must ensure backward compatibility
- Must be approved by Supervisor

#### 3. Development Workflow
- Must produce implementation specs only
- Must not generate executable code
- Must not modify existing implementations
- Must be approved by Supervisor

#### 4. Review Workflow
- Must review specifications only
- Must not approve code modifications
- Must enforce zero-impact compliance
- Must be approved by Supervisor

#### 5. Testing Workflow
- Must produce test plans only
- Must not modify existing tests
- Must not change test infrastructure
- Must be approved by Supervisor

## Validation Rules

### Output Validation
- All agent outputs must be validated
- Validation checks for zero-impact compliance
- Validation checks for code modification attempts
- Validation checks for structured output

### Compliance Checks
- Zero-impact mode compliance
- No code modifications
- Structured output format
- Safety and security

## Enforcement

### Supervisor Agent
- Ultimate authority for compliance
- Can reject non-compliant outputs
- Can require modifications
- Can block unsafe workflows

### Validation System
- Automatically validates all outputs
- Detects compliance violations
- Reports violations to Supervisor
- Blocks non-compliant outputs

## Exception Handling

### No Exceptions
- Zero-impact mode has no exceptions
- All agents must comply
- All workflows must comply
- All outputs must comply

### Human Override
- Only humans can modify code
- Only humans can deploy to production
- Only humans can change configurations
- AI agents cannot override compliance rules

## Compliance Monitoring

### Logging
- All agent actions logged
- Compliance violations logged
- Supervisor decisions logged
- Validation results logged

### Auditing
- Audit trail of all agent outputs
- Audit trail of all compliance checks
- Audit trail of all Supervisor decisions
- Audit trail of all violations

## Best Practices

### For Agents
1. Always produce specifications
2. Never suggest code modifications
3. Always check zero-impact compliance
4. Always get Supervisor approval

### For Workflows
1. Ensure all steps are specification-only
2. Validate compliance at each step
3. Get Supervisor approval before proceeding
4. Document all decisions

### For Developers
1. Review all AI agent outputs
2. Verify zero-impact compliance
3. Implement changes manually
4. Test all changes before deployment



