package test.piotics.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.piotics.common.NotificationType;
import com.piotics.common.TimeManager;
import com.piotics.common.utils.UtilityManager;
import com.piotics.model.ApplicationUser;
import com.piotics.model.Invitation;
import com.piotics.model.Notification;
import com.piotics.model.UserShort;
import com.piotics.repository.NotificationMongoRepository;
import com.piotics.repository.NotificationMongoTemplateImpl;
import com.piotics.resources.NotificationResource;
import com.piotics.service.NotificationService;
import com.piotics.service.UserProfileService;
import com.piotics.service.UserService;

import test.piotics.builder.ApplicationUserBuilder;
import test.piotics.builder.NotificationBuilder;
import test.piotics.builder.NotificationResourceBuilder;
import test.piotics.builder.UserShortBuilder;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Notification.class, NotificationService.class })
public class NotificationServiceTest {

	
	public static final String ITEM_ID = "5d38540043cb13581a800525";
	public static final String TITLE = "sample data";
	
	@InjectMocks
	NotificationService notificationService;

	@Mock

	NotificationMongoRepository notificationMongoRepository;

	@Mock
	UtilityManager utilityManager;

	@Mock
	UserService userService;

	@Mock
	TimeManager timeManager;

	@Mock
	UserProfileService userProfileService;

	@Mock
	NotificationMongoTemplateImpl notificationMongoTemplateImpl;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		notificationService = new NotificationService(notificationMongoRepository, utilityManager, userService,
				timeManager, userProfileService, notificationMongoTemplateImpl);
	}

	@Rule
	public final ExpectedException expectedEx = ExpectedException.none();

	@Test
	public void addNotificationShouldSuccess() throws Exception {
		
		UserShort owner = UserShortBuilder.aUserShort().build();
		List<UserShort> usersToNotify = new ArrayList<>();
		List<Notification> notifications = new ArrayList<>();
		
		while(usersToNotify.size()<3) {	
			
			UserShort userToNotify = UserShortBuilder.aUserShort()
					.withUserName("user_"+usersToNotify.size())
					.build();
			usersToNotify.add(userToNotify);
			notifications.add(NotificationBuilder.aNotification()
					.withId("5d38540043cb13581a800525"+usersToNotify.size())
					.withUserToNotify(userToNotify)
					.build());
		}
		
		notificationService.addNotification(owner, NotificationType.FRIEND_REQUEST, ITEM_ID, usersToNotify, TITLE);	
	}
	
	@Test
	public void getNotificationListShouldSuccess() throws Exception {
		
		ApplicationUser applicationUser = ApplicationUserBuilder.anApplicationUser().build();
		List<Notification> notifications = new ArrayList<>();
		int pageNo = 1;
		Pageable pageable = new PageRequest(pageNo - 1, 10);

		while(notifications.size()<3) {
			notifications.add(NotificationBuilder.aNotification()
					.withId("5d38540043cb13581a80052"+notifications.size()).build());
		}
		
		NotificationResource notificationResource = NotificationResourceBuilder.aNotificationResource().withNotifications(notifications).build();
		when(notificationMongoRepository.findTop10ByUserToNotifyIdOrderByCreatedOnDesc(applicationUser.getId(), pageable)).thenReturn(notifications);
//		PowerMockito.whenNew(NotificationResource.class).withAnyArguments().thenReturn(notificationResource);
		
		NotificationResource responseNotificationResource = notificationService.getNotificationList(applicationUser, pageNo);
		
		assertThat(notificationResource).isEqualsToByComparingFields(responseNotificationResource);
	}
	
	@Test
	public void readNotificationShouldSuccess() {
		
		Notification notification = NotificationBuilder.aNotification().build();
		notificationService.readNotification(notification);
	}
	
	@Test
	public void resetNewNotificationCountShouldSuccess() {
		
		ApplicationUser applicationUser = ApplicationUserBuilder.anApplicationUser().build();
		notificationService.resetNewNotificationCount(applicationUser);
	}
	
}
