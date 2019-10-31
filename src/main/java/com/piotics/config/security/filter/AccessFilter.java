package com.piotics.config.security.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.piotics.constants.UserRoles;
import com.piotics.model.Post;
import com.piotics.model.Session;
import com.piotics.service.PostService;

@Component("AccessFilter")
@PropertySource("classpath:setup.properties")
public class AccessFilter {

	@Value("${tenant.enabled}")
	boolean tenatEnabled;

	@Autowired
	PostService postService;

	public boolean hasAccess(Authentication authentication, String userId) {
		Session session = (Session) (authentication).getPrincipal();
		return (session.getId().equals(userId));
	}

	public boolean hasAccessToDeletePost(Authentication authentication, String postId) {
		Session session = (Session) (authentication).getPrincipal();
		Post post = postService.getPost(postId);
		return (session.getId().equals(post.getCreator().getId())
				|| session.getRole().equals(UserRoles.ROLE_ADMIN)
				|| session.getRole().equals(UserRoles.ROLE_POWER_ADMIN));
	}

	public boolean hasAccessToEditPost(Authentication authentication, String postId) {
		Session session = (Session) (authentication).getPrincipal();
		Post post = postService.getPost(postId);
		return (!session.getId().equals(post.getCreator().getId()));
	}

	public boolean isAdmin(Authentication authentication) {
		Session session = (Session) (authentication).getPrincipal();
		return (session.getRole().equals(UserRoles.ROLE_ADMIN)
				|| session.getRole().equals(UserRoles.ROLE_POWER_ADMIN));
	}
	
	public boolean isPowerAdmin(Authentication authentication) {
		Session session = (Session) (authentication).getPrincipal();
		return (session.getRole().equals(UserRoles.ROLE_POWER_ADMIN));
	}
	
	/**
	 * need some changes in application user - userRole
	 * @param authentication
	 * @return
	 */
	public boolean isCompanyAdmin(Authentication authentication) {
		Session session = (Session) (authentication).getPrincipal();
		return (session.getRole().equals(UserRoles.ROLE_ADMIN));
	}

	public boolean hasTenantCreationAccess(Authentication authentication) {
		Session session = (Session) (authentication).getPrincipal();
		return (session.getRole().equals(UserRoles.ROLE_POWER_ADMIN) && tenatEnabled);
	}

}
