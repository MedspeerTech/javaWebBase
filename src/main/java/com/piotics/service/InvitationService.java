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

	public boolean isInvited(String username) {

		Token dbToken = tokenService.getTokenFromDBWithTokenType(username, TokenType.INVITATION);

		boolean bool = false;

		if (dbToken == null) {
			return bool;
		} else if (tokenService.isTokenValid(dbToken)) {
			bool = true;
		}
		return bool;
	}

}
