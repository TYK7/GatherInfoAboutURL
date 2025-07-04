package com.example.companyanalyzer.service;

import com.example.companyanalyzer.dto.ExtractedWebsiteData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

// Using MockitoExtension for cleaner mock setup if not using full SpringBootTest context for this unit test
@ExtendWith(MockitoExtension.class)
public class ExtractionServiceTests {

    @Mock
    private DataCategorizationService mockDataCategorizationService; // Mock this dependency

    @InjectMocks
    private ExtractionService extractionService; // Service under test

    private Document mockDocument;

    @BeforeEach
    void setUp() {
        // Basic setup, individual tests will customize the mockDocument further
        mockDocument = Document.createShell("http://example.com");
        // Ensure categorizeExtractedData does not throw NPE when called
        doNothing().when(mockDataCategorizationService).categorizeExtractedData(any(Document.class), any(ExtractedWebsiteData.class));
    }

    @Test
    void testExtractDataFromUrl_Success() throws IOException {
        // Mock Jsoup.connect() chain
        // This is tricky because Jsoup.connect() is static and returns a Connection object with chained methods.
        // For more complex scenarios, PowerMockito or a wrapper around Jsoup might be needed.
        // For a simpler approach, if you can refactor Jsoup calls into a protected method, you can spy on ExtractionService.

        // Simplified: Assume successful connection and basic document setup
        mockDocument.title("Test Title");
        // Add meta description
        mockDocument.head().append("<meta name=\"description\" content=\"Test Description\">");
        // Add img tags
        mockDocument.body().append("<img src=\"http://example.com/image1.jpg\">");
        mockDocument.body().append("<img src=\"/relative/image2.png\">"); // relative path
        // Add OG image
        mockDocument.head().append("<meta property=\"og:image\" content=\"http://example.com/ogimage.jpg\">");
        // Add OG tags
        mockDocument.head().append("<meta property=\"og:type\" content=\"website\">");
        // Add Twitter tags
        mockDocument.head().append("<meta name=\"twitter:card\" content=\"summary\">");
        // Add social links
        mockDocument.body().append("<a href=\"http://linkedin.com/company/test\">LinkedIn</a>");
        mockDocument.body().append("<a href=\"http://twitter.com/testuser\">Twitter</a>");


        // For this example, we'll assume Jsoup.connect().get() is part of what we can't easily mock directly here
        // without more complex setup. A better unit test would mock the Document passed to private methods,
        // or make the Jsoup.connect().get() call injectable/mockable.
        // Let's assume for now we are testing the private methods by calling the public one and it gets a doc.
        // This test will be more of an integration test for the private methods if Jsoup isn't mocked.

        // A more focused unit test would be:
        // 1. Create a real Document object from a string of HTML.
        // 2. Call the private extract methods directly (by making them package-private or using reflection, though not ideal).

        // Given the current structure, let's test a scenario where the document is pre-parsed (conceptual)
        // and then passed to the categorization service.

        ExtractedWebsiteData result = new ExtractedWebsiteData("http://example.com");
        // Manually call the private methods for a more "unit" feel, assuming 'doc' is obtained
        // This requires making them package-private or using other techniques for testing.
        // For this stub, I'll acknowledge this limitation.

        // If Jsoup.connect().get() cannot be easily mocked, test the private methods separately.
        // For now, this test is more conceptual for the stub.
        // To make it runnable, we'd need to handle the static Jsoup.connect()
        // One way is to use a try-with-resources for MockedStatic if using a newer Mockito that supports it well for Jsoup.

        try (MockedStatic<Jsoup> jsoupMockedStatic = Mockito.mockStatic(Jsoup.class)) {
            org.jsoup.Connection mockConnection = mock(org.jsoup.Connection.class);
            jsoupMockedStatic.when(() -> Jsoup.connect(anyString())).thenReturn(mockConnection);
            when(mockConnection.userAgent(anyString())).thenReturn(mockConnection);
            when(mockConnection.timeout(anyInt())).thenReturn(mockConnection);
            when(mockConnection.followRedirects(anyBoolean())).thenReturn(mockConnection);
            when(mockConnection.get()).thenReturn(mockDocument);

            ExtractedWebsiteData actualResult = extractionService.extractDataFromUrl("http://example.com");

            assertEquals("Test Title", actualResult.getTitle());
            assertEquals("Test Description", actualResult.getMetaDescription());
            assertTrue(actualResult.getImageUrls().contains("http://example.com/image1.jpg"));
            // Jsoup's abs:src on a shell document might not resolve /relative/image2.png as expected without a proper base URI setup in test
            // For a real test, parse a full HTML string: Document doc = Jsoup.parse(htmlString, "http://example.com");
            // For this stub, we'll assume it works or test private methods directly.

            assertTrue(actualResult.getOgImageUrls().contains("http://example.com/ogimage.jpg"));
            assertEquals("website", actualResult.getOpenGraphTags().get("og:type"));
            assertEquals("summary", actualResult.getTwitterTags().get("twitter:card"));
            assertTrue(actualResult.getSocialMediaLinks().contains("http://linkedin.com/company/test"));
            assertTrue(actualResult.getSocialMediaLinks().contains("http://twitter.com/testuser"));

            verify(mockDataCategorizationService).categorizeExtractedData(eq(mockDocument), eq(actualResult));
        }
    }

    @Test
    void testExtractDataFromUrl_IOException() throws IOException {
         try (MockedStatic<Jsoup> jsoupMockedStatic = Mockito.mockStatic(Jsoup.class)) {
            org.jsoup.Connection mockConnection = mock(org.jsoup.Connection.class);
            jsoupMockedStatic.when(() -> Jsoup.connect(anyString())).thenReturn(mockConnection);
            when(mockConnection.userAgent(anyString())).thenReturn(mockConnection);
            when(mockConnection.timeout(anyInt())).thenReturn(mockConnection);
            when(mockConnection.followRedirects(anyBoolean())).thenReturn(mockConnection);
            when(mockConnection.get()).thenThrow(new IOException("Network error"));

            ExtractedWebsiteData result = extractionService.extractDataFromUrl("http://example.com");

            assertTrue(result.getTitle().startsWith("Error: Could not fetch content"));
            // Verify categorization is still called, possibly with null doc
            verify(mockDataCategorizationService).categorizeExtractedData(isNull(), eq(result));
        }
    }

    // Add more tests for:
    // - Different HTML structures (e.g., missing meta tags, no images)
    // - Specifics of social media link filtering
    // - Edge cases for URL resolution in images/links
}
