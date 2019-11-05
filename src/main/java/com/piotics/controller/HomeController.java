package com.piotics.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.piotics.model.ApplicationUser;
import com.piotics.model.Session;
import com.piotics.model.TenantShort;
import com.piotics.model.UserShort;
import com.piotics.resources.ResponseObj;
import com.piotics.resources.SessionUser;
import com.piotics.service.UserService;

@RestController
@EnableAutoConfiguration
public class HomeController {

	@Autowired
	UserService userService;

	@GetMapping(value = "/api/session")
    public ResponseEntity<ResponseObj> getSession(Principal principal)
    {
        if(principal==null)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        
        String id = ((Session) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getId();
        
        UserShort userShort = userService.getUserShort(id);
        ApplicationUser applicationUser = userService.getApplicationUser(id);
        TenantShort tenantShort = new TenantShort(applicationUser);
        SessionUser sessionUser=new SessionUser(userShort,tenantShort);
       
        return new ResponseEntity(sessionUser,HttpStatus.OK);
    }
}
