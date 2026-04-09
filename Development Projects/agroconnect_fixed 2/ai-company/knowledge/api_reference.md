# AgroConnectWorld API Reference

## Base URLs

- **Development**: `http://localhost:8080/api`
- **Production**: `https://api.agroconnectworld.com/api` (future)

## Authentication

All protected endpoints require JWT authentication. Include the token in the Authorization header:

```
Authorization: Bearer <token>
```

## Common Response Formats

### Success Response
```json
{
  "data": { ... },
  "message": "Success message"
}
```

### Error Response
```json
{
  "error": "Error message",
  "code": "ERROR_CODE",
  "timestamp": "2024-11-27T20:00:00Z"
}
```

## Auth Service API

### Register User
**Endpoint**: `POST /api/auth/register`  
**Authentication**: Not required

**Request Body**:
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "+1234567890",
  "password": "securePassword123",
  "role": "BUYER"
}
```

**Response**: `201 Created`
```json
{
  "id": "uuid",
  "name": "John Doe",
  "email": "john@example.com",
  "role": "BUYER"
}
```

### Login
**Endpoint**: `POST /api/auth/login`  
**Authentication**: Not required

**Request Body**:
```json
{
  "email": "john@example.com",
  "password": "securePassword123"
}
```

**Response**: `200 OK`
```json
{
  "token": "jwt_token_here",
  "user": {
    "id": "uuid",
    "name": "John Doe",
    "email": "john@example.com",
    "role": "BUYER"
  }
}
```

### Get Profile
**Endpoint**: `GET /api/auth/profile`  
**Authentication**: Required

**Response**: `200 OK`
```json
{
  "id": "uuid",
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "+1234567890",
  "role": "BUYER"
}
```

## Product Service API

### List Products
**Endpoint**: `GET /api/products`  
**Authentication**: Not required

**Query Parameters**:
- `category` (optional): Filter by category
- `isActive` (optional): Filter by active status

**Response**: `200 OK`
```json
[
  {
    "id": "uuid",
    "name": "Organic Sesame Seeds",
    "description": "Premium organic sesame seeds",
    "category": "Sesame",
    "sku": "SES-001",
    "price": 15.99,
    "isActive": true,
    "images": [
      {
        "id": "uuid",
        "url": "https://example.com/image.jpg"
      }
    ],
    "createdAt": "2024-11-27T20:00:00Z"
  }
]
```

### Get Product
**Endpoint**: `GET /api/products/{id}`  
**Authentication**: Not required

**Response**: `200 OK`
```json
{
  "id": "uuid",
  "name": "Organic Sesame Seeds",
  "description": "Premium organic sesame seeds",
  "category": "Sesame",
  "sku": "SES-001",
  "price": 15.99,
  "isActive": true,
  "images": [...],
  "createdAt": "2024-11-27T20:00:00Z"
}
```

### Create Product
**Endpoint**: `POST /api/products`  
**Authentication**: Required (ADMIN only)

**Request Body**:
```json
{
  "name": "Organic Sesame Seeds",
  "description": "Premium organic sesame seeds",
  "category": "Sesame",
  "sku": "SES-001",
  "price": 15.99,
  "isActive": true,
  "images": [
    {
      "url": "https://example.com/image.jpg"
    }
  ]
}
```

**Response**: `201 Created`

## Supplier Service API

### List Suppliers
**Endpoint**: `GET /api/suppliers`  
**Authentication**: Not required

**Response**: `200 OK`
```json
[
  {
    "id": "uuid",
    "name": "AgriSupply Co.",
    "country": "India",
    "certifications": "Organic, ISO 9001",
    "supplyCapacity": 10000,
    "phone": "+91-1234567890",
    "email": "contact@agrisupply.com"
  }
]
```

### Get Supplier
**Endpoint**: `GET /api/suppliers/{id}`  
**Authentication**: Not required

**Response**: `200 OK`
```json
{
  "id": "uuid",
  "name": "AgriSupply Co.",
  "country": "India",
  "certifications": "Organic, ISO 9001",
  "supplyCapacity": 10000,
  "phone": "+91-1234567890",
  "email": "contact@agrisupply.com",
  "certificationList": [...]
}
```

### Create Supplier
**Endpoint**: `POST /api/suppliers`  
**Authentication**: Required

**Request Body**:
```json
{
  "name": "AgriSupply Co.",
  "country": "India",
  "certifications": "Organic, ISO 9001",
  "supplyCapacity": 10000,
  "phone": "+91-1234567890",
  "email": "contact@agrisupply.com"
}
```

**Response**: `201 Created`

## Quote Service API

### List Quote Requests
**Endpoint**: `GET /api/quotes`  
**Authentication**: Required

**Response**: `200 OK`
```json
[
  {
    "id": "uuid",
    "userId": "uuid",
    "productId": "uuid",
    "quantity": 1000,
    "packagingSize": "1kg",
    "port": "Mumbai",
    "status": "PENDING",
    "createdAt": "2024-11-27T20:00:00Z"
  }
]
```

### Create Quote Request
**Endpoint**: `POST /api/quotes`  
**Authentication**: Required (BUYER)

**Request Body**:
```json
{
  "productId": "uuid",
  "quantity": 1000,
  "packagingSize": "1kg",
  "port": "Mumbai"
}
```

**Response**: `201 Created`

### Update Quote Status
**Endpoint**: `PATCH /api/quotes/{id}/status`  
**Authentication**: Required (ADMIN)

**Request Body**:
```json
{
  "status": "APPROVED"
}
```

**Response**: `200 OK`

## Order Service API

### List Orders
**Endpoint**: `GET /api/orders`  
**Authentication**: Required

**Response**: `200 OK`
```json
[
  {
    "id": "uuid",
    "userId": "uuid",
    "status": "PENDING",
    "totalAmount": 15990.00,
    "items": [
      {
        "id": "uuid",
        "productId": "uuid",
        "quantity": 1000,
        "price": 15.99
      }
    ],
    "createdAt": "2024-11-27T20:00:00Z"
  }
]
```

### Create Order
**Endpoint**: `POST /api/orders`  
**Authentication**: Required (BUYER)

**Request Body**:
```json
{
  "items": [
    {
      "productId": "uuid",
      "quantity": 1000,
      "price": 15.99
    }
  ]
}
```

**Response**: `201 Created`

## Contact Service API

### List Contact Messages
**Endpoint**: `GET /api/contact`  
**Authentication**: Required (ADMIN)

**Response**: `200 OK`
```json
[
  {
    "id": "uuid",
    "name": "John Doe",
    "email": "john@example.com",
    "message": "I have a question about...",
    "createdAt": "2024-11-27T20:00:00Z"
  }
]
```

### Submit Contact Message
**Endpoint**: `POST /api/contact`  
**Authentication**: Not required

**Request Body**:
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "message": "I have a question about..."
}
```

**Response**: `201 Created`

## Error Codes

- `AUTH_REQUIRED`: Authentication required
- `AUTH_INVALID`: Invalid authentication token
- `AUTH_EXPIRED`: Authentication token expired
- `FORBIDDEN`: Insufficient permissions
- `NOT_FOUND`: Resource not found
- `VALIDATION_ERROR`: Request validation failed
- `SERVER_ERROR`: Internal server error

## Rate Limiting

Rate limiting will be implemented in the future:
- 100 requests per minute per IP
- 1000 requests per hour per user

## API Versioning

API versioning will be implemented in the future:
- Current version: v1 (implicit)
- Future versions: `/api/v2/...`

## OpenAPI Documentation

All services provide OpenAPI documentation:
- OpenAPI JSON: `/api/docs/openapi`
- Swagger UI: `/api/docs/swagger`



