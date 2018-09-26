import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot } from "@angular/router";
import { UserService } from "../_services/user.service";
// import { Request, RequestMethod } from "@angular/http";
import { Observable } from "rxjs/Rx";

@Injectable()
export class AuthGuard implements CanActivate {

    constructor(private _router: Router, private _userService: UserService) {
    }

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> | boolean {
        
        let currentUser = localStorage.getItem('currentUser');
        // console.log('Current User: ' + currentUser);
        // let currentUser = localStorage.removeItem('currentUser');
        console.log('Current User: ' + currentUser);
        // this._router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
        return this._userService.verify().map(
            data => {
                if (data !== null) {
                    // logged in so return true
                    return true;
                }
                // error when verify so redirect to login page with the return url
                this._router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
                return false;
            },
            error => {
                console.log('Sorry Invalid Token');
                // error when verify so redirect to login page with the return url
                this._router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
                return false;
            });
    }
}