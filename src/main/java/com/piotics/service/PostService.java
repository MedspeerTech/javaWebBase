package com.piotics.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.piotics.common.TimeManager;
import com.piotics.model.Activity;
import com.piotics.model.ApplicationUser;
import com.piotics.model.Post;
import com.piotics.model.UserShort;
import com.piotics.repository.PostMongoRepository;
import com.piotics.repository.PostMongoTemplateImpl;

@Service
public class PostService {

	@Autowired
	UserService userService;

	@Autowired
	TimeManager timeManager;

	@Autowired
	PostMongoRepository postMongoRepository;

	@Autowired
	ActivityService activityService;

	@Autowired
	PostMongoTemplateImpl postMongoTemplateImpl;

	public Activity createPost(ApplicationUser applicationUser, Post post) {

		UserShort userShort = userService.getUserShort(applicationUser.getId());

		post.setCreator(userShort);
		post.setCreatedOn(Date.from(timeManager.getCurrentTimestamp().toInstant()));
		post = postMongoRepository.save(post);

		return activityService.createActivityOnPostCreation(post);
	}

	public void deletePost(String id) {

		Post post = postMongoRepository.findById(id).get();
		postMongoRepository.delete(post);

		activityService.deletePostActivities(post);
	}

	public Post editPost(Post post) {

//		activityService.updatePostActivities(post);
		return postMongoTemplateImpl.updatePost(post);
	}

	public Post getPost(String postId) {
		return postMongoRepository.findById(postId).get();
	}
}
