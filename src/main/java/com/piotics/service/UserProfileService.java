package com.piotics.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.piotics.common.TokenType;
import com.piotics.common.utils.BCryptPasswordUtils;
import com.piotics.common.utils.UtilityManager;
import com.piotics.exception.TokenException;
import com.piotics.exception.UserException;
import com.piotics.model.ApplicationUser;
import com.piotics.model.PasswordResetResource;
import com.piotics.model.Session;
import com.piotics.model.Tenant;
import com.piotics.model.Token;
import com.piotics.model.UserProfile;
import com.piotics.repository.UserMongoRepository;
import com.piotics.repository.UserProfileMongoRepository;

@Service
public class UserProfileService {

	@Autowired
	FileService fileService;

	@Autowired
	UserProfileMongoRepository userProfileMongoRepository;

	@Autowired
	@Lazy
	UserService userService;

	@Autowired
	TokenService tokenService;

	@Autowired
	MailService mailService;

	@Autowired
	UserMongoRepository userMongoRepository;

	@Autowired
	BCryptPasswordUtils bCryptPasswordUtils;

	@Autowired
	UtilityManager utilityManager;

	public UserProfileService() {
	}

	public UserProfile save(UserProfile userProfile) {

		return userProfileMongoRepository.save(userProfile);
	}

	public UserProfile getProfile(String id) {
		Optional<UserProfile> userProfileOptional = userProfileMongoRepository.findById(id);
		UserProfile userProfile = new UserProfile();
		if (userProfileOptional.isPresent()) {
			userProfile = userProfileOptional.get();
		}
		return userProfile;
	}

	public UserProfile saveProfile(UserProfile userProfile) {

		Optional<UserProfile> userProfileOptional = userProfileMongoRepository.findById(userProfile.getId());
		UserProfile dbUserProfile = new UserProfile();
		if (userProfileOptional.isPresent()) {
			dbUserProfile = userProfileOptional.get();
		}

		BeanUtils.copyProperties(userProfile, dbUserProfile, "email", "phone", "id","tenantRelations","userRole");

		dbUserProfile = userProfileMongoRepository.save(dbUserProfile);

		return dbUserProfile;
	}

	public UserProfile changeMail(Session session, String mail){

		ApplicationUser applicationUser = userService.getApplicationUser(session.getId());
		if (!utilityManager.isEmail(mail))
			throw new UserException("not a valid email");

		if (userService.isExistingUser(mail))
			throw new UserException("email already registered");

		if (applicationUser.getEmail().equals(mail))
			throw new UserException("no change found");

		try {

			Token resetMailToken = tokenService.getTokenByUserNameAndTokenType(mail, TokenType.MAIL_RESET);

			if (resetMailToken != null && tokenService.isTokenValid(resetMailToken)) {

				mailService.sendMail(resetMailToken);
			} else {

				resetMailToken = tokenService.getMailResetToken(session, mail);
				mailService.sendMail(resetMailToken);
			}

		} catch (Exception e) {

			e.printStackTrace();
			throw e;
		}

		return null;
	}

	public void verifyNewMail(Session session, String token){

		Token dbToken = tokenService.getTokenFromDbByUserIdAndTokenType(session.getId(), TokenType.MAIL_RESET);

		if (dbToken == null)
			throw new UserException("no valid request for change mail");

		tokenService.isTokenValid(dbToken);

		if (!dbToken.getToken().equals(token))
			throw new TokenException("InvalidToken");
		
		ApplicationUser applicationUser = userService.getApplicationUser(session.getId());
		applicationUser.setEmail(dbToken.getUsername());
		userService.save(applicationUser);

		UserProfile userProfile = getProfile(session.getId());
		userProfile.setEmail(dbToken.getUsername());
		save(userProfile);

		tokenService.deleteToken(dbToken);
	}

	public void changePassword(Session session, PasswordResetResource passwordresetResource){
		
		ApplicationUser applicationUser = userService.getApplicationUser(session.getId());

		if (!bCryptPasswordUtils.isMatching(passwordresetResource.getPassword(), applicationUser.getPassword()))
			throw new UserException("wrong password");

		if (bCryptPasswordUtils.isMatching(passwordresetResource.getNewPassword(), applicationUser.getPassword()))
			throw new UserException("no change found");

		applicationUser.setPassword(bCryptPasswordUtils.encodePassword(passwordresetResource.getNewPassword()));

		userService.save(applicationUser);
	}

	public UserProfile getProfileByMail(String email) {
		return userProfileMongoRepository.findByEmail(email);
	}

	public UserProfile getProfileByPhone(String phone) {
		return userProfileMongoRepository.findByPhone(phone);
	}

	public List<UserProfile> getProfileByRelationteanatName(Tenant tenant) {
		return userProfileMongoRepository.findByTenantRelationsTenantName(tenant.getName());
	}

}
