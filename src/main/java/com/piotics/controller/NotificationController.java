package com.piotics.controller;

import java.security.Principal;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.piotics.model.ApplicationUser;
import com.piotics.model.Notification;
import com.piotics.model.Session;
import com.piotics.resources.NotificationResource;
import com.piotics.service.NotificationService;

@RestController
@RequestMapping(value = "/notification")
public class NotificationController {

	@Autowired
	NotificationService notificationService;

	@GetMapping(value = "/list/{pageNo}")
	public ResponseEntity<NotificationResource> getNotificationList(Principal principal, @PathVariable int pageNo) {

		Session session = (Session) ((Authentication) (principal)).getPrincipal();
		NotificationResource notificationResource = notificationService.getNotificationList(session, pageNo);

		return new ResponseEntity<>(notificationResource, HttpStatus.OK);
	}
	
	@PostMapping(value = "/read")
	@PreAuthorize("@AccessFilter.hasAccess(authentication,#notification.getUserToNotify().getId())")
	public ResponseEntity<Notification> readNotification(Principal principal,@RequestBody @Valid Notification notification){		
		notificationService.readNotification(notification);
		return new ResponseEntity<> (HttpStatus.OK);
	}
	
	@PostMapping(value = "/resetCount")
	public ResponseEntity<Notification> resetNewNotificationCount(Principal principal){	
		Session session = (Session) ((Authentication) (principal)).getPrincipal();
		notificationService.resetNewNotificationCount(session);
		return new ResponseEntity<> (HttpStatus.OK);
	}
}
