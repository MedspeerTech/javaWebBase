package com.piotics.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.piotics.exception.UserException;
import com.piotics.model.ApplicationUser;
import com.piotics.model.Invitation;
import com.piotics.model.Token;

@Service
public class AdminService {

	@Autowired
	UserService userService;

	@Autowired
	TokenService tokenService;

	@Autowired
	InvitationService invitationService;

	@Autowired
	MailService mailService;

	@Autowired
	NotificationService notificationService;

	public Invitation invite(ApplicationUser applicationUser, Invitation invitation) throws Exception {

		String phone = invitation.getPhone();
		String email = invitation.getEmail();
		String notificationTitle = "";

		if (email != null && !email.isEmpty()) {

			sendInvite(email, invitation);
			notificationTitle = email;

		} else if (phone != null && !phone.isEmpty()) {

			sendInvite(phone, invitation);
			notificationTitle = phone;

		} else {
			throw new UserException("username not provided");
		}

		notificationService.notifyAdminsOnUserInvite(applicationUser, invitation, notificationTitle);
		return invitation;
	}

	private Invitation sendInvite(String username, Invitation invitation) {

		if (!userService.isExistingUser(username)) {

			if (!invitationService.isInvited(username)) {

				Token token = tokenService.getInviteToken(username);
				token = tokenService.save(token);

				if (invitation.getEmail() != null) {

					mailService.sendMail(token);
				}

				invitation.setToken(token);
				invitation = invitationService.save(invitation);

			} else {
				throw new UserException("user already invited");
			}

		} else {
			throw new UserException("existing user");
		}

		return invitation;
	}

}
