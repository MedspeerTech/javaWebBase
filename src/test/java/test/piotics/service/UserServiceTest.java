package test.piotics.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

import com.piotics.common.TokenType;
import com.piotics.common.utils.BCryptPasswordUtils;
import com.piotics.common.utils.UtilityManager;
import com.piotics.constants.UserRoles;
import com.piotics.exception.TokenException;
import com.piotics.exception.UserException;
import com.piotics.model.ApplicationUser;
import com.piotics.model.PasswordReset;
import com.piotics.model.SignUpUser;
import com.piotics.model.Token;
import com.piotics.model.UserShort;
import com.piotics.repository.UserMongoRepository;
import com.piotics.repository.UserShortMongoRepository;
import com.piotics.service.InvitationService;
import com.piotics.service.MailService;
import com.piotics.service.TokenService;
import com.piotics.service.UserProfileService;
import com.piotics.service.UserService;

import test.piotics.builder.ApplicationUserBuilder;
import test.piotics.builder.PasswordResetBuilder;
import test.piotics.builder.SignUpUserBuilder;
import test.piotics.builder.TokenBuilder;
import test.piotics.builder.UserShortBuilder;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ApplicationUser.class, UserService.class })
public class UserServiceTest {

	public static final String EMAIL = "user@test.com";
	public static final String PHONE = "9249304135";
	public static final String TOKEN = "jhp5nhmes455hs28pt85055555";
	public static final String ENCODED_PASS = "$2a$10$uu83nQAktGUStQrq83RpduwuIxEUt3vmPEF7yhpIVPnJd9glw3YmS";

	@InjectMocks
	private UserService userService;

	@Mock
	private UserMongoRepository userMongoRepository;

	@Mock
	UtilityManager utilityManager;

	@Mock
	BCryptPasswordUtils bCryptPasswordUtils;

	@Mock
	InvitationService invitationService;

	@Mock
	TokenService tokenService;

	@Mock
	UserProfileService userProfileService;

	@Mock
	MailService mailService;
	
	@Mock
	UserShortMongoRepository userShortMongoRepository;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		userService = new UserService(userMongoRepository, utilityManager, bCryptPasswordUtils, invitationService,
				tokenService, userProfileService, mailService,userShortMongoRepository);
	}

	@Rule
	public final ExpectedException expectedEx = ExpectedException.none();

	@Test
	public void signUpWithRegisteredEmail_throws_UserException() throws Exception {

		ApplicationUser applicationUser = ApplicationUserBuilder.anApplicationUser().build();

		expectedEx.expect(UserException.class);
		expectedEx.expectMessage("user already exists");

		signUpTest_with_registeredEmailOrPhone(EMAIL, applicationUser);

	}

	@Test
	public void signUpWithRegisteredPhoneNumber_throws_UserException() throws Exception {

		ApplicationUser applicationUser = ApplicationUserBuilder.anApplicationUser().build();

		expectedEx.expect(UserException.class);
		expectedEx.expectMessage("user already exists");

		signUpTest_with_registeredEmailOrPhone(PHONE, applicationUser);
	}

	@Test
	public void signUpWithUnRegisteredEmail_should_sucess() throws Exception {

		ApplicationUser applicationUser = null;

		final Throwable t1 = signUpTest_with_unRegisteredEmailOrPhone(EMAIL, applicationUser);
		assertThat(t1).isEqualTo(null);
	}

	@Test
	public void signUpWithUnRegisteredPhoneNumber_should_Sucess() throws Exception {

		ApplicationUser applicationUser = null;

		final Throwable t2 = signUpTest_with_unRegisteredEmailOrPhone(PHONE, applicationUser);

		assertThat(t2).isEqualTo(null);
	}

	@Test
	public void verifyEmailWill_throw_UserException_if_unregisteredEmail() {

		Token token = TokenBuilder.aToken().but().build();

		when(tokenService.getTokenFromDBWithTokenType(token.getUsername(), TokenType.EMAILVERIFICATION))
				.thenReturn(null);
		when(userMongoRepository.findByEmail(token.getUsername())).thenReturn(null);

		expectedEx.expect(UserException.class);
		expectedEx.expectMessage("UnRegistered");

		userService.verifyEmail(token);
	}

	@Test
	public void verifyEmailWill_throw_UserException_if_emailAlreadyInUse() {

		Token token = TokenBuilder.aToken().but().build();
		ApplicationUser applicationUser = ApplicationUserBuilder.anApplicationUser().build();

		when(tokenService.getTokenFromDBWithTokenType(token.getUsername(), TokenType.EMAILVERIFICATION))
				.thenReturn(null);
		when(userMongoRepository.findByEmail(token.getUsername())).thenReturn(applicationUser);

		expectedEx.expect(UserException.class);
		expectedEx.expectMessage("ExistingUser");

		userService.verifyEmail(token);
	}

	@Test
	public void verifyEmailWill_throw_TokenException_if_tokenIsNotMatchingWithDbToken() {

		Token token = TokenBuilder.aToken().but().build();
		Token dbToken = TokenBuilder.aToken().but().withToken(TOKEN).build();

		ApplicationUser applicationUser = ApplicationUserBuilder.anApplicationUser().build();

		when(tokenService.getTokenFromDBWithTokenType(token.getUsername(), TokenType.EMAILVERIFICATION))
				.thenReturn(dbToken);
		when(tokenService.isTokenValid(token)).thenReturn(true);

		expectedEx.expect(TokenException.class);
		expectedEx.expectMessage("InvalidToken");

		userService.verifyEmail(token);
	}

	@Test
	public void verifyEmail_sucess_if_validToken() {

		Token token = TokenBuilder.aToken().but().build();

		ApplicationUser applicationUser = ApplicationUserBuilder.anApplicationUser().build();

		Optional<ApplicationUser> appUserOptional = Optional.of(applicationUser);

		when(tokenService.getTokenFromDBWithTokenType(token.getUsername(), TokenType.EMAILVERIFICATION))
				.thenReturn(token);
		when(tokenService.isTokenValid(token)).thenReturn(true);
		when(userMongoRepository.findById(token.getUserId())).thenReturn(appUserOptional);

		userService.verifyEmail(token);

	}

	@Test
	public void forgetPassword_throws_UserException_if_userNotRegistered() {

		when(userMongoRepository.findByEmail(EMAIL)).thenReturn(null);
		when(userMongoRepository.findByPhone(PHONE)).thenReturn(null);

		expectedEx.expect(UserException.class);
		expectedEx.expectMessage("user not exist");

		userService.forgotPassword(EMAIL);

	}

	@Test
	public void forgetPassword_sucess_if_validTokenExists() {

		Token token = TokenBuilder.aToken().but().build();
		ApplicationUser applicationUser = ApplicationUserBuilder.anApplicationUser().build();

		when(userMongoRepository.findByEmail(EMAIL)).thenReturn(applicationUser);
		when(userMongoRepository.findByPhone(PHONE)).thenReturn(null);
		when(tokenService.getTokenByUserNameAndTokenType(EMAIL, TokenType.PASSWORDRESET)).thenReturn(token);
		when(tokenService.isTokenValid(token)).thenReturn(true);
		when(tokenService.getPasswordResetToken(EMAIL)).thenReturn(token);

		userService.forgotPassword(EMAIL);

	}

	@Test
	public void forgetPassword_sucess_if_registeredUser() {

		Token token = TokenBuilder.aToken().but().build();
		ApplicationUser applicationUser = ApplicationUserBuilder.anApplicationUser().build();

		when(userMongoRepository.findByEmail(EMAIL)).thenReturn(applicationUser);
		when(userMongoRepository.findByPhone(PHONE)).thenReturn(null);
		when(tokenService.getPasswordResetToken(EMAIL)).thenReturn(token);

		userService.forgotPassword(EMAIL);
	}

	@Test
	public void resetPassword_throws_TokenException_if_noPasswordResetTokenExists() {

		PasswordReset passwordReset = PasswordResetBuilder.aPasswordResetBuilder().build();

		when(tokenService.getTokenByUserNameAndTokenType(passwordReset.getUsername(), TokenType.PASSWORDRESET))
				.thenReturn(null);

		expectedEx.expect(TokenException.class);
		expectedEx.expectMessage("no token found for password reset");

		userService.resetPassword(passwordReset);
	}

	@Test
	public void resetPassword_throws_TokenException_if_tokenNotMatching() {

		PasswordReset passwordReset = PasswordResetBuilder.aPasswordResetBuilder().build();

		Token dbToken = TokenBuilder.aToken().withToken(TOKEN).build();

		ApplicationUser applicationUser = ApplicationUserBuilder.anApplicationUser().build();

		when(tokenService.getTokenByUserNameAndTokenType(passwordReset.getUsername(), TokenType.PASSWORDRESET))
				.thenReturn(dbToken);

		expectedEx.expect(TokenException.class);
		expectedEx.expectMessage("InvalidToken");

		userService.resetPassword(passwordReset);
	}

	@Test
	public void resetPassword_sucess_if_validToken() {

		PasswordReset passwordReset = PasswordResetBuilder.aPasswordResetBuilder().build();

		Token dbToken = TokenBuilder.aToken().build();

		ApplicationUser applicationUser = ApplicationUserBuilder.anApplicationUser().build();

		when(tokenService.getTokenByUserNameAndTokenType(passwordReset.getUsername(), TokenType.PASSWORDRESET))
				.thenReturn(dbToken);
		when(userMongoRepository.findByEmail(passwordReset.getUsername())).thenReturn(applicationUser);
		when(bCryptPasswordUtils.encodePassword(passwordReset.getPassword())).thenReturn(ENCODED_PASS);

		userService.resetPassword(passwordReset);
	}

	@Test
	public void getApplicationUserShouldSuccess() {

		String id = "5d38540043cb13581a800525";
		ApplicationUser applicationUser = ApplicationUserBuilder.anApplicationUser().build();
		Optional<ApplicationUser> appUserOptional = Optional.of(applicationUser);
		when(userMongoRepository.findById(id)).thenReturn(appUserOptional);

		ApplicationUser responseApplicationUser = userService.getApplicationUser(id);

		assertThat(applicationUser).isEqualsToByComparingFields(responseApplicationUser);
	}

	@Test
	public void saveShouldSuccess() {

		ApplicationUser applicationUser = ApplicationUserBuilder.anApplicationUser().withId(null).build();
		ApplicationUser dbApplicationUser = ApplicationUserBuilder.anApplicationUser().build();
		when(userMongoRepository.save(applicationUser)).thenReturn(dbApplicationUser);

		ApplicationUser responseApplicationUser = userService.save(applicationUser);

		assertThat(dbApplicationUser).isEqualsToByComparingFields(responseApplicationUser);
	}

	@Test
	public void getUserShortOfAdminsShouldSuccess() {

		List<ApplicationUser> adminUsers = new ArrayList<>();
		List<UserShort> adminUserShortLi = new ArrayList<UserShort>();

		while (adminUsers.size() < 3) {
			
			adminUsers.add(ApplicationUserBuilder.anApplicationUser().withUsername("username_" + adminUsers.size()).build());
			adminUserShortLi.add(UserShortBuilder.aUserShort().build());
		}
		
		Optional<UserShort> userShortOptional = Optional.of(adminUserShortLi.get(0));
		when(userMongoRepository.findByRole(UserRoles.ROLE_ADMIN)).thenReturn(adminUsers);
		when(userShortMongoRepository.findById(adminUsers.get(0).getId())).thenReturn(userShortOptional);
		
		List<UserShort> responseUserShorts = userService.getUserShortOfAdmins();
		
		assertThat(responseUserShorts).isNotNull();
		assertThat(responseUserShorts).isNotEmpty();
	}
	
	@Test
	public void getUserShortShouldSuccess() {
		
		String id = "5d38540043cb13581a800525";
		UserShort userShort = UserShortBuilder.aUserShort().build();
		
		when(userShortMongoRepository.findById(id)).thenReturn(Optional.of(userShort));
		
		UserShort responseUserShort = userService.getUserShort(id);
		assertThat(userShort).isEqualsToByComparingFields(responseUserShort);
	}

	private Throwable signUpTest_with_registeredEmailOrPhone(String username, ApplicationUser applicationUser)
			throws Exception {

		Token token = TokenBuilder.aToken().but().build();

		SignUpUser signUpUser = SignUpUserBuilder.aSignUpUser().withUserName(username).withToken(token).build();

		if (utilityManager.isEmail(signUpUser.getUsername())) {
			when(userMongoRepository.findByEmail(signUpUser.getUsername())).thenReturn(applicationUser);
		} else {
			when(userMongoRepository.findByPhone(signUpUser.getUsername())).thenReturn(applicationUser);
		}

		userService.signUp(signUpUser);

		return null;
	}

	private Throwable signUpTest_with_unRegisteredEmailOrPhone(String username, ApplicationUser applicationUser)
			throws Exception {

		Token token = TokenBuilder.aToken().but().build();

		SignUpUser signUpUser = SignUpUserBuilder.aSignUpUser().withUserName(username).withToken(token).build();

		ApplicationUser newUser = new ApplicationUser();

		if (utilityManager.isEmail(signUpUser.getUsername())) {
			when(userMongoRepository.findByEmail(signUpUser.getUsername())).thenReturn(applicationUser);
			when(utilityManager.isEmail(signUpUser.getUsername())).thenReturn(true);

			newUser = ApplicationUserBuilder.anApplicationUser().withUsername(null).withCompany(null)
					.withEmail(signUpUser.getUsername()).build();
		} else {
			when(userMongoRepository.findByPhone(signUpUser.getUsername())).thenReturn(applicationUser);
			when(utilityManager.isEmail(signUpUser.getUsername())).thenReturn(false);

			newUser = ApplicationUserBuilder.anApplicationUser().withUsername(null).withCompany(null)
					.withPhone(signUpUser.getUsername()).build();
		}

		when(bCryptPasswordUtils.encodePassword(signUpUser.getPassword())).thenReturn(ENCODED_PASS);
		PowerMockito.whenNew(ApplicationUser.class).withAnyArguments().thenReturn(newUser);

		when(invitationService.isInvited(username)).thenReturn(false);
		when(tokenService.getTokenForEmailVerification(applicationUser)).thenReturn(token);
		when(userMongoRepository.save(newUser)).thenReturn(newUser);

		userService.signUp(signUpUser);
		return null;
	}

}
