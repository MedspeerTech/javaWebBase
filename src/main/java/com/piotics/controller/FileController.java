package com.piotics.controller;

import java.io.IOException;
import java.nio.file.Path;
import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.piotics.common.MultipartFileSender;
import com.piotics.model.ApplicationUser;
import com.piotics.model.FileMeta;
import com.piotics.model.Session;
import com.piotics.service.ConversionsService;
import com.piotics.service.FileService;

@RestController
@RequestMapping(value = "file")
public class FileController {

	@Autowired
	FileService fileService;

	@Autowired
	ConversionsService conversionsService;

	@PostMapping(value = "/upload")
	public ResponseEntity<FileMeta> upload(Principal principal, @RequestPart("file") MultipartFile file)
			throws IOException {

		Session session = (Session) ((Authentication) (principal)).getPrincipal();
		if (!file.isEmpty()) {
			FileMeta fileMeta = fileService.saveFile(session, file);
			return new ResponseEntity<>(fileMeta, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
		}

	}

	@PostMapping(value = "/startVideoConversion")
	public void startVideoConversion(){

		conversionsService.convertVideos();
	}

	@GetMapping(value = "/getFileMeta/{fileId}")
	public ResponseEntity<FileMeta> getFileMeta(@PathVariable String fileId) {

		FileMeta fileMeta = fileService.getFileMeta(fileId);
		return new ResponseEntity<>(fileMeta, HttpStatus.OK);
	}

	@GetMapping(value = "/stream/video/{id}")
	public ResponseEntity<HttpStatus> streamVideo(@PathVariable("id") String fileId, HttpServletRequest httprequest,
			HttpServletResponse httpresponse) throws Exception {

		Path path = fileService.getVideoPath(fileId);
		MultipartFileSender.fromPath(path).with(httprequest).with(httpresponse).serveResource();
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping(value = "/getImage/{fileId}")
	public byte[] getImage(@PathVariable String fileId) {

		return fileService.getImage(fileId);
	}

	@GetMapping(value = "/getPreview/{fileId}")
	public byte[] getPreview(@PathVariable("fileId") String fileId) {

		return fileService.getPreview(fileId);
	}

	@GetMapping(value = "/getSlide/{fileId}/{slideNo}")
	public byte[] getSlide(@PathVariable("slideNo") int slideNo, @PathVariable String fileId) {

		return fileService.getSlide(fileId, slideNo);
	}

	@GetMapping(value = "/getFile/{id}")
	public byte[] getFile(@PathVariable("id") String fileId) {

		return fileService.getFile(fileId);
	}

}
