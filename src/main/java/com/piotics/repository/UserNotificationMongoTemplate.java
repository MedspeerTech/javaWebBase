package com.piotics.repository;

import com.piotics.model.Notification;
import com.piotics.model.UserNotification;

public interface UserNotificationMongoTemplate {

	UserNotification updateUserNotificationList(String id,Notification notification);
}
