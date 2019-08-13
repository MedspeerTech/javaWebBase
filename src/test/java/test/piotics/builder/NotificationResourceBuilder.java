package test.piotics.builder;

import java.util.ArrayList;
import java.util.List;

import com.piotics.model.Notification;
import com.piotics.resources.NotificationResource;

public class NotificationResourceBuilder implements Builder<NotificationResource>{

	private List<Notification> notifications = new ArrayList<>();
	
	public NotificationResource build() {
		
		return new NotificationResource(notifications);	
	}
	
	public static NotificationResourceBuilder aNotificationResource() {
		
		return new NotificationResourceBuilder();
	}
	
	public NotificationResourceBuilder withNotifications(List<Notification> notifications) {
		this.notifications = notifications;
		return this;
	}
	
	public NotificationResourceBuilder but() {
		return new NotificationResourceBuilder()
				.withNotifications(notifications);
	}
}
