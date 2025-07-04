import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { AuthService, AuthResponseData } from './auth.service';
import { Router } from '@angular/router';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;
  let router: Router;

  const mockAuthResponse: AuthResponseData = {
    accessToken: 'test-token',
    tokenType: 'Bearer',
    username: 'testUser'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        RouterTestingModule.withRoutes([]) // Basic router setup for navigation tests
      ],
      providers: [AuthService]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);

    // Clear localStorage before each test
    localStorage.clear();
  });

  afterEach(() => {
    httpMock.verify(); // Verify that no unmatched requests are outstanding
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('login', () => {
    it('should store token and username on successful login and update BehaviorSubjects', (done) => {
      const credentials = { username: 'testUser', password: 'password' };

      service.login(credentials).subscribe(response => {
        expect(response).toEqual(mockAuthResponse);
        expect(localStorage.getItem('jwt_token')).toBe(mockAuthResponse.accessToken);
        expect(localStorage.getItem('username')).toBe(mockAuthResponse.username);
        done();
      });

      const req = httpMock.expectOne('/api/auth/login');
      expect(req.request.method).toBe('POST');
      req.flush(mockAuthResponse);

      service.isAuthenticated$.subscribe(isAuth => expect(isAuth).toBe(true));
      service.token$.subscribe(token => expect(token).toBe(mockAuthResponse.accessToken));
      service.username$.subscribe(username => expect(username).toBe(mockAuthResponse.username));

    });

    it('should handle login error', (done) => {
      const credentials = { username: 'testUser', password: 'wrongpassword' };
      const errorResponse = { status: 401, statusText: 'Unauthorized' };

      service.login(credentials).subscribe({
        next: () => fail('should have failed with 401 response'),
        error: (error) => {
          expect(error.status).toBe(401);
          expect(localStorage.getItem('jwt_token')).toBeNull();
          expect(localStorage.getItem('username')).toBeNull();
          done();
        }
      });

      const req = httpMock.expectOne('/api/auth/login');
      req.flush('Login Failed', errorResponse);

      service.isAuthenticated$.subscribe(isAuth => expect(isAuth).toBe(false));
    });
  });

  describe('register', () => {
    it('should post registration data successfully', (done) => {
      const userData = { username: 'newUser', password: 'newPassword' };

      service.register(userData).subscribe(response => {
        expect(response).toEqual('User registered successfully!'); // Or whatever backend returns
        done();
      });

      const req = httpMock.expectOne('/api/auth/register');
      expect(req.request.method).toBe('POST');
      req.flush('User registered successfully!');
    });
  });

  describe('logout', () => {
    it('should clear token and username, update BehaviorSubjects, and navigate to login', () => {
      // First, simulate a login
      localStorage.setItem('jwt_token', 'dummy-token');
      localStorage.setItem('username', 'dummyUser');
      // Manually set BehaviorSubjects as if logged in
      (service as any).tokenSubject.next('dummy-token');
      (service as any).usernameSubject.next('dummyUser');
      (service as any).isAuthenticatedSubject.next(true);

      const navigateSpy = spyOn(router, 'navigate');

      service.logout();

      expect(localStorage.getItem('jwt_token')).toBeNull();
      expect(localStorage.getItem('username')).toBeNull();
      service.isAuthenticated$.subscribe(isAuth => expect(isAuth).toBe(false));
      service.token$.subscribe(token => expect(token).toBeNull());
      service.username$.subscribe(username => expect(username).toBeNull());
      expect(navigateSpy).toHaveBeenCalledWith(['/login']);
    });
  });

  describe('isLoggedIn', () => {
    it('should return true if token exists', () => {
      localStorage.setItem('jwt_token', 'some-token');
      expect(service.isLoggedIn()).toBeTrue();
    });

    it('should return false if token does not exist', () => {
      localStorage.removeItem('jwt_token'); // Ensure it's removed
      expect(service.isLoggedIn()).toBeFalse();
    });
  });

  describe('getAuthorizationToken', () => {
    it('should return token if it exists', () => {
      localStorage.setItem('jwt_token', 'my-test-token');
      expect(service.getAuthorizationToken()).toBe('my-test-token');
    });

    it('should return null if token does not exist', () => {
      expect(service.getAuthorizationToken()).toBeNull();
    });
  });

  // Test initial state of BehaviorSubjects
  it('BehaviorSubjects should initialize correctly based on localStorage', () => {
    localStorage.setItem('jwt_token', 'initial-token');
    localStorage.setItem('username', 'initial-user');
    // Re-initialize service to pick up from localStorage
    // Need to get HttpClient, not HttpTestingController for service constructor
    service = new AuthService(TestBed.inject(HttpClient), router);

    service.isAuthenticated$.subscribe(isAuth => expect(isAuth).toBe(true));
    service.token$.subscribe(token => expect(token).toBe('initial-token'));
    service.username$.subscribe(username => expect(username).toBe('initial-user'));
  });

   it('BehaviorSubjects should initialize correctly if localStorage is empty', () => {
    localStorage.clear();
    // Re-initialize service
    service = new AuthService(TestBed.inject(HttpClient), router);

    service.isAuthenticated$.subscribe(isAuth => expect(isAuth).toBe(false));
    service.token$.subscribe(token => expect(token).toBeNull());
    service.username$.subscribe(username => expect(username).toBeNull());
  });

});
