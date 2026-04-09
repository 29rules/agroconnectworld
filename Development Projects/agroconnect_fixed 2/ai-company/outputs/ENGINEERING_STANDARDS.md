# Engineering Standards

## Code Standards

### Java
- **Version**: Java 21
- **Style**: Follow existing Spring Boot patterns
- **Naming**: camelCase for methods, PascalCase for classes
- **Documentation**: Javadoc for public methods

### LangChain4j
- **Pattern**: Use `@AiService` for agent interfaces
- **Tools**: Use `@Tool` annotation for tool methods
- **Memory**: Use `MessageWindowChatMemory` for conversation context
- **Output**: Structured JSON responses

## Architecture Standards

### Microservices
- **Framework**: Spring Boot 3.3.3
- **Database**: PostgreSQL with separate schemas
- **API**: RESTful APIs
- **Documentation**: OpenAPI/Swagger

### Frontend
- **Framework**: React with Vite
- **UI Library**: Bootstrap
- **State Management**: React Context
- **Routing**: React Router

## Documentation Standards

### Architecture Documentation
- **Format**: Markdown
- **Diagrams**: Mermaid syntax
- **Location**: `/outputs/architecture/`

### API Documentation
- **Format**: OpenAPI 3.0
- **Location**: `/outputs/code_specs/`

### Code Specifications
- **Format**: Markdown with code blocks
- **Location**: `/outputs/code_specs/`

## Testing Standards

### Test Plans
- **Format**: Markdown
- **Coverage**: All acceptance criteria
- **Location**: `/outputs/tech_docs/`

### Postman Collections
- **Format**: JSON (Postman Collection v2.1)
- **Location**: `/outputs/tech_docs/`

## Approval Process

### Review Requirements
1. CTO Agent: Final approval
2. AI Architect: Architecture compliance
3. AI Engineer: Implementation feasibility
4. DevOps: Infrastructure impact
5. QA: Test coverage

### Approval Criteria
- ✅ Zero-impact compliance verified
- ✅ Architecture aligns with existing patterns
- ✅ Implementation is feasible
- ✅ Test coverage is adequate
- ✅ Documentation is complete
- ✅ No breaking changes identified



