package com.piotics.service;

import java.util.ArrayList;
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
import com.piotics.model.Tenant;
import com.piotics.model.UserProfile;
import com.piotics.model.UserShort;
import com.piotics.repository.NotificationMongoRepository;
import com.piotics.repository.NotificationMongoTemplateImpl;
import com.piotics.resources.NotificationResource;

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

	@Autowired
	UserProfileService userProfileService;

	@Autowired
	NotificationMongoTemplateImpl notificationMongoTemplateImpl;
	
	@Autowired
	TenantService tenantService;

	public void notifyAdminsOnUserInvite(ApplicationUser applicationUser, Invitation item, String title) {
		UserShort owner = userService.getUserShort(applicationUser.getId());
		List<UserShort> notifyTo = userService.getUserShortOfAdmins();
		addNotification(owner, NotificationType.INVITATION, item.getId(), notifyTo, title);
	}

	public void addNotification(UserShort owner, NotificationType notificationType, String itemId,
			List<UserShort> usersToNotify, String title) {

		List<Notification> notifications = new ArrayList<>();
		List<UserShort> notifyTo = new ArrayList<>();

		for (UserShort userToNotify : usersToNotify) {

			if (!userToNotify.getId().equals(owner.getId())) {

				Notification notification = new Notification(owner, userToNotify, notificationType, itemId, title);
				notifications.add(notification);
				notifyTo.add(userToNotify);
			}
		}
		notificationMongoRepository.saveAll(notifications);
		notificationMongoTemplateImpl.incrementNewNotificationCountByOneForUsersToNotify(notifyTo);
	}

	public NotificationResource getNotificationList(ApplicationUser applicationUser, int pageNo) {

		Pageable pageable = new PageRequest(pageNo - 1, 10);
		List<Notification> notifications = notificationMongoRepository
				.findTop10ByUserToNotifyIdOrderByCreatedOnDesc(applicationUser.getId(), pageable);

		return new NotificationResource(notifications);
	}

	public void readNotification(Notification notification) {
		notificationMongoTemplateImpl.markAsReadNotifcation(notification.getId());
	}

	public void resetNewNotificationCount(ApplicationUser applicationUser) {

		notificationMongoTemplateImpl.resetUserNotificationCount(applicationUser.getId());
	}

	public void notifyUserOnTenantInvitation(UserProfile userProfile, Invitation invitation) {
		UserShort userToNotify = userService.getUserShort(userProfile.getId());
		UserShort owner = invitation.getInvitedBY();
		Tenant tenant = tenantService.getTenantById(invitation.getTenantId());
		Notification notification = new Notification(owner, userToNotify, NotificationType.TENANT_INVITATION, invitation.getId(), tenant.getName());
		notificationMongoRepository.save(notification);
	}
}
