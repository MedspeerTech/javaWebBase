package com.piotics.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.piotics.model.ApplicationUser;
import com.piotics.model.Session;
import com.piotics.model.UserProfile;
import com.piotics.resources.ResponseObj;
import com.piotics.resources.SessionUser;
import com.piotics.service.UserProfileService;

@RestController
@EnableAutoConfiguration
public class HomeController {
	
	@Autowired
	UserProfileService userProfileService;

	@GetMapping(value = "/api/session")
    public ResponseEntity<ResponseObj> getSession(Principal principal)
    {
        if(principal==null){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        String id = ((Session) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getId();
        
        SessionUser sessionUser=new SessionUser();
        sessionUser.setId(id);
        UserProfile userProfile = userProfileService.getProfile(id);
        sessionUser.setFileId(userProfile.getFileId());
        sessionUser.setUsername(userProfile.getUsername());  
        
        return new ResponseEntity(sessionUser,HttpStatus.OK);
    }
}
