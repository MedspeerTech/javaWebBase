package com.piotics.service;

import java.util.Date;
import java.util.Optional;

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

		Optional<Post> postOptional = postMongoRepository.findById(id);
		Post post = new Post();
		if (postOptional.isPresent()) {
			post = postOptional.get();
		}
		postMongoRepository.delete(post);

		activityService.deletePostActivities(post);
	}

	public Post editPost(Post post) {

		return postMongoTemplateImpl.updatePost(post);
	}

	public Post getPost(String postId) {

		Optional<Post> postOptional = postMongoRepository.findById(postId);
		Post post = new Post();
		if (postOptional.isPresent()) {
			post = postOptional.get();
		}
		return post;
	}
}
