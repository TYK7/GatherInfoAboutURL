package com.example.companyanalyzer.service;

import com.example.companyanalyzer.dto.ExtractedWebsiteData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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
public class DataCategorizationServiceTests {

    @InjectMocks
    private DataCategorizationService dataCategorizationService;

    private ExtractedWebsiteData testData;
    private Document mockDoc;

    @BeforeEach
    void setUp() {
        testData = new ExtractedWebsiteData("http://example.com");
        // Basic mock document, specific tests can add more elements
        mockDoc = Jsoup.parse("<html><head><title>Test</title></head><body></body></html>", "http://example.com");
    }

    @Test
    void testCategorizeExtractedData_Counts() {
        testData.setImageUrls(Arrays.asList("img1.jpg", "img2.jpg"));
        testData.setOgImageUrls(Collections.singletonList("ogimg1.jpg")); // Total 3 images

        HashMap<String, String> ogTags = new HashMap<>();
        ogTags.put("og:title", "Test OG Title");
        ogTags.put("og:type", "website");
        testData.setOpenGraphTags(ogTags); // 2 OG tags

        HashMap<String, String> twitterTags = new HashMap<>();
        twitterTags.put("twitter:card", "summary");
        testData.setTwitterTags(twitterTags); // 1 Twitter tag

        dataCategorizationService.categorizeExtractedData(mockDoc, testData);

        assertEquals(3, testData.getImageCount());
        assertEquals(2, testData.getOpenGraphTagCount());
        assertEquals(1, testData.getTwitterTagCount());
    }

    @Test
    void testCategorizeExtractedData_FaviconPresent() {
        mockDoc.head().append("<link rel=\"icon\" href=\"favicon.ico\">");
        dataCategorizationService.categorizeExtractedData(mockDoc, testData);
        assertTrue(testData.getHasFavicon());
    }

    @Test
    void testCategorizeExtractedData_FaviconShortcutIcon() {
        mockDoc.head().append("<link rel=\"shortcut icon\" href=\"favicon.png\">");
        dataCategorizationService.categorizeExtractedData(mockDoc, testData);
        assertTrue(testData.getHasFavicon());
    }

    @Test
    void testCategorizeExtractedData_FaviconAppleTouchIcon() {
        mockDoc.head().append("<link rel=\"apple-touch-icon\" href=\"apple-touch-icon.png\">");
        dataCategorizationService.categorizeExtractedData(mockDoc, testData);
        assertTrue(testData.getHasFavicon());
    }

    @Test
    void testCategorizeExtractedData_NoFavicon() {
        dataCategorizationService.categorizeExtractedData(mockDoc, testData);
        assertFalse(testData.getHasFavicon());
    }

    @Test
    void testCategorizeExtractedData_NullDocument() {
        testData.setImageUrls(Arrays.asList("img1.jpg")); // 1 image
        testData.setOpenGraphTags(new HashMap<>()); // 0 OG tags
        // twitterTags is null

        // Pass null for the document
        dataCategorizationService.categorizeExtractedData(null, testData);

        assertEquals(1, testData.getImageCount(), "Image count should be based on DTO if doc is null");
        assertEquals(0, testData.getOpenGraphTagCount(), "OG tag count should be based on DTO if doc is null");
        assertEquals(0, testData.getTwitterTagCount(), "Twitter tag count should be 0 if DTO field is null");
        assertFalse(testData.getHasFavicon(), "Favicon should be false if doc is null");
    }

    @Test
    void testCategorizeExtractedData_NullExtractedData() {
        // This should not throw an error, service method has a null check.
        assertDoesNotThrow(() -> {
            dataCategorizationService.categorizeExtractedData(mockDoc, null);
        });
    }

    @Test
    void testCategorizeExtractedData_NullImageListsInDto() {
        // testData.imageUrls is null by default
        // testData.ogImageUrls is null by default
        dataCategorizationService.categorizeExtractedData(mockDoc, testData);
        assertEquals(0, testData.getImageCount());
    }
}
