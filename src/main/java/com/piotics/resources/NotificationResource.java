package com.piotics.resources;

import java.util.List;

import com.piotics.model.Notification;

public class NotificationResource {

	private List<Notification> notifications;

	public NotificationResource(List<Notification> notifications) {

		this.notifications = notifications;
	}

	public List<Notification> getNotifications() {
		return notifications;
	}

	public void setNotifications(List<Notification> notifications) {
		this.notifications = notifications;
	}
}
