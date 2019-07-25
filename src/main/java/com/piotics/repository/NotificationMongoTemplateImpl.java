package com.piotics.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.piotics.model.Notification;
import com.piotics.model.UserProfile;

@Repository
public class NotificationMongoTemplateImpl implements NotificationMongoTemplate {

	@Autowired
	MongoTemplate mongoTemplate;

	@Override
	public UserProfile updateUserNotificationCount(Notification notification) {
		Query query = new Query(Criteria.where("id").is(notification.getUserToNotify().getId()));
		Update update = new Update().inc("newNotifications", 1);

		return mongoTemplate.findAndModify(query, update, UserProfile.class);
	}

	@Override
	public void resetUserNotificationCount(String id) {
		Query query = new Query(Criteria.where("id").is(id));
		Update update = new Update().set("newNotifications", 0);
		mongoTemplate.findAndModify(query, update, UserProfile.class);
	}
	
	@Override
	public void markAsReadNotifcation(String id) {
		
		Query query = new Query(Criteria.where("id").is(id));
		Update update = new Update().set("read", true);
		mongoTemplate.findAndModify(query, update, Notification.class);
	}

}
