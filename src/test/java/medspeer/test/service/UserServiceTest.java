package medspeer.test.service;

//import static org.assertj.core.api.Assertions.assertThat;
import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import medspeer.tech.common.MailManager;
import medspeer.tech.common.TokenManager;
import medspeer.tech.common.TokenType;
import medspeer.tech.exception.FileException;
import medspeer.tech.exception.TokenException;
import medspeer.tech.exception.UserException;
import medspeer.tech.model.ApplicationUser;
import medspeer.tech.model.Attachment;
import medspeer.tech.model.PasswordReset;
import medspeer.tech.model.PasswordResetResource;
import medspeer.tech.model.Token;
import medspeer.tech.repository.RoleJPARepository;
import medspeer.tech.repository.TokenJpaRepository;
import medspeer.tech.repository.UserJpaRepository;
import medspeer.tech.repository.UserRepository;
import medspeer.tech.service.FileService;
import medspeer.tech.service.UserService;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

	private static final String REGISTRATION_EMAIL_ADDRESS = "dijofrancis@medspeer.com";
	private static final String REGISTRATION_PASSWORD = "passpass";

//	@Mock
	@InjectMocks
	private UserService userService;

	@Mock
//    @Autowired
	private UserJpaRepository userJpaRepository;

	@Mock
	private RoleJPARepository roleJPARepository;

	@Mock
	private TokenManager tokenManager;

	@Mock
	private TokenJpaRepository tokenJpaRepository;

	@Mock
	private MailManager mailManager;

	@Mock
	private FileService fileService;
	
	@Mock
	UserRepository userRepository;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		userService = new UserService(userJpaRepository, roleJPARepository, tokenManager, tokenJpaRepository,
				mailManager, fileService,userRepository, 2);
	}

	@Test
	public void signUp_NewEmail_ShouldSucess() throws UserException {

		ApplicationUser applicationUser = new ApplicationUser();
		applicationUser.setEmail(null);
		applicationUser.setUsername(REGISTRATION_EMAIL_ADDRESS);
		applicationUser.setPassword(REGISTRATION_PASSWORD);

		when(userJpaRepository.findByUsername(REGISTRATION_EMAIL_ADDRESS)).thenReturn(null);
		
//		userService = new UserService(userJpaRepository, roleJPARepository, tokenManager, tokenJpaRepository,
//				mailManager);
		
		catchException(userService).signUp(applicationUser);
		final Throwable thrown = caughtException();
		assertThat(thrown).isEqualTo(null);
	}

	@Test
	public void signUp_DuplicateEmail_ShouldCheckThatEmailIsUnique() throws UserException {

		ApplicationUser applicationUser = new ApplicationUser();
		applicationUser.setEmail(null);
		applicationUser.setUsername(REGISTRATION_EMAIL_ADDRESS);
		applicationUser.setPassword(REGISTRATION_PASSWORD);

		ApplicationUser dummyUser = new ApplicationUser();
		dummyUser.setUsername(REGISTRATION_EMAIL_ADDRESS);
		when(userJpaRepository.findByUsername(REGISTRATION_EMAIL_ADDRESS)).thenReturn(dummyUser);
		
//		userService = new UserService(userJpaRepository, roleJPARepository, tokenManager, tokenJpaRepository,
//				mailManager);
		
		catchException(userService).signUp(applicationUser);
		
		final Throwable thrown = caughtException();
		assertThat(thrown).isExactlyInstanceOf(UserException.class);
//        verify(userJpaRepository, times(1)).findByUsername(REGISTRATION_EMAIL_ADDRESS);	
	}
	
	@Test
	public void forgetPassword_RegisteredEmail_ShouldSuccess() {
		
		ApplicationUser dummyUser = new ApplicationUser();
		dummyUser.setUsername(REGISTRATION_EMAIL_ADDRESS);
		
		when(userJpaRepository.findByUsername(REGISTRATION_EMAIL_ADDRESS)).thenReturn(dummyUser);
		when(tokenJpaRepository.findByUsernameAndTokenType(REGISTRATION_EMAIL_ADDRESS, TokenType.PASSWORDRESET)).thenReturn(null);
		
		catchException(userService).forgotPassword(REGISTRATION_EMAIL_ADDRESS);
		final Throwable thrown = caughtException();
		assertThat(thrown).isEqualTo(null);
	}
	
	@Test
	public void forgetPassword_UnregisteredEmail_ShouldFail() {
		
		Token token = new Token();
		token.setToken("hf554");
		
		ApplicationUser dummyUser = new ApplicationUser();
		dummyUser.setUsername(REGISTRATION_EMAIL_ADDRESS);
		
		when(userJpaRepository.findByUsername(REGISTRATION_EMAIL_ADDRESS)).thenReturn(null);
		when(tokenJpaRepository.findByUsernameAndTokenType(REGISTRATION_EMAIL_ADDRESS, TokenType.PASSWORDRESET)).thenReturn(null);
		
		catchException(userService).forgotPassword(REGISTRATION_EMAIL_ADDRESS);
		final Throwable thrown = caughtException();
		assertThat(thrown).isExactlyInstanceOf(UsernameNotFoundException.class);
	}
	
	@Test
	public void forgetPassword_DuplicateToken_ShouldFail() {
		
		Token token = new Token();
		token.setToken("hf554");
		
		ApplicationUser dummyUser = new ApplicationUser();
		dummyUser.setUsername(REGISTRATION_EMAIL_ADDRESS);
		
		when(userJpaRepository.findByUsername(REGISTRATION_EMAIL_ADDRESS)).thenReturn(dummyUser);
		when(tokenJpaRepository.findByUsernameAndTokenType(REGISTRATION_EMAIL_ADDRESS, TokenType.PASSWORDRESET)).thenReturn(token);
		
		catchException(userService).forgotPassword(REGISTRATION_EMAIL_ADDRESS);
		final Throwable thrown = caughtException();
		assertThat(thrown).isExactlyInstanceOf(TokenException.class);
	}
	
	@Test
	public void resetPassword_nullToken_shouldReturn_TokenException() {
		
		PasswordReset dummyObj = new PasswordReset();
		
		when(tokenJpaRepository.findByUsernameAndTokenType(REGISTRATION_EMAIL_ADDRESS, TokenType.PASSWORDRESET)).thenReturn(null);
		
		catchException(userService).resetPassword(dummyObj);
		final Throwable thrown = caughtException();
		
		assertThat(thrown).isExactlyInstanceOf(TokenException.class);
	}
	
	@Test
	public void resetPassword_ExpiredToken_shouldReturn_TokenException() {
		
		PasswordReset dummyObj = new PasswordReset();
		dummyObj.setUsername(REGISTRATION_EMAIL_ADDRESS);
		dummyObj.setToken("19ngp2c42qdd74jnjgmq3ci9a6");
		dummyObj.setPassword("passpass");
		
		Token token = new Token();
		token.setTokenType(TokenType.PASSWORDRESET);
		token.setToken(dummyObj.getToken());
		Date tokenIssuedDate = DateUtils.addDays(new Date(),-3);
		token.setCreationDate(tokenIssuedDate);
		
		ApplicationUser dummyUser = new ApplicationUser();
		dummyUser.setUsername(REGISTRATION_EMAIL_ADDRESS);
		
		when(tokenJpaRepository.findByUsernameAndTokenType(REGISTRATION_EMAIL_ADDRESS, TokenType.PASSWORDRESET)).thenReturn(token);
		when(userJpaRepository.findByUsername(REGISTRATION_EMAIL_ADDRESS)).thenReturn(dummyUser);

		catchException(userService).resetPassword(dummyObj);
		final Throwable thrown = caughtException();
		assertThat(thrown).isExactlyInstanceOf(TokenException.class);
	}

	@Test
	public void changePassword_unregisteredEmail_shouldThrow_UserException() {
		
		PasswordResetResource dummyResource = new PasswordResetResource();
		dummyResource.setUsername(REGISTRATION_EMAIL_ADDRESS);
		dummyResource.setPassword("passpass");
		dummyResource.setNewpassword(REGISTRATION_PASSWORD);
		
		when(userJpaRepository.findByUsernameAndPassword(REGISTRATION_EMAIL_ADDRESS, REGISTRATION_PASSWORD)).thenReturn(null);
		
		catchException(userService).changePassword(dummyResource);
		final Throwable thrown = caughtException();
		assertThat(thrown).isExactlyInstanceOf(UserException.class);
		
	}
	
	@Test
	public void changePassword_nullInNewpassword_shouldThrow_NullPointerException() {
		
		PasswordResetResource dummyResource = new PasswordResetResource();
		dummyResource.setUsername(REGISTRATION_EMAIL_ADDRESS);
		dummyResource.setPassword(REGISTRATION_PASSWORD);
		
		when(userJpaRepository.findByUsernameAndPassword(REGISTRATION_EMAIL_ADDRESS, REGISTRATION_PASSWORD)).thenReturn(new ApplicationUser());
		
		catchException(userService).changePassword(dummyResource);
		final Throwable thrown = caughtException();
		assertThat(thrown).isExactlyInstanceOf(NullPointerException.class);
	}
	
	@Test
	public void changePassword_nullInEmail_shouldThrow_UserException() {
		
		PasswordResetResource dummyResource = new PasswordResetResource();
		dummyResource.setPassword(REGISTRATION_PASSWORD);

		when(userJpaRepository.findByUsernameAndPassword(REGISTRATION_EMAIL_ADDRESS, REGISTRATION_PASSWORD)).thenReturn(new ApplicationUser());
		
		catchException(userService).changePassword(dummyResource);
		final Throwable thrown = caughtException();
		assertThat(thrown).isExactlyInstanceOf(UserException.class);
	}
	
	@Test
	public void changePassword_validEmail_shouldSucess() {
		
		PasswordResetResource dummyResource = new PasswordResetResource();
		dummyResource.setPassword(REGISTRATION_PASSWORD);
		dummyResource.setNewpassword("newpass");
		dummyResource.setUsername(REGISTRATION_EMAIL_ADDRESS);
		
		when(userJpaRepository.findByUsernameAndPassword(REGISTRATION_EMAIL_ADDRESS, REGISTRATION_PASSWORD)).thenReturn(new ApplicationUser());
		
		catchException(userService).changePassword(dummyResource);
		final Throwable thrown = caughtException();
		assertThat(thrown).isEqualTo(null);
	}
	
	@Test
	public void updateProfileImage_nullImageData_shouldFail() throws Exception {
		
		String imageData = null;

		catchException(userService).updateProfileImage(imageData);
		Throwable thrown = caughtException();
		
		assertThat(thrown).isExactlyInstanceOf(NullPointerException.class);
		
	}
	
	@Test
	public void updateProfileImage_emptyImageData_shouldFail() throws Exception {
		
		String imageData = "";
		catchException(userService).updateProfileImage(imageData);
		Throwable thrown = caughtException();
		
		assertThat(thrown).isExactlyInstanceOf(FileException.class);
		
	}

	@Test
	public void updateProfileImage_validImageData_shouldPass() throws Exception {

		String imageData = "data:image/png;base64,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";
		
		Attachment attachment = new Attachment();
		attachment.setId(1);
		attachment.setAttachmentName(UUID.randomUUID().toString() + ".png");

		when(fileService.storeImageInStorageLocation(imageData)).thenReturn(UUID.randomUUID().toString() + ".png");

		catchException(userService).updateProfileImage(imageData);
		Throwable thrown = caughtException();

		assertThat(thrown).isEqualTo(null);

	}
}
