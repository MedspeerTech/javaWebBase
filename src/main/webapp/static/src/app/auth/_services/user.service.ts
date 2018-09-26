import { Injectable } from "@angular/core";
import { Headers, Http, URLSearchParams, RequestOptions, Response } from "@angular/http";
import {ActivatedRoute, Router} from "@angular/router";
import { User } from "../_models/index";
import { Observable } from "rxjs/Observable";
import { AuthenticationService } from "./authentication.service";

@Injectable()
export class UserService {

    constructor(private http: Http,
        private route: ActivatedRoute,
        private authService: AuthenticationService,
    ) {
    }

    // verify() {
    //     let headers = new Headers();
    //     return this.http.get('http://localhost:9090/login', this.jwt()).map(
    //         (response: Response) => {
    //             let data = response;
    //             console.log(data.headers);
    //             // response.json());
    //         }
    //     );
    // }

    verify() {
        let headers = new Headers();
        return this.http.get('http://localhost:9090/api/session', this.jwt()).map(
            (response: Response) => {
                let data = response;
                console.log(data.headers);
                // response.json());
            }
        );
    }

    create(user: User) {
        console.log(user);
        let headers = new Headers();
        headers.append('Content-Type', 'application/json; charset=UTF-8');
        return this.http.post('/user/signup', user, {headers: headers}).map(
            (response: Response) => {
            let data = response.json();
        });
    }

    forgotPassword(Username: string) {
        console.log(Username);
        let headers = new Headers();
        headers.set('Content-Type', 'text/plain');
        let myParams = new URLSearchParams();
        myParams.set('Username', Username);
        let options = new RequestOptions({ headers: headers, params: myParams });
        return this.http.get('/user/forgotpassword', options)
        .map(
            (response: Response) => {
                let data = response;
                console.log(data);
            });
    }

    
    

    getById(id: number) {
        return this.http.get('/api/users/' + id, this.jwt()).map((response: Response) => response.json());
    }
    update(user: User) {
        return this.http.put('/api/users/' + user.id, user, this.jwt()).map((response: Response) => response.json());
    }

    delete(id: number) {
        return this.http.delete('/api/users/' + id, this.jwt()).map((response: Response) => response.json());
    }

    // private helper methods

    private jwt() {
        // create authorization header with jwt token
        
        let currentUser = localStorage.getItem('currentUser');
        if (currentUser && currentUser) {
            let headers = new Headers({ 'Authorization': 'Bearer ' + currentUser });
            return new RequestOptions({ headers: headers });
        }
    }
}