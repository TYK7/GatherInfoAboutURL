import { Component, OnInit } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { ApiService, ExtractPayload, ExtractResponse, AiAnalysisData } from '../core/api.service'; // Import AiAnalysisData
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  username: string | null = null;
  urlForm: FormGroup;
  extractionResult: ExtractResponse | null = null;
  extractionError: string | null = null;
  isLoadingExtraction: boolean = false; // For loading indicator during extraction

  aiAnalysisResult: AiAnalysisData | null = null;
  aiAnalysisError: string | null = null;
  isLoadingAiAnalysis: boolean = false; // For loading indicator during AI analysis


  // Helper for iterating over map keys in template
  objectKeys = Object.keys;

  constructor(
    private authService: AuthService,
    private apiService: ApiService, // Inject ApiService
    private fb: FormBuilder // Inject FormBuilder
  ) {
    this.urlForm = this.fb.group({
      urlToExtract: ['', [Validators.required, Validators.pattern('https?://.+')]] // Basic URL validation
    });
  }

  ngOnInit(): void {
    this.authService.username$.subscribe(name => {
      this.username = name;
    });
  }

  submitUrl(): void {
    if (this.urlForm.invalid) {
      this.extractionError = 'Please enter a valid URL.';
      this.extractionResult = null;
      return;
    }

    this.extractionError = null;
    this.extractionResult = null;
    this.aiAnalysisResult = null; // Reset AI analysis when new URL is submitted
    this.aiAnalysisError = null;
    this.isLoadingExtraction = true;

    const payload: ExtractPayload = {
      url: this.urlForm.value.urlToExtract
    };

    this.apiService.submitUrlForExtraction(payload).subscribe({
      next: (response) => {
        this.extractionResult = response;
        this.isLoadingExtraction = false;
        console.log('Extraction API Response:', response);
        // Check if extraction itself reported an error (e.g., in title)
        if (response.title && response.title.toLowerCase().includes("error:")) {
            this.extractionError = `Extraction issue: ${response.title}`;
        }
      },
      error: (err) => {
        this.extractionError = `Error submitting URL: ${err.message || 'Unknown error'}`;
        if (err.status === 401 || err.status === 403) {
          this.extractionError += '. Your session might have expired. Please try logging out and logging back in.';
        }
        this.isLoadingExtraction = false;
        console.error('Extraction API Error:', err);
      }
    });
  }

  getAIAnalysis(): void {
    if (!this.extractionResult || (this.extractionResult.title && this.extractionResult.title.toLowerCase().includes("error:")) ) {
      this.aiAnalysisError = 'Cannot perform AI analysis. Initial data extraction was not successful or contained errors.';
      this.aiAnalysisResult = null;
      return;
    }

    this.aiAnalysisError = null;
    this.aiAnalysisResult = null;
    this.isLoadingAiAnalysis = true;

    this.apiService.getAiAnalysis(this.extractionResult).subscribe({
      next: (response) => {
        this.aiAnalysisResult = response;
        this.isLoadingAiAnalysis = false;
        console.log('AI Analysis API Response:', response);
      },
      error: (err) => {
        this.aiAnalysisError = `Error getting AI analysis: ${err.message || 'Unknown error'}`;
        if (err.status === 401 || err.status === 403) {
          this.aiAnalysisError += '. Your session might have expired. Please try logging out and logging back in.';
        }
        this.isLoadingAiAnalysis = false;
        console.error('AI Analysis API Error:', err);
      }
    });
  }

  // Helper to check if AI analysis can be triggered
  canGetAiAnalysis(): boolean {
    return !!this.extractionResult && !(this.extractionResult.title && this.extractionResult.title.toLowerCase().includes("error:")) && !this.isLoadingAiAnalysis && !this.isLoadingExtraction;
  }
}
