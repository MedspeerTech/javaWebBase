package medspeer.tech.controller;

import java.security.Principal;


import medspeer.tech.model.FileData;
import medspeer.tech.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping(value="/file")
public class FileController {

	@Autowired
    FileService fileService;

	@RequestMapping(value = "/upload")
	public ResponseEntity Upload(Principal principal, @RequestParam("file") MultipartFile file) {

		((Authentication) principal).getPrincipal();
		if (!file.isEmpty()) {
			FileData fileData = fileService.saveFile(file);
			return new ResponseEntity(fileData, HttpStatus.OK);
		} else {
			return new ResponseEntity(HttpStatus.FAILED_DEPENDENCY);
		}

	}

}
