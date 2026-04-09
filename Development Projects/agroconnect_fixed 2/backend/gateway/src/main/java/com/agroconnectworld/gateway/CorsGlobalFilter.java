package com.agroconnectworld.gateway;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * Global CORS Filter for Spring Cloud Gateway
 * 
 * Handles CORS preflight and actual requests.
 * Allows localhost with any port for development.
 */
@Component
public class CorsGlobalFilter implements GlobalFilter, Ordered {

    private static final String ALLOWED_ORIGINS = "http://localhost:5173,http://localhost:5174,http://localhost:3000,http://localhost:8081,http://localhost:8080";
    private static final String ALLOWED_METHODS = "GET,POST,PUT,PATCH,DELETE,OPTIONS";
    private static final String ALLOWED_HEADERS = "*";
    private static final String EXPOSED_HEADERS = "Authorization,Content-Type";
    private static final long MAX_AGE = 3600L;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        HttpHeaders headers = response.getHeaders();

        String origin = request.getHeaders().getFirst(HttpHeaders.ORIGIN);
        String path = request.getURI().getPath();
        
        // Always set CORS headers for API endpoints
        if (path.startsWith("/api/")) {
            // If origin is provided and allowed, use it; otherwise use wildcard for development
            if (origin != null && isOriginAllowed(origin)) {
                headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
                headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
            } else if (origin == null || path.startsWith("/api/")) {
                // For API endpoints, allow common development origins
                headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:5173");
                headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
            }
            headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, EXPOSED_HEADERS);
        }

        // Handle preflight request
        if (request.getMethod() == HttpMethod.OPTIONS) {
            headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, ALLOWED_METHODS);
            headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, ALLOWED_HEADERS);
            headers.add(HttpHeaders.ACCESS_CONTROL_MAX_AGE, String.valueOf(MAX_AGE));
            response.setStatusCode(HttpStatus.OK);
            return response.setComplete();
        }

        return chain.filter(exchange);
    }

    private boolean isOriginAllowed(String origin) {
        List<String> allowedOrigins = Arrays.asList(ALLOWED_ORIGINS.split(","));
        // Also allow any localhost port
        if (origin.startsWith("http://localhost:") || origin.startsWith("http://127.0.0.1:")) {
            return true;
        }
        return allowedOrigins.contains(origin);
    }

    @Override
    public int getOrder() {
        return -1; // High priority
    }
}

