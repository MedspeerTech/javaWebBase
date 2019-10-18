package com.piotics.config.security.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

import com.piotics.constants.UserRoles;
import com.piotics.controller.BaseController;
import com.piotics.model.ApplicationUser;
import com.piotics.model.Post;
import com.piotics.repository.PostMongoRepository;
import com.piotics.service.PostService;

@Component("AccessFilter")
@PropertySource("classpath:setup.properties")
public class AccessFilter {

	@Value("${tenant.enabled}")
	boolean tenatEnabled;

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

	public boolean hasTenantCreationAccess(Authentication authentication) throws Exception {

		boolean bool = false;
		ApplicationUser applicationUser = (ApplicationUser) (authentication).getPrincipal();
		if (applicationUser.getRole().equals(UserRoles.ROLE_POWER_ADMIN))
			if (tenatEnabled)
				bool = true;
		return bool;
	}

}
