import { Component, OnInit, ViewChild, ViewContainerRef, ViewEncapsulation, ElementRef } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { Http, Headers } from '@angular/http';
import { AlertComponent } from "../_directives/alert.component";
import { AlertService } from '../_services/alert.service';
import { Observable } from "rxjs/Rx";
import "rxjs/add/operator/map";

@Component({
  selector: 'app-email-verify',
  templateUrl: './email-verify.component.html',
  styles: []
})
export class EmailVerifyComponent implements OnInit {
  model: any = {};
  token: {username: String, token: String};

  constructor(
    private _router: Router,
    private _route: ActivatedRoute,
    private _http: Http,
    private _alertService: AlertService,
  ) { }


  ngOnInit() {
    this.token = {
      username: this._route.snapshot.params['email'],
      token: this._route.snapshot.params['token']
    };
    let headers = new Headers();
    let username = this._route.snapshot.params['email'];
    console.log(username);
    let token = this._route.snapshot.params['token'];
    console.log(token);
    let token_type = "EMAILVERIFICATION";
    headers.append('Content-Type', 'application/json');
    return this._http.post('/user/verifyemail', JSON.stringify({ username:username, token: token, token_type:token_type }), {headers: headers})
    .subscribe(result => {
      if (result) {
        console.log(result);
        this._router.navigate(['/index']);
      }
    },
    error => {
      this._alertService.error(error);
    });
  }
}
