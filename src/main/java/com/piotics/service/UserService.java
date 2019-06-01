package com.piotics.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.piotics.Repository.TokenMongoRepository;
import com.piotics.Repository.UserMongoRepository;
import com.piotics.common.MailManager;
import com.piotics.common.TokenManager;
import com.piotics.constants.UserRoles;
import com.piotics.exception.UserException;
import com.piotics.model.ApplicationUser;
import com.piotics.model.EMail;
import com.piotics.model.Token;


@Service
public class UserService {

	@Autowired
	UserMongoRepository userMongoRepository;

	@Autowired
	TokenMongoRepository tokenMongoRepository;

	@Autowired
	TokenManager tokenManager;

	@Autowired
	MailManager mailManager;

	public void signUp(ApplicationUser applicationUser) {

		ApplicationUser newUser = null;

		if (userMongoRepository.findByUsername(applicationUser.getUsername()) == null) {
			BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
			String password = bCryptPasswordEncoder.encode(applicationUser.getPassword());
			applicationUser.setPassword(password);
			applicationUser.setRole(UserRoles.ROLE_USER);
			newUser = userMongoRepository.save(applicationUser);
		} else {
			throw new UserException("ExistingUser");
		}
		Token token = tokenManager.getTokenForEmailVerification(applicationUser.getUsername());
		tokenMongoRepository.save(token); 
		EMail email = mailManager.composeSignupVerificationEmail(token);
		
		mailManager.sendEmail(email);
		return;
	}


}
