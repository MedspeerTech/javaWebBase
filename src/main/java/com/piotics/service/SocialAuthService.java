package com.piotics.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.piotics.config.JwtTokenProvider;
import com.piotics.constants.UserRoles;
import com.piotics.exception.UserException;
import com.piotics.model.ApplicationSocialUser;
import com.piotics.model.ApplicationUser;
import com.piotics.model.Invitation;
import com.piotics.model.Tenant;
import com.piotics.model.UserProfile;
import com.piotics.repository.ApplicationSocialUserMongoRepository;
import com.piotics.repository.InvitationMongoRepository;
import com.piotics.repository.TokenMongoRepository;
import com.piotics.repository.UserMongoRepository;
import com.piotics.repository.UserProfileMongoRepository;
import com.piotics.resources.SocialUser;

@Service
public class SocialAuthService {

	@Autowired
	ApplicationSocialUserMongoRepository applicationSocialUserMongoRepository;

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@Autowired
	InvitationService invitationService;

	@Autowired
	TokenService tokenService;

	@Autowired
	UserProfileService userProfileService;

	@Autowired
	UserService userService;

	@Autowired
	TenantService tenantService;

	private ApplicationSocialUser applicationSocialUser;
	private ApplicationUser applicationUser;

	@Value("${invite.required}")
	public boolean inviteRequired;

	public String socialLogin(SocialUser socialUser) {

		if (!userService.isExistingUser(socialUser.getEmail())) {

			if (inviteRequired && !invitationService.isInvited(socialUser.getEmail())) {
				throw new UserException("user not invited");
			} else {
				Invitation invitation = invitationService.getInviationByUsername(socialUser.getEmail());

				if (tenantService.isTenantEnabled())
					return proceedTosocialLogin(socialUser, invitation);
				else
					return proceedTosocialLogin(socialUser, null);
			}
		} else {

			return proceedTosocialLogin(socialUser, null);
		}

	}
	
	private String proceedTosocialLogin(SocialUser socialUser, Invitation invitation) {

		Optional<ApplicationSocialUser> applicationSocialUser = applicationSocialUserMongoRepository
				.findBySocialId(socialUser.getId());

		if (applicationSocialUser.isPresent()) {
			this.applicationSocialUser = applicationSocialUser.get();
			this.applicationUser = userService.getApplicationUser(this.applicationSocialUser.getId());
		} else {

			this.applicationUser = userService.getUserByEmail(socialUser.getEmail());
			if (this.applicationUser == null) {

				if (invitation == null)
					createApplicationUser(socialUser, UserRoles.ROLE_USER, null);
				else
					createApplicationUser(socialUser, null, invitation);
//			createUserInfo(socialUser);
			} else if (!(this.applicationUser.isEnabled())) {
				this.applicationUser.setEnabled(true);
				userService.save(this.applicationUser);
			}
			ApplicationSocialUser newApplicationSocialUser = new ApplicationSocialUser(socialUser);
			newApplicationSocialUser.setId(this.applicationUser.getId());
			this.applicationSocialUser = applicationSocialUserMongoRepository.save(newApplicationSocialUser);

			UserProfile userProfile = new UserProfile(socialUser.getEmail(), this.applicationUser.getId());
			userProfileService.save(userProfile);

			if (invitationService.isInvited(this.applicationSocialUser.getEmail()))
				tokenService.deleteInviteTkenByUsername(this.applicationSocialUser.getEmail());
		}

		return prepareJWT(socialUser, applicationUser);
	}

//	private void createUserInfo(SocialUser socialUser) {
//		UserInfo userInfo=new UserInfo();
//		userInfo.setId(this.applicationUser.getId());
//		userInfo.setEmail(socialUser.getEmail());
//		userInfoJPARepository.save(userInfo);
//		userInfoShortJPARepository.save(new UserInfoShort(this.applicationUser.getId()));
//
//	}

	private String prepareJWT(SocialUser socialUser, ApplicationUser applicationUser) {
		UserDetails sessionDetails = new ApplicationUser(socialUser, applicationUser);
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(sessionDetails, null,
				sessionDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(auth);
		return jwtTokenProvider.generateJwtToken(auth, null);
	}

	private void createApplicationUser(SocialUser socialUser, UserRoles role, Invitation invitation) {
		ApplicationUser applicationUser = new ApplicationUser(socialUser, role);
		applicationUser.setEnabled(true);
		if (tenantService.isTenantEnabled() && invitation != null) {
			Tenant tenant = tenantService.getTenantById(invitation.getTenantId());
			applicationUser.setCompany(tenant);
			applicationUser.setRole(invitation.getUserRole());
			this.applicationUser = userService.save(applicationUser);

			UserProfile userProfile = userProfileService.getProfile(this.applicationUser.getId());
			tenant.setOwnerId(this.applicationUser.getId());
			tenantService.save(tenant);
			tenantService.updateTenatRelation(userProfile, tenant, invitation.getUserRole());
		} else {
			applicationUser.setRole(role);
			this.applicationUser = userService.save(applicationUser);
		}
	}

	public boolean isUserSignedUp(SocialUser socialUser) {
		Optional<ApplicationSocialUser> applicationSocialUser = applicationSocialUserMongoRepository
				.findBySocialId(socialUser.getId());
		if (applicationSocialUser.isPresent())
			this.applicationSocialUser = applicationSocialUser.get();
		return applicationSocialUser.isPresent();
	}
}
