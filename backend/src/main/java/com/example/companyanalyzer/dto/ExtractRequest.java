package com.example.companyanalyzer.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL; // For URL validation

public class ExtractRequest {

    @NotBlank(message = "URL cannot be blank")
    @URL(message = "Invalid URL format")
    private String url;

    public ExtractRequest() {
    }

    public ExtractRequest(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
