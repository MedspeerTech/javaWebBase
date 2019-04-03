package medspeer.test.service;

//import static org.assertj.core.api.Assertions.assertThat;
import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import medspeer.tech.common.MailManager;
import medspeer.tech.common.TokenManager;
import medspeer.tech.common.TokenType;
import medspeer.tech.exception.TokenException;
import medspeer.tech.exception.UserException;
import medspeer.tech.model.ApplicationUser;
import medspeer.tech.model.PasswordReset;
import medspeer.tech.model.PasswordResetResource;
import medspeer.tech.model.Token;
import medspeer.tech.repository.RoleJPARepository;
import medspeer.tech.repository.TokenJpaRepository;
import medspeer.tech.repository.UserJpaRepository;
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

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		userService = new UserService(userJpaRepository, roleJPARepository, tokenManager, tokenJpaRepository,
				mailManager,2);
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
	
}
