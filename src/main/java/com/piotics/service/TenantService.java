package com.piotics.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.piotics.constants.UserRoles;
import com.piotics.model.ApplicationUser;
import com.piotics.model.Invitation;
import com.piotics.model.Tenant;
import com.piotics.model.TenantRelation;
import com.piotics.model.Token;
import com.piotics.model.UserProfile;
import com.piotics.repository.TenantMongoRepository;

@Service
public class TenantService {

	@Autowired
	UserService userService;

	@Autowired
	UserProfileService userProfileService;

	@Autowired
	TokenService tokenService;

	@Autowired
	TenantMongoRepository tenantMongoRepository;

	@Autowired
	MailService mailService;

	@Autowired
	AdminService adminService;

	public Tenant createTenant(ApplicationUser applicationUser, Tenant tenant) throws Exception {

		if (userService.isExistingUser(tenant.getOwnerEmail())) {

			UserProfile userProfile = userProfileService.getProfileByMail(tenant.getOwnerEmail());
			tenant.setOwnerId(userProfile.getId());

			userProfile = updateTenatRelation(userProfile, tenant);
			userProfileService.save(userProfile);
			tenantMongoRepository.save(tenant);
			
			mailService.notifyOwnerOnTenantCreation(tenant);
		} else {

			tenant = tenantMongoRepository.save(tenant);
			List<Invitation> invitations = new ArrayList<>();
			invitations.add(new Invitation(tenant.getId(), UserRoles.ROLE_ADMIN, tenant.getOwnerEmail()));
			adminService.invite(applicationUser, invitations);
		}

		return tenant;
	}

	private UserProfile updateTenatRelation(UserProfile userProfile, Tenant tenant) {

		TenantRelation tenantRelation = new TenantRelation(tenant.getName(), UserRoles.ROLE_ADMIN);
		if (userProfile.getTenantRelations() != null) {

			List<TenantRelation> tenantRelations = userProfile.getTenantRelations();
			tenantRelations.add(tenantRelation); 
			userProfile.setTenantRelations(tenantRelations);
		}else {
			List<TenantRelation> tenantRelations = new ArrayList<>();
			tenantRelations.add(tenantRelation);
			userProfile.setTenantRelations(tenantRelations);
		}
		return userProfile;
	}

}
