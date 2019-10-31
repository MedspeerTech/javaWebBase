package com.piotics.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.piotics.model.ApplicationUser;
import com.piotics.model.Session;
import com.piotics.model.TenantRelation;
import com.piotics.resources.StringResource;
import com.piotics.resources.TenantInviteResource;
import com.piotics.service.AdminService;

@RestController
@RequestMapping(value = "/admin")
public class AdminController {

	@Autowired
	AdminService adminService;

	@PostMapping(value = "/invite")
	@PreAuthorize("@AccessFilter.isPowerAdmin(authentication)")
	public ResponseEntity<StringResource> invite(Principal principal, @RequestBody StringResource invitationLi) {

		Session session = (Session) ((Authentication) (principal)).getPrincipal();
		StringResource failedList = adminService.sendInvite(session, invitationLi);

		return new ResponseEntity<>(failedList, HttpStatus.OK);
	}

	@PostMapping(value = "/invite/tenantUser")
	@PreAuthorize("@AccessFilter.isCompanyAdmin(authentication)")
	public ResponseEntity<StringResource> tenantUserInvite(Principal principal,
			@RequestBody List<TenantInviteResource> invitationLi) {

		Session session = (Session) ((Authentication) (principal)).getPrincipal();
		StringResource failedList = adminService.sendTenantInvite(session, invitationLi);
		return new ResponseEntity<>(failedList, HttpStatus.OK);
	}

}
