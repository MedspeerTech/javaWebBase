package com.piotics.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user_notification")
public class UserNotification {
	
	private String id;
	private List<Notification> notifications;
	private int newNotifications;
	
	public UserNotification(String id,ArrayList arrayList) {

		this.id = id;
		this.notifications = arrayList;
	}
	public UserNotification() {}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<Notification> getNotifications() {
		return notifications;
	}
	public void setNotifications(List<Notification> notifications) {
		this.notifications = notifications;
	}
	public int getNewNotifications() {
		return newNotifications;
	}
	public void setNewNotifications(int newNotifications) {
		this.newNotifications = newNotifications;
	}
	
	
	
	

}
