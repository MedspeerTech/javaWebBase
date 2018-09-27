package medspeer.tech.controller;

import java.io.IOException;
import java.security.Principal;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import medspeer.tech.service.UserService;

@RestController
@RequestMapping(value="/user")
public class UserInfoController {
	
	
	@Autowired
	UserService userService;
	
	@RequestMapping(value = "/uploadProfileImage", method = RequestMethod.POST)
	public ResponseEntity updateProfileImage(@Valid Principal principal, @RequestParam String imageData) throws IOException{
		((Authentication) principal).getPrincipal();
		//ApplicationUser applicationUser=new ApplicationUser();
		userService.updateProfileImage(imageData);
		return new ResponseEntity(HttpStatus.ACCEPTED);
	}

}
