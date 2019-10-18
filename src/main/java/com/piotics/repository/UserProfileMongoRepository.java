package com.piotics.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.piotics.model.UserProfile;

public interface UserProfileMongoRepository extends MongoRepository<UserProfile, String> {

	UserProfile findByEmail(String email);

}
