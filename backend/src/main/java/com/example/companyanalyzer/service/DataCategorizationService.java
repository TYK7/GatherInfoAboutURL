package com.example.companyanalyzer.service;

import com.example.companyanalyzer.dto.ExtractedWebsiteData;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DataCategorizationService {

    private static final Logger logger = LoggerFactory.getLogger(DataCategorizationService.class);

    public void categorizeExtractedData(Document doc, ExtractedWebsiteData extractedData) {
        if (extractedData == null) {
            logger.warn("ExtractedWebsiteData is null, skipping categorization.");
            return;
        }
        if (doc == null) {
            logger.warn("JSoup Document is null for URL: {}. Skipping categorization based on document content.", extractedData.getRequestedUrl());
            // Still, we can categorize based on data already in extractedData if any
            if (extractedData.getImageUrls() != null) {
                extractedData.setImageCount(extractedData.getImageUrls().size() + (extractedData.getOgImageUrls() != null ? extractedData.getOgImageUrls().size() : 0));
            } else {
                extractedData.setImageCount(0);
            }
             if (extractedData.getOpenGraphTags() != null) {
                extractedData.setOpenGraphTagCount(extractedData.getOpenGraphTags().size());
            } else {
                extractedData.setOpenGraphTagCount(0);
            }
            if (extractedData.getTwitterTags() != null) {
                extractedData.setTwitterTagCount(extractedData.getTwitterTags().size());
            } else {
                extractedData.setTwitterTagCount(0);
            }
            extractedData.setHasFavicon(false); // Cannot determine without doc
            return;
        }


        // Image Count (already extracted, just sum them up)
        int standardImages = extractedData.getImageUrls() != null ? extractedData.getImageUrls().size() : 0;
        int ogImages = extractedData.getOgImageUrls() != null ? extractedData.getOgImageUrls().size() : 0;
        // Note: og:image might be redundant with standard images. For a simple count, this is okay.
        // For unique images, further processing would be needed.
        extractedData.setImageCount(standardImages + ogImages);

        // Meta Tag Counts
        extractedData.setOpenGraphTagCount(extractedData.getOpenGraphTags() != null ? extractedData.getOpenGraphTags().size() : 0);
        extractedData.setTwitterTagCount(extractedData.getTwitterTags() != null ? extractedData.getTwitterTags().size() : 0);

        // Check for Favicon
        // Common rel values for favicons: "icon", "shortcut icon", "apple-touch-icon"
        Elements faviconLinks = doc.select("link[rel~=(?i)\\bicon\\b|\\bshortcut icon\\b|\\bapple-touch-icon\\b]");
        extractedData.setHasFavicon(!faviconLinks.isEmpty());

        // Placeholder for more complex categorizations:
        // - Color palette analysis (would require image processing or CSS parsing)
        // - Font analysis (CSS parsing)
        // - Brand tone (NLP on text content)
        // - Social media presence strength (requires actual social media data, not just links)
        // - Website structure/UX (complex, heuristic-based)

        logger.info("Basic categorization complete for URL: {}", extractedData.getRequestedUrl());
    }
}
