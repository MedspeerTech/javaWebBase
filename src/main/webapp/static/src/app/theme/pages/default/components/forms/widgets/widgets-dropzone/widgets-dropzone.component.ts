import { Input, Output, EventEmitter, HostListener } from '@angular/core';
import { Http, RequestOptions, Headers } from '@angular/http';
import { Component, OnInit, ViewEncapsulation, AfterViewInit, ElementRef } from '@angular/core';
import { Helpers } from '../../../../../../../helpers';
import { ScriptLoaderService } from '../../../../../../../_services/script-loader.service';
import { FormsModule } from '@angular/forms';
import { Observable } from 'rxjs/Observable';
import { FileUploaderService } from './file-uploader.service';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/observable/throw';


declare let Dropzone: any;
@Component({
    selector: ".m-grid__item.m-grid__item--fluid.m-wrapper",
    templateUrl: "./widgets-dropzone.component.html",
    encapsulation: ViewEncapsulation.None,
    providers: [FileUploaderService]
})
export class WidgetsDropzoneComponent {
    public imageChangedEvent: any = '';
    public croppedImage: any = ''; 
    public baseImage: any = '';
    private headers: Headers;
    private http: Http;
    
    errors: Array<string> =[];
    dragAreaClass: string = 'dragarea';
    @Input() projectId: number = 0;
    @Input() sectionId: number = 0;
    @Input() fileExt: string = "JPG, GIF, PNG";
    @Input() maxFiles: number = 5;
    @Input() maxSize: number = 5; // 5MB
    @Output() uploadStatus = new EventEmitter();

    constructor(private _script: ScriptLoaderService, private elem: ElementRef, private fileUploader: FileUploaderService, private _http:Http) {
        
    }
    

    public uploadFile(): void{
        let files = this.elem.nativeElement.querySelector('#selectFile').files;
        let formData = new FormData();
        let file = files[0];
        formData.append('file', file, file.name);
        let forms = formData;
        console.log(forms);
        this.fileUploader.uploadFile(formData)
        .subscribe(res => {
            console.log('File Uploaded');
        });
    }

    fileChangeEvent(event: any): void {
        this.imageChangedEvent = event;
    }
    imageCropped(image: string) {
        this.croppedImage = image;
        // let baseImage = this.croppedImage;
        
    }

    public imageCropping(formData: any){
        let imageData1 = this.croppedImage;
        
        let fromData = new FormData();
        fromData.append('imageData', imageData1);
        // let img1 = imgData.append('imageData', imageData);
        // console.log(img1);
        let currentUser = localStorage.getItem('currentUser');
        console.log(currentUser);
        let headers = new Headers();
        headers.append('Authorization', currentUser);
        // headers.append('Content-Type', 'image/jpeg');
        headers.append('Accept', 'application/json');
        let options = new RequestOptions({headers: headers});
        console.log(options);
        return this._http.post('/user/uploadProfileImage', fromData, options)
        .subscribe(res => {
            console.log('Image Uploaded');
        });
    }
}
