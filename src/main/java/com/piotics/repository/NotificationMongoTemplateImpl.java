package com.piotics.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.piotics.model.Notification;
import com.piotics.model.UserProfile;
import com.piotics.model.UserShort;
import com.piotics.resources.NotificationDocument;

@Repository
public class NotificationMongoTemplateImpl implements NotificationMongoTemplate {

	@Autowired
	MongoTemplate mongoTemplate;

	@Autowired
	MongoOperations mongoOperations;

	@Autowired
	NotificationDocument notificationDocument;

	@Override
	public UserProfile updateUserNotificationCount(Notification notification) {
		Query query = new Query(Criteria.where("id").is(notification.getUserToNotify().getId()));
		Update update = new Update().inc(notificationDocument.COUNT, 1);

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

	public void incrementNewNotificationCountByOneForUsersToNotify(List<UserShort> usersToNotify) {

		int count = 0;
		int batch = 100;

		BulkOperations bulkOps = mongoOperations.bulkOps(BulkOperations.BulkMode.UNORDERED, UserProfile.class);

		for (UserShort userToNotify : usersToNotify) {

			Query query = new Query();
			Criteria criteria = Criteria.where("_id").is(userToNotify.getId());
			query.addCriteria(criteria);

			Update update = new Update();
			update.set(notificationDocument.COUNT, 1);

			bulkOps.updateOne(query, update);

			count++;
			if (count == batch) {
				bulkOps.execute();
				count = 0;
			}
		}

		if (count > 0)
			bulkOps.execute();
	}
}
