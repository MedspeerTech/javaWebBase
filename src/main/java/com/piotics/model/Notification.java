package com.piotics.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.google.firebase.database.annotations.NotNull;
import com.piotics.common.NotificationType;
import com.piotics.common.TimeManager;

@Document(collection = "#{@tenantManager.getTenantId()}"+"notification")
public class Notification {

	@Id
	@NotNull
	private String id;	
	private String title;
	
	@DBRef
	private UserShort owner;
	@DBRef
	private UserShort userToNotify;
	private String itemId;
	private NotificationType type;

	@Indexed(name = "createdOn", direction = IndexDirection.DESCENDING)
	private Date createdOn;
	private boolean read;

	public Notification(UserShort owner, UserShort userToNotify, NotificationType type, String itemId,String title) {

		this.owner = owner;
		this.userToNotify = userToNotify;
		this.type = type;
		this.itemId = itemId;
		this.createdOn = Date.from(TimeManager.getCurrentTimestamp().toInstant());
		this.title = title;
	}

	public Notification() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public UserShort getOwner() {
		return owner;
	}

	public void setOwner(UserShort owner) {
		this.owner = owner;
	}

	public UserShort getUserToNotify() {
		return userToNotify;
	}

	public void setUserToNotify(UserShort userToNotify) {
		this.userToNotify = userToNotify;
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
