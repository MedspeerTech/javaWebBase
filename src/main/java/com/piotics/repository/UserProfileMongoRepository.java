package com.piotics.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.piotics.model.UserProfile;

public interface UserProfileMongoRepository extends MongoRepository<UserProfile, String> {

	UserProfile findByEmail(String email);

	UserProfile findByPhone(String phone);

	List<UserProfile> findByTenantRelationsTenantName(String name);

}
