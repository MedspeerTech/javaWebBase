import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LogoutComponent } from "./auth/logout/logout.component";
import { ResetpasswordComponent } from './auth/resetpassword/resetpassword.component';
import { EmailVerifyComponent } from './auth/email-verify/email-verify.component'


const routes: Routes = [
    { 
        path: 'login',
         loadChildren: './auth/auth.module#AuthModule'
    },
    { 
        path: 'resetPassword/:email/:token',
        component:ResetpasswordComponent
    },
    { 
        path: 'completeRegistration/:email/:token',
        component:EmailVerifyComponent
    },
    { 
        path: 'logout',
        component: LogoutComponent
    },
    { 
        path: '',
        redirectTo: 'index',
        pathMatch: 'full'
    },
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule { }