package com.piotics.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.piotics.model.Notification;
import com.piotics.model.UserNotification;

@Repository
public class UserNotificationMongoTemplateImpl implements UserNotificationMongoTemplate {

	
	@Autowired
	MongoTemplate mongoTemplate;
		
	public UserNotification updateUserNotificationList(String id,Notification notification){
		
		Query query = new Query(Criteria.where("id").is(id));
		Update update = new Update().addToSet("notifications", notification);
		update.inc("newNotifications", 1);
		
		return mongoTemplate.findAndModify(query, update, UserNotification.class);		
	}
}
