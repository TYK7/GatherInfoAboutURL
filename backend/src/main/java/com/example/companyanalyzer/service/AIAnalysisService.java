package com.example.companyanalyzer.service;

import com.example.companyanalyzer.dto.AiAnalysisResponse;
import com.example.companyanalyzer.dto.ExtractedWebsiteData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List; // Import List
import java.util.ArrayList; // Import ArrayList

@Service
public class AIAnalysisService {

    private static final Logger logger = LoggerFactory.getLogger(AIAnalysisService.class);

    /**
     * Generates a placeholder AI analysis based on the extracted website data.
     * In a real application, this method would interact with an external AI service.
     *
     * @param extractedData The data extracted from the website.
     * @return An AiAnalysisResponse containing the mock analysis.
     */
    public AiAnalysisResponse getAnalysis(ExtractedWebsiteData extractedData) {
        logger.info("Generating placeholder AI analysis for URL: {}", extractedData.getRequestedUrl());

        // Mock analysis based on some extracted data points
        List<String> pros = new ArrayList<>();
        List<String> cons = new ArrayList<>();
        List<String> opportunities = new ArrayList<>();
        List<String> redFlags = new ArrayList<>();
        String summary;

        if (extractedData.getTitle() == null || extractedData.getTitle().toLowerCase().contains("error:")) {
            summary = "AI analysis could not be performed due to issues fetching website data.";
            redFlags.add("Website data extraction failed or was incomplete.");
            return new AiAnalysisResponse(pros, cons, opportunities, redFlags, summary);
        }

        pros.add("Company has an online presence with a website titled: '" + extractedData.getTitle() + "'.");
        if (extractedData.getMetaDescription() != null && !extractedData.getMetaDescription().isEmpty()) {
            pros.add("Website includes a meta description, good for SEO.");
        } else {
            cons.add("Website is missing a meta description, which can negatively impact SEO.");
        }

        if (extractedData.getHasFavicon() != null && extractedData.getHasFavicon()) {
            pros.add("Website has a favicon, improving brand recognition in browser tabs.");
        } else {
            cons.add("Website is missing a favicon.");
        }

        if (extractedData.getImageCount() != null && extractedData.getImageCount() > 5) {
            pros.add("Website contains multiple images (" + extractedData.getImageCount() + "), suggesting visual content.");
        } else if (extractedData.getImageCount() != null && extractedData.getImageCount() < 2 && extractedData.getImageCount() >0) {
            cons.add("Website has very few images ("+ extractedData.getImageCount() +"), potentially lacking visual appeal or content.");
        } else if (extractedData.getImageCount() != null && extractedData.getImageCount() == 0) {
             cons.add("Website does not seem to contain any images.");
        }


        if (extractedData.getOpenGraphTagCount() != null && extractedData.getOpenGraphTagCount() > 2) {
            pros.add("Good use of OpenGraph tags (" + extractedData.getOpenGraphTagCount() + ") for social media sharing.");
        } else {
            cons.add("Limited use of OpenGraph tags, potentially impacting social media preview quality.");
            opportunities.add("Enhance OpenGraph tags (og:title, og:description, og:image) for better social media sharing.");
        }

        if (extractedData.getTwitterTagCount() != null && extractedData.getTwitterTagCount() > 1) {
            pros.add("Twitter card tags are present, good for Twitter sharing.");
        } else {
            opportunities.add("Implement Twitter card meta tags for optimized sharing on Twitter/X.");
        }

        if (extractedData.getSocialMediaLinks() != null && !extractedData.getSocialMediaLinks().isEmpty()) {
            pros.add("Links to social media profiles detected, indicating social presence.");
            opportunities.add("Verify and leverage identified social media channels (" + String.join(", ", extractedData.getSocialMediaLinks()) + ") for engagement.");
        } else {
            cons.add("No clear links to social media profiles found on the homepage. This might indicate a weak social media strategy or poor website navigation to these assets.");
            opportunities.add("If social media profiles exist, ensure they are clearly linked from the website.");
        }

        summary = "This is a mock AI summary. The company appears to have a basic online presence. " +
                  "Key areas for improvement include optimizing meta tags and ensuring clear social media profile linkage.";

        // For placeholder, ensure lists are not null if empty
        return new AiAnalysisResponse(
                pros.isEmpty() ? Collections.emptyList() : pros,
                cons.isEmpty() ? Collections.emptyList() : cons,
                opportunities.isEmpty() ? Collections.emptyList() : opportunities,
                redFlags.isEmpty() ? Collections.emptyList() : redFlags,
                summary
        );
    }
}
