import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http'; // HttpClientModule should be imported in AppModule typically
import { RouterModule } from '@angular/router'; // Import RouterModule

import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
// AuthService is provided in 'root'
// AuthGuard is provided in 'root'
// AuthInterceptor will be provided in AppModule

@NgModule({
  declarations: [
    LoginComponent,
    RegisterComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule, // Add RouterModule here for routerLink directives in components
    // HttpClientModule // Typically imported once in AppModule
  ],
  exports: [
    LoginComponent,
    RegisterComponent
  ]
  // No providers needed here for services providedIn: 'root'
})
export class AuthModule { }
