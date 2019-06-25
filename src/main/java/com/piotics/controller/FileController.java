package com.piotics.controller;

import java.io.IOException;
import java.nio.file.Path;
import java.security.Principal;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.piotics.common.MultipartFileSender;
import com.piotics.model.ApplicationUser;
import com.piotics.model.FileMeta;
import com.piotics.service.ConversionsService;
import com.piotics.service.FileService;

@RestController
@RequestMapping(value = "file")
public class FileController {

	@Autowired
	FileService fileService;

	@Autowired
	ConversionsService conversionsService;

	@RequestMapping(value = "/upload")
	public ResponseEntity upload(Principal principal, @RequestPart("file") MultipartFile file) throws IOException {

		ApplicationUser applicationUser = ((ApplicationUser) ((Authentication) (principal)).getPrincipal());
		((Authentication) principal).getPrincipal();
		if (!file.isEmpty()) {
			FileMeta fileMeta = fileService.saveFile(applicationUser, file);
			return new ResponseEntity(fileMeta, HttpStatus.OK);
		} else {
			return new ResponseEntity(HttpStatus.FAILED_DEPENDENCY);
		}

	}

	@RequestMapping(value = "/startVideoConversion", method = RequestMethod.POST)
	public void startVideoConversion() throws IOException {

		conversionsService.convertVideos();
	}

	@RequestMapping(value = "/getFileMeta/{fileId}")
	public ResponseEntity<FileMeta> getFileMeta(@PathVariable String fileId) {
		
		Optional<FileMeta> optFileMeta = fileService.getFileMeta(fileId);
		return new ResponseEntity<FileMeta>(optFileMeta.get(), HttpStatus.OK);
	}

	@RequestMapping(value = "/stream/video/{id}", method = RequestMethod.GET)
	public ResponseEntity streamVideo(@PathVariable("id") String fileId, HttpServletRequest httprequest,
			HttpServletResponse httpresponse) throws Exception {

		Path path = fileService.getVideoPath(fileId, httprequest, httpresponse);
		MultipartFileSender.fromPath(path).with(httprequest).with(httpresponse).serveResource();
		return new ResponseEntity(HttpStatus.OK);
	}

	@RequestMapping(value = "/getImage/{fileId}")
	public byte[] getImage(@PathVariable Integer fileId) {
		
		return fileService.getImage(fileId);
	}

	@RequestMapping(value = "/getPreview/{fileId}", method = RequestMethod.GET)
	private byte[] getPreview(@PathVariable("fileId") String fileId) {

		return fileService.getPreview(fileId);
	}

	@RequestMapping(value = "/getSlide/{fileId}/{slideNo}", method = RequestMethod.GET)
	private byte[] getSlide(@PathVariable("slideNo") int slideNo, @PathVariable String fileId) {

		return fileService.getSlide(fileId, slideNo);
	}

	@RequestMapping(value = "/getFile/{id}", method = RequestMethod.GET)
	private byte[] getFile(@PathVariable("id") Integer fileId) {

		return fileService.getFile(fileId);
	}

}
