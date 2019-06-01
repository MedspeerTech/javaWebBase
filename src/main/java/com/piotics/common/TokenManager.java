package com.piotics.common;

import java.math.BigInteger;
import java.security.SecureRandom;

import com.piotics.model.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TokenManager {
	
	@Autowired
	TimeManager timeManager;
	
	private static SecureRandom random=new SecureRandom();
	
	public static String getToken() {
		return new BigInteger(130,random).toString(32);
	}
	
	public Token getTokenForEmailVerification(String Username) {
		return new Token(Username,getToken(),TokenType.EMAILVERIFICATION,timeManager.getCurrentTimestamp());
		
	}
	
	public Token getTokenForPasswordReset(String Username) {
		return new Token(Username,getToken(),TokenType.PASSWORDRESET,timeManager.getCurrentTimestamp());
		
	}

}
