package com.piotics.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.piotics.config.JwtTokenProvider;
import com.piotics.constants.*;
import com.piotics.exception.UserException;
import com.piotics.model.ApplicationSocialUser;
import com.piotics.model.ApplicationUser;
import com.piotics.model.Invitation;
import com.piotics.repository.ApplicationSocialUserMongoRepository;
import com.piotics.repository.ApplicationUserMongoRepository;
import com.piotics.repository.InvitationMongoRepository;
import com.piotics.resources.SocialUser;

@Service
public class SocialAuthService {

	@Autowired
	ApplicationSocialUserMongoRepository applicationSocialUserMongoRepository;

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@Autowired
	ApplicationUserMongoRepository applicationUserMongoRepository;

	@Autowired
	InvitationMongoRepository invitationMongoRepository;


	private ApplicationSocialUser applicationSocialUser;
	private ApplicationUser applicationUser;

	public String socialLogin(SocialUser socialUser) {
		if (isInvited(socialUser)) {

			Optional<ApplicationSocialUser> applicationSocialUser = applicationSocialUserMongoRepository
					.findBySocialId(socialUser.getId());

			if (applicationSocialUser.isPresent()) {
				this.applicationSocialUser = applicationSocialUser.get();
				Optional<ApplicationUser> applicationUser = applicationUserMongoRepository
						.findById(this.applicationSocialUser.getId());
				this.applicationUser = applicationUser.get();

			} else {

				this.applicationUser = applicationUserMongoRepository.findByUsername(socialUser.getEmail());
				if (this.applicationUser == null) {
					createApplicationUser(socialUser, UserRoles.ROLE_USER);
//				createUserInfo(socialUser);
				} else if (!(this.applicationUser.isEnabled())) {
					this.applicationUser.setEnabled(true);
					applicationUserMongoRepository.save(this.applicationUser);
				}
				ApplicationSocialUser newApplicationSocialUser = new ApplicationSocialUser(socialUser);
				newApplicationSocialUser.setId(this.applicationUser.getId());
				this.applicationSocialUser = applicationSocialUserMongoRepository.save(newApplicationSocialUser);

			}

			UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(this.applicationUser,
					null, null);
//			String token = jwtTokenProvider.generateToken(auth);

//			return token;
			return null;

		} else {
			throw new UserException("user not invited");
		}
	}

	private boolean isInvited(SocialUser socialUser) {

		boolean bool = false;
		
		if (socialUser.getEmail() != null) {

			Invitation invitation = invitationMongoRepository.findByEmail(socialUser.getEmail());

			if (invitation == null) {

				bool = false;
			} else {

				bool = true;
			}
		}
		return bool;
	}

//	private void createUserInfo(SocialUser socialUser) {
//		UserInfo userInfo=new UserInfo();
//		userInfo.setId(this.applicationUser.getId());
//		userInfo.setEmail(socialUser.getEmail());
//		userInfoJPARepository.save(userInfo);
//		userInfoShortJPARepository.save(new UserInfoShort(this.applicationUser.getId()));
//
//	}

	private void createApplicationUser(SocialUser socialUser, UserRoles role) {
		ApplicationUser applicationUser = new ApplicationUser(socialUser, role);
		applicationUser.setEnabled(true);
		applicationUser.setRole(UserRoles.ROLE_USER);
		this.applicationUser = applicationUserMongoRepository.save(applicationUser);
	}

	public boolean isUserSignedUp(SocialUser socialUser) {
		Optional<ApplicationSocialUser> applicationSocialUser = applicationSocialUserMongoRepository
				.findBySocialId(socialUser.getId());
		this.applicationSocialUser = applicationSocialUser.get();
		return applicationSocialUser.isPresent();
	}
}
