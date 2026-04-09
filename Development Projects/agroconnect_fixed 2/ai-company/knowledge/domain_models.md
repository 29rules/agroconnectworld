# AgroConnectWorld Domain Models

## Entity Relationships

### Core Entities

#### User
**Schema**: `auth_service`  
**Purpose**: Represents platform users

**Fields**:
- `id` (UUID): Primary key
- `name` (VARCHAR): User's full name
- `email` (VARCHAR, UNIQUE): User's email address
- `phone` (VARCHAR): User's phone number
- `password` (VARCHAR): Encrypted password (BCrypt)
- `role` (ENUM): ADMIN, BUYER, or SUPPLIER
- `created_at` (TIMESTAMP): Account creation timestamp

**Relationships**:
- One-to-many with QuoteRequest (as buyer)
- One-to-many with Order (as buyer)

#### Product
**Schema**: `product_service`  
**Purpose**: Represents agricultural products in catalog

**Fields**:
- `id` (UUID): Primary key
- `name` (VARCHAR): Product name
- `description` (TEXT): Product description
- `category` (VARCHAR): Product category (e.g., "Sesame", "Spices")
- `sku` (VARCHAR): Stock keeping unit
- `price` (DECIMAL): Product price
- `is_active` (BOOLEAN): Product availability status
- `created_at` (TIMESTAMP): Product creation timestamp

**Relationships**:
- One-to-many with ProductImage
- One-to-many with QuoteRequest
- One-to-many with OrderItem

#### ProductImage
**Schema**: `product_service`  
**Purpose**: Product images

**Fields**:
- `id` (UUID): Primary key
- `product_id` (UUID, FK): Reference to Product
- `url` (TEXT): Image URL

**Relationships**:
- Many-to-one with Product

#### Supplier
**Schema**: `supplier_service`  
**Purpose**: Represents suppliers on the platform

**Fields**:
- `id` (UUID): Primary key
- `name` (VARCHAR): Supplier company name
- `country` (VARCHAR): Supplier country
- `certifications` (TEXT): Certifications (JSON or text)
- `supply_capacity` (INT): Supply capacity
- `phone` (VARCHAR): Contact phone
- `email` (VARCHAR): Contact email

**Relationships**:
- One-to-many with Certification

#### Certification
**Schema**: `supplier_service`  
**Purpose**: Supplier certifications

**Fields**:
- `id` (UUID): Primary key
- `supplier_id` (UUID, FK): Reference to Supplier
- `certification_type` (VARCHAR): Type of certification
- `issued_date` (DATE): Certification issue date
- `expiry_date` (DATE): Certification expiry date

**Relationships**:
- Many-to-one with Supplier

#### QuoteRequest
**Schema**: `quote_service`  
**Purpose**: Buyer quote requests

**Fields**:
- `id` (UUID): Primary key
- `user_id` (UUID, FK): Reference to User (buyer)
- `product_id` (UUID, FK): Reference to Product
- `quantity` (INT): Requested quantity
- `packaging_size` (VARCHAR): Packaging size (100g, 250g, 500g, 1kg)
- `port` (VARCHAR): Shipping port
- `status` (ENUM): PENDING, REVIEWING, APPROVED, REJECTED
- `created_at` (TIMESTAMP): Request creation timestamp

**Relationships**:
- Many-to-one with User (buyer)
- Many-to-one with Product

#### Order
**Schema**: `order_service`  
**Purpose**: Customer orders

**Fields**:
- `id` (UUID): Primary key
- `user_id` (UUID, FK): Reference to User (buyer)
- `status` (ENUM): PENDING, CONFIRMED, SHIPPED, DELIVERED
- `total_amount` (DECIMAL): Total order amount
- `created_at` (TIMESTAMP): Order creation timestamp

**Relationships**:
- Many-to-one with User (buyer)
- One-to-many with OrderItem

#### OrderItem
**Schema**: `order_service`  
**Purpose**: Individual items in an order

**Fields**:
- `id` (UUID): Primary key
- `order_id` (UUID, FK): Reference to Order
- `product_id` (UUID, FK): Reference to Product
- `quantity` (INT): Item quantity
- `price` (DECIMAL): Item price at time of order

**Relationships**:
- Many-to-one with Order
- Many-to-one with Product

#### ContactMessage
**Schema**: `contact_service`  
**Purpose**: Contact form submissions

**Fields**:
- `id` (UUID): Primary key
- `name` (VARCHAR): Sender's name
- `email` (VARCHAR): Sender's email
- `message` (TEXT): Message content
- `created_at` (TIMESTAMP): Message creation timestamp

**Relationships**: None (standalone entity)

## Domain Relationships Diagram

```
User (auth_service)
  ├── 1:N QuoteRequest (quote_service)
  └── 1:N Order (order_service)

Product (product_service)
  ├── 1:N ProductImage (product_service)
  ├── 1:N QuoteRequest (quote_service)
  └── 1:N OrderItem (order_service)

Supplier (supplier_service)
  └── 1:N Certification (supplier_service)

QuoteRequest (quote_service)
  ├── N:1 User (buyer)
  └── N:1 Product

Order (order_service)
  ├── N:1 User (buyer)
  └── 1:N OrderItem

OrderItem (order_service)
  ├── N:1 Order
  └── N:1 Product
```

## Business Rules

### User Domain
- Email must be unique across all users
- Password must be encrypted (BCrypt)
- Role determines access permissions
- Users can have multiple roles (future enhancement)

### Product Domain
- Products can have multiple images
- Products can be active or inactive
- SKU must be unique
- Price is stored as DECIMAL for precision

### Quote Domain
- Quote requests are created by buyers
- Quotes require admin approval before being sent
- Quote status transitions: PENDING → REVIEWING → APPROVED/REJECTED
- Quotes reference specific products and quantities

### Order Domain
- Orders are created from approved quotes
- Order items capture product and price at time of order
- Order status progresses: PENDING → CONFIRMED → SHIPPED → DELIVERED
- Total amount is sum of all order items

### Supplier Domain
- Suppliers can have multiple certifications
- Certifications have expiry dates
- Supply capacity indicates maximum supply capability

## Data Integrity

### Cross-Schema References
- Foreign keys reference entities in other schemas
- No direct database foreign keys (enforced at application level)
- UUIDs used for all primary keys

### Constraints
- Email uniqueness enforced at application level
- Required fields validated at service layer
- Enum values validated at service layer

## Domain Events (Future)

Potential domain events for event-driven architecture:
- `UserRegistered`
- `ProductCreated`
- `QuoteRequested`
- `QuoteApproved`
- `OrderPlaced`
- `OrderShipped`
- `OrderDelivered`



