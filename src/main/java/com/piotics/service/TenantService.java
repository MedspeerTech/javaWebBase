package com.piotics.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.piotics.constants.UserRoles;
import com.piotics.exception.ResourceNotFoundException;
import com.piotics.model.Invitation;
import com.piotics.model.Notification;
import com.piotics.model.Session;
import com.piotics.model.Tenant;
import com.piotics.model.TenantRelation;
import com.piotics.model.UserProfile;
import com.piotics.repository.TenantMongoRepository;
import com.piotics.repository.UserProfileMongoRepository;

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

	@Autowired
	UserProfileMongoRepository userProfileMongoRepository;

	@Autowired
	NotificationService notificationService;

	@Value("${tenant.enabled}")
	boolean tenantEnabled;

	public Tenant createTenant(Session session, Tenant tenant) throws Exception {

		if (userService.isExistingUser(tenant.getOwnerEmail())) {

			UserProfile userProfile = userProfileService.getProfileByMail(tenant.getOwnerEmail());
			tenant.setOwnerId(userProfile.getId());

			tenant = tenantMongoRepository.save(tenant);
			userProfile = updateTenatRelation(userProfile, tenant, UserRoles.ROLE_ADMIN);
			userProfileService.save(userProfile);

			mailService.notifyOwnerOnTenantCreation(tenant);
		} else {

			tenant = tenantMongoRepository.save(tenant);
			List<Invitation> invitations = new ArrayList<>();
			invitations.add(new Invitation(tenant.getId(), UserRoles.ROLE_ADMIN, tenant.getOwnerEmail()));
			adminService.invite(session, invitations);
		}

		return tenant;
	}

	public UserProfile updateTenatRelation(UserProfile userProfile, Tenant tenant, UserRoles userRole) {

		TenantRelation tenantRelation = new TenantRelation(tenant.getName(), userRole);
		if (userProfile.getTenantRelations() != null && !isRelationAlreadyExisting(userProfile, tenant)) {

			List<TenantRelation> tenantRelations = userProfile.getTenantRelations();
			tenantRelations.add(tenantRelation);
			userProfile.setTenantRelations(tenantRelations);
		} else {
			List<TenantRelation> tenantRelations = new ArrayList<>();
			tenantRelations.add(tenantRelation);
			userProfile.setTenantRelations(tenantRelations);
		}
		return userProfileService.save(userProfile);
	}

	private boolean isRelationAlreadyExisting(UserProfile userProfile, Tenant tenant) {

		List<UserProfile> userProfiles = userProfileService.getProfileByRelationteanatName(tenant); 
		return (userProfiles.contains(userProfile));
	}

	public Tenant getTenantById(String tenantId) {
		Optional<Tenant> tenantOptional = tenantMongoRepository.findById(tenantId);
		if (tenantOptional.isPresent()) {
			return tenantOptional.get();
		} else {
			throw new ResourceNotFoundException("invalid tenant id");
		}
	}

	public boolean isTenantEnabled() {
		return tenantEnabled;
	}

	public Tenant editTenant(Tenant tenant) {
		if (tenant.getId() != null) {
			Optional<Tenant> tenantDBOptional = tenantMongoRepository.findById(tenant.getId());
			if (tenantDBOptional.isPresent()) {
				Tenant tenantDB = tenantDBOptional.get();
				BeanUtils.copyProperties(tenant, tenantDB, "id", "fileId", "ownerId", "ownerEmail");
				return save(tenantDB);
			} else {
				throw new ResourceNotFoundException("tenant id shoud not be null");
			}
		} else {
			throw new ResourceNotFoundException("tenant id shoud not be null");
		}
	}

	public Tenant save(Tenant tenant) {
		return tenantMongoRepository.save(tenant);
	}

}
