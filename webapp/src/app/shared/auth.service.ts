import {Injectable} from '@angular/core';
import {Router} from '@angular/router';
import {User} from './user.model';
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUser: User | null = null;

  constructor(private router: Router) {
  }

  getCurrentUser(): User | null {
    return this.currentUser;
  }

  login(user: string, password: string): void {

    let avatarBaseUrl = environment.authService.avatarBaseUrl;
    if (avatarBaseUrl.startsWith('/')) {
      const l = window.location;
      avatarBaseUrl = l.protocol + '//' + l.hostname +
        (((l.port !== '80') && (l.port !== '443')) ? ':' + l.port : '') +
        avatarBaseUrl;
    }
    // const avatar = user;
    const avatar = 'default.png';
    this.currentUser = new User(user, avatarBaseUrl + avatar);
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
