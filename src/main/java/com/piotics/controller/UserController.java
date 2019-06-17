package com.piotics.controller;

import javax.validation.Valid;

import com.piotics.model.PasswordResetResource;
import com.piotics.model.SignUpUser;
import com.piotics.model.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.piotics.model.PasswordReset;

import com.piotics.model.ApplicationUser;
import com.piotics.model.Invitation;
import com.piotics.service.UserService;

@RestController
@RequestMapping(value="/user")
public class UserController {
	
	@Autowired
	UserService userService;
	
	
	@RequestMapping(value = "/invite",method = RequestMethod.POST)
	public ResponseEntity<Invitation> invite(@RequestBody Invitation invitation) throws Exception{
		
		invitation = userService.invite(invitation);
		
		return new ResponseEntity<Invitation>(invitation,HttpStatus.OK);
	}

	@RequestMapping(value="/signUp",method=RequestMethod.POST)
	public ResponseEntity SignUp(@Valid @RequestBody SignUpUser signUpUser) throws Exception{
		userService.signUp(signUpUser);
		return new ResponseEntity(HttpStatus.ACCEPTED);
	}

	@RequestMapping(value="/verifyEmail",method=RequestMethod.POST)
	public ResponseEntity verifyEmail(@Valid @RequestBody Token token) {
		userService.verifyEmail(token);
		return new ResponseEntity(HttpStatus.OK);
		
	}
	
//	@RequestMapping(value="/forgotPassword",method=RequestMethod.GET)
//	public ResponseEntity forgotPassword(@Valid @RequestParam String username) {
//		userService.forgotPassword(username);
//		return new ResponseEntity(HttpStatus.OK);
//	}
//
//	@RequestMapping(value="/resetPassword",method=RequestMethod.POST)
//	public ResponseEntity resetPassword(@Valid @RequestBody PasswordReset passwordReset) {
//		userService.resetPassword(passwordReset);
//		return new ResponseEntity(HttpStatus.ACCEPTED);
//		
//	}
//	
//	@RequestMapping(value="/changePassword",method=RequestMethod.POST)
//	public ResponseEntity changePassword(@Valid @RequestBody PasswordResetResource passwordresetResource) {
//		userService.changePassword(passwordresetResource);
//		return new ResponseEntity(HttpStatus.ACCEPTED);
//		
//	}
}
