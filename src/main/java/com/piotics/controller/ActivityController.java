package com.piotics.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.piotics.model.Activity;
import com.piotics.service.ActivityService;

@RestController
@RequestMapping(value = "/activity")
public class ActivityController {
	
	@Autowired
	ActivityService activityService;
	
	@GetMapping(value="/get/{id}")
	public ResponseEntity<Activity>	getActivityById(@PathVariable String id) {		
		Activity activity = activityService.getActivityById(id);
		return new ResponseEntity<>(activity,HttpStatus.OK); 
	}

}
