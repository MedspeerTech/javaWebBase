package com.piotics.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.google.firebase.auth.FirebaseToken;
import com.piotics.common.TokenType;
import com.piotics.common.utils.BCryptPasswordUtils;
import com.piotics.common.utils.HttpServletRequestUtils;
import com.piotics.common.utils.UtilityManager;
import com.piotics.config.JwtTokenProvider;
import com.piotics.constants.UserRoles;
import com.piotics.exception.TokenException;
import com.piotics.exception.UserException;
import com.piotics.model.ApplicationUser;
import com.piotics.model.PasswordReset;
import com.piotics.model.SignUpUser;
import com.piotics.model.Token;
import com.piotics.model.UserProfile;
import com.piotics.model.UserShort;
import com.piotics.repository.UserMongoRepository;
import com.piotics.repository.UserShortMongoRepository;


@Service
@Lazy
public class UserService {

	@Autowired
	UserMongoRepository userMongoRepository;

	@Autowired
	TokenService tokenService;

	@Autowired
	BCryptPasswordUtils bCryptPasswordUtils;

//	@Autowired
//	ApplicationUserMongoRepository applicationUserMongoRepository;

	@Autowired
	HttpServletRequestUtils httpServletRequestUtils;

	@Autowired
	UtilityManager utilityManager;

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@Autowired
	UserProfileService userProfileService;

	@Autowired
	InvitationService invitationService;

	@Autowired
	MailService mailService;

	@Autowired
	UserShortMongoRepository userShortMongoRepository;

	@Autowired
	NotificationService notificationService;
	
	
	

	@Value("${invite.required}")
	public boolean inviteRequired;

	public UserService(UserMongoRepository userMongoRepository, UtilityManager utilityManager,
			BCryptPasswordUtils bCryptPasswordUtils, InvitationService invitationService, TokenService tokenService,
			UserProfileService userProfileService, MailService mailService, UserShortMongoRepository userShortMongoRepository) {

		this.userMongoRepository = userMongoRepository;
		this.utilityManager = utilityManager;
		this.bCryptPasswordUtils = bCryptPasswordUtils;
		this.invitationService = invitationService;
		this.tokenService = tokenService;
		this.userProfileService = userProfileService;
		this.mailService = mailService;
		this.userShortMongoRepository = userShortMongoRepository;
	}

	public void signUp(SignUpUser signUpUser) throws Exception {

		if (isExistingUser(signUpUser.getUsername()))
			throw new UserException("user already exists");

		if (inviteRequired && signUpUser.getToken() != null && !invitationService.isInvited(signUpUser.getUsername()))
			throw new UserException("user not invited");

		proceedToSignUp(signUpUser);

		return;
	}

	private void proceedToSignUp(SignUpUser signUpUser) throws Exception {

		String encodedPassword = bCryptPasswordUtils.encodePassword(signUpUser.getPassword());

		ApplicationUser newUser = new ApplicationUser(signUpUser.getUsername(), encodedPassword, UserRoles.ROLE_USER,true);
		Token token = new Token();

		if (invitationService.isInvited(signUpUser.getUsername()))
			tokenService.deleteInviteTkenByUsername(signUpUser.getUsername());

		if (utilityManager.isEmail(signUpUser.getUsername())) {

			if (signUpUser.getToken() == null) {
				newUser.setEnabled(false);
				token = tokenService.getTokenForEmailVerification(newUser);
				mailService.sendMail(token);
			}
		}

		newUser = userMongoRepository.save(newUser);
		token.setUserId(newUser.getId());
		tokenService.save(token);

		UserProfile userProfile = new UserProfile(newUser.getId(), newUser.getEmail(), newUser.getPhone());
		userProfileService.save(userProfile);
	}

	public boolean isExistingUser(String userName) {
		return !(userMongoRepository.findByEmail(userName) == null
				&& userMongoRepository.findByPhone(userName) == null);

	}

	public void verifyEmail(Token token) {

		Token dbToken = tokenService.getTokenFromDBWithTokenType(token.getUsername(), TokenType.EMAILVERIFICATION);

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
		tokenService.deleteByUsernameAndTokenType(token.getUsername(), TokenType.EMAILVERIFICATION);
		return;

	}

	public void forgotPassword(String username) {

		if (!isExistingUser(username))
			throw new UserException("user not exist");

		Token dbToken = tokenService.getTokenByUserNameAndTokenType(username, TokenType.PASSWORDRESET);

		if (dbToken != null && tokenService.isTokenValid(dbToken)) {

			mailService.sendMail(dbToken);
		} else {
			ApplicationUser appUser = userMongoRepository.findByEmail(username);
			Token token = tokenService.getPasswordResetToken(username);
			token.setUserId(appUser.getId());
			tokenService.save(token);
			mailService.sendMail(token);
			return;
		}

	}

	public void resetPassword(PasswordReset passwordReset) {
		Token dbToken = tokenService.getTokenByUserNameAndTokenType(passwordReset.getUsername(),
				TokenType.PASSWORDRESET);

		if (dbToken == null)
			throw new TokenException("no token found for password reset");

		tokenService.isTokenValid(dbToken);
		if (!passwordReset.getToken().equals(dbToken.getToken()))
			throw new TokenException("InvalidToken");

		ApplicationUser user = userMongoRepository.findByEmail(passwordReset.getUsername());

		user.setPassword(bCryptPasswordUtils.encodePassword(passwordReset.getPassword()));
		user.setAccountNonLocked(true);
		userMongoRepository.save(user);

		tokenService.deleteByUsernameAndTokenAndTokenType(passwordReset.getUsername(), passwordReset.getToken(),
				TokenType.PASSWORDRESET);
	}

	public String verifyIdToken(Authentication authentication, FirebaseToken decodedToken) {

		ApplicationUser applicationUser = new ApplicationUser();
		Optional<ApplicationUser> applicationUserOptional = userMongoRepository.findById(decodedToken.getUid());
		String token = null;
		if (applicationUserOptional.isPresent()) {

			// user exists. generate JWT
			applicationUser = applicationUserOptional.get();
			token = jwtTokenProvider.generateToken(authentication);
		} else {
			// user not exist. create a new user with given uid
			// and generate JWT
			try {
				SignUpUser signUpUser = new SignUpUser(decodedToken.getEmail(), "welcome");

				signUp(signUpUser);

				token = jwtTokenProvider.generateToken(authentication);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return token;
	}

	public ApplicationUser getApplicationUser(String id) {

		return userMongoRepository.findById(id).get();
	}

	public ApplicationUser save(ApplicationUser applicationUser) {

		return userMongoRepository.save(applicationUser);
	}

	public List<UserShort> getUserShortOfAdmins() {

		List<ApplicationUser> adminUsers = userMongoRepository.findByRole(UserRoles.ROLE_ADMIN);

		List<UserShort> adminUserShortLi = new ArrayList<UserShort>();

		for (ApplicationUser admin : adminUsers) {

			adminUserShortLi.add(userShortMongoRepository.findById(admin.getId()).get());
		}

		return adminUserShortLi;
	}

	public UserShort getUserShort(String id) {

		return userShortMongoRepository.findById(id).get();
	}
}
