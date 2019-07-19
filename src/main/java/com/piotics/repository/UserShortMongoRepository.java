package com.piotics.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.piotics.model.UserShort;

public interface UserShortMongoRepository extends MongoRepository<UserShort, String>{

}
