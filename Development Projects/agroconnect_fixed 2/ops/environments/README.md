# Environment Configurations

This directory contains Docker Compose configurations for different deployment environments.

## Environments

### 1. Development (`dev/`)
- **Purpose**: Local development and testing
- **Ports**: 
  - Frontend: 8080 (via nginx)
  - Postgres: 5432
  - Redis: 6379
- **Features**:
  - Hot reload enabled
  - Debug logging
  - Development database
  - Default credentials (change in production)

**Usage:**
```bash
cd ops/environments/dev
docker compose -f docker-compose.dev.yml up -d
```

### 2. UAT (`uat/`)
- **Purpose**: User Acceptance Testing
- **Ports**:
  - Frontend: 8080 (via nginx)
  - Postgres: 5433
  - Redis: 6380
- **Features**:
  - Production-like configuration
  - Separate database
  - UAT-specific environment variables

**Usage:**
```bash
cd ops/environments/uat
# Set environment variables in .env file
docker compose -f docker-compose.uat.yml up -d
```

### 3. Pre-Production (`preprod/`)
- **Purpose**: Final testing before production
- **Ports**:
  - Frontend: 8080 (via nginx)
  - Postgres: 5434
  - Redis: 6381
- **Features**:
  - Production configuration
  - SSL/TLS ready
  - Performance testing environment
  - Separate database

**Usage:**
```bash
cd ops/environments/preprod
# Set environment variables in .env file
docker compose -f docker-compose.preprod.yml up -d
```

### 4. Production (`production/`)
- **Purpose**: Live production environment
- **Ports**:
  - HTTP: 80
  - HTTPS: 443
  - Postgres: Internal only
  - Redis: Internal only
- **Features**:
  - SSL/TLS enabled
  - Production database
  - High availability
  - Monitoring and logging
  - Backups configured

**Usage:**
```bash
cd ops/environments/production
# Set environment variables in .env file
# Ensure SSL certificates are in ./ssl directory
docker compose -f docker-compose.prod.yml up -d
```

## Environment Variables

Each environment requires a `.env` file with the following variables:

```env
# Database
DB_USERNAME=your_db_username
DB_PASSWORD=your_secure_password

# JWT
JWT_SECRET=your_secure_jwt_secret_key

# API Keys
VITE_OPENROUTER_API_KEY=your_openrouter_api_key

# URLs (for UAT, Preprod, Production)
VITE_API_BASE_URL=https://your-domain.com/api
UAT_URL=https://uat.your-domain.com
PREPROD_URL=https://preprod.your-domain.com
PROD_URL=https://your-domain.com
```

## Testing

The AI Company's WebsiteTestAgent can test all environments:

```bash
# Test all environments
cd ai-company
mvn exec:java -Dexec.mainClass="com.ai.company.tests.TestWebsiteComprehensive"

# Test specific environment
# Set environment variables and run tests
```

## Deployment Workflow

1. **Development** → Local development and feature testing
2. **UAT** → User acceptance testing
3. **Pre-Production** → Final validation and performance testing
4. **Production** → Live deployment

## Security Notes

- Never commit `.env` files to git
- Use strong passwords and secrets in production
- Rotate JWT secrets regularly
- Enable SSL/TLS in production
- Configure firewall rules appropriately
- Regular security audits

## Monitoring

Each environment includes:
- Health checks for all services
- Container status monitoring
- Log aggregation (configure separately)
- Performance metrics (configure separately)



