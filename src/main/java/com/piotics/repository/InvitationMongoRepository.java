package com.piotics.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.piotics.model.Invitation;

public interface InvitationMongoRepository extends MongoRepository<Invitation, String>{
	
	Invitation findByEmail(String email);
	Invitation findByPhone(String phone);

}
