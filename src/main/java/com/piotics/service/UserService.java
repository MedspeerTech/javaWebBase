package com.piotics.service;

import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
//import com.google.cloud.Date;
import com.piotics.common.MailManager;
import com.piotics.common.TimeManager;
import com.piotics.common.TokenManager;
import com.piotics.common.TokenType;
import com.piotics.constants.UserRoles;
import com.piotics.exception.TokenException;
import com.piotics.exception.UserException;
import com.piotics.model.ApplicationUser;
import com.piotics.model.EMail;
import com.piotics.model.Invitation;
import com.piotics.model.SignUpUser;
import com.piotics.model.Token;
import com.piotics.repository.ApplicationUserMongoRepository;
import com.piotics.repository.InvitationMongoRepository;
import com.piotics.repository.TokenMongoRepository;
import com.piotics.repository.UserMongoRepository;

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
	TokenManager tokenManager;

	@Autowired
	TimeManager timeManager;
	@Autowired
	MailManager mailManager;

//	@Value("#{isInviteRequired}")
//	@Value("#{new Boolean('${invite.required}'.trim())}")
	private Boolean inviteRequired = true;

	@Value("${token.expiration.days}")
	Integer tokenExpDays;

	public void signUp(SignUpUser signUpUser) throws Exception {

		ApplicationUser newUser = new ApplicationUser();

		if (inviteRequired) {

			if (isInvited(signUpUser.getUserName(), signUpUser.getToken())) {

				if (!isExistingUser(signUpUser.getUserName())) {

					BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
					String password = bCryptPasswordEncoder.encode(signUpUser.getPassword());
					newUser.setUsername(signUpUser.getUserName());
					newUser.setPassword(password);
					newUser.setRole(UserRoles.ROLE_USER);
					newUser.setEnabled(true);
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

						if (mailManager.isEmail(signUpUser.getUserName())) {
							Token token = tokenManager.getTokenForEmailVerification(signUpUser.getUserName());
							tokenMongoRepository.save(token);
							EMail email = mailManager.composeSignupVerificationEmail(token);
							mailManager.sendEmail(email);
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
				if(applicationUser != null) {
					return true;
				}else {
					return false;
				}
			}else {
				return true;
			}

		
		} else {
			throw new Exception("username not provided");
		}

	}

//	public boolean isExistingUser(String userName) throws Exception {
//
//		boolean bool = true;
//
//		if (userName != null || !userName.isEmpty()) {
//
//			ApplicationUser applicationUser = applicationUserMongoRepository.findByEmail(userName);
//
//			if (applicationUser == null) {
//
//				bool = false;
//
//			} else {
//
//				applicationUser = applicationUserMongoRepository.findByPhone(userName);
//
//				if (applicationUser == null) {
//
//					bool = false;
//				}
//			}
//		}
//
//		return bool;
//	}

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
}
