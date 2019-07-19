package com.piotics.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.piotics.model.UserNotification;

public interface UserNotificationMongoRepository extends MongoRepository<UserNotification, String>{
	
}
