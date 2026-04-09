package com.agroconnectworld.auth.service;

import com.agroconnectworld.auth.dto.AuthResponse;
import com.agroconnectworld.auth.dto.LoginRequest;
import com.agroconnectworld.auth.dto.ProfileResponse;
import com.agroconnectworld.auth.dto.RegisterRequest;
import com.agroconnectworld.auth.entity.Role;
import com.agroconnectworld.auth.entity.User;
import com.agroconnectworld.auth.repository.UserRepository;
import com.agroconnectworld.auth.security.JwtUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    public AuthService(UserRepository userRepository, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.encoder = new BCryptPasswordEncoder();
        this.jwtUtils = jwtUtils;
    }

    public AuthResponse register(RegisterRequest request) {
        Optional<User> existing = userRepository.findByEmail(request.getEmail());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Email already taken");
        }
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setRole(Role.valueOf(request.getRole().toUpperCase()));
        userRepository.save(user);
        String token = jwtUtils.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token, user.getRole().name());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        String token = jwtUtils.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token, user.getRole().name());
    }

    public ProfileResponse profile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return new ProfileResponse(user.getId(), user.getName(), user.getEmail(), user.getPhone(), user.getRole().name());
    }
}




