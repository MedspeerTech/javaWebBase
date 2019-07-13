package com.piotics.service;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.piotics.common.TokenType;
import com.piotics.common.utils.BCryptPasswordUtils;
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
	
	public UserProfile save(UserProfile userProfile) {

		return userProfileMongoRepository.save(userProfile);
	}

	public UserProfile getProfile(String id) {

		UserProfile userProfile = userProfileMongoRepository.findById(id).get();
		return userProfile;
	}

	public UserProfile editProfile(UserProfile userProfile) {

		UserProfile dbUserProfile = getProfile(userProfile.getId());

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
			dbUserProfile = save(dbUserProfile);
		}
		return dbUserProfile;
	}

	public void resetMail(UserProfile userProfile) throws Exception {

		UserProfile dbUserProfile = getProfile(userProfile.getId());

		Token resetMailToken = tokenService.getTokenByUserNameAndTokenType(userProfile.getUsername(),
				TokenType.MAIL_RESET);

		if (resetMailToken == null) {

			if (!userService.isExistingUser(userProfile.getEmail())) {

				try {
					resetMailToken = tokenService.getMailResetToken(userService.getApplicationUser(userProfile.getId()),
							userProfile.getEmail());
					if (dbUserProfile.getEmail() != null) {

						if (!dbUserProfile.getEmail().equals(userProfile.getEmail())) {

							mailService.sendMail(resetMailToken);

						} else {

							throw new Exception("this email has been already verified");
						}
					} else {

						mailService.sendMail(resetMailToken);
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
}
