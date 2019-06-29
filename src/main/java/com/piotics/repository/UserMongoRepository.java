package com.piotics.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.piotics.model.ApplicationUser;

public interface UserMongoRepository extends MongoRepository<ApplicationUser, String> {

	ApplicationUser findByUsername(String username);

	ApplicationUser findByUsernameAndPassword(String Username, String password);

	ApplicationUser findByEmail(String email);
	ApplicationUser findByPhone(String phone);
}
