package com.piotics.model;

import org.springframework.data.mongodb.core.mapping.Document;

import com.piotics.constants.UserRoles;

@Document
public class TenantRelation {

	private String id;
	private String tenantName;
	private UserRoles userRole;

	public TenantRelation(String tenantName, UserRoles userRole) {
		
		this.tenantName = tenantName;
		this.userRole = userRole;
	}
	
	public TenantRelation() {}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTenantName() {
		return tenantName;
	}

	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}

	public UserRoles getUserRole() {
		return userRole;
	}

	public void setUserRole(UserRoles userRole) {
		this.userRole = userRole;
	}

}
