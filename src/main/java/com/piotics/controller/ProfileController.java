package com.piotics.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.piotics.model.ApplicationUser;
import com.piotics.model.PasswordResetResource;
import com.piotics.model.Token;
import com.piotics.model.UserProfile;
import com.piotics.service.UserProfileService;
import java.security.Principal;

@RestController
@RequestMapping(value = "/profile")
public class ProfileController {
	
	@Autowired
	UserProfileService userProfileService;

	
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@PreAuthorize("@AccessFilter.isSessionUser(authentication,#userProfile")
	public ResponseEntity<UserProfile> saveProfile(@Valid @RequestBody UserProfile userProfile) {
		userProfile = userProfileService.saveProfile(userProfile);
		return new ResponseEntity<UserProfile>(userProfile, HttpStatus.ACCEPTED);
	}

	@RequestMapping(value = "/get", method = RequestMethod.GET)
	public ResponseEntity<UserProfile> getProfile(@RequestParam String id) {
		UserProfile userProfile = userProfileService.getProfile(id);
		return new ResponseEntity<UserProfile>(userProfile, HttpStatus.OK);
	}
}
