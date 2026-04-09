package com.agroconnectworld.gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        // Using environment variables with Docker service name defaults.
        // Docker service names work because containers share the "internal" network.
        // For local dev (no Docker), override via environment variables:
        //   AUTH_SERVICE_URL=http://localhost:8081, etc.
        return builder.routes()
                .route("auth-service", r -> r
                        .path("/api/auth/**")
                        .uri("${AUTH_SERVICE_URL:http://auth-service:8081}"))
                .route("product-service", r -> r
                        .path("/api/products/**")
                        .uri("${PRODUCT_SERVICE_URL:http://product-service:8082}"))
                .route("supplier-service", r -> r
                        .path("/api/suppliers/**")
                        .uri("${SUPPLIER_SERVICE_URL:http://supplier-service:8083}"))
                .route("quote-service", r -> r
                        .path("/api/quotes/**")
                        .uri("${QUOTE_SERVICE_URL:http://quote-service:8084}"))
                .route("order-service", r -> r
                        .path("/api/orders/**")
                        .uri("${ORDER_SERVICE_URL:http://order-service:8085}"))
                .route("contact-service", r -> r
                        .path("/api/contact/**")
                        .uri("${CONTACT_SERVICE_URL:http://contact-service:8086}"))
                .build();
    }
}
