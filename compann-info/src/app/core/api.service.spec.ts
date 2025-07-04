import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ApiService, ExtractPayload, ExtractResponse, AiAnalysisData } from './api.service';

describe('ApiService', () => {
  let service: ApiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ApiService]
    });
    service = TestBed.inject(ApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify(); // Make sure that there are no outstanding requests
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('submitUrlForExtraction', () => {
    it('should send a POST request to /api/extract and return data', (done) => {
      const mockPayload: ExtractPayload = { url: 'http://example.com' };
      const mockResponse: ExtractResponse = {
        requestedUrl: 'http://example.com',
        title: 'Example Domain',
        metaDescription: 'An example domain.',
        imageUrls: [],
        ogImageUrls: [],
        openGraphTags: {},
        twitterTags: {},
        socialMediaLinks: [],
        imageCount: 0,
        openGraphTagCount: 0,
        twitterTagCount: 0,
        hasFavicon: true
      };

      service.submitUrlForExtraction(mockPayload).subscribe(response => {
        expect(response).toEqual(mockResponse);
        done();
      });

      const req = httpMock.expectOne('/api/extract');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(mockPayload);
      req.flush(mockResponse);
    });
  });

  describe('getAiAnalysis', () => {
    it('should send a POST request to /api/analyze with extracted data and return AI analysis', (done) => {
      const mockExtractedData: ExtractResponse = { // This is the payload for /api/analyze
        requestedUrl: 'http://example.com',
        title: 'Example Domain',
        // ... other fields from ExtractResponse
        metaDescription: null, imageUrls: null, ogImageUrls: null, openGraphTags: null, twitterTags: null, socialMediaLinks: null
      };
      const mockAiResponse: AiAnalysisData = {
        summary: 'This is a great company.',
        pros: ['Good title'],
        cons: ['No meta description'],
        opportunities: [],
        redFlags: []
      };

      service.getAiAnalysis(mockExtractedData).subscribe(response => {
        expect(response).toEqual(mockAiResponse);
        done();
      });

      const req = httpMock.expectOne('/api/analyze');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(mockExtractedData);
      req.flush(mockAiResponse);
    });
  });
});
