package com.agroconnectworld.auth.controller;

import com.agroconnectworld.auth.dto.AuthResponse;
import com.agroconnectworld.auth.dto.LoginRequest;
import com.agroconnectworld.auth.dto.ProfileResponse;
import com.agroconnectworld.auth.dto.RegisterRequest;
import com.agroconnectworld.auth.entity.Role;
import com.agroconnectworld.auth.entity.User;
import com.agroconnectworld.auth.repository.UserRepository;
import com.agroconnectworld.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> profile(@RequestHeader("X-User-Email") String email) {
        return ResponseEntity.ok(authService.profile(email));
    }

    /**
     * Endpoint to promote a user to CEO role.
     * 
     * SECURITY: Requires ADMIN_SECRET header matching the configured secret.
     * Set ADMIN_SECRET environment variable to a strong random value.
     * 
     * Usage: POST /api/auth/promote-ceo?email=user@example.com
     *        Header: X-Admin-Secret: <your-secret>
     */
    @PostMapping("/promote-ceo")
    public ResponseEntity<Map<String, String>> promoteToCeo(
            @RequestParam String email,
            @RequestHeader(value = "X-Admin-Secret", required = false) String adminSecret) {

        // Validate admin secret
        String requiredSecret = System.getenv().getOrDefault("ADMIN_SECRET", "");
        if (requiredSecret.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Admin secret not configured");
            error.put("message", "Set ADMIN_SECRET environment variable to enable this endpoint");
            return ResponseEntity.status(503).body(error);
        }
        if (!requiredSecret.equals(adminSecret)) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Unauthorized");
            error.put("message", "Invalid or missing X-Admin-Secret header");
            return ResponseEntity.status(401).body(error);
        }

        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "User not found");
            error.put("message", "No user found with email: " + email);
            return ResponseEntity.badRequest().body(error);
        }
        
        User user = userOpt.get();
        user.setRole(Role.CEO);
        userRepository.save(user);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "User promoted to CEO successfully");
        response.put("email", email);
        response.put("role", "CEO");
        
        return ResponseEntity.ok(response);
    }
}


