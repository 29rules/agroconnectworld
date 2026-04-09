package com.agroconnectworld.gateway;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * Authentication Filter for Spring Cloud Gateway
 * 
 * Validates JWT tokens for protected endpoints.
 * Protected endpoints: /api/orders, /api/quotes (except GET for quotes)
 */
@Component
public class AuthFilter implements GlobalFilter, Ordered {

    @Value("${jwt.secret:2PSi6ESWyf1OImIRWl4FaQ1QhsTpvmU2yi0CJJGqy6Q=}")
    private String jwtSecret;

    // Public endpoints that don't require authentication
    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
        "/api/auth/",
        "/api/products",
        "/api/contact",
        "/actuator/"
    );
    
    // Note: /api/auth/promote-ceo is public for testing purposes
    
    // Note: /api/auth/promote-ceo should be public for initial setup
    // In production, this should be protected or removed

    // Protected endpoints that require authentication
    private static final List<String> PROTECTED_PATTERNS = Arrays.asList(
        "/api/orders",
        "/api/quotes" // Quotes might need auth for POST/PUT/DELETE
    );

    // CEO-only endpoints that require CEO role
    private static final List<String> CEO_ONLY_PATTERNS = Arrays.asList(
        "/admin/ceo"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // MANDATORY: Bypass JWT filter for all auth endpoints (register, login, refresh)
        // These endpoints don't have tokens yet, so they MUST be allowed through
        if (path.startsWith("/api/auth/")) {
            return chain.filter(exchange);
        }

        // Skip authentication for public endpoints
        if (isPublicEndpoint(path)) {
            return chain.filter(exchange);
        }

        // Check if this is a CEO-only endpoint
        if (isCeoOnlyEndpoint(path)) {
            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return handleForbidden(exchange, "Missing or invalid Authorization header");
            }

            String token = authHeader.substring(7); // Remove "Bearer " prefix

            try {
                // Validate JWT token and check CEO role
                Claims claims = validateTokenAndGetClaims(token);
                if (claims == null) {
                    return handleForbidden(exchange, "Invalid or expired token");
                }

                // Check if user has CEO role
                String role = claims.get("role", String.class);
                if (role == null || !role.equals("CEO")) {
                    return handleForbidden(exchange, "Access denied. CEO role required.");
                }

                // Add user info to request headers for downstream services
                ServerHttpRequest modifiedRequest = request.mutate()
                        .header("X-User-Email", claims.getSubject())
                        .header("X-User-Role", role)
                        .build();

                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            } catch (Exception e) {
                return handleForbidden(exchange, "Token validation failed: " + e.getMessage());
            }
        }

        // Check if this is a protected endpoint
        if (isProtectedEndpoint(path)) {
            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return handleUnauthorized(exchange, "Missing or invalid Authorization header");
            }

            String token = authHeader.substring(7); // Remove "Bearer " prefix

            try {
                // Validate JWT token
                Claims claims = validateTokenAndGetClaims(token);
                if (claims == null) {
                    return handleUnauthorized(exchange, "Invalid or expired token");
                }

                // Add user info to request headers for downstream services
                ServerHttpRequest modifiedRequest = request.mutate()
                        .header("X-User-Email", claims.getSubject())
                        .header("X-User-Role", claims.get("role", String.class))
                        .build();

                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            } catch (Exception e) {
                return handleUnauthorized(exchange, "Token validation failed: " + e.getMessage());
            }
        }

        // Not a protected endpoint, continue
        return chain.filter(exchange);
    }

    private boolean isPublicEndpoint(String path) {
        return PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith);
    }

    private boolean isProtectedEndpoint(String path) {
        return PROTECTED_PATTERNS.stream().anyMatch(path::startsWith);
    }

    private boolean isCeoOnlyEndpoint(String path) {
        return CEO_ONLY_PATTERNS.stream().anyMatch(path::startsWith);
    }

    private Claims validateTokenAndGetClaims(String token) {
        try {
            Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            // Using 0.11.x API (matching auth-service)
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
            
            return claims;
        } catch (Exception e) {
            return null;
        }
    }

    private Mono<Void> handleUnauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");
        
        String body = "{\"error\":\"Unauthorized\",\"message\":\"" + message + "\"}";
        return response.writeWith(
            Mono.just(response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8)))
        );
    }

    private Mono<Void> handleForbidden(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");
        
        String body = "{\"error\":\"Forbidden\",\"message\":\"" + message + "\"}";
        return response.writeWith(
            Mono.just(response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8)))
        );
    }

    @Override
    public int getOrder() {
        return 0; // Run after CORS filter but before routing
    }
}

