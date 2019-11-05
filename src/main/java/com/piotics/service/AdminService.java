package com.piotics.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.piotics.common.utils.UtilityManager;
import com.piotics.constants.UserRoles;
import com.piotics.exception.UserException;
import com.piotics.model.Invitation;
import com.piotics.model.Session;
import com.piotics.model.Tenant;
import com.piotics.model.Token;
import com.piotics.model.UserProfile;
import com.piotics.model.UserShort;
import com.piotics.resources.StringResource;
import com.piotics.resources.TenantInviteResource;

@Service
@PropertySource("classpath:setup.properties")
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

	@Autowired
	UserProfileService userProfileService;

	@Autowired
	TenantService tenantService;

	@Value("${tenant.enabled}")
	boolean tenatEnabled;

	public StringResource sendInvite(Session session, StringResource invitationLi) {

		List<Invitation> invitations = populateStringsToInvitation(session, invitationLi);
		return invite(session, invitations);
	}

	public StringResource invite(Session session, List<Invitation> invitations) {

		List<String> failedList = new ArrayList<>();
		for (Invitation invitation : invitations) {
			String emailOrPhone = null;
			try {

				if (invitation.getEmail() != null && !invitation.getEmail().isEmpty()) {

					emailOrPhone = invitation.getEmail();
					sendInvite(invitation.getEmail(), invitation);

				} else if (invitation.getPhone() != null && !invitation.getPhone().isEmpty()) {

					emailOrPhone = invitation.getPhone();
					sendInvite(invitation.getPhone(), invitation);
				}
				notificationService.notifyAdminsOnUserInvite(session, invitation,emailOrPhone);
			} catch (UserException e) {

				failedList.add(emailOrPhone);
			}
		}

		return new StringResource(failedList);
	}

	private List<Invitation> populateStringsToInvitation(Session session, StringResource invitationLi) {

		List<Invitation> invitations = new ArrayList<>();
		for (String invitedId : invitationLi.getStrings()) {

			Invitation invitation = new Invitation();
			UserShort invitedBy = userService.getUserShort(session.getId());
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

		Principal principal = SecurityContextHolder.getContext().getAuthentication();
		Session session = (Session) ((Authentication) (principal)).getPrincipal();
		
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

			if (tenatEnabled && session.getRole().equals(UserRoles.ROLE_ADMIN)) {
				UserProfile userProfile = new UserProfile();
				if (utilityManager.isEmail(username)) {
					userProfile = userProfileService.getProfileByMail(username);
				} else {
					userProfile = userProfileService.getProfileByPhone(username);
				}
				Tenant tenant = tenantService.getTenantById(invitation.getTenantId());
				tenantService.updateTenatRelation(userProfile, tenant, invitation.getUserRole());
				notificationService.notifyUserOnTenantInvitation(userProfile,invitation);
			} else {
				throw new UserException("existing user");
			}
		}

		return invitation;
	}

	public StringResource sendTenantInvite(Session session, List<TenantInviteResource> invitationLi) {
		
		List<Invitation> invitations = populateTenantInviteResourceToInvitation(session, invitationLi);
		return invite(session, invitations);
	}

	private List<Invitation> populateTenantInviteResourceToInvitation(Session session,
			List<TenantInviteResource> invitationLi) {
		List<Invitation> invitations = new ArrayList<>();
		
		
		for (TenantInviteResource inviteResource : invitationLi) {

			Invitation invitation = new Invitation();
			UserShort invitedBy = userService.getUserShort(session.getId());
			invitation.setInvitedBY(invitedBy);
			invitation.setTenantId(inviteResource.getTenantId());
			invitation.setUserRole(inviteResource.getUserRole());
			if (utilityManager.isEmail(inviteResource.getUsername())) {

				invitation.setEmail(inviteResource.getUsername());
			} else {
				invitation.setPhone(inviteResource.getUsername());
			}
			invitations.add(invitation);
		}

		return invitations;
	}

}
