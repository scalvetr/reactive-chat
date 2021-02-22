import { Injectable } from '@angular/core';
import {Router} from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUser: string | null = null;

  constructor(private router: Router) {
  }

  getCurrentUser(): string | null{
    return this.currentUser;
  }

  login(user: string, password: string): void {
    this.currentUser = user;
    console.log('user ' + user + 'logged in');
    this.router.navigate(['/']);
  }

  logout(): void {
    this.currentUser = null;
  }

  isUserLoggedIn(): Promise<boolean> {
    return new Promise<boolean>(
      (resolve, reject) =>
        setTimeout(() => resolve(this.currentUser !== null), 500)
    );
  }
}
