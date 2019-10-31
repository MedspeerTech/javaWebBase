package com.piotics.service;

import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.piotics.common.TimeManager;
import com.piotics.common.TokenManager;
import com.piotics.common.TokenType;
import com.piotics.exception.TokenException;
import com.piotics.model.ApplicationUser;
import com.piotics.model.Session;
import com.piotics.model.Token;
import com.piotics.repository.TokenMongoRepository;

@Service
public class TokenService {
	
	@Autowired
	TokenMongoRepository tokenMongoRepository;
	
	@Autowired
	TokenManager tokenManager;
	
	@Autowired
	TimeManager timeManager;

	@Value("${token.expiration.days}")
	Integer tokenExpDays;
	
	public void deleteInviteToken(String username, Token token) {
		
		if(token != null) {
			
			tokenMongoRepository.deleteByUsernameAndTokenAndTokenType(username,
					token.getToken(), TokenType.INVITATION);
		}else {
			
			tokenMongoRepository.deleteByUsernameAndTokenType(username,
					TokenType.INVITATION);
		}
		
	}
	
	public void deleteInviteTkenByUsername(String username) {
		
		tokenMongoRepository.deleteByUsernameAndTokenType(username,
				TokenType.INVITATION);
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

	public Token getTokenFromDBWithTokenType(String username, TokenType tokenType) {

		return tokenMongoRepository.findByUsernameAndTokenType(username,tokenType);

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

	public Token getMailResetToken(Session session, String email) {
		
		Token token = tokenManager.getTokenForMailReset(email);
		token.setUserId(session.getId());
		token.setTokenType(TokenType.MAIL_RESET);
		return tokenMongoRepository.save(token);
	}

	public void deleteToken(Token token) {

		tokenMongoRepository.delete(token);		
	}
	
	public boolean isTokenValid(Token dbToken) {
		ZonedDateTime currentDate = timeManager.getCurrentTimestamp();

		final ZoneId systemDefault = ZoneId.systemDefault();
		int days = Period
				.between(currentDate.toLocalDate(),
						ZonedDateTime.ofInstant(dbToken.getCreationDate().toInstant(), systemDefault).toLocalDate())
				.getDays();

		if (days > tokenExpDays) {
			
			deleteToken(dbToken);
			throw new TokenException("ExpiredToken");
		} else {

			return true;
		}
	}

	public void deleteByUsernameAndTokenType(String username, TokenType tokenType) {

		tokenMongoRepository.deleteByUsernameAndTokenType(username,tokenType);
	}

	public Token getTokenFromDbByUserIdAndTokenType(String id, TokenType tokenType) {

		return tokenMongoRepository.findByUserIdAndTokenType(id,tokenType);
	}
	
}
