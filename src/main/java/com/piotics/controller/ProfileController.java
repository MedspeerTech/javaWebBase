package com.piotics.controller;

import java.security.Principal;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.piotics.model.ApplicationUser;
import com.piotics.model.PasswordResetResource;
import com.piotics.model.UserProfile;
import com.piotics.service.UserProfileService;

@RestController
@RequestMapping(value = "/profile")
public class ProfileController extends BaseController {

	@Autowired
	UserProfileService userProfileService;

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@PreAuthorize("@AccessFilter.hasAccess(authentication,#userProfile.getId())")
	public ResponseEntity<UserProfile> saveProfile(@Valid @RequestBody UserProfile userProfile) {

		userProfile = userProfileService.saveProfile(userProfile);
		return new ResponseEntity<UserProfile>(userProfile, HttpStatus.ACCEPTED);
	}

	@RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
	public ResponseEntity<UserProfile> getProfile(@PathVariable String id) {

		UserProfile userProfile = userProfileService.getProfile(id);
		return new ResponseEntity<UserProfile>(userProfile, HttpStatus.OK);
	}

	@RequestMapping(value = "/changeMail/{mail}", method = RequestMethod.POST)
	public ResponseEntity<UserProfile> changeMail(Principal principal, @PathVariable String mail) throws Exception {

		ApplicationUser applicationUser = (ApplicationUser) ((Authentication) (principal)).getPrincipal();
		UserProfile userProfile = userProfileService.changeMail(applicationUser, mail);
		return new ResponseEntity<UserProfile>(userProfile, HttpStatus.OK);
	}

	@RequestMapping(value = "/verifyNewMail/{token}", method = RequestMethod.POST)
	public ResponseEntity verifyNewMail(Principal principal, @PathVariable String token) throws Exception {

		ApplicationUser applicationUser = (ApplicationUser) ((Authentication) (principal)).getPrincipal();
		userProfileService.verifyNewMail(applicationUser, token);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/changePassword", method = RequestMethod.POST)
	public ResponseEntity changePassword(Principal principal,
			@Valid @RequestBody PasswordResetResource passwordresetResource) throws Exception {

		ApplicationUser applicationUser = (ApplicationUser) ((Authentication) (principal)).getPrincipal();
		userProfileService.changePassword(applicationUser, passwordresetResource);
		return new ResponseEntity<>(HttpStatus.ACCEPTED);
	}

}
