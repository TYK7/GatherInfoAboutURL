import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../auth.service'; // Corrected path

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {
  registerForm: FormGroup;
  errorMessage: string = '';
  successMessage: string = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      password: ['', [Validators.required, Validators.minLength(6)]]
      // Add other fields like email if necessary
    });
  }

  ngOnInit(): void {
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/dashboard']);
    }
  }

  onSubmit(): void {
    if (this.registerForm.valid) {
      this.authService.register(this.registerForm.value).subscribe({
        next: (response) => {
          this.successMessage = 'Registration successful! You can now login.';
          this.errorMessage = '';
          this.registerForm.reset();
          // Optionally navigate to login page after a short delay or directly
          // this.router.navigate(['/login']);
        },
        error: (err) => {
          this.successMessage = '';
          if (err.error && typeof err.error === 'string') {
            this.errorMessage = err.error;
          } else if (err.message) {
            this.errorMessage = err.message;
          } else {
            this.errorMessage = 'Registration failed. Please try again.';
          }
          console.error(err);
        }
      });
    } else {
      this.errorMessage = 'Please fill in all fields correctly.';
      this.successMessage = '';
    }
  }
}
