package com.piotics.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.piotics.config.JwtTokenProvider;
import com.piotics.constants.UserRoles;
import com.piotics.exception.UserException;
import com.piotics.model.ApplicationSocialUser;
import com.piotics.model.ApplicationUser;
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
	UserMongoRepository userMongoRepository;

	@Autowired
	InvitationMongoRepository invitationMongoRepository;

	@Autowired
	UserProfileMongoRepository userProfileMongoRepository;

	@Autowired
	TokenMongoRepository tokenMongoRepository;

	@Autowired
	UserService userService;

	private ApplicationSocialUser applicationSocialUser;
	private ApplicationUser applicationUser;

	@Value("${invite.required}")
	public boolean inviteRequired;

	public String socialLogin(SocialUser socialUser) {

		if (!userService.isExistingUser(socialUser.getEmail())) {

			if (inviteRequired && !invitationService.isInvited(socialUser.getEmail()))
				throw new UserException("user not invited");
			else
				proceedTosocialLogin(socialUser);
		}else {
			proceedTosocialLogin(socialUser);
		}
		return null;
	}

	private String proceedTosocialLogin(SocialUser socialUser) {

		Optional<ApplicationSocialUser> applicationSocialUser = applicationSocialUserMongoRepository
				.findBySocialId(socialUser.getId());

		if (applicationSocialUser.isPresent()) {
			this.applicationSocialUser = applicationSocialUser.get();
			Optional<ApplicationUser> applicationUser = userMongoRepository
					.findById(this.applicationSocialUser.getId());
			this.applicationUser = applicationUser.get();

		} else {

			this.applicationUser = userMongoRepository.findByUsername(socialUser.getEmail());
			if (this.applicationUser == null) {
				createApplicationUser(socialUser, UserRoles.ROLE_USER);
//			createUserInfo(socialUser);
			} else if (!(this.applicationUser.isEnabled())) {
				this.applicationUser.setEnabled(true);
				userMongoRepository.save(this.applicationUser);
			}
			ApplicationSocialUser newApplicationSocialUser = new ApplicationSocialUser(socialUser);
			newApplicationSocialUser.setId(this.applicationUser.getId());
			this.applicationSocialUser = applicationSocialUserMongoRepository.save(newApplicationSocialUser);

			UserProfile userProfile = new UserProfile(socialUser.getEmail(), this.applicationUser.getId());
			userProfileMongoRepository.save(userProfile);

			if (invitationService.isInvited(this.applicationSocialUser.getEmail()))
				tokenService.deleteInviteTkenByUsername(this.applicationSocialUser.getEmail());
		}

		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(this.applicationUser, null,
				null);
//		String token = jwtTokenProvider.generateToken(auth);

//		return token;
		return null;
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
		this.applicationUser = userMongoRepository.save(applicationUser);
	}

	public boolean isUserSignedUp(SocialUser socialUser) {
		Optional<ApplicationSocialUser> applicationSocialUser = applicationSocialUserMongoRepository
				.findBySocialId(socialUser.getId());
		this.applicationSocialUser = applicationSocialUser.get();
		return applicationSocialUser.isPresent();
	}
}
