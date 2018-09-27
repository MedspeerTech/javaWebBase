package medspeer.tech.controller;

import javax.validation.Valid;

import medspeer.tech.model.PasswordResetResource;
import medspeer.tech.model.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import medspeer.tech.model.ApplicationUser;
import medspeer.tech.model.PasswordReset;
import medspeer.tech.service.UserService;

@RestController
@RequestMapping(value="/user")
public class UserController {
	
	@Autowired
	UserService userService;
	
	@RequestMapping(value="/signUp",method=RequestMethod.POST)
	public ResponseEntity SignUp(@Valid @RequestBody ApplicationUser applicationUser){
		userService.signUp(applicationUser);
		return new ResponseEntity(HttpStatus.ACCEPTED);
	}

	@RequestMapping(value="/verifyEmail",method=RequestMethod.POST)
	public ResponseEntity verifyEmail(@Valid @RequestBody Token token) {
		userService.verifyEmail(token);
		return new ResponseEntity(HttpStatus.OK);
		
	}
	
	@RequestMapping(value="/forgotPassword",method=RequestMethod.GET)
	public ResponseEntity forgotPassword(@Valid @RequestParam String Username) {
		userService.forgotPassword(Username);
		return new ResponseEntity(HttpStatus.OK);
	}

	@RequestMapping(value="/resetPassword",method=RequestMethod.POST)
	public ResponseEntity resetPassword(@Valid @RequestBody PasswordReset passwordReset) {
		userService.resetPassword(passwordReset);
		return new ResponseEntity(HttpStatus.ACCEPTED);
		
	}
	
	@RequestMapping(value="/changePassword",method=RequestMethod.POST)
	public ResponseEntity changePassword(@Valid @RequestBody PasswordResetResource passwordresetResource) {
		userService.changePassword(passwordresetResource);
		return new ResponseEntity(HttpStatus.ACCEPTED);
		
	}
}
