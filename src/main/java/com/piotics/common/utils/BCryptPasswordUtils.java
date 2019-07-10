package com.piotics.common.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptPasswordUtils {
	
	
	BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
	
	public String encodePassword(String password) {
		return bCryptPasswordEncoder.encode(password);
	}
	
	public boolean isMatching(String password1,String password2) {
		
		return bCryptPasswordEncoder.matches(password1, password2);
	}

}
