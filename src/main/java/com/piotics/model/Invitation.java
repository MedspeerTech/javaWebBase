package com.piotics.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.piotics.constants.UserRoles;

@Document(collection = "invitation")
public class Invitation {

	@Id
	private String id;
	private String email;
	private String phone;
	@DBRef
	private UserShort invitedBY;
	private Token token;
	private String tenantId;
	private UserRoles userRole;

	public Invitation(String tenantId, UserRoles userRole, String email) {

		this.tenantId = tenantId;
		this.userRole = userRole;
		this.email = email;	
	}

	public Invitation() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public UserShort getInvitedBY() {
		return invitedBY;
	}

	public void setInvitedBY(UserShort invitedBY) {
		this.invitedBY = invitedBY;
	}

	public Token getToken() {
		return token;
	}

	public void setToken(Token token) {
		this.token = token;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public UserRoles getUserRole() {
		return userRole;
	}

	public void setUserRole(UserRoles userRole) {
		this.userRole = userRole;
	}

}
