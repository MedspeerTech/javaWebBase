package test.piotics.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import com.piotics.common.TokenType;
import com.piotics.common.utils.BCryptPasswordUtils;
import com.piotics.common.utils.UtilityManager;
import com.piotics.exception.UserException;
import com.piotics.model.ApplicationUser;
import com.piotics.model.PasswordReset;
import com.piotics.model.PasswordResetResource;
import com.piotics.model.Token;
import com.piotics.model.UserProfile;
import com.piotics.repository.UserMongoRepository;
import com.piotics.repository.UserProfileMongoRepository;
import com.piotics.service.FileService;
import com.piotics.service.MailService;
import com.piotics.service.TokenService;
import com.piotics.service.UserProfileService;
import com.piotics.service.UserService;

import test.piotics.builder.ApplicationUserBuilder;
import test.piotics.builder.PasswordResetBuilder;
import test.piotics.builder.PasswordResetResourceBuilder;
import test.piotics.builder.TokenBuilder;
import test.piotics.builder.UserProfileBuilder;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ UserProfile.class, UserProfileService.class })
public class UserProfileServiceTest {

	public static final String EMAIL = "user@test.com";
	public static final String PHONE = "9249304135";

	@InjectMocks
	private UserProfileService userProfileService;

	@Mock
	FileService fileService;

	@Mock
	UserProfileMongoRepository userProfileMongoRepository;

	@Mock
	UserService userService;

	@Mock
	TokenService tokenService;

	@Mock
	MailService mailService;

	@Mock
	UserMongoRepository userMongoRepository;

	@Mock
	BCryptPasswordUtils bCryptPasswordUtils;

	@Mock
	UtilityManager utilityManager;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		userProfileService = new UserProfileService(fileService, userProfileMongoRepository, userService, tokenService,
				mailService, userMongoRepository, bCryptPasswordUtils, utilityManager);
	}

	@Rule
	public final ExpectedException expectedEx = ExpectedException.none();

	@Test
	public void saveShouldSuccess() {
		
		UserProfile userProfile = UserProfileBuilder.aUserProfile().withId(null).build();
		UserProfile DbUserProfile = UserProfileBuilder.aUserProfile().build();
		when(userProfileMongoRepository.save(userProfile)).thenReturn(DbUserProfile);
		
		UserProfile responseProfile = userProfileService.save(userProfile);
		assertThat(responseProfile).isEqualTo(DbUserProfile);
	}
	
	@Test
	public void getProfileShouldSuccess() {
		UserProfile DbUserProfile = UserProfileBuilder.aUserProfile().build();		
		Optional<UserProfile> optionalProfile = Optional.of(DbUserProfile);

		when(userProfileMongoRepository.findById(DbUserProfile.getId())).thenReturn(optionalProfile);
		
		UserProfile responseProfile = userProfileService.getProfile(DbUserProfile.getId());
		assertThat(DbUserProfile).isEqualTo(responseProfile);
	}
	
	@Test
	public void saveprofileShouldSucess() {

		UserProfile dbUserProfile = UserProfileBuilder.aUserProfile().build();
		UserProfile userProfile = UserProfileBuilder.aUserProfile().but().withId("5d38540043cb13581a800525")
				.withUsername("dijo").withAbout("new data").withEmail("dijofrancis01@gmail.com").withPhone("8525016985")
				.build();

		Optional<UserProfile> userProfileOptional = Optional.of(dbUserProfile);
		when(userProfileMongoRepository.findById(userProfile.getId())).thenReturn(userProfileOptional);

		userProfileService.saveProfile(userProfile);
	}

	@Test
	public void changeMail_throws_Exception_if_notMailNotValid() throws Exception {

		ApplicationUser applicationUser = ApplicationUserBuilder.anApplicationUser().build();

		when(utilityManager.isEmail(EMAIL)).thenReturn(false);

		expectedEx.expect(Exception.class);
		expectedEx.expectMessage("not a valid email");

		userProfileService.changeMail(applicationUser, EMAIL);
	}

	@Test
	public void changeMail_throws_UserException_if_mailIdAlreadyRegistered() throws Exception {

		ApplicationUser applicationUser = ApplicationUserBuilder.anApplicationUser().build();
		when(utilityManager.isEmail(EMAIL)).thenReturn(true);
		when(userService.isExistingUser(EMAIL)).thenReturn(true);

		expectedEx.expect(UserException.class);
		expectedEx.expectMessage("email already registered");

		userProfileService.changeMail(applicationUser, EMAIL);
	}

	@Test
	public void changeMail_throws_Exception_if_noChangeInMailId() throws Exception {

		ApplicationUser applicationUser = ApplicationUserBuilder.anApplicationUser().withEmail("user@test.com").build();
		when(utilityManager.isEmail(EMAIL)).thenReturn(true);
		when(userService.isExistingUser(EMAIL)).thenReturn(false);

		expectedEx.expect(Exception.class);
		expectedEx.expectMessage("no change found");

		userProfileService.changeMail(applicationUser, EMAIL);
	}

	@Test
	public void changeMail_should_sucess_if_vaildMailId_and_validMailResetTokenExists() throws Exception {

		ApplicationUser applicationUser = ApplicationUserBuilder.anApplicationUser().build();

		Token token = TokenBuilder.aToken().withTokenType(TokenType.MAIL_RESET).withUsername(EMAIL).build();

		when(utilityManager.isEmail(EMAIL)).thenReturn(true);
		when(userService.isExistingUser(EMAIL)).thenReturn(false);
		when(tokenService.getTokenByUserNameAndTokenType(EMAIL, TokenType.MAIL_RESET)).thenReturn(token);

		userProfileService.changeMail(applicationUser, EMAIL);
	}

	@Test
	public void changeMail_should_sucess_if_vaildMailId() throws Exception {

		ApplicationUser applicationUser = ApplicationUserBuilder.anApplicationUser().build();

		Token token = TokenBuilder.aToken().withTokenType(TokenType.MAIL_RESET).withUsername(EMAIL).build();

		when(utilityManager.isEmail(EMAIL)).thenReturn(true);
		when(userService.isExistingUser(EMAIL)).thenReturn(false);
		when(tokenService.getTokenByUserNameAndTokenType(EMAIL, TokenType.MAIL_RESET)).thenReturn(null);
		when(tokenService.getMailResetToken(applicationUser, EMAIL)).thenReturn(token);
		
		userProfileService.changeMail(applicationUser, EMAIL);
	}

	@Test
	public void verifyNewMail_throws_Exception_if_mailResetTokenNotExists() throws Exception {
		
		ApplicationUser applicationUser = ApplicationUserBuilder.anApplicationUser().build();
		
		when(tokenService.getTokenFromDbByUserIdAndTokenType(applicationUser.getId(), TokenType.MAIL_RESET)).thenReturn(null);
		
		expectedEx.expect(Exception.class);
		expectedEx.expectMessage("");
		
		userProfileService.verifyNewMail(applicationUser, "tokenString");
	}
	
	@Test
	public void verifyNewMail_throws_TokenException_if_tokenIsNotMatichingWithDbToken() throws Exception {
		
		ApplicationUser applicationUser = ApplicationUserBuilder.anApplicationUser().build();
		Token dbToken = TokenBuilder.aToken().build();
			
		when(tokenService.getTokenFromDbByUserIdAndTokenType(applicationUser.getId(), TokenType.MAIL_RESET)).thenReturn(dbToken);		
		expectedEx.expect(Exception.class);
		expectedEx.expectMessage("InvalidToken");
		
		userProfileService.verifyNewMail(applicationUser, "abcdefges355hs28pt8501256j");
	}
	
	@Test
	public void verifyNewMail_sucess_if_validToken() throws Exception {
		
		ApplicationUser applicationUser = ApplicationUserBuilder.anApplicationUser().build();
		Token dbToken = TokenBuilder.aToken().build();
		
		UserProfile userProfile = UserProfileBuilder.aUserProfile().build();
		Optional<UserProfile> userProfileOptional = Optional.of(userProfile); 
	
		when(tokenService.getTokenFromDbByUserIdAndTokenType(applicationUser.getId(), TokenType.MAIL_RESET)).thenReturn(dbToken);	
		
		Mockito.when(userProfileMongoRepository.findById(Mockito.anyString())).thenReturn(userProfileOptional);
		
		userProfileService.verifyNewMail(applicationUser, dbToken.getToken());
	}

	@Test
	public void changePassword_throws_Exception_if_authenticationFails() throws Exception {
		ApplicationUser applicationUser = ApplicationUserBuilder.anApplicationUser().build();
		PasswordResetResource passwordResetResource = PasswordResetResourceBuilder.aPasswordResetResource().build();

		when(bCryptPasswordUtils.isMatching(passwordResetResource.getPassword(), applicationUser.getPassword()))
				.thenReturn(false);

		expectedEx.expect(Exception.class);
		expectedEx.expectMessage("wrong password");

		userProfileService.changePassword(applicationUser, passwordResetResource);
	}

	@Test
	public void changePassword_throws_Exception_if_newPasswordAndCurrentPasswordAreSame() throws Exception {
		ApplicationUser applicationUser = ApplicationUserBuilder.anApplicationUser().build();
		PasswordResetResource passwordResetResource = PasswordResetResourceBuilder.aPasswordResetResource().build();

		when(bCryptPasswordUtils.isMatching(passwordResetResource.getPassword(), applicationUser.getPassword()))
				.thenReturn(true);

		when(bCryptPasswordUtils.isMatching(passwordResetResource.getNewPassword(), applicationUser.getPassword()))
				.thenReturn(true);

		expectedEx.expect(Exception.class);
		expectedEx.expectMessage("no change found");

		userProfileService.changePassword(applicationUser, passwordResetResource);
	}

	@Test
	public void changePassword_success_if_validData() throws Exception {
		ApplicationUser applicationUser = ApplicationUserBuilder.anApplicationUser().build();
		PasswordResetResource passwordResetResource = PasswordResetResourceBuilder.aPasswordResetResource().build();

		when(bCryptPasswordUtils.isMatching(passwordResetResource.getPassword(), applicationUser.getPassword()))
				.thenReturn(true);

		when(bCryptPasswordUtils.isMatching(passwordResetResource.getNewPassword(), applicationUser.getPassword()))
				.thenReturn(false);

		userProfileService.changePassword(applicationUser, passwordResetResource);
	}

}
