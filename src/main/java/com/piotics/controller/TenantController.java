package com.piotics.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.piotics.model.ApplicationUser;
import com.piotics.model.Session;
import com.piotics.model.Tenant;
import com.piotics.service.TenantService;

@RestController
@RequestMapping(value = "/tenant")
public class TenantController {

	@Autowired
	TenantService tenantService;

	@PostMapping(value = "/create")
	@PreAuthorize("@AccessFilter.hasTenantCreationAccess(authentication)")
	public ResponseEntity<Tenant> create(Principal principal, @RequestBody Tenant tenant) throws Exception {
		Session session = (Session) ((Authentication) (principal)).getPrincipal();
		tenant = tenantService.createTenant(session,tenant);
		return new ResponseEntity<>(tenant, HttpStatus.OK);
	}
	
	@PostMapping(value = "edit")
	@PreAuthorize("@AccessFilter.hasAdminAccessToTenant(authentication,#tenant.getId())")
	public ResponseEntity<Tenant> edit(@RequestBody Tenant tenant){
		tenant = tenantService.editTenant(tenant);
		return new ResponseEntity<>(tenant,HttpStatus.OK);
	}

}
