package com.piotics.repository;

import com.piotics.model.Notification;
import com.piotics.model.UserProfile;

public interface NotificationMongoTemplate {

	UserProfile updateUserNotificationCount(Notification notification);
	void resetUserNotificationCount(String id);
	void markAsReadNotifcation(String id);
}
