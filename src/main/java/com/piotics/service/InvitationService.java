package com.piotics.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.piotics.model.Invitation;
import com.piotics.repository.InvitationMongoRepository;

@Service
public class InvitationService {

	@Autowired
	InvitationMongoRepository invitationMongoRepository;
	public Invitation save(Invitation invitation) {
		
		return invitationMongoRepository.save(invitation);
	}

}
