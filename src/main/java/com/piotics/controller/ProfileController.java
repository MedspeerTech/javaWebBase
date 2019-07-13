package com.piotics.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.piotics.model.PasswordResetResource;
import com.piotics.model.UserProfile;
import com.piotics.service.UserProfileService;

@RestController
@RequestMapping(value = "/profile")
public class ProfileController {
	
	@Autowired
	UserProfileService userProfileService;

	@RequestMapping(value = "/editProfile", method = RequestMethod.POST)
	public ResponseEntity<UserProfile> editProfile(@RequestBody UserProfile userProfile) {
		userProfile = userProfileService.editProfile(userProfile);
		return new ResponseEntity<UserProfile>(userProfile, HttpStatus.ACCEPTED);
	}

	@RequestMapping(value = "/getProfile", method = RequestMethod.GET)
	public ResponseEntity<UserProfile> getProfile(@RequestParam String id) {
		UserProfile userProfile = userProfileService.getProfile(id);
		return new ResponseEntity<UserProfile>(userProfile, HttpStatus.OK);
	}

	@RequestMapping(value = "/resetMail", method = RequestMethod.POST)
	public ResponseEntity<UserProfile> resetMail(@RequestBody UserProfile userProfile) throws Exception {
		userProfileService.resetMail(userProfile);
		return new ResponseEntity<UserProfile>(HttpStatus.OK);
	}

	@RequestMapping(value = "/changePassword", method = RequestMethod.POST)
	public ResponseEntity changePassword(@Valid @RequestBody PasswordResetResource passwordresetResource) {
		userProfileService.changePassword(passwordresetResource);
		return new ResponseEntity(HttpStatus.ACCEPTED);
	}

}
