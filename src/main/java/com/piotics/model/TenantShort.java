package com.piotics.model;

import com.piotics.constants.UserRoles;

public class TenantShort {

	private String id;
	private String name;
	private UserRoles role;

	public TenantShort(ApplicationUser applicationUser) {
		this.id = applicationUser.getCompany().getId();
		this.name = applicationUser.getCompany().getName();
		this.role = applicationUser.getRole();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UserRoles getRole() {
		return role;
	}

	public void setRole(UserRoles role) {
		this.role = role;
	}

}
