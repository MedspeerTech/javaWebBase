package com.piotics.service;

import static com.piotics.config.SecurityConstants.HEADER_STRING;
import static com.piotics.config.SecurityConstants.TOKEN_PREFIX;

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
import com.piotics.common.TokenType;
import com.piotics.common.utils.BCryptPasswordUtils;
import com.piotics.common.utils.HttpServletRequestUtils;
import com.piotics.config.JwtTokenProvider;
import com.piotics.constants.UserRoles;
import com.piotics.exception.TokenException;
import com.piotics.exception.UserException;
import com.piotics.model.ApplicationUser;
import com.piotics.model.PasswordReset;
import com.piotics.model.SignUpUser;
import com.piotics.model.Token;
import com.piotics.model.UserProfile;
import com.piotics.repository.ApplicationUserMongoRepository;
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
	MailManager mailManager;

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@Autowired
	UserProfileService userProfileService;

	@Autowired
	InvitationService invitationService;
	
	@Autowired
	MailService mailService;

	@Value("${invite.required}")
	public boolean inviteRequired;

	

	public void signUp(SignUpUser signUpUser, HttpServletRequest req) throws Exception {

		if (!isExistingUser(signUpUser.getUsername())) {

			if (inviteRequired) {

				if (invitationService.isInvited(signUpUser.getUsername(), signUpUser.getToken())) {
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

		String encodedPassword = bCryptPasswordUtils.encodePassword(signUpUser.getPassword());

		ApplicationUser newUser = new ApplicationUser(encodedPassword, UserRoles.ROLE_USER);
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
					mailService.sendMail(token);
				}
			} else {
				newUser.setEnabled(true);
			}
		}
		newUser = userMongoRepository.save(newUser);

		UserProfile userProfile = new UserProfile(newUser.getId(), newUser.getEmail(), newUser.getPhone());
		userProfileService.save(userProfile);
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

	

	public void verifyEmail(Token token) {

		Token dbToken = tokenService.getTokenFromDBWithTokenType(token.getUsername(), token.getTokenType());

		if (dbToken.getTokenType().equals(TokenType.EMAILVERIFICATION)) {

			if (dbToken == null) {
				if (userMongoRepository.findByEmail(token.getUsername()) == null)
					throw new UserException("UnRegistered");
				else
					throw new UserException("ExistingUser");
			}

			tokenService.isTokenValid(dbToken);
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

			tokenService.isTokenValid(dbToken);
			if (!dbToken.getToken().equals(token.getToken()))
				throw new TokenException("InvalidToken");

			ApplicationUser user = userMongoRepository.findById(dbToken.getUserId()).get();
			user.setEmail(token.getUsername());
			userMongoRepository.save(user);

			UserProfile userProfile = userProfileService.getProfile(dbToken.getUserId());
			userProfile.setEmail(token.getUsername());
			userProfileService.save(userProfile);

			tokenService.deleteByUsernameAndTokenAndTokenType(token.getUsername(), token.getToken(),
					token.getTokenType());
		}

		return;

	}

	public void forgotPassword(@Valid String username) throws Exception {

		if (isExistingUser(username)) {

			Token dbToken = tokenService.getTokenByUserNameAndTokenType(username, TokenType.PASSWORDRESET);
			ApplicationUser appUser = userMongoRepository.findByEmail(username);
			if (dbToken == null) {

				Token token = tokenService.getPasswordResetToken(username);
				token.setUserId(appUser.getId());
				tokenService.save(token);
				mailService.sendMail(token);
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

		tokenService.isTokenValid(dbToken);
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

	public ApplicationUser getApplicationUser(String id) {

		return userMongoRepository.findById(id).get();
	}

}
