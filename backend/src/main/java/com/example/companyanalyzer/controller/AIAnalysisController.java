package com.example.companyanalyzer.controller;

import com.example.companyanalyzer.dto.AiAnalysisResponse;
import com.example.companyanalyzer.dto.ExtractedWebsiteData;
import com.example.companyanalyzer.service.AIAnalysisService;
import jakarta.validation.Valid; // For validating the input DTO
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/analyze")
public class AIAnalysisController {

    private static final Logger logger = LoggerFactory.getLogger(AIAnalysisController.class);

    @Autowired
    private AIAnalysisService aiAnalysisService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AiAnalysisResponse> analyzeCompanyData(
            @Valid @RequestBody ExtractedWebsiteData extractedData, Principal principal) {

        String username = principal.getName();
        logger.info("User '{}' requested AI analysis for data from URL: {}", username, extractedData.getRequestedUrl());

        // In a real application, you might want to validate if extractedData is complete enough
        // or if it indicates previous extraction errors before proceeding.
        if (extractedData.getTitle() != null && extractedData.getTitle().toLowerCase().contains("error:")) {
            logger.warn("Attempting AI analysis on data that indicates prior extraction error for URL: {}", extractedData.getRequestedUrl());
            // Proceeding, as the AI service placeholder can also handle this
        }

        AiAnalysisResponse analysisResult = aiAnalysisService.getAnalysis(extractedData);

        return ResponseEntity.ok(analysisResult);
    }
}
