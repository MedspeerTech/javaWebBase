package com.piotics.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.piotics.model.ApplicationSocialUser;

public interface ApplicationSocialUserMongoRepository extends MongoRepository<ApplicationSocialUser, String>{

	Optional<ApplicationSocialUser> findBySocialId(String id);

	ApplicationSocialUser findByEmail(String email);

}
