import { Injectable } from "@angular/core";
import { 
    Http,
    Headers,
    ResponseOptions,
    Response,
    Request,
    RequestMethod,
    RequestOptions
    } from "@angular/http";
import { 
    ActivatedRouteSnapshot, 
    CanActivate,
    Router, 
    RouterStateSnapshot 
} from "@angular/router";
import "rxjs/add/operator/map";


@Injectable()
export class AuthenticationService {
    // isUserLoggedIn = true;
    // redirectUrl = "/index";
    // loginUrl = "/";
    isAuthenticated: boolean = false;
    constructor(private _http: Http,
    private _router: Router) {
    }

    // login(username: string, password: string){
    //     let url = "/login";
        
    //     let body = new FormData();
    //     body.append('username', username);
    //     body.append('password', password);
    //     // return this._http.post(url, body);
    //     return this._http.post(url, body).map((res: Response) => {
    //            if (res.status === 200) {
    //             res.text();
    //             console.log(res.text());
    //             }
    //         });                           
    //     }

    login(username: string, password: string) {
        let headers = new Headers();
        
        // headers.append('Content-Type', 'application/json');
        return this._http.post('/login', JSON.stringify({ username: username, password: password }), {headers: headers})
            .map((response: Response) => {
                // login successful if there's a jwt token in the response
                
                // console.log(response);
                let token = response.headers.get('Authorization');
                let status = response.status;
                // console.log(status);
                let data = response;
                
                // console.log("Your TOken: " + token);
                // console.log("Here Your Data: " + data.status);
                // let data = response.json() && response.json().token;
                
                if (data.status == 200 ) {
                    // store user details and jwt token in local storage to keep user logged in between page refreshes
                    
                    // console.log('Here we save some user info');

                    localStorage.setItem('currentUser', token);

                    let local = localStorage.getItem('currentUser');
                    console.log("Token: " + local);
                }
                if (data.status === 401 ) {
                    // store user details and jwt token in local storage to keep user logged in between page refreshes
                    console.log('Not a Valid Token');
                }
            });
    }

    logout() {
        // remove user from local storage to log user out
        let local = localStorage.getItem('currentUser');
        console.log("Token: " + local);
        localStorage.removeItem('currentUser');
        
    }
}