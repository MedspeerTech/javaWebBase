package com.piotics.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.piotics.common.NotificationType;
import com.piotics.common.TimeManager;
import com.piotics.common.utils.UtilityManager;
import com.piotics.model.ApplicationUser;
import com.piotics.model.Invitation;
import com.piotics.model.Notification;
import com.piotics.model.UserShort;
import com.piotics.repository.NotificationMongoRepository;
import com.piotics.resources.NotificationResource;;

@Service
public class NotificationService {

	@Autowired
	NotificationMongoRepository notificationMongoRepository;

	@Autowired
	UtilityManager utilityManager;

	@Autowired
	UserService userService;

	@Autowired
	TimeManager timeManager;

	public void notifyAdminsOnUserInvite(ApplicationUser applicationUser, Invitation item) {
		UserShort owner = userService.getUserShort(applicationUser.getId());
		List<UserShort> notifyTo = userService.getUserShortOfAdmins();
		addNotification(owner, NotificationType.INVITATION, item, notifyTo);
	}

	public void addNotification(UserShort owner, NotificationType notificationType, Invitation item,
			List<UserShort> usersToNotify) {

		for (UserShort userShort : usersToNotify) {

			if (!userShort.getId().equals(owner.getId())) {

				Notification notification = new Notification(owner, userShort, notificationType, item.getId());
//				notification.setTest(item.getEmail());
				notificationMongoRepository.save(notification);
			}
		}

	}

	public NotificationResource getNotificationList(ApplicationUser applicationUser, int pageNo) {

		Pageable pageable = new PageRequest(pageNo - 1, 10);
		List<Notification> notifications = notificationMongoRepository
				.findTop10ByUserToNotifyIdAndReadFalseOrderByCreatedOnDesc(applicationUser.getId(), pageable);

		return new NotificationResource(notifications);
	}
}
