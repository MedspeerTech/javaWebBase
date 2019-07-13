package com.piotics.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.piotics.exception.UserException;
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
	
	public Invitation invite(Invitation invitation) throws Exception {

		String phone = invitation.getPhone();
		String email = invitation.getEmail();

		if (email != null && !email.isEmpty()) {
			if (!userService.isExistingUser(email)) {

				if (!invitationService.isInvited(email, null)) {

					Token token = tokenService.getInviteToken(invitation.getEmail());
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
		} else if (phone != null && !phone.isEmpty()) {

			if (!userService.isExistingUser(phone)) {

				// user not exist continue signup

				if (!invitationService.isInvited(phone, null)) {

					Token token = tokenService.getInviteToken(invitation.getPhone());
					token = tokenService.save(token);

					invitation.setToken(token);
					invitation = invitationService.save(invitation);

				} else {
					throw new UserException("user already invited");
				}

			} else {
				throw new UserException("conflict");
			}
		} else {
			throw new UserException("username not provided");
		}

		return invitation;
	}

}
