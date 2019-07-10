package com.piotics.service;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.piotics.common.TokenManager;
import com.piotics.common.TokenType;
import com.piotics.model.ApplicationUser;
import com.piotics.model.Token;
import com.piotics.model.UserProfile;
import com.piotics.repository.TokenMongoRepository;

@Service
public class TokenService {
	
	@Autowired
	TokenMongoRepository tokenMongoRepository;
	
	@Autowired
	TokenManager tokenManager;

	public void deleteInviteToken(String username, Token token) {
		
		if(token != null) {
			
			tokenMongoRepository.deleteByUsernameAndTokenAndTokenType(username,
					token.getToken(), TokenType.INVITATION);
		}else {
			
			tokenMongoRepository.deleteByUsernameAndTokenType(username,
					TokenType.INVITATION);
		}
		
	}

	public Token getTokenForEmailVerification(ApplicationUser appUser) {
		
		Token token = tokenManager.getTokenForEmailVerification(appUser.getEmail());
		token.setUserId(appUser.getId());
		token.setTokenType(TokenType.EMAILVERIFICATION);
		return tokenMongoRepository.save(token);
	}

	public Token getInviteToken(String email) {
		
		return tokenManager.getTokenForInvite(email);
	}

	public Token save(Token token) {
		
		return tokenMongoRepository.save(token);
	}

	public Token getTokenFromDB(String username) {
		
		return tokenMongoRepository.findByUsername(username);

	}

	public void deleteByUsernameAndTokenAndTokenType(String username, String token, TokenType tokenType) {
		
		tokenMongoRepository.deleteByUsernameAndTokenAndTokenType(username,token,tokenType);
	}
	
	public Token getTokenByUserNameAndTokenType(String username,TokenType tokenType) {
		
		return tokenMongoRepository.findByUsernameAndTokenType(username, tokenType);
	}

	public Token getPasswordResetToken(@Valid String username) {

		return tokenManager.getTokenForPasswordReset(username);
	}

	public Token getMailResetToken(ApplicationUser appUser, String email) {
		
		Token token = tokenManager.getTokenForMailReset(email);
		token.setUserId(appUser.getId());
		token.setTokenType(TokenType.MAIL_RESET);
		return tokenMongoRepository.save(token);
	}

	public void deleteToken(Token token) {

		tokenMongoRepository.delete(token);		
	}
}
