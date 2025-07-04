import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service'; // Corrected path

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private authService: AuthService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    const authToken = this.authService.getAuthorizationToken();

    // Clone the request to add the new header.
    // Don't add Authorization header for login/register requests or if token is not present
    if (authToken && !request.url.includes('/api/auth/')) {
      const authReq = request.clone({
        headers: request.headers.set('Authorization', `Bearer ${authToken}`)
      });
      return next.handle(authReq);
    }

    // If no token or it's an auth endpoint, pass the original request
    return next.handle(request);
  }
}
