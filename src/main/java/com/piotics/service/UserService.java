package com.piotics.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.piotics.repository.ApplicationUserMongoRepository;
import com.piotics.repository.InvitationMongoRepository;
import com.piotics.repository.TokenMongoRepository;
import com.piotics.repository.UserMongoRepository;
import com.piotics.common.MailManager;
import com.piotics.common.TokenManager;
import com.piotics.constants.UserRoles;
import com.piotics.exception.UserException;
import com.piotics.model.ApplicationUser;
import com.piotics.model.EMail;
import com.piotics.model.Invitation;
import com.piotics.model.Token;

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

	public Invitation invite(Invitation invitation) throws Exception {

		if (!isExistingUser(invitation)) {

			// user not exist continue signup

			if (!isInvited(invitation)) {

				Token token = tokenManager.getTokenForEmailVerification(invitation.getEmail());
				tokenMongoRepository.save(token);
				
				if (invitation.getEmail() != null) {
					EMail email = mailManager.composeInviteVerificationEmail(token);
					mailManager.sendEmail(email);
				}
				invitation.setToken(token);
				invitationMongoRepository.save(invitation);

			} else {

				throw new UserException("conflict");
			}

		} else {
			throw new UserException("conflict");
		}

		return null;
	}

	private boolean isInvited(Invitation invitation) {

		boolean bool = true;

		if (invitation.getEmail() != null || invitation.getPhone() != null) {

			if (invitation.getEmail() != null) {

				Invitation invitationDB = invitationMongoRepository.findByEmail(invitation.getEmail());

				if (invitationDB == null) {

					bool = false;
				} else {

					bool = true;
				}
			}
			if (invitation.getPhone() != null) {

				Invitation invitationDB = invitationMongoRepository.findByPhone(invitation.getPhone());

				if (invitationDB == null) {

					bool = false;
				} else {

					bool = true;
				}
			}

		}

		return bool;
	}

	public boolean isExistingUser(Invitation invitation) throws Exception {

		boolean bool = true;

		if (invitation.getEmail() != null || invitation.getPhone() != null) {

			if (invitation.getEmail() != null) {

				ApplicationUser applicationUser = applicationUserMongoRepository.findByEmail(invitation.getEmail());

				if (applicationUser == null) {

					bool = false;

				} else {
					bool = true;
				}
			}
			if (invitation.getPhone() != null) {

				ApplicationUser applicationUser = applicationUserMongoRepository.findByPhone(invitation.getPhone());

				if (applicationUser == null) {

					bool = false;

				} else {
					bool = true;
				}
			}
		} else {

			throw new Exception("invalid data");
		}

		return bool;
	}

}
