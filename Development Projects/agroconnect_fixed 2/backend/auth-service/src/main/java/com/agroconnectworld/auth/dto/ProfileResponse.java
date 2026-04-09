package com.agroconnectworld.auth.dto;

import java.util.UUID;

public class ProfileResponse {
    private UUID id;
    private String name;
    private String email;
    private String phone;
    private String role;

    public ProfileResponse(UUID id, String name, String email, String phone, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

    public UUID getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public String getPhone() {
        return phone;
    }
    public String getRole() {
        return role;
    }
}




