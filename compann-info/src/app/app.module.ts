import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http'; // Import HttpClientModule and HTTP_INTERCEPTORS
import { ReactiveFormsModule } from '@angular/forms'; // Often needed, good to have

import { AppRoutingModule } from './app-routing.module'; // Will create this next
import { AppComponent } from './app.component'; // Will create this next

import { AuthModule } from './auth/auth.module';
import { AuthInterceptor } from './auth/auth.interceptor'; // Corrected path
// AuthService and AuthGuard are provided in root, so no need to import here for providing.

import { DashboardComponent } from './dashboard/dashboard.component'; // Import DashboardComponent

@NgModule({
  declarations: [
    AppComponent,
    DashboardComponent // Declare DashboardComponent here as it's not part of another module yet
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule, // Add HttpClientModule for HTTP requests
    ReactiveFormsModule, // Add ReactiveFormsModule for form handling in components if not strictly in AuthModule
    AuthModule // Import AuthModule to make its components available
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true } // Provide the interceptor
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
