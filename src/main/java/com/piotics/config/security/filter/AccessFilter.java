package com.piotics.config.security.filter;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.piotics.model.ApplicationUser;
import com.piotics.model.UserProfile;

@Component("AccessFilter")
public class AccessFilter {

	
	public boolean hasAccess(Authentication authentication, String userId) {

		ApplicationUser applicationUser = (ApplicationUser) (authentication).getPrincipal();
		return (applicationUser.getId().equals(userId));
	}

}
