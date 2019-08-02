package com.piotics.controller;

import java.security.Principal;

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

import com.piotics.model.Activity;
import com.piotics.model.ApplicationUser;
import com.piotics.model.Post;
import com.piotics.service.PostService;

@RestController
@RequestMapping(value = "/post")
public class PostController {

	@Autowired
	PostService postService;

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ResponseEntity<Activity> createPost(Principal principal, @RequestBody Post post) {

		ApplicationUser applicationUser = (ApplicationUser) ((Authentication) (principal)).getPrincipal();
		Activity activity = postService.createPost(applicationUser, post);

		return new ResponseEntity<Activity>(activity, HttpStatus.OK);
	}

	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
	@PreAuthorize("@AccessFilter.hasAccessToDeletePost(authentication,#id)")
	public ResponseEntity deletePost(@PathVariable String id) {

		postService.deletePost(id);
		return new ResponseEntity(HttpStatus.OK);
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@PreAuthorize("@AccessFilter.hasAccessToEditPost(authentication,#post.id)")
	public ResponseEntity<Post> editPost(@RequestBody Post post) {

		post = postService.editPost(post);
		return new ResponseEntity<Post>(post, HttpStatus.OK);
	}
}
