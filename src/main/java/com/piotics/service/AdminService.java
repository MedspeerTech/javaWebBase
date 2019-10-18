package com.piotics.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.piotics.common.utils.UtilityManager;
import com.piotics.exception.UserException;
import com.piotics.model.ApplicationUser;
import com.piotics.model.Invitation;
import com.piotics.model.Token;
import com.piotics.model.UserShort;
import com.piotics.resources.StringResource;

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

	@Autowired
	UtilityManager utilityManager;

//	public Invitation invite(ApplicationUser applicationUser, Invitation invitation) throws Exception {
//
//		String phone = invitation.getPhone();
//		String email = invitation.getEmail();
//		String notificationTitle = "";
//
//		if (email != null && !email.isEmpty()) {
//
//			sendInvite(email, invitation);
//			notificationTitle = email;
//
//		} else if (phone != null && !phone.isEmpty()) {
//
//			sendInvite(phone, invitation);
//			notificationTitle = phone;
//
//		} else {
//			throw new UserException("username not provided");
//		}
//
//		notificationService.notifyAdminsOnUserInvite(applicationUser, invitation, notificationTitle);
//		return invitation;
//	}

	public StringResource senInvite(ApplicationUser applicationUser, StringResource invitationLi) throws Exception {

		List<Invitation> invitations = populateStringsToInvitation(applicationUser, invitationLi);
		return invite(applicationUser, invitations);
	}

	public StringResource invite(ApplicationUser applicationUser, List<Invitation> invitations) throws Exception {

		List<String> failedList = new ArrayList<>();
		String notificationTitle = "";

		for (Invitation invitation : invitations) {
			String emailOrPhone = null;
			try {

				if (invitation.getEmail() != null && !invitation.getEmail().isEmpty()) {

					emailOrPhone = invitation.getEmail();
					sendInvite(invitation.getEmail(), invitation);
					notificationTitle = invitation.getEmail();

				} else if (invitation.getPhone() != null && !invitation.getPhone().isEmpty()) {

					emailOrPhone = invitation.getPhone();
					sendInvite(invitation.getPhone(), invitation);
					notificationTitle = invitation.getPhone();
				}
				notificationService.notifyAdminsOnUserInvite(applicationUser, invitation, notificationTitle);

			} catch (UserException e) {

				failedList.add(emailOrPhone);
			}
		}
		StringResource stringResource = new StringResource(failedList);

		return stringResource;
	}

	private List<Invitation> populateStringsToInvitation(ApplicationUser applicationUser, StringResource invitationLi) {

		List<Invitation> invitations = new ArrayList<>();
		for (String invitedId : invitationLi.getStrings()) {

			Invitation invitation = new Invitation();
			UserShort invitedBy = userService.getUserShort(applicationUser.getId());
			invitation.setInvitedBY(invitedBy);

			if (utilityManager.isEmail(invitedId)) {

				invitation.setEmail(invitedId);
			} else {
				invitation.setPhone(invitedId);
			}

			invitations.add(invitation);
		}

		return invitations;
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
