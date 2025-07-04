package com.example.companyanalyzer.dto;

// Using jakarta.validation if available, or just plain POJO
// For Spring Boot 3+, jakarta.validation-api is part of spring-boot-starter-web
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
