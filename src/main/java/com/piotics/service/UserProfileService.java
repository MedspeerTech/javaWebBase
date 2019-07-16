package com.piotics.service;

import javax.validation.Valid;

import org.apache.tools.ant.taskdefs.SendEmail;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.piotics.common.TokenType;
import com.piotics.common.utils.BCryptPasswordUtils;
import com.piotics.common.utils.UtilityManager;
import com.piotics.exception.FileException;
import com.piotics.exception.TokenException;
import com.piotics.exception.UserException;
import com.piotics.model.ApplicationUser;
import com.piotics.model.FileMeta;
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


}
