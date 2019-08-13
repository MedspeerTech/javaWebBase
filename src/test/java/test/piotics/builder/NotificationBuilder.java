package test.piotics.builder;

import java.util.Date;

import com.piotics.common.NotificationType;
import com.piotics.model.Notification;
import com.piotics.model.UserShort;

public class NotificationBuilder implements Builder<Notification>{

	private String id = "abcdefges355hs28pt8501256j";	
	private String title = "test data";
	private UserShort owner = new UserShort();
	private UserShort userToNotify = new UserShort();
	private String itemId = "lmn5opges355h308pt8501256k";
	private NotificationType type = NotificationType.INVITATION;
	private Date createdOn = new Date();
	private boolean read = false;
	
	public Notification build() {
		
		Notification notification = new Notification(owner, userToNotify, type, itemId, title);
		notification.setCreatedOn(createdOn);
		notification.setRead(read);
		notification.setId(id);
		
		return notification;
	}
	
	public static NotificationBuilder aNotification() {
		
		return new NotificationBuilder()
				.withOwner(UserShortBuilder.aUserShort().build())
				.withUserToNotify(UserShortBuilder.aUserShort()
						.withId("abcdefges355hs28pt85055555")
						.withUserName("user1")
						.withFileId("abcdefges355h541526s28pt85")
						.build());
	}
	
	public NotificationBuilder withId(String id) {
		
		this.id = id;
		return this;
	}
	
	public NotificationBuilder withTitle(String title) {
		this.title = title;
		return this;
	}
	
	public NotificationBuilder withOwner(UserShort owner) {
		this.owner = owner;
		return this;
	}
	
	public NotificationBuilder withUserToNotify(UserShort userToNotify) {
		this.userToNotify = userToNotify;
		return this;
	}
	
	public NotificationBuilder withItemId(String itemId) {
		this.itemId = itemId;
		return this;
	}
	
	public NotificationBuilder withType(NotificationType type) {
		this.type = type;
		return this;
	}
	
	public NotificationBuilder withCreatedOn(Date createdOn) {
		this.createdOn  = createdOn;
		return this;
	}
	
	public NotificationBuilder withIsRead(boolean read) {
		this.read = read;
		return this;
	}
	
	public NotificationBuilder but() {
		return new NotificationBuilder()
				.withId(id)
				.withTitle(title)
				.withOwner(owner)
				.withUserToNotify(userToNotify)
				.withItemId(itemId)
				.withType(type)
				.withCreatedOn(createdOn)
				.withIsRead(read);
	}
	

}
