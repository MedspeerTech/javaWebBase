package com.piotics.service;

import static com.piotics.config.SecurityConstants.HEADER_STRING;
import static com.piotics.config.SecurityConstants.TOKEN_PREFIX;

import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.firebase.auth.FirebaseToken;
import com.piotics.common.MailManager;
import com.piotics.common.TimeManager;
import com.piotics.common.TokenManager;
import com.piotics.common.TokenType;
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
import com.piotics.repository.InvitationMongoRepository;
import com.piotics.repository.TokenMongoRepository;
import com.piotics.repository.UserMongoRepository;
import com.piotics.repository.UserProfileMongoRepository;

@Service
public class UserService {

	@Autowired
	UserMongoRepository userMongoRepository;

	@Autowired
	TokenMongoRepository tokenMongoRepository;

	@Autowired
	ApplicationUserMongoRepository applicationUserMongoRepository;

	@Autowired
	InvitationMongoRepository invitationMongoRepository;

	@Autowired
	HttpServletRequestUtils httpServletRequestUtils;

	@Autowired
	TokenManager tokenManager;

	@Autowired
	TimeManager timeManager;

	@Autowired
	MailManager mailManager;

	@Autowired
	UserProfileMongoRepository userProfileMongoRepository;

	@Autowired
	FileService fileService;

	@Autowired
	FileMetaMongoRepository fileMetaMongoRepository;

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@Value("${invite.required}")
	public boolean inviteRequired;

	@Value("${token.expiration.days}")
	Integer tokenExpDays;

	public ApplicationUser signUp(SignUpUser signUpUser, HttpServletRequest req) throws Exception {

		ApplicationUser appUser = new ApplicationUser();
		ApplicationUser newUser = new ApplicationUser();

		if (inviteRequired) {

			if (!isExistingUser(signUpUser.getUserName())) {

				if (isInvited(signUpUser.getUserName(), signUpUser.getToken())) {

					BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
					String password = bCryptPasswordEncoder.encode(signUpUser.getPassword());
//					newUser.setUsername(signUpUser.getUserName());
					newUser.setPassword(password);
					newUser.setRole(UserRoles.ROLE_USER);
					newUser = userMongoRepository.save(newUser);
					if (mailManager.isEmail(signUpUser.getUserName())) {
						newUser.setEmail(signUpUser.getUserName());
					} else {
						newUser.setPhone(signUpUser.getUserName());
					}

					if (signUpUser.getToken() != null) {
						newUser.setEnabled(true);
						tokenMongoRepository.deleteByUsernameAndTokenAndTokenType(signUpUser.getUserName(),
								signUpUser.getToken().getToken(), TokenType.INVITATION);
						appUser.setEnabled(true);
					} else {

						newUser.setEnabled(false);
						tokenMongoRepository.deleteByUsernameAndTokenType(signUpUser.getUserName(),
								TokenType.INVITATION);

						String clientBrowser = httpServletRequestUtils.getClientBrowser(req);
						boolean remember = Boolean.parseBoolean(req.getHeader("Remember"));

						if (!remember || !clientBrowser.contains("Android") || !clientBrowser.contains("IPhone")) {
							if (mailManager.isEmail(signUpUser.getUserName())) {
								Token token = tokenManager.getTokenForEmailVerification(signUpUser.getUserName());
								token.setUserId(newUser.getId());
								token.setTokenType(TokenType.EMAILVERIFICATION);
								tokenMongoRepository.save(token);
								EMail email = mailManager.composeSignupVerificationEmail(token);
								mailManager.sendEmail(email);
								appUser.setEnabled(false);
							}
						} else {
							newUser.setEnabled(true);
							appUser.setEnabled(true);
						}
					}
					newUser = userMongoRepository.save(newUser);

					UserProfile userProfile = new UserProfile();
					userProfile.setId(newUser.getId());
					userProfile.setEmail(newUser.getEmail());
					userProfile.setPhone(newUser.getPhone());
					userProfile = userProfileMongoRepository.save(userProfile);

				} else {

					throw new UserException("user not invited");
				}

			} else {

				throw new UserException("user already exists");
			}

		} else {

			if (!isExistingUser(signUpUser.getUserName())) {

				BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
				String password = bCryptPasswordEncoder.encode(signUpUser.getPassword());
//				newUser.setUsername(signUpUser.getUserName());
				newUser.setPassword(password);
				newUser.setRole(UserRoles.ROLE_USER);
				newUser = userMongoRepository.save(newUser);
				newUser.setEnabled(true);
				if (mailManager.isEmail(signUpUser.getUserName())) {
					newUser.setEmail(signUpUser.getUserName());
				} else {
					newUser.setPhone(signUpUser.getUserName());
				}

				if (mailManager.isEmail(signUpUser.getUserName())) {
					Token token = tokenManager.getTokenForEmailVerification(signUpUser.getUserName());
					tokenMongoRepository.save(token);
					EMail email = mailManager.composeSignupVerificationEmail(token);

					mailManager.sendEmail(email);
				}
				newUser = userMongoRepository.save(newUser);

				UserProfile userProfile = new UserProfile();
				userProfile.setId(newUser.getId());
				userProfile.setEmail(newUser.getEmail());
				userProfile.setPhone(newUser.getPhone());
				userProfile = userProfileMongoRepository.save(userProfile);
				
				appUser.setEnabled(true);

			} else {

				throw new UserException("user already exists");
			}

		}

		return appUser;

	}

	public Invitation invite(Invitation invitation) throws Exception {

		String phone = invitation.getPhone();
		String email = invitation.getEmail();

		if (email != null && !email.isEmpty()) {
			if (!isExistingUser(email)) {

				if (!isInvited(email, null)) {

					Token token = tokenManager.getTokenForEmailVerification(invitation.getEmail());
					token.setUsername(email);
					token.setTokenType(TokenType.INVITATION);
					token.setCreationDate(Date.from(timeManager.getCurrentTimestamp().toInstant()));
					tokenMongoRepository.save(token);

					if (invitation.getEmail() != null) {
						EMail eMail = mailManager.composeInviteVerificationEmail(token);
						mailManager.sendEmail(eMail);
					}
					invitation.setToken(token);
					invitation = invitationMongoRepository.save(invitation);

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

					Token token = tokenManager.getTokenForEmailVerification(invitation.getEmail());
					token.setUsername(phone);
					token.setTokenType(TokenType.INVITATION);
					token.setCreationDate(Date.from(timeManager.getCurrentTimestamp().toInstant()));
					tokenMongoRepository.save(token);

					invitation.setToken(token);
					invitation = invitationMongoRepository.save(invitation);

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

	private boolean isInvited(String userName, Token token) {

		Token dbToken = tokenMongoRepository.findByUsernameAndTokenType(userName, TokenType.INVITATION);
		if (dbToken != null) {

			if (isTokenValid(dbToken)) {

				if (token != null) {
					if (dbToken.getToken().equals(token.getToken())) {

						return true;
					} else {

						throw new TokenException("InvalidToken");
					}
				} else {
					return true;
				}

			} else {
				tokenMongoRepository.delete(dbToken);
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
//        long diff = currentDate - dbToken.getCreationDate().getTime();
//        long days = diff / 1000 / 60 / 60 / 24;
		final ZoneId systemDefault = ZoneId.systemDefault();
		int days = Period
				.between(currentDate.toLocalDate(),
						ZonedDateTime.ofInstant(dbToken.getCreationDate().toInstant(), systemDefault).toLocalDate())
				.getDays();

		if (days > tokenExpDays) {

//			tokenMongoRepository.delete(dbToken);
			throw new TokenException("ExpiredToken");
		} else {

			return true;
		}
	}

	public void verifyEmail(Token token) {

		Token dbToken = tokenMongoRepository.findByUsernameAndTokenType(token.getUsername(), token.getTokenType());

		if (token.getTokenType().equals(TokenType.EMAILVERIFICATION)) {

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
			tokenMongoRepository.deleteByUsernameAndTokenAndTokenType(token.getUsername(), token.getToken(),
					token.getTokenType());
		} else if (token.getTokenType().equals(TokenType.MAIL_RESET)) {

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

			UserProfile userProfile = userProfileMongoRepository.findById(dbToken.getUserId()).get();
			userProfile.setEmail(token.getUsername());

			tokenMongoRepository.deleteByUsernameAndTokenAndTokenType(token.getUsername(), token.getToken(),
					token.getTokenType());
		}

		return;

	}

	public void forgotPassword(@Valid String username) throws Exception {

		if (isExistingUser(username)) {

			EMail emailToSend = new EMail();
			Token dbToken = tokenMongoRepository.findByUsernameAndTokenType(username, TokenType.PASSWORDRESET);
			if (dbToken == null) {
				Token token = tokenManager.getTokenForPasswordReset(username);
				tokenMongoRepository.save(token);
				emailToSend = mailManager.composeForgotPasswordMail(token);
				mailManager.sendEmail(emailToSend);
				return;
			}
		} else {

			throw new UserException("user not exist");
		}

	}

	public void resetPassword(@Valid PasswordReset passwordReset) {
		Token dbToken = tokenMongoRepository.findByUsernameAndTokenType(passwordReset.getUsername(),
				TokenType.PASSWORDRESET);
		if (dbToken == null)
			throw new TokenException("InvalidToken");

		isTokenValid(dbToken);
		if (!passwordReset.getToken().equals(dbToken.getToken())) {
			throw new TokenException("InvalidToken");
		}

		ApplicationUser user = userMongoRepository.findByUsername(passwordReset.getUsername());
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		user.setPassword(bCryptPasswordEncoder.encode(passwordReset.getPassword()));
		user.setAccountNonLocked(true);
		userMongoRepository.save(user);

		tokenMongoRepository.deleteByUsernameAndTokenAndTokenType(passwordReset.getUsername(), passwordReset.getToken(),
				TokenType.PASSWORDRESET);

	}

	public void changePassword(@Valid PasswordResetResource passwordresetResource) {

		ApplicationUser user = userMongoRepository.findByUsername(passwordresetResource.getUserName());
		if (user != null) {
			BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

			// check if the password matches
			if (bCryptPasswordEncoder.matches(passwordresetResource.getPassword(), user.getPassword())) {

				user.setPassword(bCryptPasswordEncoder.encode(passwordresetResource.getNewPassword()));
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
				SignUpUser signUpUser = new SignUpUser();
				signUpUser.setUserName(decodedToken.getEmail());
				signUpUser.setPassword("welcome");

				signUp(signUpUser, req);

				String token = jwtTokenProvider.generateToken(authentication);

				res.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public UserProfile editProfile(UserProfile userProfile) {

		UserProfile dbUserProfile = userProfileMongoRepository.findById(userProfile.getId()).get();

		if (dbUserProfile != null) {

			if (userProfile.getUserName() != null && !userProfile.getUserName().isEmpty()) {

				if (dbUserProfile.getUserName() != null) {
					if (!dbUserProfile.getUserName().equals(userProfile.getUserName())) {
						dbUserProfile.setUserName(userProfile.getUserName());
					}
				} else {
					dbUserProfile.setUserName(userProfile.getUserName());
				}
			}
			if (userProfile.getFileId() != null && !userProfile.getFileId().isEmpty()) {

				if (dbUserProfile.getFileId() != null) {

					if (!dbUserProfile.getFileId().equals(userProfile.getFileId())) {

						FileMeta fileMeta = fileMetaMongoRepository.findById(userProfile.getId()).get();

						if (fileService.isImageFile(fileMeta)) {

							dbUserProfile.setFileId(userProfile.getFileId());

						} else {

							throw new FileException("not an image file");
						}
					}
				} else {

					FileMeta fileMeta = fileMetaMongoRepository.findById(userProfile.getId()).get();

					if (fileService.isImageFile(fileMeta)) {

						dbUserProfile.setFileId(userProfile.getFileId());

					} else {

						throw new FileException("not an image file");
					}
				}
			}
			dbUserProfile = userProfileMongoRepository.save(dbUserProfile);
		}
		return dbUserProfile;
	}

	public UserProfile getProfile(String id) {

		UserProfile userProfile = userProfileMongoRepository.findById(id).get();
		return userProfile;
	}

	public void resetMail(UserProfile userProfile) throws Exception {

		UserProfile dbUserProfile = userProfileMongoRepository.findById(userProfile.getId()).get();

		Token resetMailToken = tokenMongoRepository.findByUsernameAndTokenType(userProfile.getUserName(),
				TokenType.MAIL_RESET);
		if (resetMailToken == null) {

			if (!isExistingUser(userProfile.getEmail())) {

				try {
					resetMailToken = new Token();
					String token = TokenManager.getToken();
					Date date = Date.from(timeManager.getCurrentTimestamp().toInstant());
					resetMailToken = new Token(userProfile.getId(), userProfile.getEmail(), token, TokenType.MAIL_RESET,
							date);

					resetMailToken = tokenMongoRepository.save(resetMailToken);
					if (dbUserProfile.getEmail() != null) {

						if (!dbUserProfile.getEmail().equals(userProfile.getEmail())) {

							EMail emailToSend = new EMail();
							emailToSend = mailManager.composeMailResetVerificationEmail(resetMailToken);
							mailManager.sendEmail(emailToSend);
						} else {

							throw new Exception("this email has been already verified");
						}
					} else {

						EMail emailToSend = new EMail();
						emailToSend = mailManager.composeMailResetVerificationEmail(resetMailToken);
						mailManager.sendEmail(emailToSend);
					}
				} catch (Exception e) {
					e.printStackTrace();
					tokenMongoRepository.delete(resetMailToken);
					throw new Exception(e.getMessage());
				}
			} else {
				throw new UserException("mail id already registered");
			}
		} else {
			throw new TokenException("reset mail request already exist");
		}

	}

}
