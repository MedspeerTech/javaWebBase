import { Component, ComponentFactoryResolver, OnInit, ViewChild, ViewContainerRef, ViewEncapsulation } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { Headers, Http, RequestOptions, Response } from "@angular/http";
import { ScriptLoaderService } from "../_services/script-loader.service";
import { AuthenticationService } from "./_services/authentication.service";
import { AlertService } from "./_services/alert.service";
import { UserService } from "./_services/user.service";
import { AlertComponent } from "./_directives/alert.component";
import { LoginCustom } from "./_helpers/login-custom";
import { Helpers } from "../helpers";


@Component({
    selector: ".m-grid.m-grid--hor.m-grid--root.m-page",
    templateUrl: './login/login.component.html',
    encapsulation: ViewEncapsulation.None
})

export class AuthComponent implements OnInit {
    model: any = {};
    loading = false;
    returnUrl: string;

    @ViewChild('alertSignin', { read: ViewContainerRef }) alertSignin: ViewContainerRef;
    @ViewChild('alertSignup', { read: ViewContainerRef }) alertSignup: ViewContainerRef;
    @ViewChild('alertForgotPass', { read: ViewContainerRef }) alertForgotPass: ViewContainerRef;

    constructor(private _router: Router,
        private _script: ScriptLoaderService,
        private _userService: UserService,
        private _route: ActivatedRoute,
        private _authService: AuthenticationService,
        private _alertService: AlertService,
        private http: Http,
        private cfr: ComponentFactoryResolver) {
    }

    ngOnInit() {
        this.model.remember = true;
        // get return url from route parameters or default to '/'
        this.returnUrl = this._route.snapshot.queryParams['returnUrl'] || '/';
        this._router.navigate([this.returnUrl]);

        this._script.load('body', 'assets/vendors/base/vendors.bundle.js', 'assets/demo/default/base/scripts.bundle.js')
            .then(() => {
                Helpers.setLoading(false);
                LoginCustom.init();
            });
    }

    signin() {
        this.loading = true;
        // console.log('Before result');
        this._authService.login(this.model.username, this.model.password)
            .subscribe(result => {
                if (result) {
                // console.log(result);
                this._router.navigate([this.returnUrl]);
                }
            },
            error => {
                this.showAlert('alertSignin');
                console.log('Result: Invalid Token');
                this._alertService.error(error);
                this.loading = false;
            });
    }

    signup() {
        this.loading = true;
        this._userService.create(this.model)
            .subscribe(
            data => {
                console.log(data);
                this.showAlert('alertSignin');
                this._alertService.success('Thank you. To complete your registration please check your email.', true);
                this.loading = false;
                LoginCustom.displaySignInForm();
                this.model = {};
            },
            error => {
                this.showAlert('alertSignup');
                this._alertService.error(error);
                this.loading = false;
            });
    }

    forgotPass() {
        this.loading = true;
        this._userService.forgotPassword(this.model.email)
            .subscribe(
            data => {
                this.showAlert('alertSignin');
                this._alertService.success('Cool! Password recovery instruction has been sent to your email.', true);
                this.loading = false;
                LoginCustom.displaySignInForm();
                this.model = {};
            },
            error => {
                this.showAlert('alertForgotPass');
                this._alertService.error(error);
                this.loading = false;
            });
    }

    
        emailVerify(){
        let headers = new Headers();
        let creation_date = "2017-12-21 16:15:59";
        let username = "santhosh1991it@gmail.com";
        let token = "i46iikaol42rv52lbl2q7kv91h";
        let token_type = "EMAILVERIFICATION";

        headers.append('Content-Type', 'application/json');
        return this.http.post('/user/verifyemail', JSON.stringify({ creation_date: creation_date, username:username, token: token, token_type:token_type }), {headers: headers}).map(
            (response: Response) => {
                let data = response;
                console.log(data);
            })
            .subscribe(result => {
                if (result) {
                console.log(result);
                this._router.navigate(['/index']);
                }
            },
            error => {
                this.showAlert('alertSignin');
                this._alertService.error(error);
                this.loading = false;
            });
    }
    
    // resetPassword(email, password){
    //     console.log(email);
    //     console.log(password);

    //     let headers = new Headers();
    //     headers.append('Content-Type', 'application/json');
    //     return this.http.post('/user/resetpassword', JSON.stringify({  }), {headers: headers}).map(
    //         (response: Response) => {
    //             let data = response;
    //         })
    //         .subscribe(result => {
    //             if (result) {
    //             this._router.navigate(['/index']);
    //             }
    //         },
    //         error => {
    //             this.showAlert('alertSignin');
    //             this._alertService.error(error);
    //             this.loading = false;
    //         });
    // }

    showAlert(target) {
        this[target].clear();
        let factory = this.cfr.resolveComponentFactory(AlertComponent);
        let ref = this[target].createComponent(factory);
        ref.changeDetectorRef.detectChanges();
    }
}