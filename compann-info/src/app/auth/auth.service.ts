import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';
import { Router } from '@angular/router'; // Added Router

export interface AuthResponseData {
  accessToken: string;
  tokenType: string;
  username: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = '/api/auth'; // Assuming backend is served on the same domain or proxied

  private tokenSubject = new BehaviorSubject<string | null>(this.getToken());
  public token$ = this.tokenSubject.asObservable();

  private usernameSubject = new BehaviorSubject<string | null>(this.getUsername());
  public username$ = this.usernameSubject.asObservable();

  private isAuthenticatedSubject = new BehaviorSubject<boolean>(!!this.getToken());
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) { } // Injected Router

  register(user: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, user);
  }

  login(credentials: any): Observable<AuthResponseData> {
    return this.http.post<AuthResponseData>(`${this.apiUrl}/login`, credentials)
      .pipe(
        tap(response => {
          this.storeToken(response.accessToken);
          this.storeUsername(response.username); // Assuming username is part of AuthResponseData
          this.tokenSubject.next(response.accessToken);
          this.usernameSubject.next(response.username);
          this.isAuthenticatedSubject.next(true);
        })
      );
  }

  logout(): void {
    this.removeToken();
    this.removeUsername();
    this.tokenSubject.next(null);
    this.usernameSubject.next(null);
    this.isAuthenticatedSubject.next(false);
    this.router.navigate(['/login']); // Navigate to login on logout
  }

  private storeToken(token: string): void {
    localStorage.setItem('jwt_token', token);
  }

  private getToken(): string | null {
    return localStorage.getItem('jwt_token');
  }

  private removeToken(): void {
    localStorage.removeItem('jwt_token');
  }

  private storeUsername(username: string): void {
    localStorage.setItem('username', username);
  }

  private getUsername(): string | null {
    return localStorage.getItem('username');
  }

  private removeUsername(): void {
    localStorage.removeItem('username');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  getAuthorizationToken(): string | null {
    return this.getToken();
  }
}
