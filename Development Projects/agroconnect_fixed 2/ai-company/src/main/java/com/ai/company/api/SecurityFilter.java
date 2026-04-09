package com.ai.company.api;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.List;

/**
 * Security Filter for AI Company API
 * 
 * Validates JWT tokens and ensures CEO role for protected endpoints.
 * All /ai/** endpoints require CEO role.
 */
@Component
@Order(1)
public class SecurityFilter extends OncePerRequestFilter {
    
    private static final Logger log = LoggerFactory.getLogger(SecurityFilter.class);
    
    @Value("${jwt.secret:${JWT_SECRET:2PSi6ESWyf1OImIRWl4FaQ1QhsTpvmU2yi0CJJGqy6Q=}}")
    private String jwtSecret;
    
    // Public endpoints (if any)
    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
        "/actuator/health",
        "/actuator/info"
    );
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        
        String path = request.getRequestURI();

        //Allow public CTO  test endpoint
        if (path.equals("/ai/ctochat/test")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Handle CORS preflight requests - MUST be before authentication
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            String origin = request.getHeader("Origin");
            if (origin != null && (origin.contains("localhost:5173") || origin.contains("localhost:5174") || 
                origin.contains("localhost:3000") || origin.contains("localhost:8080"))) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.setHeader("Access-Control-Allow-Origin", origin);
                response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH");
                response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, X-Requested-With");
                response.setHeader("Access-Control-Allow-Credentials", "true");
                response.setHeader("Access-Control-Max-Age", "3600");
                return;
            }
        }
        
        // Allow public endpoints
        if (isPublicEndpoint(path)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // All /ai/** endpoints require CEO role
        if (path.startsWith("/ai/")) {
            String authHeader = request.getHeader("Authorization");
            
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, 
                    "Missing or invalid Authorization header");
                return;
            }
            
            String token = authHeader.substring(7);
            
            try {
                Claims claims = validateTokenAndGetClaims(token);
                if (claims == null) {
                    sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, 
                        "Invalid or expired token");
                    return;
                }
                
                // Check CEO role
                String role = claims.get("role", String.class);
                if (role == null || !role.equals("CEO")) {
                    sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, 
                        "Access denied. CEO role required.");
                    return;
                }
                
                // Add user info to request attributes for controllers
                request.setAttribute("userEmail", claims.getSubject());
                request.setAttribute("userRole", role);
                
                log.debug("CEO access granted for: {}", claims.getSubject());
                
            } catch (Exception e) {
                log.error("Token validation failed", e);
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, 
                    "Token validation failed: " + e.getMessage());
                return;
            }
        }
        
        // Add CORS headers to all responses
        String origin = request.getHeader("Origin");
        if (origin != null && (origin.contains("localhost:5173") || origin.contains("localhost:5174") || 
            origin.contains("localhost:3000") || origin.contains("localhost:8080"))) {
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Access-Control-Allow-Credentials", "true");
        }
        
        filterChain.doFilter(request, response);
    }
    
    private boolean isPublicEndpoint(String path) {
        return PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith);
    }
    
    private Claims validateTokenAndGetClaims(String token) {
        try {
            Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
            
            return claims;
        } catch (Exception e) {
            log.debug("Token validation failed: {}", e.getMessage());
            return null;
        }
    }
    
    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.format(
            "{\"error\":true,\"status\":%d,\"message\":\"%s\"}", status, message));
    }
}

