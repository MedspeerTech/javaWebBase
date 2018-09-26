import { Component, OnInit, ViewChild, ViewContainerRef, ViewEncapsulation, ElementRef } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { Http, Headers } from '@angular/http';
import { AlertComponent } from "../_directives/alert.component";
import { AlertService } from '../_services/alert.service';
import { Observable } from "rxjs/Rx";
import "rxjs/add/operator/map";

@Component({
  selector: 'app-resetpassword',
  templateUrl:'./resetpassword.component.html',
  styles: []
})
export class ResetpasswordComponent implements OnInit {

  model: any = {};
  token: {username: String, token: String};
  loading = false;
  returnUrl: string;
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
    
  }
  
  resetPassword(form: any){
    let password = form.password;
    let token = this._route.snapshot.params['token'];
    let username = this._route.snapshot.params['email'];
    let headers = new Headers();
    return this._http.post('/user/resetpassword', { username, password, token })
    .subscribe(result => {
            if (result) {
            this._router.navigate(['/index']);
            }
        },
        error => {
            this._alertService.error(error);
            this.loading = false;
        });
}
}
