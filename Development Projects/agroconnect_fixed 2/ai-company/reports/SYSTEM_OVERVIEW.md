# AgroConnectWorld - System Architecture Overview

**Generated:** 2025-11-28  
**Version:** 1.0  
**Status:** Production-Ready

---

## Table of Contents

1. [Microservices List](#microservices-list)
2. [APIs & Endpoints](#apis--endpoints)
3. [Database Connections](#database-connections)
4. [DTOs (Data Transfer Objects)](#dtos-data-transfer-objects)
5. [Entity Mappings](#entity-mappings)
6. [Service Relationships (Call Graph)](#service-relationships-call-graph)
7. [Gateway Routes](#gateway-routes)
8. [Nginx Routes](#nginx-routes)
9. [Docker Services](#docker-services)
10. [Docker Volumes](#docker-volumes)
11. [Environment Variables](#environment-variables)
12. [Frontend Routes](#frontend-routes)
13. [Build Commands](#build-commands)

---

## Microservices List

### Backend Services

| Service | Port | Container Name | Database Schema | Status |
|---------|------|----------------|-----------------|--------|
| **Gateway** | 8080 | `gateway_service` | N/A (Routes only) | ✅ Active |
| **Auth Service** | 8081 | `auth_service` | `auth_service` | ✅ Active |
| **Product Service** | 8082 | `product_service` | `product_service` | ✅ Active |
| **Supplier Service** | 8083 | `supplier_service` | `supplier_service` | ✅ Active |
| **Quote Service** | 8084 | `quote_service` | `quote_service` | ✅ Active |
| **Order Service** | 8085 | `order_service` | `order_service` | ✅ Active |
| **Contact Service** | 8086 | `contact_service` | `contact_service` | ✅ Active |

### Infrastructure Services

| Service | Port | Container Name | Purpose | Status |
|---------|------|----------------|---------|--------|
| **Nginx** | 80 | `edge_service` | Edge/Reverse Proxy | ✅ Active |
| **PostgreSQL** | 5432 | `postgres_service` | Primary Database | ✅ Active |
| **Redis** | 6379 | `redis_service` | Cache/Session Store | ✅ Active |
| **MinIO** | 9000/9001 | `minio_service` | Object Storage | ✅ Active |

### Frontend

| Service | Port | Container Name | Framework | Status |
|---------|------|----------------|-----------|--------|
| **Frontend** | 8081 (internal) | `frontend_service` | React + Vite | ✅ Active |

---

## APIs & Endpoints

### Gateway Routes (Port 8080)

All API requests go through the Gateway, which routes to appropriate microservices:

| Route Pattern | Target Service | Target IP:Port | Description |
|---------------|----------------|----------------|-------------|
| `/api/auth/**` | Auth Service | `172.20.0.2:8081` | Authentication endpoints |
| `/api/products/**` | Product Service | `172.20.0.10:8082` | Product management |
| `/api/suppliers/**` | Supplier Service | `172.20.0.7:8083` | Supplier management |
| `/api/quotes/**` | Quote Service | `172.20.0.8:8084` | Quote requests |
| `/api/orders/**` | Order Service | `172.20.0.4:8085` | Order management |
| `/api/contact/**` | Contact Service | `172.20.0.3:8086` | Contact messages |

### Auth Service APIs (`/api/auth`)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| `POST` | `/api/auth/register` | Register new user | ❌ No |
| `POST` | `/api/auth/login` | User login | ❌ No |
| `GET` | `/api/auth/profile` | Get user profile | ✅ Yes (Header: `X-User-Email`) |
| `POST` | `/api/auth/promote-ceo` | Promote user to CEO (temporary) | ❌ No |

**Request/Response Examples:**

- **Register:** `POST /api/auth/register`
  ```json
  {
    "name": "John Doe",
    "email": "john@example.com",
    "phone": "+1234567890",
    "password": "password123",
    "role": "BUYER"
  }
  ```
  Response: `{ "token": "...", "role": "BUYER" }`

- **Login:** `POST /api/auth/login`
  ```json
  {
    "email": "john@example.com",
    "password": "password123"
  }
  ```
  Response: `{ "token": "...", "role": "BUYER" }`

### Product Service APIs (`/api/products`)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| `GET` | `/api/products` | List all products | ❌ No |
| `GET` | `/api/products/{id}` | Get product by ID | ❌ No |
| `POST` | `/api/products` | Create product | ✅ Yes |
| `PUT` | `/api/products/{id}` | Update product | ✅ Yes |
| `DELETE` | `/api/products/{id}` | Delete product | ✅ Yes |

### Supplier Service APIs (`/api/suppliers`)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| `GET` | `/api/suppliers` | List all suppliers | ✅ Yes |
| `GET` | `/api/suppliers/{id}` | Get supplier by ID | ✅ Yes |
| `POST` | `/api/suppliers` | Create supplier | ✅ Yes |
| `PUT` | `/api/suppliers/{id}` | Update supplier | ✅ Yes |

### Quote Service APIs (`/api/quotes`)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| `GET` | `/api/quotes` | List all quote requests | ✅ Yes |
| `GET` | `/api/quotes/{id}` | Get quote by ID | ✅ Yes |
| `POST` | `/api/quotes` | Create quote request | ✅ Yes |
| `PATCH` | `/api/quotes/{id}/status` | Update quote status | ✅ Yes |

### Order Service APIs (`/api/orders`)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| `GET` | `/api/orders` | List all orders | ✅ Yes |
| `GET` | `/api/orders/{id}` | Get order by ID | ✅ Yes |
| `POST` | `/api/orders` | Create order | ✅ Yes |

### Contact Service APIs (`/api/contact`)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| `GET` | `/api/contact` | List all contact messages | ✅ Yes |
| `POST` | `/api/contact` | Create contact message | ❌ No |

---

## Database Connections

### PostgreSQL Configuration

**Host:** `postgres_service` (Docker network) or `localhost:5432` (external)  
**Database:** `agro_master`  
**User:** `agro` (default)  
**Password:** `agro_pass` (default)

### Schema Isolation

Each microservice uses its own PostgreSQL schema within the same database:

| Service | Schema Name | Purpose |
|---------|-------------|---------|
| Auth Service | `auth_service` | User authentication & authorization |
| Product Service | `product_service` | Product catalog |
| Supplier Service | `supplier_service` | Supplier information |
| Quote Service | `quote_service` | Quote requests |
| Order Service | `order_service` | Order management |
| Contact Service | `contact_service` | Contact messages |

### Connection String Pattern

```
jdbc:postgresql://postgres:5432/agro_master
```

**Per-Service Configuration:**
- **Auth Service:** `spring.jpa.properties.hibernate.default_schema=auth_service`
- **Product Service:** `spring.jpa.properties.hibernate.default_schema=product_service`
- **Supplier Service:** `spring.jpa.properties.hibernate.default_schema=supplier_service`
- **Quote Service:** `spring.jpa.properties.hibernate.default_schema=quote_service`
- **Order Service:** `spring.jpa.properties.hibernate.default_schema=order_service`
- **Contact Service:** `spring.jpa.properties.hibernate.default_schema=contact_service`

### Redis Configuration

**Host:** `redis_service` (Docker network) or `localhost:6379` (external)  
**Port:** `6379`  
**Purpose:** Caching, session storage (future use)

### MinIO Configuration

**Host:** `minio_service` (Docker network)  
**API Port:** `9000`  
**Console Port:** `9001`  
**Root User:** `agroadmin` (default)  
**Root Password:** `agroadminpass` (default)  
**Purpose:** Object storage for product images, documents

---

## DTOs (Data Transfer Objects)

### Auth Service DTOs

| DTO | Purpose | Fields |
|-----|---------|--------|
| `LoginRequest` | Login input | `email` (String), `password` (String) |
| `RegisterRequest` | Registration input | `name`, `email`, `phone`, `password`, `role` |
| `AuthResponse` | Auth output | `token` (String), `role` (String) |
| `ProfileResponse` | User profile | `id`, `name`, `email`, `phone`, `role`, `createdAt` |

### Product Service DTOs

| DTO | Purpose | Fields |
|-----|---------|--------|
| `Product` (Entity used as DTO) | Product data | `id`, `name`, `description`, `category`, `sku`, `price`, `isActive`, `createdAt`, `productImages` |

### Supplier Service DTOs

| DTO | Purpose | Fields |
|-----|---------|--------|
| `Supplier` (Entity used as DTO) | Supplier data | `id`, `name`, `email`, `phone`, `address`, `country`, `certifications`, `createdAt` |

### Quote Service DTOs

| DTO | Purpose | Fields |
|-----|---------|--------|
| `CreateQuoteRequest` | Create quote | `productId`, `quantity`, `message`, `buyerEmail` |
| `UpdateQuoteStatusRequest` | Update status | `status` (PENDING, APPROVED, REJECTED) |
| `QuoteRequest` (Entity) | Quote data | `id`, `productId`, `quantity`, `message`, `status`, `buyerEmail`, `supplierId`, `createdAt` |

### Order Service DTOs

| DTO | Purpose | Fields |
|-----|---------|--------|
| `CreateOrderRequest` | Create order | `items` (List<OrderItemRequest>), `shippingAddress`, `paymentMethod` |
| `OrderItemRequest` | Order item | `productId`, `quantity`, `price` |
| `Order` (Entity) | Order data | `id`, `userId`, `items`, `totalAmount`, `status`, `shippingAddress`, `paymentMethod`, `createdAt` |

### Contact Service DTOs

| DTO | Purpose | Fields |
|-----|---------|--------|
| `CreateContactMessageRequest` | Create message | `name`, `email`, `subject`, `message` |
| `ContactMessage` (Entity) | Message data | `id`, `name`, `email`, `subject`, `message`, `createdAt` |

---

## Entity Mappings

### Auth Service Entities

#### User Entity
```java
@Entity
@Table(name = "users")
Schema: auth_service
Fields:
  - id (UUID, Primary Key)
  - name (String)
  - email (String, Unique)
  - phone (String)
  - password (String, BCrypt hashed)
  - role (Role enum: ADMIN, BUYER, SUPPLIER, CEO)
  - createdAt (Instant)
```

#### Role Enum
```java
Enum: ADMIN, BUYER, SUPPLIER, CEO
```

### Product Service Entities

#### Product Entity
```java
@Entity
@Table(name = "products")
Schema: product_service
Fields:
  - id (UUID, Primary Key)
  - name (String)
  - description (TEXT)
  - category (String)
  - sku (String)
  - price (BigDecimal)
  - isActive (Boolean)
  - createdAt (Instant)
  - productImages (OneToMany -> ProductImage)
```

#### ProductImage Entity
```java
@Entity
@Table(name = "product_images")
Schema: product_service
Fields:
  - id (UUID, Primary Key)
  - productId (UUID, Foreign Key -> Product)
  - imageUrl (String)
  - isPrimary (Boolean)
```

### Supplier Service Entities

#### Supplier Entity
```java
@Entity
@Table(name = "suppliers")
Schema: supplier_service
Fields:
  - id (UUID, Primary Key)
  - name (String)
  - email (String)
  - phone (String)
  - address (String)
  - country (String)
  - certifications (String)
  - createdAt (Instant)
```

### Quote Service Entities

#### QuoteRequest Entity
```java
@Entity
@Table(name = "quote_requests")
Schema: quote_service
Fields:
  - id (UUID, Primary Key)
  - productId (UUID)
  - quantity (Integer)
  - message (TEXT)
  - status (Enum: PENDING, APPROVED, REJECTED)
  - buyerEmail (String)
  - supplierId (UUID)
  - createdAt (Instant)
```

### Order Service Entities

#### Order Entity
```java
@Entity
@Table(name = "orders")
Schema: order_service
Fields:
  - id (UUID, Primary Key)
  - userId (UUID)
  - items (OneToMany -> OrderItem)
  - totalAmount (BigDecimal)
  - status (Enum: PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED)
  - shippingAddress (String)
  - paymentMethod (String)
  - createdAt (Instant)
```

#### OrderItem Entity
```java
@Entity
@Table(name = "order_items")
Schema: order_service
Fields:
  - id (UUID, Primary Key)
  - orderId (UUID, Foreign Key -> Order)
  - productId (UUID)
  - quantity (Integer)
  - price (BigDecimal)
```

### Contact Service Entities

#### ContactMessage Entity
```java
@Entity
@Table(name = "contact_messages")
Schema: contact_service
Fields:
  - id (UUID, Primary Key)
  - name (String)
  - email (String)
  - subject (String)
  - message (TEXT)
  - createdAt (Instant)
```

---

## Service Relationships (Call Graph)

### Request Flow

```
Client (Browser)
    ↓
Nginx (Edge Service) :80
    ↓
    ├─→ Frontend Service :8081 (Static assets, React app)
    └─→ Gateway Service :8080
            ↓
            ├─→ Auth Service :8081
            │       └─→ PostgreSQL (auth_service schema)
            │
            ├─→ Product Service :8082
            │       └─→ PostgreSQL (product_service schema)
            │
            ├─→ Supplier Service :8083
            │       └─→ PostgreSQL (supplier_service schema)
            │
            ├─→ Quote Service :8084
            │       └─→ PostgreSQL (quote_service schema)
            │
            ├─→ Order Service :8085
            │       └─→ PostgreSQL (order_service schema)
            │
            └─→ Contact Service :8086
                    └─→ PostgreSQL (contact_service schema)
```

### Inter-Service Communication

**Current Architecture:** Synchronous REST (via Gateway)

| From Service | To Service | Purpose | Method |
|--------------|------------|---------|--------|
| Gateway | Auth Service | JWT validation, user lookup | HTTP |
| Gateway | All Services | Route requests | HTTP |
| Order Service | Product Service | (Future) Validate products | HTTP |
| Order Service | Quote Service | (Future) Convert quotes to orders | HTTP |

**Note:** Currently, services are independent. Future enhancements may include:
- Service-to-service calls for order validation
- Event-driven architecture with message queues
- Service discovery

### Authentication Flow

```
1. Client → Gateway → Auth Service: POST /api/auth/login
2. Auth Service validates credentials
3. Auth Service generates JWT token
4. Response: { token, role }
5. Client stores token in localStorage
6. Subsequent requests: Client → Gateway (with Authorization header)
7. Gateway validates JWT via AuthFilter
8. Gateway forwards request to target service with X-User-Email header
```

---

## Gateway Routes

### Spring Cloud Gateway Configuration

**File:** `backend/gateway/src/main/java/com/agroconnectworld/gateway/GatewayConfig.java`

**Routes (Using IP addresses due to Docker hostname limitations):**

| Route ID | Path Pattern | Target URI | Description |
|----------|--------------|------------|-------------|
| `auth-service` | `/api/auth/**` | `http://172.20.0.2:8081` | Authentication endpoints |
| `product-service` | `/api/products/**` | `http://172.20.0.10:8082` | Product management |
| `supplier-service` | `/api/suppliers/**` | `http://172.20.0.7:8083` | Supplier management |
| `quote-service` | `/api/quotes/**` | `http://172.20.0.8:8084` | Quote requests |
| `order-service` | `/api/orders/**` | `http://172.20.0.4:8085` | Order management |
| `contact-service` | `/api/contact/**` | `http://172.20.0.3:8086` | Contact messages |

### Gateway Filters

1. **CorsGlobalFilter** (Order: -1)
   - Handles CORS preflight and actual requests
   - Allows: `http://localhost:5173`, `http://localhost:5174`, `http://localhost:3000`, `http://localhost:8081`, `http://localhost:8080`
   - Sets CORS headers for all API endpoints

2. **AuthFilter** (Order: 0)
   - Validates JWT tokens for protected routes
   - Public endpoints: `/api/auth/**`, `/api/products`, `/api/contact`
   - Protected endpoints: `/api/orders`, `/api/quotes`
   - CEO-only endpoints: `/admin/ceo`
   - Extracts user email and role from JWT
   - Adds headers: `X-User-Email`, `X-User-Role`

---

## Nginx Routes

### Configuration File

**File:** `ops/nginx/default.conf`

### Routes

| Location | Proxy Target | Purpose |
|----------|--------------|---------|
| `/health` | N/A (Direct response) | Health check endpoint |
| `/` | `http://frontend_service:8081` | React frontend application |
| `/api/` | `http://gateway_service:8080` | API Gateway (all backend requests) |

### Nginx Configuration Details

```nginx
server {
  listen 80;
  server_name _;

  # Health check
  location /health {
    return 200 "ok\n";
    add_header Content-Type text/plain;
  }

  # Frontend (React app)
  location / {
    proxy_pass http://frontend_service:8081;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
  }

  # API Gateway
  location /api/ {
    proxy_pass http://gateway_service:8080;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
  }
}
```

---

## Docker Services

### Service Definitions

**File:** `ops/docker-compose.yml`

### Services Overview

| Service | Image/Build | Container Name | Ports | Networks | Profiles |
|---------|------------|----------------|-------|----------|----------|
| **nginx** | `nginx:alpine` | `edge_service` | `8080:80` | web, internal | default |
| **frontend** | Build from `../frontend` | `frontend_service` | `8081` (internal) | internal | default |
| **gateway** | Build from `../backend/gateway` | `gateway_service` | `8080` (internal) | internal | api |
| **auth-service** | Build from `../backend/auth-service` | `auth_service` | `8081` (internal) | internal | api |
| **product-service** | Build from `../backend/product-service` | `product_service` | `8082` (internal) | internal | api |
| **supplier-service** | Build from `../backend/supplier-service` | `supplier_service` | `8083` (internal) | internal | api |
| **quote-service** | Build from `../backend/quote-service` | `quote_service` | `8084` (internal) | internal | api |
| **order-service** | Build from `../backend/order-service` | `order_service` | `8085` (internal) | internal | api |
| **contact-service** | Build from `../backend/contact-service` | `contact_service` | `8086` (internal) | internal | api |
| **postgres** | `postgres:16-alpine` | `postgres_service` | `5432:5432` | internal | db |
| **redis** | `redis:7-alpine` | `redis_service` | `6379:6379` | internal | cache |
| **minio** | `quay.io/minio/minio` | `minio_service` | `9000:9000`, `9001:9001` | internal | storage |

### Docker Networks

1. **web** - External-facing network (Nginx only)
2. **internal** - Internal service communication network

### Service Dependencies

```
nginx
  ├─→ frontend
  └─→ gateway
      ├─→ postgres
      ├─→ redis
      ├─→ auth-service
      ├─→ product-service
      ├─→ supplier-service
      ├─→ quote-service
      ├─→ order-service
      └─→ contact-service
```

---

## Docker Volumes

### Volume Definitions

**File:** `ops/docker-compose.yml`

| Volume Name | Mount Point | Purpose | Service |
|-------------|-------------|---------|---------|
| `pg_data` | `/var/lib/postgresql/data` | PostgreSQL data persistence | postgres |
| `minio_data` | `/data` | MinIO object storage data | minio |

### Volume Usage

- **pg_data**: Stores all PostgreSQL database files, including all schemas (auth_service, product_service, etc.)
- **minio_data**: Stores uploaded files (product images, documents)

---

## Environment Variables

### Global Environment Variables

**File:** `ops/.env` (create from `ops/.env.example`)

| Variable | Default | Description |
|----------|---------|-------------|
| `PROJECT_NAME` | `agroconnect` | Docker Compose project name |
| `NGINX_HTTP_PORT` | `8080` | Nginx external port |
| `GATEWAY_PORT` | `8080` | Gateway service port |
| `POSTGRES_USER` | `agro` | PostgreSQL username |
| `POSTGRES_PASSWORD` | `agro_pass` | PostgreSQL password |
| `POSTGRES_DB` | `agro_master` | PostgreSQL database name |
| `POSTGRES_PORT` | `5432` | PostgreSQL external port |
| `REDIS_PORT` | `6379` | Redis external port |
| `MINIO_ROOT_USER` | `agroadmin` | MinIO root user |
| `MINIO_ROOT_PASSWORD` | `agroadminpass` | MinIO root password |
| `MINIO_PORT` | `9000` | MinIO API port |
| `MINIO_CONSOLE_PORT` | `9001` | MinIO console port |

### Service-Specific Environment Variables

#### Auth Service
- `POSTGRES_USER` - Database username
- `POSTGRES_PASSWORD` - Database password
- `POSTGRES_DB` - Database name
- `JWT_SECRET` - JWT signing secret (default: `2PSi6ESWyf1OImIRWl4FaQ1QhsTpvmU2yi0CJJGqy6Q=`)
- `JWT_EXPIRATION_MS` - Token expiration (default: 86400000 = 24 hours)

#### Gateway Service
- `SERVER_PORT` - Gateway port (default: 8080)
- `JWT_SECRET` - JWT validation secret (must match auth-service)
- `SPRING_DATASOURCE_URL` - PostgreSQL connection URL
- `SPRING_DATASOURCE_USERNAME` - Database username
- `SPRING_DATASOURCE_PASSWORD` - Database password
- `SPRING_REDIS_HOST` - Redis hostname
- `SPRING_REDIS_PORT` - Redis port

#### Frontend
- `NODE_ENV` - Node environment (production/development)
- `VITE_API_BASE_URL` - API base URL (default: `/api` in dev)

### Frontend Environment Variables

**File:** `frontend/.env`

| Variable | Description |
|----------|-------------|
| `VITE_API_BASE_URL` | API base URL (default: `/api`) |
| `VITE_OPENROUTER_API_KEY` | OpenRouter API key for chatbot |

---

## Frontend Routes

### React Router Configuration

**File:** `frontend/src/App.jsx`

### Public Routes

| Path | Component | Description |
|------|-----------|-------------|
| `/` | `Home` | Landing page |
| `/about` | `About` | About page |
| `/buyer` | `Buyer` | Buyer information page |
| `/supplier` | `Supplier` | Supplier information page |
| `/industry` | `Industry` | Industry information page |
| `/products` | `Products` | Product listing page |
| `/products/:id` | `ProductDetail` | Product detail page |
| `/news-updates` | `NewsUpdates` | News and updates page |
| `/contact` | `Contact` | Contact page |
| `/login` | `Login` | Login page |
| `/register` | `Register` | Registration page |
| `/cart` | `CartPage` | Shopping cart page |

### Protected Routes (Require Authentication)

| Path | Component | Required Role | Description |
|------|-----------|---------------|-------------|
| `/quotes` | `MyQuotes` | Any authenticated user | User's quote requests |
| `/quotes/:id` | `QuoteDetail` | Any authenticated user | Quote request details |
| `/quotes/manage` | `QuoteManagement` | `SUPPLIER` | Supplier quote management |
| `/checkout` | `Checkout` | Any authenticated user | Checkout page |
| `/orders` | `MyOrders` | Any authenticated user | User's orders |
| `/orders/:id` | `OrderDetail` | Any authenticated user | Order details |
| `/orders/:id/confirmation` | `OrderConfirmation` | Any authenticated user | Order confirmation |
| `/supplier-dashboard` | `SupplierDashboard` | `SUPPLIER` | Supplier dashboard |
| `/admin-dashboard` | `AdminDashboard` | `ADMIN` | Admin dashboard |
| `/admin/ceo` | `CEODashboard` | `CEO` | CEO executive dashboard |

### Route Protection

- **ProtectedRoute Component**: Wraps routes requiring authentication
- **Role-based Access**: Uses `requiredRoles` prop for role-based restrictions
- **Redirect**: Unauthenticated users redirected to `/login`
- **Access Denied**: Users without required role see access denied message

---

## Build Commands

### Backend Services (Maven)

#### Build All Services
```bash
# From project root
cd backend
mvn clean install -DskipTests
```

#### Build Individual Service
```bash
# Auth Service
cd backend/auth-service
mvn clean package -DskipTests

# Product Service
cd backend/product-service
mvn clean package -DskipTests

# Gateway
cd backend/gateway
mvn clean package -DskipTests

# Other services follow same pattern
```

### Frontend (NPM/Vite)

#### Development
```bash
cd frontend
npm install
npm run dev
# Runs on http://localhost:5173
```

#### Production Build
```bash
cd frontend
npm install
npm run build
# Output: frontend/dist/
```

#### Preview Production Build
```bash
cd frontend
npm run preview
# Runs on http://localhost:5173
```

### Docker Build Commands

#### Build All Services
```bash
# From ops directory
cd ops
docker compose build
```

#### Build Individual Service
```bash
# Gateway
docker build -t gateway_service:latest backend/gateway

# Auth Service
docker build -t auth_service:latest backend/auth-service

# Frontend
docker build -t frontend_service:latest frontend

# Other services follow same pattern
```

#### Build with Profiles
```bash
# Build only API services
docker compose --profile api build

# Build with database
docker compose --profile db --profile api build

# Build with cache
docker compose --profile cache --profile api build

# Build with storage
docker compose --profile storage --profile api build
```

### Docker Compose Commands

#### Start Services
```bash
cd ops
# Start all services
docker compose up -d

# Start with specific profiles
docker compose --profile api --profile db up -d
docker compose --profile api --profile db --profile cache --profile storage up -d
```

#### Stop Services
```bash
cd ops
docker compose down
```

#### View Logs
```bash
# All services
docker compose logs -f

# Specific service
docker compose logs -f gateway
docker compose logs -f auth-service
```

#### Restart Service
```bash
docker compose restart gateway
docker compose restart auth-service
```

### Database Migration

#### Manual Schema Creation
```bash
# Connect to PostgreSQL
docker exec -it postgres_service psql -U agro -d agro_master

# Create schemas
CREATE SCHEMA IF NOT EXISTS auth_service;
CREATE SCHEMA IF NOT EXISTS product_service;
CREATE SCHEMA IF NOT EXISTS supplier_service;
CREATE SCHEMA IF NOT EXISTS quote_service;
CREATE SCHEMA IF NOT EXISTS order_service;
CREATE SCHEMA IF NOT EXISTS contact_service;
```

#### Auto-Migration (Hibernate)
- Hibernate `ddl-auto=update` is enabled
- Schemas are created automatically on service startup
- Tables are created/updated based on Entity definitions

---

## Additional Notes

### Service Discovery

**Current:** Static IP addresses in Gateway routes (due to Docker hostname underscore limitation)  
**Future:** Consider implementing service discovery (Eureka, Consul) or using Docker Compose service names with hyphens

### Health Checks

All services have health check endpoints:
- **Spring Boot Services:** `/actuator/health`
- **Nginx:** `/health`
- **PostgreSQL:** `pg_isready`
- **Redis:** `redis-cli ping`
- **MinIO:** `http://localhost:9000/minio/health/ready`

### Monitoring

- **Actuator Endpoints:** Available on all Spring Boot services
- **Swagger UI:** Available on services with OpenAPI documentation
  - Auth Service: `http://localhost:8081/api/docs/swagger`
  - Product Service: `http://localhost:8082/api/docs/swagger`

### Security

- **JWT Authentication:** Used for API authentication
- **CORS:** Configured in Gateway and individual services
- **Role-Based Access:** Implemented in Gateway and Frontend
- **Password Hashing:** BCrypt (10 rounds)

### Development vs Production

- **Development:** Services run via Docker Compose with hot-reload
- **Production:** Services built as Docker images and deployed
- **Environment Variables:** Use `.env` files for configuration

---

## Document Maintenance

**Last Updated:** 2025-11-28  
**Maintained By:** AI Company System  
**Update Frequency:** On architecture changes

**To Update This Document:**
1. Run architecture scan
2. Update relevant sections
3. Verify all information is accurate
4. Commit to repository

---

**End of System Overview**



