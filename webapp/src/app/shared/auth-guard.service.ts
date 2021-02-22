import {Injectable} from '@angular/core';
import {AuthService} from './auth.service';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree} from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private authService: AuthService, private router: Router) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Promise<boolean | UrlTree> {
    return this.authService.isUserLoggedIn().then(
      (authenticated: boolean) => {
        if (authenticated) {
          console.log('logged in');
          return true;
        } else {
          console.log('not logged in');
          return this.router.parseUrl('/login');
        }
      }
    );
  }

}
