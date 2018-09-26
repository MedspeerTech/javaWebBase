import { Injectable } from '@angular/core';
import { Http, Response, RequestOptions, Headers } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/observable/throw';



@Injectable()
export class FileUploaderService {

    constructor(private _http:Http) { }
    public uploadFile(formData: any){
        let currentUser = localStorage.getItem('currentUser');
        // console.log(currentUser);
        let headers = new Headers();
        
        
        headers.append('Authorization',  'Bearer ' + currentUser);
        // headers.append('Authorization',  'Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzYW50aG9zaDE5OTFpdEBnbWFpbC5jb20iLCJleHAiOjE1MTYzNjk2MDl9._vYNecPI0VbqMfsywT-mfnMF04xgVErS2Ks9f8eNUajwLE_u6ci3WwSo4nkDM9rpk3HropbGmuOHc-11kbdl2g');
        // headers.append('Content-Type', 'multipart/form-data');
        headers.append('Accept', 'application/json');
        let options = new RequestOptions({headers: headers});
        return this._http.post('/file/upload', formData, options)
        .catch(this._errorHandler);
    }
    private _errorHandler(error: Response){
        console.log('Error occured: ' + error);
        return Observable.throw(error || 'Some Error Occured');
    }
}