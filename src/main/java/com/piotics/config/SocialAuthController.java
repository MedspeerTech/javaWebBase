package com.piotics.config;

import static com.piotics.config.SecurityConstants.*;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.piotics.resources.SocialUser;
import com.piotics.service.SocialAuthService;

@RestController
@RequestMapping(value = "/socialAuth")
public class SocialAuthController {

	@Autowired
	SocialAuthService socialAuthService;
	
	@RequestMapping(method = RequestMethod.POST,value = "/login")
	public void socialLogin(@RequestBody SocialUser socialUser, HttpServletResponse res){
		String token = socialAuthService.socialLogin(socialUser);
		res.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
		return;
	}

	@RequestMapping(method = RequestMethod.POST,value = "/signUp")
	public void socialSignUp(@RequestBody SocialUser socialUser){

		socialAuthService.socialLogin(socialUser);

	}
}
