package com.piotics.controller;

import java.security.Principal;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.piotics.model.ApplicationUser;
import com.piotics.model.PasswordResetResource;
import com.piotics.model.Session;
import com.piotics.model.UserProfile;
import com.piotics.service.UserProfileService;

@RestController
@RequestMapping(value = "/profile")
public class ProfileController extends BaseController {

	@Autowired
	UserProfileService userProfileService;

	@PostMapping(value = "/save")
	@PreAuthorize("@AccessFilter.hasAccess(authentication,#userProfile.getId())")
	public ResponseEntity<UserProfile> saveProfile(@Valid @RequestBody UserProfile userProfile) {

		userProfile = userProfileService.saveProfile(userProfile);
		return new ResponseEntity<>(userProfile, HttpStatus.ACCEPTED);
	}

	@GetMapping(value = "/get/{id}")
	public ResponseEntity<UserProfile> getProfile(@PathVariable String id) {

		UserProfile userProfile = userProfileService.getProfile(id);
		return new ResponseEntity<>(userProfile, HttpStatus.OK);
	}

	@PostMapping(value = "/changeMail/{mail}")
	public ResponseEntity<UserProfile> changeMail(Principal principal, @PathVariable String mail) throws Exception {

		Session session = (Session) ((Authentication) (principal)).getPrincipal();
		UserProfile userProfile = userProfileService.changeMail(session, mail);
		return new ResponseEntity<>(userProfile, HttpStatus.OK);
	}

	@PostMapping(value = "/verifyNewMail/{token}")
	public ResponseEntity <HttpStatus>verifyNewMail(Principal principal, @PathVariable String token) throws Exception {

		Session session = (Session) ((Authentication) (principal)).getPrincipal();
		userProfileService.verifyNewMail(session, token);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(value = "/changePassword")
	public ResponseEntity<HttpStatus> changePassword(Principal principal,
			@Valid @RequestBody PasswordResetResource passwordresetResource){

		Session session = (Session) ((Authentication) (principal)).getPrincipal();
		userProfileService.changePassword(session, passwordresetResource);
		return new ResponseEntity<>(HttpStatus.ACCEPTED);
	}

}
