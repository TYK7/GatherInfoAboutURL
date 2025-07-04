package com.example.companyanalyzer.service;

import com.example.companyanalyzer.dto.ExtractedWebsiteData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired; // Added Autowired
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExtractionService {

    private static final Logger logger = LoggerFactory.getLogger(ExtractionService.class);
    private static final int TIMEOUT = 10000; // 10 seconds timeout for Jsoup connection
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";

    @Autowired
    private DataCategorizationService dataCategorizationService; // Inject DataCategorizationService


    public ExtractedWebsiteData extractDataFromUrl(String url) {
        ExtractedWebsiteData extractedData = new ExtractedWebsiteData(url);
        Document doc = null; // Initialize doc to null
        try {
            logger.info("Attempting to connect to URL: {}", url);
            doc = Jsoup.connect(url)
                       .userAgent(USER_AGENT)
                       .timeout(TIMEOUT)
                       .followRedirects(true)
                       .get();
            logger.info("Successfully connected and fetched document from URL: {}", url);

            extractBasicInfo(doc, extractedData);
            extractImages(doc, extractedData);
            extractMetadata(doc, extractedData);
            extractSocialMediaLinks(doc, extractedData);
            // Future calls to extract fonts, colors, etc. would go here

        } catch (IOException e) {
            logger.error("Error connecting to or parsing URL {}: {}", url, e.getMessage(), e);
            extractedData.setTitle("Error: Could not fetch content - " + e.getMessage());
        } catch (Exception e) {
            logger.error("An unexpected error occurred during extraction from URL {}: {}", url, e.getMessage(), e);
            extractedData.setTitle("Error: Unexpected error during extraction - " + e.getMessage());
        } finally {
            // Perform categorization even if there was an error fetching the doc,
            // as some data might have been partially populated or can be inferred.
            // The categorization service itself should handle a null doc.
            if (dataCategorizationService != null) { // Ensure service is injected before calling
                 dataCategorizationService.categorizeExtractedData(doc, extractedData);
            } else {
                logger.warn("DataCategorizationService not injected. Skipping categorization for URL: {}", url);
                // Manually set defaults if service is not available, to prevent null pointer on DTO fields
                extractedData.setImageCount(0);
                extractedData.setOpenGraphTagCount(0);
                extractedData.setTwitterTagCount(0);
                extractedData.setHasFavicon(false);
            }
        }
        return extractedData;
    }

    private void extractBasicInfo(Document doc, ExtractedWebsiteData extractedData) {
        extractedData.setTitle(doc.title());
        Elements metaDescriptionTags = doc.select("meta[name=description]");
        if (!metaDescriptionTags.isEmpty()) {
            extractedData.setMetaDescription(metaDescriptionTags.first().attr("content"));
        } else {
             Elements ogDescriptionTags = doc.select("meta[property=og:description]");
             if(!ogDescriptionTags.isEmpty()){
                extractedData.setMetaDescription(ogDescriptionTags.first().attr("content"));
             }
        }
    }

    private void extractImages(Document doc, ExtractedWebsiteData extractedData) {
        // Standard <img> tags
        List<String> imageUrls = doc.select("img[src]").stream()
                .map(element -> element.attr("abs:src")) // abs:src resolves relative URLs
                .filter(src -> !src.isEmpty())
                .distinct()
                .collect(Collectors.toList());
        extractedData.setImageUrls(imageUrls);

        // OpenGraph og:image tags
        List<String> ogImageUrls = doc.select("meta[property=og:image]").stream()
                .map(element -> element.attr("abs:content")) // abs:content for meta tags
                .filter(src -> !src.isEmpty())
                .distinct()
                .collect(Collectors.toList());
        extractedData.setOgImageUrls(ogImageUrls);
    }

    private void extractMetadata(Document doc, ExtractedWebsiteData extractedData) {
        // OpenGraph Tags
        Map<String, String> openGraphTags = new HashMap<>();
        Elements ogTags = doc.select("meta[property^=og:]"); // Selects tags with property starting with "og:"
        for (Element tag : ogTags) {
            openGraphTags.put(tag.attr("property"), tag.attr("content"));
        }
        extractedData.setOpenGraphTags(openGraphTags);

        // Twitter Card Tags
        Map<String, String> twitterTags = new HashMap<>();
        Elements twTags = doc.select("meta[name^=twitter:]"); // Selects tags with name starting with "twitter:"
        for (Element tag : twTags) {
            twitterTags.put(tag.attr("name"), tag.attr("content"));
        }
        extractedData.setTwitterTags(twitterTags);
    }

    private void extractSocialMediaLinks(Document doc, ExtractedWebsiteData extractedData) {
        List<String> socialLinks = new ArrayList<>();
        // Common social media domains to look for in href attributes
        String[] socialDomains = {
                "linkedin.com/company", "linkedin.com/in",
                "twitter.com", "x.com", // x.com for twitter
                "facebook.com",
                "instagram.com",
                "youtube.com/channel", "youtube.com/user", "youtube.com" // More specific youtube paths first
        };

        Elements links = doc.select("a[href]"); // Select all anchor tags with an href attribute

        for (Element link : links) {
            String href = link.attr("abs:href").toLowerCase(); // Get absolute URL and lower case it
            if (href.isEmpty()) continue;

            for (String domain : socialDomains) {
                if (href.contains(domain)) {
                    // Basic filtering to avoid common non-profile links (can be improved)
                    if (domain.equals("twitter.com") && (href.contains("/intent/") || href.contains("/share") || href.contains("/search"))) {
                        continue;
                    }
                    if (domain.equals("facebook.com") && (href.contains("/sharer/") || href.contains("/plugins/"))) {
                        continue;
                    }
                    if (domain.equals("linkedin.com/company") && href.contains("/shareArticle?")) {
                        continue;
                    }
                     if (domain.equals("x.com") && (href.contains("/intent/") || href.contains("/share") || href.contains("/search"))) {
                        continue;
                    }

                    socialLinks.add(link.attr("abs:href")); // Add the original case URL
                    break; // Found a match, no need to check other domains for this link
                }
            }
        }
        extractedData.setSocialMediaLinks(socialLinks.stream().distinct().collect(Collectors.toList()));
    }
}
