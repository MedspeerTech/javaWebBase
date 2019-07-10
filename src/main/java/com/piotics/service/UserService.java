package com.piotics.service;

import static com.piotics.config.SecurityConstants.HEADER_STRING;
import static com.piotics.config.SecurityConstants.TOKEN_PREFIX;

import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.google.firebase.auth.FirebaseToken;
import com.piotics.common.MailManager;
import com.piotics.common.TimeManager;
import com.piotics.common.TokenType;
import com.piotics.common.utils.BCryptPasswordUtils;
import com.piotics.common.utils.HttpServletRequestUtils;
import com.piotics.config.JwtTokenProvider;
import com.piotics.constants.UserRoles;
import com.piotics.exception.FileException;
import com.piotics.exception.TokenException;
import com.piotics.exception.UserException;
import com.piotics.model.ApplicationUser;
import com.piotics.model.EMail;
import com.piotics.model.FileMeta;
import com.piotics.model.Invitation;
import com.piotics.model.PasswordReset;
import com.piotics.model.PasswordResetResource;
import com.piotics.model.SignUpUser;
import com.piotics.model.Token;
import com.piotics.model.UserProfile;
import com.piotics.repository.ApplicationUserMongoRepository;
import com.piotics.repository.FileMetaMongoRepository;
import com.piotics.repository.UserMongoRepository;

@Service
public class UserService {

	@Autowired
	UserMongoRepository userMongoRepository;

	@Autowired
	TokenService tokenService;

	@Autowired
	BCryptPasswordUtils bCryptPasswordUtils;

	@Autowired
	ApplicationUserMongoRepository applicationUserMongoRepository;

	@Autowired
	HttpServletRequestUtils httpServletRequestUtils;

	@Autowired
	TimeManager timeManager;

	@Autowired
	MailManager mailManager;

	@Autowired
	FileService fileService;

	@Autowired
	FileMetaMongoRepository fileMetaMongoRepository;

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@Autowired
	UserProfileService userProfileService;

	@Autowired
	InvitationService invitationService;

	@Value("${invite.required}")
	public boolean inviteRequired;

	@Value("${token.expiration.days}")
	Integer tokenExpDays;

	public void signUp(SignUpUser signUpUser, HttpServletRequest req) throws Exception {

		if (!isExistingUser(signUpUser.getUsername())) {

			if (inviteRequired) {

				if (isInvited(signUpUser.getUsername(), signUpUser.getToken())) {

					proceedToSignUp(signUpUser, req);
				} else {
					throw new UserException("user not invited");
				}

			} else {

				proceedToSignUp(signUpUser, req);
			}
		} else {
			throw new UserException("user already exists");
		}
		return;
	}

	private void proceedToSignUp(SignUpUser signUpUser, HttpServletRequest req) throws Exception {

		String password = bCryptPasswordUtils.encodePassword(signUpUser.getPassword());

		ApplicationUser newUser = new ApplicationUser(password, UserRoles.ROLE_USER);
		newUser = userMongoRepository.save(newUser);

		if (mailManager.isEmail(signUpUser.getUsername())) {
			newUser.setEmail(signUpUser.getUsername());
		} else {
			newUser.setPhone(signUpUser.getUsername());
		}

		if (signUpUser.getToken().getToken() != null) {
			newUser.setEnabled(true);

			tokenService.deleteInviteToken(signUpUser.getUsername(), signUpUser.getToken());
		} else {

			newUser.setEnabled(false);

			tokenService.deleteInviteToken(signUpUser.getUsername(), null);

			String clientBrowser = httpServletRequestUtils.getClientBrowser(req);
			boolean remember = Boolean.parseBoolean(req.getHeader("Remember"));

			if (!remember || !clientBrowser.contains("Android") || !clientBrowser.contains("IPhone")) {
				if (mailManager.isEmail(signUpUser.getUsername())) {

					Token token = tokenService.getTokenForEmailVerification(newUser);
					sendMail(token);
				}
			} else {
				newUser.setEnabled(true);
			}
		}
		newUser = userMongoRepository.save(newUser);

		UserProfile userProfile = new UserProfile(newUser.getId(), newUser.getEmail(), newUser.getPhone());
		userProfileService.save(userProfile);
	}

	private void sendMail(Token token) {

//		Token token = tokenService.getTokenForEmailVerification(appUser);
		EMail email = new EMail();
		if (token.getTokenType() == TokenType.EMAILVERIFICATION) {

			email = mailManager.composeSignupVerificationEmail(token);

		} else if (token.getTokenType() == TokenType.INVITATION) {

			email = mailManager.composeInviteVerificationEmail(token);
		} else if (token.getTokenType() == TokenType.PASSWORDRESET) {

			email = mailManager.composeForgotPasswordMail(token);
		} else if (token.getTokenType() == TokenType.MAIL_RESET) {

			email = mailManager.composeMailResetVerificationEmail(token);
		}
		mailManager.sendEmail(email);
	}

	public Invitation invite(Invitation invitation) throws Exception {

		String phone = invitation.getPhone();
		String email = invitation.getEmail();

		if (email != null && !email.isEmpty()) {
			if (!isExistingUser(email)) {

				if (!isInvited(email, null)) {

					Token token = tokenService.getInviteToken(invitation.getEmail());
					token = tokenService.save(token);

					if (invitation.getEmail() != null) {

						sendMail(token);
					}

					invitation.setToken(token);
					invitation = invitationService.save(invitation);

				} else {
					throw new UserException("user already invited");
				}

			} else {
				throw new UserException("existing user");
			}
		} else if (phone != null && !phone.isEmpty()) {

			if (!isExistingUser(phone)) {

				// user not exist continue signup

				if (!isInvited(phone, null)) {

					Token token = tokenService.getInviteToken(invitation.getPhone());
					token = tokenService.save(token);

					invitation.setToken(token);
					invitation = invitationService.save(invitation);

				} else {
					throw new UserException("user already invited");
				}

			} else {
				throw new UserException("conflict");
			}
		} else {
			throw new UserException("username not provided");
		}

		return invitation;
	}

	private boolean isInvited(String username, Token token) {

		Token dbToken = tokenService.getTokenFromDB(username);

		if (dbToken != null) {

			if (isTokenValid(dbToken)) {

				if (dbToken.getTokenType().equals(TokenType.INVITATION)) {

					return true;
				} else {
					return false;
				}

			} else {
				tokenService.deleteInviteToken(username, dbToken);
				return false;
			}
		} else {

			return false;
		}
	}

	public boolean isExistingUser(String userName) throws Exception {

		if (userName != null || !userName.isEmpty()) {

			ApplicationUser applicationUser = applicationUserMongoRepository.findByEmail(userName);
			if (applicationUser == null) {
				applicationUser = applicationUserMongoRepository.findByPhone(userName);
				if (applicationUser != null) {
					return true;
				} else {
					return false;
				}
			} else {
				return true;
			}

		} else {
			throw new Exception("username not provided");
		}

	}

	boolean isTokenValid(Token dbToken) {
		ZonedDateTime currentDate = timeManager.getCurrentTimestamp();

		final ZoneId systemDefault = ZoneId.systemDefault();
		int days = Period
				.between(currentDate.toLocalDate(),
						ZonedDateTime.ofInstant(dbToken.getCreationDate().toInstant(), systemDefault).toLocalDate())
				.getDays();

		if (days > tokenExpDays) {

			throw new TokenException("ExpiredToken");
		} else {

			return true;
		}
	}

	public void verifyEmail(Token token) {

		Token dbToken = tokenService.getTokenFromDB(token.getUsername());

		if (dbToken.getTokenType().equals(TokenType.EMAILVERIFICATION)) {

			if (dbToken == null) {
				if (userMongoRepository.findByEmail(token.getUsername()) == null)
					throw new UserException("UnRegistered");
				else
					throw new UserException("ExistingUser");
			}

			isTokenValid(dbToken);
			if (!dbToken.getToken().equals(token.getToken()))
				throw new TokenException("InvalidToken");

			ApplicationUser user = userMongoRepository.findById(dbToken.getUserId()).get();
			user.setEnabled(true);
			userMongoRepository.save(user);
			tokenService.deleteByUsernameAndTokenAndTokenType(token.getUsername(), token.getToken(),
					token.getTokenType());

		} else if (dbToken.getTokenType().equals(TokenType.MAIL_RESET)) {

			if (dbToken == null) {
				if (userMongoRepository.findByEmail(token.getUsername()) == null)
					throw new UserException("UnRegistered");
				else
					throw new UserException("ExistingUser");
			}

			isTokenValid(dbToken);
			if (!dbToken.getToken().equals(token.getToken()))
				throw new TokenException("InvalidToken");

			ApplicationUser user = userMongoRepository.findById(dbToken.getUserId()).get();
			user.setEmail(token.getUsername());
			userMongoRepository.save(user);

			UserProfile userProfile = userProfileService.getProfile(dbToken.getUserId());
			userProfile.setEmail(token.getUsername());

			tokenService.deleteByUsernameAndTokenAndTokenType(token.getUsername(), token.getToken(),
					token.getTokenType());
		}

		return;

	}

	public void forgotPassword(@Valid String username) throws Exception {

		if (isExistingUser(username)) {

			EMail emailToSend = new EMail();
			Token dbToken = tokenService.getTokenByUserNameAndTokenType(username, TokenType.PASSWORDRESET);
			ApplicationUser appUser = userMongoRepository.findByEmail(username);
			if (dbToken == null) {

				Token token = tokenService.getPasswordResetToken(username);
				token.setUserId(appUser.getId());
				tokenService.save(token);
				sendMail(token);
				return;
			}
		} else {

			throw new UserException("user not exist");
		}

	}

	public void resetPassword(@Valid PasswordReset passwordReset) {
		Token dbToken = tokenService.getTokenByUserNameAndTokenType(passwordReset.getUsername(),
				TokenType.PASSWORDRESET);

		if (dbToken == null)
			throw new TokenException("InvalidToken");

		isTokenValid(dbToken);
		if (!passwordReset.getToken().equals(dbToken.getToken())) {
			throw new TokenException("InvalidToken");
		}

		ApplicationUser user = userMongoRepository.findByEmail(passwordReset.getUsername());

		user.setPassword(bCryptPasswordUtils.encodePassword(passwordReset.getPassword()));
		user.setAccountNonLocked(true);
		userMongoRepository.save(user);

		tokenService.deleteByUsernameAndTokenAndTokenType(passwordReset.getUsername(), passwordReset.getToken(),
				TokenType.PASSWORDRESET);
	}

	public void changePassword(@Valid PasswordResetResource passwordresetResource) {

		ApplicationUser user = userMongoRepository.findByEmail(passwordresetResource.getUsername());
		if (user != null) {
			// check if the password matches
			if (bCryptPasswordUtils.isMatching(passwordresetResource.getPassword(), user.getPassword())) {

				user.setPassword(bCryptPasswordUtils.encodePassword(passwordresetResource.getNewPassword()));
				userMongoRepository.save(user);

			} else {

				throw new UserException("username and password mismatch");
			}
		} else {
			throw new UserException("username not valid");
		}

	}

	public void verifyIdToken(Authentication authentication, HttpServletResponse res, FirebaseToken decodedToken,
			HttpServletRequest req) {

		ApplicationUser applicationUser = new ApplicationUser();
		Optional<ApplicationUser> applicationUserOptional = userMongoRepository.findById(decodedToken.getUid());

		if (applicationUserOptional.isPresent()) {

			// user exists. generate JWT
			applicationUser = applicationUserOptional.get();
			String token = jwtTokenProvider.generateToken(authentication);
			res.addHeader(HEADER_STRING, TOKEN_PREFIX + token);

		} else {
			// user not exist. create a new user with given uid
			// and generate JWT
			try {
				SignUpUser signUpUser = new SignUpUser(decodedToken.getEmail(), "welcome");

				signUp(signUpUser, req);

				String token = jwtTokenProvider.generateToken(authentication);
				res.addHeader(HEADER_STRING, TOKEN_PREFIX + token);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public UserProfile editProfile(UserProfile userProfile) {

		UserProfile dbUserProfile = userProfileService.getProfile(userProfile.getId());

		if (dbUserProfile != null) {

			if (userProfile.getUsername() != null && !userProfile.getUsername().isEmpty()) {

				if (dbUserProfile.getUsername() != null) {
					if (!dbUserProfile.getUsername().equals(userProfile.getUsername())) {
						dbUserProfile.setUsername(userProfile.getUsername());
					}
				} else {
					dbUserProfile.setUsername(userProfile.getUsername());
				}
			}
			if (userProfile.getFileId() != null && !userProfile.getFileId().isEmpty()) {

				if (dbUserProfile.getFileId() != null) {

					if (!dbUserProfile.getFileId().equals(userProfile.getFileId())) {

						FileMeta fileMeta = fileService.getFileById(userProfile.getId());

						if (fileService.isImageFile(fileMeta)) {

							dbUserProfile.setFileId(userProfile.getFileId());

						} else {

							throw new FileException("not an image file");
						}
					}
				} else {

					FileMeta fileMeta = fileService.getFileById(userProfile.getId());

					if (fileService.isImageFile(fileMeta)) {

						dbUserProfile.setFileId(userProfile.getFileId());

					} else {

						throw new FileException("not an image file");
					}
				}
			}
			dbUserProfile = userProfileService.save(dbUserProfile);
		}
		return dbUserProfile;
	}

	public void resetMail(UserProfile userProfile) throws Exception {

		UserProfile dbUserProfile = userProfileService.getProfile(userProfile.getId());

		Token resetMailToken = tokenService.getTokenByUserNameAndTokenType(userProfile.getUsername(),
				TokenType.MAIL_RESET);

		if (resetMailToken == null) {

			if (!isExistingUser(userProfile.getEmail())) {

				try {

					resetMailToken = tokenService.getMailResetToken(getApplicationUser(userProfile.getId()),
							userProfile.getEmail());

					if (dbUserProfile.getEmail() != null) {

						if (!dbUserProfile.getEmail().equals(userProfile.getEmail())) {

							sendMail(resetMailToken);

						} else {

							throw new Exception("this email has been already verified");
						}
					} else {

						sendMail(resetMailToken);
					}
				} catch (Exception e) {
					e.printStackTrace();
					tokenService.deleteToken(resetMailToken);
					throw new Exception(e.getMessage());
				}
			} else {
				throw new UserException("mail id already registered");
			}
		} else {
			throw new TokenException("reset mail request already exist");
		}

	}

	public UserProfile getProfile(String id) {

		return userProfileService.getProfile(id);
	}

	public ApplicationUser getApplicationUser(String id) {

		return userMongoRepository.findById(id).get();
	}

}
