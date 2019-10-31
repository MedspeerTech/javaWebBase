package test.piotics.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.powermock.api.support.membermodification.MemberMatcher.method;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.mockito.expectation.WithAnyArguments;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.mockito.ArgumentMatchers;

import com.piotics.common.utils.UtilityManager;
import com.piotics.constants.UserRoles;
import com.piotics.model.ApplicationUser;
import com.piotics.model.Invitation;
import com.piotics.model.Session;
import com.piotics.model.UserShort;
import com.piotics.resources.StringResource;
import com.piotics.service.AdminService;
import com.piotics.service.InvitationService;
import com.piotics.service.MailService;
import com.piotics.service.NotificationService;
import com.piotics.service.TokenService;
import com.piotics.service.UserService;

import test.piotics.builder.ApplicationUserBuilder;
import test.piotics.builder.InvitationBuilder;
import test.piotics.builder.SessionBuilder;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.management.*")
@PrepareForTest({ AdminService.class })
public class AdminServiceTest {

	@InjectMocks
	AdminService adminService;

	@Mock
	UserService userService;

	@Mock
	TokenService tokenService;

	@Mock
	InvitationService invitationService;

	@Mock
	MailService mailService;

	@Mock
	NotificationService notificationService;

	@Mock
	UtilityManager utilityManager;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Rule
	public final ExpectedException expectedEx = ExpectedException.none();

	/**
	 * Test for checking whether the method is returning empty failedList if invited
	 * users are new to application
	 * 
	 * @throws Exception
	 */
	@Test
	public void sendInviteShouldReturnEmptyFailedList_if_invitedUsersAreNew() throws Exception {
		List<Invitation> invitations = new ArrayList<>();
		invitations.add(new InvitationBuilder().withEmail("test@123.co.in").build());
		invitations.add(new InvitationBuilder().withEmail("test2@123.co.in").build());
		
		Session session = new SessionBuilder().withId("12345256")
				.withRole(UserRoles.ROLE_POWER_ADMIN)
				.build();
		List<String> strings = new ArrayList<>();
		strings.add("test@123.co.in");
		strings.add("test2@123.co.in");
		StringResource invitationLi = new StringResource();
		invitationLi.setStrings(strings);
		StringResource failedLi = new StringResource();
		failedLi.setStrings(new ArrayList<>());

		AdminService adminServiceSpy = Mockito.spy(adminService);
		PowerMockito.doReturn(failedLi).when(adminServiceSpy, "invite", Matchers.any(), Matchers.any());

//			PowerMockito.when(adminServiceSpy, method(AdminService.class, "populateStringsToInvitation", ApplicationUser.class, StringResource.class))
//             .withArguments(appUser,invitationLi)
//             .thenReturn(invitations);
		PowerMockito.when(utilityManager.isEmail(ArgumentMatchers.anyString())).thenReturn(true);
		PowerMockito.when(userService.getUserShort(ArgumentMatchers.anyString())).thenReturn(new UserShort());

		StringResource response = adminService.sendInvite(session, invitationLi);
		assertThat(response.getStrings(), is(failedLi.getStrings()));
	}

	/**
	 * Test if the mail id is added to failed list if the id is already registered
	 * in application
	 * 
	 * @throws Exception
	 */
	@Test
	public void inviteShouldReturn_DuplicateIdsInFailedList() throws Exception {
		List<Invitation> invitations = new ArrayList<>();
		invitations.add(new InvitationBuilder().withEmail("test@123.co.in").build());
		Session session = new SessionBuilder().withId("12345256")
				.withRole(UserRoles.ROLE_POWER_ADMIN)
				.build();
		List<String> strings = new ArrayList<>();
		strings.add("test@123.co.in");
		StringResource failedLi = new StringResource();
		failedLi.setStrings(strings);

		PowerMockito.doNothing().when(notificationService, "notifyAdminsOnUserInvite", ArgumentMatchers.any(), ArgumentMatchers.any(),
				ArgumentMatchers.anyString());
		PowerMockito.when(userService, "isExistingUser", ArgumentMatchers.any()).thenReturn(true);
		StringResource response = adminService.invite(session, invitations);
		assertThat(response.getStrings(), is(failedLi.getStrings()));
	}	
	
}
