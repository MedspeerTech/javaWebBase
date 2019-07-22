package com.piotics.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.piotics.common.NotificationType;
import com.piotics.common.utils.UtilityManager;
import com.piotics.model.ApplicationUser;
import com.piotics.model.Invitation;
import com.piotics.model.Notification;
import com.piotics.model.UserNotification;
import com.piotics.model.UserShort;
import com.piotics.repository.UserNotificationMongoRepository;
import com.piotics.repository.UserNotificationMongoTemplateImpl;;

@Service
public class NotificationService {

//	@Autowired
//	NotificationMongoRepository notificationMongoRepository;
	
	@Autowired
	UtilityManager utilityManager;

	@Autowired
	UserService userService;

	@Autowired
	UserNotificationMongoRepository userNotificationMongoRepository;

	@Autowired
	UserNotificationMongoTemplateImpl userNotificationMongoTemplateImpl;

	
	public void notifyAdminsOnUserInvite(ApplicationUser applicationUser, Invitation item) {
		UserShort owner = userService.getUserShort(applicationUser.getId());
		List<UserShort> notifyTo = userService.getUserShortOfAdmins();
		addNotification(owner, NotificationType.INVITATION, item.getId(), notifyTo);
	}

	public void addNotification(UserShort owner, NotificationType notificationType, String itemId,
			List<UserShort> notifyTo) {

		
		Notification notification = new Notification(owner, notificationType, itemId);
		notification.setId(utilityManager.generateObjectId());
		
		for (UserShort userShort : notifyTo) {

			if (!userShort.getId().equals(owner.getId())) {
				updateUserNotification(userShort.getId(), notification);
			}
		}

	}

	private UserNotification updateUserNotification(String id, Notification notification) {

		return userNotificationMongoTemplateImpl.updateUserNotificationList(id, notification);
	}

	public void save(UserNotification userNotification) {

		userNotificationMongoRepository.save(userNotification);
	}
}
