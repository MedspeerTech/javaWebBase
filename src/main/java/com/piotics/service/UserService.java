package com.piotics.service;

import static com.piotics.config.SecurityConstants.*;

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
import com.piotics.config.JwtTokenProvider;
import com.piotics.constants.UserRoles;
import com.piotics.exception.TokenException;
import com.piotics.exception.UserException;
import com.piotics.model.ApplicationUser;
import com.piotics.model.EMail;
import com.piotics.model.Invitation;
import com.piotics.model.PasswordReset;
import com.piotics.model.PasswordResetResource;
import com.piotics.model.SignUpUser;
import com.piotics.model.Token;
import com.piotics.repository.ApplicationUserMongoRepository;
import com.piotics.repository.InvitationMongoRepository;
import com.piotics.repository.TokenMongoRepository;
import com.piotics.repository.UserMongoRepository;

import com.piotics.common.utils.HttpServletRequestUtils;

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
	JwtTokenProvider jwtTokenProvider;

//	@Value("#{isInviteRequired}")
//	@Value("#{new Boolean('${invite.required}'.trim())}")
	private Boolean inviteRequired = true;

	@Value("${token.expiration.days}")
	Integer tokenExpDays;

	public void signUp(SignUpUser signUpUser, HttpServletRequest req) throws Exception {

		ApplicationUser newUser = new ApplicationUser();

		if (inviteRequired) {

			if (isInvited(signUpUser.getUserName(), signUpUser.getToken())) {

				if (!isExistingUser(signUpUser.getUserName())) {

					BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
					String password = bCryptPasswordEncoder.encode(signUpUser.getPassword());
					newUser.setUsername(signUpUser.getUserName());
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
					} else {

						newUser.setEnabled(false);
						tokenMongoRepository.deleteByUsernameAndTokenType(signUpUser.getUserName(),
								TokenType.INVITATION);
						
						String clientBrowser = httpServletRequestUtils.getClientBrowser(req);
						boolean remember = Boolean.parseBoolean(req.getHeader("Remember"));

						if (!remember || !clientBrowser.contains("Android")||!clientBrowser.contains("IPhone")) {
							if (mailManager.isEmail(signUpUser.getUserName())) {
								Token token = tokenManager.getTokenForEmailVerification(signUpUser.getUserName());
								tokenMongoRepository.save(token);
								EMail email = mailManager.composeSignupVerificationEmail(token);
								mailManager.sendEmail(email);
							}
						}else {
							newUser.setEnabled(true);
						}
					}
					newUser = userMongoRepository.save(newUser);

				} else {

					throw new UserException("user already exists");
				}

			} else {

				throw new UserException("user not invited");
			}

		} else {

			if (!isExistingUser(signUpUser.getUserName())) {

				BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
				String password = bCryptPasswordEncoder.encode(signUpUser.getPassword());
				newUser.setUsername(signUpUser.getUserName());
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

			} else {

				throw new UserException("user already exists");
			}

		}

		return;

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

		Token dbToken = tokenMongoRepository.findByUsernameAndTokenType(token.getUsername(),
				TokenType.EMAILVERIFICATION);

		if (dbToken == null) {
			if (userMongoRepository.findByUsername(token.getUsername()) == null)
				throw new UserException("UnRegistered");
			else
				throw new UserException("ExistingUser");
		}

		isTokenValid(dbToken);
		if (!dbToken.getToken().equals(token.getToken()))
			throw new TokenException("InvalidToken");

		ApplicationUser user = userMongoRepository.findByUsername(token.getUsername());
		user.setEnabled(true);
		userMongoRepository.save(user);
		tokenMongoRepository.deleteByUsernameAndTokenAndTokenType(token.getUsername(), token.getToken(),
				TokenType.EMAILVERIFICATION);

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
	
}
