package com.piotics.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.piotics.common.TokenType;
import com.piotics.common.utils.BCryptPasswordUtils;
import com.piotics.common.utils.UtilityManager;
import com.piotics.exception.TokenException;
import com.piotics.exception.UserException;
import com.piotics.model.ApplicationUser;
import com.piotics.model.PasswordResetResource;
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

	public UserProfile save(UserProfile userProfile) {

		return userProfileMongoRepository.save(userProfile);
	}

	public UserProfile getProfile(String id) {

		UserProfile userProfile = userProfileMongoRepository.findById(id).get();
		return userProfile;
	}

	public UserProfile saveProfile(UserProfile userProfile) {

		UserProfile dbUserProfile = userProfileMongoRepository.findById(userProfile.getId()).get();

		BeanUtils.copyProperties(userProfile, dbUserProfile, "email", "phone", "id");

		dbUserProfile = userProfileMongoRepository.save(dbUserProfile);

		return dbUserProfile;
	}

	public UserProfile changeMail(ApplicationUser applicationUser, String mail) throws Exception {

		if (!utilityManager.isEmail(mail))
			throw new Exception("not a valid email");

		if (userService.isExistingUser(mail))
			throw new UserException("email already registered");

		if (applicationUser.getEmail() == mail)
			throw new Exception("no change");

		try {

			Token resetMailToken = tokenService.getTokenByUserNameAndTokenType(mail, TokenType.MAIL_RESET);

			if (resetMailToken != null && tokenService.isTokenValid(resetMailToken)) {

				mailService.sendMail(resetMailToken);
			} else {

				resetMailToken = tokenService.getMailResetToken(applicationUser, mail);
				mailService.sendMail(resetMailToken);
			}

		} catch (Exception e) {

			e.printStackTrace();
			throw e;
		}

		return null;
	}

	public void verifyNewMail(ApplicationUser applicationUser, String token) throws Exception {

		Token dbToken = tokenService.getTokenFromDbByUserIdAndTokenType(applicationUser.getId(), TokenType.MAIL_RESET);

		if (dbToken == null)
			throw new Exception("no valid request for change mail");

		tokenService.isTokenValid(dbToken);

		if (!dbToken.getToken().equals(token))
			throw new TokenException("InvalidToken");

		applicationUser.setEmail(dbToken.getUsername());
		userService.save(applicationUser);

		UserProfile userProfile = getProfile(applicationUser.getId());
		userProfile.setEmail(dbToken.getUsername());
		save(userProfile);

		tokenService.deleteToken(dbToken);
	}

	
	public void changePassword(ApplicationUser applicationUser, PasswordResetResource passwordresetResource) throws Exception {

		if(!bCryptPasswordUtils.isMatching(passwordresetResource.getPassword(), applicationUser.getPassword()))
			throw new Exception("wrong password");
		
		if(bCryptPasswordUtils.isMatching(passwordresetResource.getNewPassword(), applicationUser.getPassword()))
			throw new Exception("no change found");
		
		applicationUser.setPassword(bCryptPasswordUtils.encodePassword(passwordresetResource.getNewPassword()));
		
		userService.save(applicationUser);
	}


}
