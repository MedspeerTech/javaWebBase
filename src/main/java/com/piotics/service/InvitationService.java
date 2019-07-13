package com.piotics.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.piotics.common.TokenType;
import com.piotics.model.Invitation;
import com.piotics.model.Token;
import com.piotics.repository.InvitationMongoRepository;

@Service
public class InvitationService {

	@Autowired
	InvitationMongoRepository invitationMongoRepository;
	
	@Autowired
	TokenService tokenService;
	
	public Invitation save(Invitation invitation) {
		
		return invitationMongoRepository.save(invitation);
	}

	public boolean isInvited(String username, Token token) {

		Token dbToken = tokenService.getTokenFromDB(username);

		if (dbToken != null) {

			if (tokenService.isTokenValid(dbToken)) {

				if (dbToken.getTokenType().equals(TokenType.INVITATION)) {

					return true;
				} else {
					return false;
				}

			} else {
				tokenService.deleteInviteToken(username, dbToken);
				return false;
			}
		} else {

			return false;
		}
	}
}
