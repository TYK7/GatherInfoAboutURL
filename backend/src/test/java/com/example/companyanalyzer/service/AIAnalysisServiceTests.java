package com.example.companyanalyzer.service;

import com.example.companyanalyzer.dto.AiAnalysisResponse;
import com.example.companyanalyzer.dto.ExtractedWebsiteData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AIAnalysisServiceTests {

    @InjectMocks
    private AIAnalysisService aiAnalysisService;

    private ExtractedWebsiteData testData;

    @BeforeEach
    void setUp() {
        testData = new ExtractedWebsiteData("http://example.com");
        // Initialize with some default valid data that doesn't indicate errors
        testData.setTitle("Test Company");
        testData.setMetaDescription("A great company.");
        testData.setHasFavicon(true);
        testData.setImageCount(10);
        testData.setOpenGraphTagCount(3);
        testData.setTwitterTagCount(2);
        testData.setSocialMediaLinks(Arrays.asList("http://linkedin.com/company/testco"));
    }

    @Test
    void testGetAnalysis_SuccessfulExtractionData() {
        AiAnalysisResponse response = aiAnalysisService.getAnalysis(testData);

        assertNotNull(response);
        assertFalse(response.getPros().isEmpty(), "Should have some pros for good data.");
        // Based on current mock logic, it might produce some cons/opportunities even for "good" data.
        // For instance, if OG tags < 3 or Twitter tags < 2 by default it adds cons/opps.
        // Let's adjust setup for a "perfect" scenario based on current mock logic:
        testData.setOpenGraphTagCount(5);
        testData.setTwitterTagCount(3);

        response = aiAnalysisService.getAnalysis(testData);

        assertTrue(response.getCons().isEmpty(), "Cons should be empty for 'perfect' mock data.");
        assertTrue(response.getOpportunities().isEmpty(), "Opportunities should be empty for 'perfect' mock data.");
        assertFalse(response.getSummary().isEmpty());
    }

    @Test
    void testGetAnalysis_DataWithMissingElements() {
        testData.setMetaDescription(null); // Missing meta description
        testData.setHasFavicon(false);    // Missing favicon
        testData.setImageCount(1);       // Few images
        testData.setOpenGraphTagCount(0); // No OG tags
        testData.setTwitterTagCount(0);   // No Twitter tags
        testData.setSocialMediaLinks(Collections.emptyList()); // No social links

        AiAnalysisResponse response = aiAnalysisService.getAnalysis(testData);

        assertNotNull(response);
        assertFalse(response.getCons().isEmpty(), "Should list missing elements as cons.");
        assertFalse(response.getOpportunities().isEmpty(), "Should suggest opportunities for missing elements.");
        assertTrue(response.getPros().stream().anyMatch(p -> p.contains(testData.getTitle())));
    }

    @Test
    void testGetAnalysis_ExtractionErrorInTitle() {
        testData.setTitle("Error: Could not connect");
        AiAnalysisResponse response = aiAnalysisService.getAnalysis(testData);

        assertNotNull(response);
        assertFalse(response.getRedFlags().isEmpty(), "Should have red flags if extraction failed.");
        assertTrue(response.getSummary().contains("AI analysis could not be performed"));
        assertTrue(response.getPros().isEmpty());
        assertTrue(response.getCons().isEmpty());
        assertTrue(response.getOpportunities().isEmpty());
    }

    @Test
    void testGetAnalysis_NullTitleIndicatesError() {
        // Scenario where title is null, which the AI service logic also treats as an issue
        testData.setTitle(null);
        AiAnalysisResponse response = aiAnalysisService.getAnalysis(testData);

        assertNotNull(response);
        assertFalse(response.getRedFlags().isEmpty(), "Should have red flags if title is null (indicates extraction error).");
        assertTrue(response.getSummary().contains("AI analysis could not be performed"));
    }

    // Add more tests for specific combinations of extracted data points
    // to verify the placeholder logic in AIAnalysisService.
}
