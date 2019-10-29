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
import org.springframework.web.bind.annotation.RestController;

import com.piotics.model.ApplicationUser;
import com.piotics.resources.StringResource;
import com.piotics.service.AdminService;

@RestController
@RequestMapping(value = "/admin")
public class AdminController {

	@Autowired
	AdminService adminService;

	@PostMapping(value = "/invite")
	@PreAuthorize("@AccessFilter.isAdmin(authentication)")
	public ResponseEntity<StringResource> invite(Principal principal, @RequestBody StringResource invitationLi) {

		ApplicationUser applicationUser = (ApplicationUser) ((Authentication) (principal)).getPrincipal();
		StringResource failedList = adminService.sendInvite(applicationUser, invitationLi);

		return new ResponseEntity<>(failedList, HttpStatus.OK);
	}

}
