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
	
	@RequestMapping(value="/signup",method=RequestMethod.POST)
	public ResponseEntity<ApplicationUser> Signup(@Valid @RequestBody ApplicationUser applicationUser){
		ApplicationUser newUser=userService.signup(applicationUser);
		return new ResponseEntity<ApplicationUser>(newUser,HttpStatus.ACCEPTED);
		/*System.out.println("Welcome spade");
		return null;*/
	}

	@RequestMapping(value="/verifyemail",method=RequestMethod.POST)
	public ResponseEntity verifyEmail(@Valid @RequestBody Token token) {
		userService.verifyEmail(token);
		return new ResponseEntity(HttpStatus.OK);
		
	}
	
	@RequestMapping(value="/forgotpassword",method=RequestMethod.GET)
	public ResponseEntity forgotPassword(@Valid @RequestParam String Username) {
		userService.forgotpassword(Username);
		return new ResponseEntity(HttpStatus.OK);
	}

	@RequestMapping(value="/resetpassword",method=RequestMethod.POST)
	public ResponseEntity resetPasswore(@Valid @RequestBody PasswordReset passwordReset) {
		userService.resetPassword(passwordReset);
		return new ResponseEntity(HttpStatus.ACCEPTED);
		
	}
	
	@RequestMapping(value="/changePassword",method=RequestMethod.POST)
	public ResponseEntity changePassword(@Valid @RequestBody PasswordResetResource passwordresetResource) {
		userService.changePassword(passwordresetResource);
		return new ResponseEntity(HttpStatus.ACCEPTED);
		
	}
}
