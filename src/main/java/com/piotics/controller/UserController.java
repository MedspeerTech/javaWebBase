package com.piotics.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.piotics.model.PasswordResetResource;
import com.piotics.model.SignUpUser;
import com.piotics.model.Token;
import com.piotics.model.UserProfile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.piotics.model.PasswordReset;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.piotics.model.ApplicationUser;
import com.piotics.model.Invitation;
import com.piotics.service.UserService;

@RestController
@RequestMapping(value="/user")
public class UserController {
	
	@Autowired
	UserService userService;

	@RequestMapping(value = "/invite", method = RequestMethod.POST)
	public ResponseEntity<Invitation> invite(@RequestBody Invitation invitation) throws Exception {

		invitation = userService.invite(invitation);

		return new ResponseEntity<Invitation>(invitation, HttpStatus.OK);
	}

	@RequestMapping(value = "/signUp", method = RequestMethod.POST)
	public ResponseEntity<ApplicationUser> SignUp(@Valid @RequestBody SignUpUser signUpUser, HttpServletRequest req) throws Exception {
		ApplicationUser appUser = userService.signUp(signUpUser, req);
		return new ResponseEntity<ApplicationUser>(appUser,HttpStatus.ACCEPTED);
	}

	@RequestMapping(value = "/verifyEmail", method = RequestMethod.POST)
	public ResponseEntity verifyEmail(@Valid @RequestBody Token token) {
		userService.verifyEmail(token);
		return new ResponseEntity(HttpStatus.OK);
	}

	@RequestMapping(value = "/forgotPassword", method = RequestMethod.GET)
	public ResponseEntity forgotPassword(@Valid @RequestParam String username) throws Exception {
		userService.forgotPassword(username);
		return new ResponseEntity(HttpStatus.OK);
	}

	@RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
	public ResponseEntity resetPassword(@Valid @RequestBody PasswordReset passwordReset) {
		userService.resetPassword(passwordReset);
		return new ResponseEntity(HttpStatus.ACCEPTED);
	}

	@RequestMapping(value = "/changePassword", method = RequestMethod.POST)
	public ResponseEntity changePassword(@Valid @RequestBody PasswordResetResource passwordresetResource) {
		userService.changePassword(passwordresetResource);
		return new ResponseEntity(HttpStatus.ACCEPTED);
	}

	@RequestMapping(value = "/verifyIdToken", method = RequestMethod.POST)
	public ResponseEntity verifyIdToken(Authentication authentication, @RequestParam String idToken,
			HttpServletRequest req, HttpServletResponse res) throws FirebaseAuthException {
		FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
		String uid = decodedToken.getUid();

//		String uid = "12333";

		userService.verifyIdToken(authentication, res, decodedToken, req);
//		userService.verifyIdToken(authentication,res,uid);

		return new ResponseEntity(HttpStatus.OK);
	}
	
	@RequestMapping(value="/editProfile",method=RequestMethod.POST)
	public ResponseEntity<UserProfile> editProfile(@RequestBody UserProfile userProfile) {
		userProfile = userService.editProfile(userProfile);
		return new ResponseEntity<UserProfile>(userProfile,HttpStatus.ACCEPTED);
	}
	
	@RequestMapping(value="/getProfile",method=RequestMethod.GET)
	public ResponseEntity<UserProfile> getProfile(@RequestParam String id) {
		UserProfile userProfile = userService.getProfile(id);
		return new ResponseEntity<UserProfile>(userProfile,HttpStatus.OK);
	}
	
	@RequestMapping(value="/resetMail",method=RequestMethod.POST)
	public ResponseEntity<UserProfile> resetMail(@RequestBody UserProfile userProfile) throws Exception{
		userService.resetMail(userProfile);
		return new ResponseEntity<UserProfile>(HttpStatus.OK);
	}
	
	
	
}
