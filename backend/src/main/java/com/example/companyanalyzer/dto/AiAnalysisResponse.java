package com.example.companyanalyzer.dto;

import java.util.List;

public class AiAnalysisResponse {
    private List<String> pros;
    private List<String> cons;
    private List<String> opportunities;
    private List<String> redFlags;
    private String summary; // A general summary statement

    public AiAnalysisResponse() {
    }

    public AiAnalysisResponse(List<String> pros, List<String> cons, List<String> opportunities, List<String> redFlags, String summary) {
        this.pros = pros;
        this.cons = cons;
        this.opportunities = opportunities;
        this.redFlags = redFlags;
        this.summary = summary;
    }

    // Getters and Setters
    public List<String> getPros() {
        return pros;
    }

    public void setPros(List<String> pros) {
        this.pros = pros;
    }

    public List<String> getCons() {
        return cons;
    }

    public void setCons(List<String> cons) {
        this.cons = cons;
    }

    public List<String> getOpportunities() {
        return opportunities;
    }

    public void setOpportunities(List<String> opportunities) {
        this.opportunities = opportunities;
    }

    public List<String> getRedFlags() {
        return redFlags;
    }

    public void setRedFlags(List<String> redFlags) {
        this.redFlags = redFlags;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
