#!/bin/bash
# Health Check Script for Deployment Validation
# Usage: ./health-check.sh <environment> <service>

set -e

ENV=${1:-production}
SERVICE=${2:-all}
TIMEOUT=${3:-60}
INTERVAL=5

echo "=== Health Check ==="
echo "Environment: $ENV"
echo "Service: $SERVICE"
echo "Timeout: ${TIMEOUT}s"
echo ""

# Check service health
check_service_health() {
    local service=$1
    local port=$2
    local max_attempts=$((TIMEOUT / INTERVAL))
    local attempt=0
    
    while [ $attempt -lt $max_attempts ]; do
        if curl -f -s "http://localhost:$port/actuator/health" > /dev/null 2>&1; then
            echo "✅ $service is healthy"
            return 0
        fi
        
        attempt=$((attempt + 1))
        echo "⏳ Attempt $attempt/$max_attempts: $service not ready yet..."
        sleep $INTERVAL
    done
    
    echo "❌ Health check failed for $service after ${TIMEOUT}s"
    return 1
}

# Check all services
check_all_services() {
    local all_healthy=true
    
    services=(
        "gateway:8080"
        "auth-service:8081"
        "product-service:8082"
        "supplier-service:8083"
        "quote-service:8084"
        "order-service:8085"
        "contact-service:8086"
    )
    
    for service_port in "${services[@]}"; do
        IFS=':' read -r service port <<< "$service_port"
        if ! check_service_health "$service" "$port"; then
            all_healthy=false
        fi
    done
    
    if [ "$all_healthy" = true ]; then
        echo "✅ All services are healthy"
        return 0
    else
        echo "❌ Some services are unhealthy"
        return 1
    fi
}

# Main
if [ "$SERVICE" = "all" ]; then
    check_all_services
else
    PORT=$(case $SERVICE in
        gateway) echo "8080" ;;
        auth-service) echo "8081" ;;
        product-service) echo "8082" ;;
        supplier-service) echo "8083" ;;
        quote-service) echo "8084" ;;
        order-service) echo "8085" ;;
        contact-service) echo "8086" ;;
        *) echo "8080" ;;
    esac)
    check_service_health "$SERVICE" "$PORT"
fi



