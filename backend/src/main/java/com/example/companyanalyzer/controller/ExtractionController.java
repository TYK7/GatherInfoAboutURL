package com.example.companyanalyzer.controller;

import com.example.companyanalyzer.dto.ExtractRequest;
import com.example.companyanalyzer.dto.ExtractedWebsiteData; // Import the new DTO
import com.example.companyanalyzer.service.ExtractionService; // Import the service
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/extract")
public class ExtractionController {

    private static final Logger logger = LoggerFactory.getLogger(ExtractionController.class);

    @Autowired
    private ExtractionService extractionService; // Inject the service

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> submitUrlForExtraction(@Valid @RequestBody ExtractRequest extractRequest, Principal principal) {
        String username = principal.getName();
        logger.info("User '{}' submitted URL for extraction: {}", username, extractRequest.getUrl());

        try {
            ExtractedWebsiteData extractedData = extractionService.extractDataFromUrl(extractRequest.getUrl());
            // You might want to check if extractedData contains error indicators from the service
            if (extractedData.getTitle() != null && extractedData.getTitle().startsWith("Error:")) {
                 // If service indicates an error in the title (as per current service logic for connection errors)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(extractedData);
            }
            return ResponseEntity.ok(extractedData);
        } catch (Exception e) {
            // This catch block is for unexpected errors not handled by the service's own try-catch
            logger.error("Unexpected error in ExtractionController for URL {}: {}", extractRequest.getUrl(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(java.util.Map.of("error", "An unexpected error occurred while processing the URL.", "details", e.getMessage()));
        }
    }
}
