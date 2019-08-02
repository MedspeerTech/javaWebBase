package com.piotics.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.piotics.constants.Targets;
import com.piotics.model.Activity;
import com.piotics.model.ActivityMarker;
import com.piotics.model.Post;
import com.piotics.repository.ActivityMongoRepository;
import com.piotics.repository.ActivityMongoTemplateImpl;
import com.piotics.resources.ActivityResource;

@Service
public class ActivityService {

	@Autowired
	ActivityMongoRepository activityMongoRepository;

	@Autowired
	ActivityMongoTemplateImpl activityMongoTemplateImpl;

	public Activity createActivityOnPostCreation(Post post) {

		Activity activity = new Activity(post, Targets.POST);
		return activityMongoRepository.save(activity);
	}

	public void deletePostActivities(Post post) {

		activityMongoRepository.deleteAllByActivityMarker(post);
	}

	public Activity getActivityById(String id) {
		
		return activityMongoRepository.findById(id).get();
	}
}
