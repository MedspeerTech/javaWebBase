package com.piotics.controller;

import java.security.Principal;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.piotics.model.ApplicationUser;
import com.piotics.model.Notification;
import com.piotics.resources.NotificationResource;
import com.piotics.service.NotificationService;

@RestController
@RequestMapping(value = "/notification")
public class NotificationController {

	@Autowired
	NotificationService notificationService;

	@RequestMapping(value = "/list/{pageNo}", method = RequestMethod.GET)
	public ResponseEntity<NotificationResource> getNotificationList(Principal principal, @PathVariable int pageNo) {

		ApplicationUser applicationUser = (ApplicationUser) ((Authentication) (principal)).getPrincipal();
		NotificationResource notificationResource = notificationService.getNotificationList(applicationUser, pageNo);

		return new ResponseEntity<NotificationResource>(notificationResource, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/read", method = RequestMethod.POST)
	@PreAuthorize("@AccessFilter.hasAccess(authentication,#notification.getUserToNotify().getId())")
	public ResponseEntity<Notification> readNotification(Principal principal,@RequestBody @Valid Notification notification){		
		notificationService.readNotification(notification);
		return new ResponseEntity<> (HttpStatus.OK);
	}
	
	@RequestMapping(value = "/resetCount", method = RequestMethod.POST)
	public ResponseEntity<Notification> resetNewNotificationCount(Principal principal){	
		ApplicationUser applicationUser = (ApplicationUser) ((Authentication) (principal)).getPrincipal();
		notificationService.resetNewNotificationCount(applicationUser);
		return new ResponseEntity<> (HttpStatus.OK);
	}
}
