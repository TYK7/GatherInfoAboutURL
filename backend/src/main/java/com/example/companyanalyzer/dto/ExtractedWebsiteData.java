package com.example.companyanalyzer.dto;

import java.util.List;
import java.util.Map;

public class ExtractedWebsiteData {
    private String requestedUrl;
    private String title;
    private String metaDescription;
    private List<String> imageUrls;
    private List<String> ogImageUrls;
    private Map<String, String> openGraphTags;
    private Map<String, String> twitterTags;
    private List<String> socialMediaLinks;

    // Basic Categorization Fields
    private Integer imageCount;
    private Integer openGraphTagCount;
    private Integer twitterTagCount;
    private Boolean hasFavicon; // Example: will need logic to find favicon link

    // Add more fields as needed, e.g., for fonts, colors, schema.org, etc.

    public ExtractedWebsiteData(String requestedUrl) {
        this.requestedUrl = requestedUrl;
    }

    // Getters and Setters
    public String getRequestedUrl() {
        return requestedUrl;
    }

    public void setRequestedUrl(String requestedUrl) {
        this.requestedUrl = requestedUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMetaDescription() {
        return metaDescription;
    }

    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public List<String> getOgImageUrls() {
        return ogImageUrls;
    }

    public void setOgImageUrls(List<String> ogImageUrls) {
        this.ogImageUrls = ogImageUrls;
    }

    public Map<String, String> getOpenGraphTags() {
        return openGraphTags;
    }

    public void setOpenGraphTags(Map<String, String> openGraphTags) {
        this.openGraphTags = openGraphTags;
    }

    public Map<String, String> getTwitterTags() {
        return twitterTags;
    }

    public void setTwitterTags(Map<String, String> twitterTags) {
        this.twitterTags = twitterTags;
    }

    public List<String> getSocialMediaLinks() {
        return socialMediaLinks;
    }

    public void setSocialMediaLinks(List<String> socialMediaLinks) {
        this.socialMediaLinks = socialMediaLinks;
    }

    // Getters and Setters for Categorization Fields
    public Integer getImageCount() {
        return imageCount;
    }

    public void setImageCount(Integer imageCount) {
        this.imageCount = imageCount;
    }

    public Integer getOpenGraphTagCount() {
        return openGraphTagCount;
    }

    public void setOpenGraphTagCount(Integer openGraphTagCount) {
        this.openGraphTagCount = openGraphTagCount;
    }

    public Integer getTwitterTagCount() {
        return twitterTagCount;
    }

    public void setTwitterTagCount(Integer twitterTagCount) {
        this.twitterTagCount = twitterTagCount;
    }

    public Boolean getHasFavicon() {
        return hasFavicon;
    }

    public void setHasFavicon(Boolean hasFavicon) {
        this.hasFavicon = hasFavicon;
    }
}
