package com.piotics.model;

import java.util.Date;

import org.springframework.data.annotation.Id;

import com.piotics.common.NotificationType;

public class Notification {

	@Id
	private String id;
	private UserShort owner;
//	private UserShort createdBy;
	private String itemId;
	private NotificationType type;
	private Date createdOn;
	private boolean read;

	public Notification(UserShort owner, NotificationType type, String itemId) {

		this.owner = owner;
		this.type = type;
		this.itemId = itemId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public UserShort getOwner() {
		return owner;
	}

	public void setOwner(UserShort owner) {
		this.owner = owner;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public NotificationType getType() {
		return type;
	}

	public void setType(NotificationType type) {
		this.type = type;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}
}
