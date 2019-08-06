package com.piotics.config.security.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.piotics.constants.UserRoles;
import com.piotics.model.ApplicationUser;
import com.piotics.model.Post;
import com.piotics.repository.PostMongoRepository;
import com.piotics.service.PostService;

@Component("AccessFilter")
public class AccessFilter {

	@Autowired
	PostService postService;

	public boolean hasAccess(Authentication authentication, String userId) {

		ApplicationUser applicationUser = (ApplicationUser) (authentication).getPrincipal();
		return (applicationUser.getId().equals(userId));
	}

	public boolean hasAccessToDeletePost(Authentication authentication, String postId) {

		ApplicationUser applicationUser = (ApplicationUser) (authentication).getPrincipal();
		Post post = postService.getPost(postId);

		if (applicationUser.getId() != post.getCreator().getId()) {

			if (applicationUser.getRole().equals(UserRoles.ROLE_ADMIN)) {
				return true;
			} else {
				return false;
			}
		} else {

			return true;
		}

	}

	public boolean hasAccessToEditPost(Authentication authentication, String postId) {

		ApplicationUser applicationUser = (ApplicationUser) (authentication).getPrincipal();
		Post post = postService.getPost(postId);

		if (!applicationUser.getId().equals(post.getCreator().getId()))
			return false;

		else
			return true;
	}

	public boolean isAdmin(Authentication authentication) {

		ApplicationUser applicationUser = (ApplicationUser) (authentication).getPrincipal();

		if (applicationUser.getRole().equals(UserRoles.ROLE_ADMIN)) {
			return true;
		} else {
			return false;
		}

	}

}
