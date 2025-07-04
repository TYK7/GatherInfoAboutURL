import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// Define an interface for the ExtractRequest payload
export interface ExtractPayload {
  url: string;
}

// Updated interface to match backend's ExtractedWebsiteData DTO
export interface ExtractResponse {
  requestedUrl: string;
  title: string | null; // Can be null if extraction fails or page has no title
  metaDescription: string | null;
  imageUrls: string[] | null;
  ogImageUrls: string[] | null;
  openGraphTags: { [key: string]: string } | null;
  twitterTags: { [key: string]: string } | null;
  socialMediaLinks: string[] | null;
  // Basic Categorization Fields
  imageCount?: number | null; // Use optional if they might not always be present
  openGraphTagCount?: number | null;
  twitterTagCount?: number | null;
  hasFavicon?: boolean | null;
  // Add other fields from ExtractedWebsiteData as they are implemented
  // For error responses from the controller (e.g., if title starts with "Error:")
  error?: string;
  details?: string;
}

// Interface for the AI Analysis Response from backend
export interface AiAnalysisData {
  pros: string[] | null;
  cons: string[] | null;
  opportunities: string[] | null;
  redFlags: string[] | null;
  summary: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private baseUrl = '/api'; // Base URL for API calls

  constructor(private http: HttpClient) { }

  // Method to call the protected /api/extract endpoint
  submitUrlForExtraction(payload: ExtractPayload): Observable<ExtractResponse> {
    return this.http.post<ExtractResponse>(`${this.baseUrl}/extract`, payload);
  }

  // Method to call the protected /api/analyze endpoint
  // It sends the result of the extraction (ExtractResponse) as payload
  getAiAnalysis(extractedData: ExtractResponse): Observable<AiAnalysisData> {
    return this.http.post<AiAnalysisData>(`${this.baseUrl}/analyze`, extractedData);
  }

  // Example of a GET request to a protected endpoint (if we add one later)
  // getProtectedData(): Observable<any> {
  //   return this.http.get<any>(`${this.baseUrl}/some-protected-resource`);
  // }
}
