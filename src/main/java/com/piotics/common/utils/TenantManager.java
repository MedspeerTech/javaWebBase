package com.piotics.common.utils;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.piotics.model.Session;
import com.piotics.service.TenantService;

@Component
public class TenantManager {

	@Autowired
	TenantService tenantService;

	public String getTenantId() {
		if (tenantService.isTenantEnabled()) {
			Principal principal = SecurityContextHolder.getContext().getAuthentication();
			if (principal != null) {
				Session session = (Session) ((Authentication) (principal)).getPrincipal();
				if (session.getTenantId().equals(""))
					return null;
				else
					return session.getTenantId() + "_";
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
}
