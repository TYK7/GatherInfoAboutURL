import { Component, OnInit, OnDestroy } from '@angular/core';
import { AuthService } from './auth/auth.service'; // Corrected path
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'] // Will create an empty app.component.scss
})
export class AppComponent implements OnInit, OnDestroy {
  title = 'Company Analyzer'; // Default title
  isAuthenticated: boolean = false;
  private authSubscription!: Subscription; // Definite assignment assertion

  constructor(public authService: AuthService) {} // Made public for template access

  ngOnInit() {
    this.authSubscription = this.authService.isAuthenticated$.subscribe(
      isAuth => this.isAuthenticated = isAuth
    );
  }

  ngOnDestroy() {
    if (this.authSubscription) {
      this.authSubscription.unsubscribe();
    }
  }

  logout() {
    this.authService.logout();
  }
}
