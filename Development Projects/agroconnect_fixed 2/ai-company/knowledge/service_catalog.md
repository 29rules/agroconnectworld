# AgroConnectWorld Service Catalog

## Microservices Architecture

AgroConnectWorld follows a microservices architecture with separate services for each business domain. All services communicate via REST APIs through the API Gateway.

## Service Overview

### 1. API Gateway Service
**Port**: 8080  
**Technology**: Spring Cloud Gateway  
**Purpose**: Single entry point for all API requests

**Responsibilities**:
- Route requests to appropriate microservices
- Handle CORS
- Rate limiting (future)
- API versioning (future)
- Request/response logging

**Routes**:
- `/api/auth/**` → auth-service
- `/api/products/**` → product-service
- `/api/suppliers/**` → supplier-service
- `/api/quotes/**` → quote-service
- `/api/orders/**` → order-service
- `/api/contact/**` → contact-service

**Database**: None (stateless routing only)

### 2. Auth Service
**Port**: 8081  
**Technology**: Spring Boot, Spring Security, JWT  
**Database Schema**: `auth_service`  
**Purpose**: User authentication and authorization

**Endpoints**:
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `GET /api/auth/profile` - Get user profile
- `POST /api/auth/refresh` - Refresh JWT token

**Entities**:
- `User`: id, name, email, phone, password, role, created_at
- `Role`: ADMIN, BUYER, SUPPLIER

**Features**:
- JWT token generation and validation
- Password encryption (BCrypt)
- Role-based access control
- Session management

**JWT Configuration**:
- Secret: 32-byte Base64 key
- Expiration: 86400000 ms (24 hours)

### 3. Product Service
**Port**: 8082  
**Technology**: Spring Boot, JPA  
**Database Schema**: `product_service`  
**Purpose**: Product catalog management

**Endpoints**:
- `GET /api/products` - List all products
- `GET /api/products/{id}` - Get product by ID
- `POST /api/products` - Create product (admin only)
- `PUT /api/products/{id}` - Update product (admin only)
- `DELETE /api/products/{id}` - Delete product (admin only)

**Entities**:
- `Product`: id, name, description, category, sku, price, is_active, created_at
- `ProductImage`: id, product_id, url

**Features**:
- Product CRUD operations
- Product image management
- Category filtering
- Active/inactive product status
- Product variations support

### 4. Supplier Service
**Port**: 8083  
**Technology**: Spring Boot, JPA  
**Database Schema**: `supplier_service`  
**Purpose**: Supplier management

**Endpoints**:
- `GET /api/suppliers` - List all suppliers
- `GET /api/suppliers/{id}` - Get supplier by ID
- `POST /api/suppliers` - Create supplier
- `PUT /api/suppliers/{id}` - Update supplier

**Entities**:
- `Supplier`: id, name, country, certifications, supply_capacity, phone, email
- `Certification`: id, supplier_id, certification_type, issued_date, expiry_date

**Features**:
- Supplier registration and management
- Certification tracking
- Capacity management
- Country/region information

### 5. Quote Service
**Port**: 8084  
**Technology**: Spring Boot, JPA  
**Database Schema**: `quote_service`  
**Purpose**: Quote request and response management

**Endpoints**:
- `GET /api/quotes` - List all quote requests
- `GET /api/quotes/{id}` - Get quote request by ID
- `POST /api/quotes` - Create quote request
- `PATCH /api/quotes/{id}/status` - Update quote status

**Entities**:
- `QuoteRequest`: id, user_id, product_id, quantity, packaging_size, port, status, created_at

**Status Values**:
- `PENDING`: Quote request submitted, awaiting response
- `REVIEWING`: Quote being reviewed by admin
- `APPROVED`: Quote approved and sent to buyer
- `REJECTED`: Quote rejected

**Features**:
- Quote request creation by buyers
- Quote status management
- Admin approval workflow
- Product and quantity tracking

### 6. Order Service
**Port**: 8085  
**Technology**: Spring Boot, JPA  
**Database Schema**: `order_service`  
**Purpose**: Order management and fulfillment

**Endpoints**:
- `GET /api/orders` - List all orders
- `GET /api/orders/{id}` - Get order by ID
- `POST /api/orders` - Create order

**Entities**:
- `Order`: id, user_id, status, total_amount, created_at
- `OrderItem`: id, order_id, product_id, quantity, price

**Status Values**:
- `PENDING`: Order created, awaiting confirmation
- `CONFIRMED`: Order confirmed
- `SHIPPED`: Order shipped
- `DELIVERED`: Order delivered

**Features**:
- Order creation from approved quotes
- Order item management
- Order status tracking
- Total amount calculation

### 7. Contact Service
**Port**: 8086  
**Technology**: Spring Boot, JPA  
**Database Schema**: `contact_service`  
**Purpose**: Contact form and inquiry management

**Endpoints**:
- `GET /api/contact` - List all contact messages
- `POST /api/contact` - Submit contact message

**Entities**:
- `ContactMessage`: id, name, email, message, created_at

**Features**:
- Contact form submission
- Message storage
- Admin inbox management

## Database Architecture

### Database: `agro_master`
**Technology**: PostgreSQL  
**Schemas**: Each service has its own schema
- `auth_service`
- `product_service`
- `supplier_service`
- `quote_service`
- `order_service`
- `contact_service`

**Connection**:
- Host: `postgres` (Docker service name)
- Port: 5432
- Database: `agro_master`
- Username: `agro`
- Password: `agro_pass`

## Service Communication

### Synchronous Communication
- All services communicate via REST APIs
- API Gateway routes requests
- Services are independent and stateless

### Future Enhancements
- Asynchronous messaging (Kafka) for events
- Service discovery
- Circuit breakers
- Distributed tracing

## Service Health

All services expose health endpoints:
- `GET /actuator/health` - Service health check
- `GET /actuator/info` - Service information

## API Documentation

All services provide OpenAPI/Swagger documentation:
- OpenAPI JSON: `/api/docs/openapi`
- Swagger UI: `/api/docs/swagger`

## Deployment

All services are containerized with Docker and orchestrated with Docker Compose. Each service:
- Has its own Dockerfile
- Uses Java 21 (Eclipse Temurin)
- Exposes a specific port
- Connects to shared PostgreSQL database
- Has health checks configured



