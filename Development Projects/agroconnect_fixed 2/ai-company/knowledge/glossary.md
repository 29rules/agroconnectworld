# AgroConnectWorld Glossary

## A

### Admin
A user role with full system access, including user management, product management, and order oversight.

### API Gateway
The single entry point for all API requests, routing requests to appropriate microservices.

### Authentication
The process of verifying user identity, typically using JWT tokens.

### Authorization
The process of determining what actions a user is allowed to perform based on their role.

## B

### BCrypt
A password hashing algorithm used to securely store user passwords.

### Buyer
A user role representing companies that purchase agricultural products in bulk.

### Bounded Context
A domain-driven design concept representing a boundary within which a domain model is valid.

## C

### Certification
A credential or qualification held by a supplier, such as organic certification or ISO standards.

### Contact Service
Microservice handling contact form submissions and customer inquiries.

### Container
A Docker container running a service or application in isolation.

## D

### Docker
Containerization platform used to package and deploy services.

### Docker Compose
Tool for defining and running multi-container Docker applications.

### Domain Model
A conceptual model of the business domain, including entities and their relationships.

## E

### Entity
A domain object with a unique identity, such as User, Product, or Order.

### Execution Log
A record of agent or workflow execution, including inputs, outputs, and performance metrics.

## F

### Frontend
The React-based user interface accessible via web browser.

## G

### Gateway
See API Gateway.

## H

### Hibernate
Java ORM (Object-Relational Mapping) framework used for database interactions.

### Health Check
An endpoint that reports the health status of a service.

## I

### ISO 9001
An international quality management standard that suppliers may hold.

## J

### JPA
Java Persistence API, a specification for managing relational data in Java applications.

### JWT
JSON Web Token, used for stateless authentication.

## M

### Microservice
An independent, deployable service that implements a specific business capability.

### Migration
A script that modifies database schema, typically versioned and reversible.

## N

### Nginx
Reverse proxy and web server used to serve frontend and route API requests.

## O

### Order
A purchase order created by a buyer, containing one or more order items.

### Order Item
An individual product line within an order, specifying product, quantity, and price.

### Order Service
Microservice managing orders and order fulfillment.

## P

### Packaging Size
The size of product packaging, such as 100g, 250g, 500g, or 1kg.

### Port
Shipping port where goods are loaded/unloaded, specified in quote requests.

### Product
An agricultural product available for purchase, such as sesame seeds or spices.

### Product Image
An image associated with a product, stored as a URL.

### Product Service
Microservice managing the product catalog.

## Q

### Quote Request
A request from a buyer for pricing on a specific product and quantity.

### Quote Service
Microservice managing quote requests and responses.

### Quote Status
The current state of a quote request: PENDING, REVIEWING, APPROVED, or REJECTED.

## R

### React
JavaScript library for building user interfaces.

### Redis
In-memory data store used for caching (future use).

### Role
User role determining access permissions: ADMIN, BUYER, or SUPPLIER.

## S

### Schema
A database schema, with each microservice having its own schema.

### Session
A user session identified by a session ID, used for tracking related operations.

### SKU
Stock Keeping Unit, a unique identifier for a product.

### Spring Boot
Java framework for building microservices.

### Spring Cloud Gateway
API Gateway implementation for routing requests to microservices.

### Supplier
A user role representing companies that supply agricultural products.

### Supplier Service
Microservice managing supplier information and certifications.

### Supply Capacity
The maximum quantity a supplier can provide.

## T

### Token
JWT token used for authentication.

## U

### UUID
Universally Unique Identifier, used as primary keys for all entities.

### User
A platform user with authentication credentials and a role.

## V

### Vite
Build tool and development server for React applications.

## W

### Workflow
A multi-step process involving multiple agents, such as planning or development workflows.

### Workflow Execution Log
A record of workflow execution, including all agent executions and overall status.

## Z

### Zero-Impact Mode
A compliance mode where AI agents produce specifications only and never modify existing code or configurations.



