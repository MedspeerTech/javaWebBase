package com.piotics.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.piotics.model.Invitation;
import com.piotics.service.AdminService;

@RestController
@RequestMapping(value="/admin")
public class AdminController {
	
	@Autowired
	AdminService adminService;

	@RequestMapping(value = "/invite", method = RequestMethod.POST)
	public ResponseEntity<Invitation> invite(@RequestBody Invitation invitation) throws Exception {

		invitation = adminService.invite(invitation);

		return new ResponseEntity<Invitation>(invitation, HttpStatus.OK);
	}

}
