package medspeer.tech.service;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Date;

import medspeer.tech.common.MailManager;
import medspeer.tech.common.TimeManager;
import medspeer.tech.common.TokenManager;
import medspeer.tech.common.TokenType;
import medspeer.tech.constants.UserRoles;
import medspeer.tech.exception.TokenException;
import medspeer.tech.repository.RoleJPARepository;
import medspeer.tech.repository.TokenJpaRepository;
import medspeer.tech.repository.UserJpaRepository;
import medspeer.tech.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import medspeer.tech.exception.UserException;
import medspeer.tech.model.ApplicationUser;
import medspeer.tech.model.Attachment;
import medspeer.tech.model.EMail;
import medspeer.tech.model.PasswordReset;
import medspeer.tech.model.PasswordResetResource;
import medspeer.tech.model.Token;

@Service
public class UserService {

	@Autowired
	UserJpaRepository userJpaRepository;

	@Autowired
	TokenJpaRepository tokenjpaRepository;

	@Autowired
	TokenManager tokenManager;

	@Autowired
	TimeManager timeManager;

	@Autowired
	MailManager mailManager;

	@Autowired
	FileService fileService;

	@Autowired
	UserRepository userRepository;

	@Value("${token.expiration.days}")
	Integer tokenExpDays;

	@Autowired
	RoleJPARepository roleJPARepository;

	@Autowired
	public UserService(UserJpaRepository userJpaRepository, RoleJPARepository roleJPARepository,
			TokenManager tokenManager, TokenJpaRepository tokenJpaRepository, MailManager mailManager,
			Integer tokenExpDays) {

		this.userJpaRepository = userJpaRepository;
		this.roleJPARepository = roleJPARepository;
		this.tokenManager = tokenManager;
		this.tokenjpaRepository = tokenJpaRepository;
		this.mailManager = mailManager;
		this.tokenExpDays = tokenExpDays;

	}

	@Autowired
	public UserService(UserJpaRepository userJpaRepository) {
		this.userJpaRepository = userJpaRepository;
	}

	public void signUp(ApplicationUser applicationUser) {

		ApplicationUser newUser = null;

		if (userJpaRepository.findByUsername(applicationUser.getUsername()) == null) {
			BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
			String password = bCryptPasswordEncoder.encode(applicationUser.getPassword());
			applicationUser.setPassword(password);
			applicationUser.setRoles(Arrays.asList(roleJPARepository.findByName(UserRoles.ROLE_USER)));
			newUser = userJpaRepository.save(applicationUser);
		} else {
			throw new UserException("ExistingUser");
		}
		Token token = tokenManager.getTokenForEmailVerification(applicationUser.getUsername());
		tokenjpaRepository.save(token);

		EMail email = mailManager.composeSignupVerificationEmail(token);
		mailManager.sendEmail(email);
		return;
	}

	public void verifyEmail(Token token) {

		Token dbToken = tokenjpaRepository.findByUsernameAndTokenType(token.getUsername(), TokenType.EMAILVERIFICATION);

		if (dbToken == null) {
			if (userJpaRepository.findByUsername(token.getUsername()) == null)
				throw new UserException("UnRegistered");
			else
				throw new UserException("ExistingUser");
		}

		isTokenValid(dbToken);
		if (!dbToken.getToken().equals(token.getToken()))
			throw new TokenException("InvalidToken");

		ApplicationUser user = userJpaRepository.findByUsername(token.getUsername());
		user.setEnabled(true);
		userJpaRepository.save(user);
		tokenjpaRepository.deleteByUsernameAndTokenAndTokenType(token.getUsername(), token.getToken(),
				TokenType.EMAILVERIFICATION);

		return;
	}

	void isTokenValid(Token dbToken) {
		Date currentDate = timeManager.getCurrentTimestamp();

		System.out.println(dbToken.getCreationDate().getTime());

		long diff = currentDate.getTime() - dbToken.getCreationDate().getTime();
		long days = diff / 1000 / 60 / 60 / 24;

		if (days > tokenExpDays)
			throw new TokenException("ExpiredToken");
	}

	public void forgotPassword(String Username) {

		EMail emailToSend = new EMail();
		ApplicationUser applicationUser = userJpaRepository.findByUsername(Username);
		if (applicationUser != null) {

			Token dbToken = tokenjpaRepository.findByUsernameAndTokenType(Username, TokenType.PASSWORDRESET);
			if (dbToken == null) {
				Token token = tokenManager.getTokenForPasswordReset(Username);
				tokenjpaRepository.save(token);
				emailToSend = mailManager.composeForgotPasswordMail(token);
				mailManager.sendEmail(emailToSend);
				return;
			} else {
				throw new TokenException("token already exists");
			}
		} else {
			throw new UsernameNotFoundException("user not found");
		}
	}

	public void resetPassword(PasswordReset passwordReset) {
		Token dbToken = tokenjpaRepository.findByUsernameAndTokenType(passwordReset.getUsername(),
				TokenType.PASSWORDRESET);
		if (dbToken == null)
			throw new TokenException("InvalidToken");

		isTokenValid(dbToken);
		if (!passwordReset.getToken().equals(dbToken.getToken())) {
			throw new TokenException("InvalidToken");
		}

		ApplicationUser user = userJpaRepository.findByUsername(passwordReset.getUsername());
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		user.setPassword(bCryptPasswordEncoder.encode(passwordReset.getPassword()));
		user.setAccountNonLocked(true);
		userJpaRepository.save(user);

		tokenjpaRepository.deleteByUsernameAndTokenAndTokenType(passwordReset.getUsername(), passwordReset.getToken(),
				TokenType.PASSWORDRESET);
	}

	public void changePassword(PasswordResetResource passwordresetResource) {

		ApplicationUser user = userJpaRepository.findByUsernameAndPassword(passwordresetResource.getUsername(),
				passwordresetResource.getPassword());
		if (user != null) {
			BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
			user.setPassword(bCryptPasswordEncoder.encode(passwordresetResource.getNewpassword()));
			userJpaRepository.save(user);
		} else {
			throw new UserException("username and password mismatch");
		}
	}

	public void updateProfileImage(String imageData) {
		Attachment attachment = new Attachment();
		attachment.setId(1);
		attachment.setAttachmentName(fileService.storeImageInStorageLocation(imageData));
		userRepository.updateProfileImage(attachment);
	}
}
