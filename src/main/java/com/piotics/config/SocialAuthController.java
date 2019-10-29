package com.piotics.config;

import static com.piotics.config.SecurityConstants.HEADER_STRING;
import static com.piotics.config.SecurityConstants.TOKEN_PREFIX;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.piotics.resources.SocialUser;
import com.piotics.service.SocialAuthService;

@RestController
@RequestMapping(value = "/socialAuth")
public class SocialAuthController {

	@Autowired
	SocialAuthService socialAuthService;
	
	@PostMapping(value = "/login")
	public void socialLogin(@RequestBody SocialUser socialUser, HttpServletResponse res){
		String token = socialAuthService.socialLogin(socialUser);
		res.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
	}

	@PostMapping(value = "/signUp")
	public void socialSignUp(@RequestBody SocialUser socialUser){
		socialAuthService.socialLogin(socialUser);
	}
}
