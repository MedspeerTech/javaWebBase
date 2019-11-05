package com.piotics.model;

import java.util.Collection;

import org.springframework.beans.BeanUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.piotics.constants.UserRoles;
import com.piotics.resources.SocialUser;

public class Session implements UserDetails {

	private String id;

	private String tenantId;
	private UserRoles role;

	public Session() {
	}

	public Session(SocialUser socialUser, UserRoles role) {
		BeanUtils.copyProperties(socialUser, this);
		this.role = role;
	}

	public Session(String id,String tenantId, String roles) {
		this.id = id;
		this.tenantId = tenantId;
		this.role = UserRoles.valueOf(roles);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public UserRoles getRole() {
		return role;
	}

	public void setRole(UserRoles role) {
		this.role = role;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
	}

	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public String getUsername() {
		return null;
	}

	@Override
	public boolean isAccountNonExpired() {
		return false;
	}

	@Override
	public boolean isAccountNonLocked() {
		return false;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnabled() {
		return false;
	}

//		public void setNewUser() {
//			this.accountNonExpired = true;
//			this.credentialsNonExpired = true;
//			this.accountNonLocked = true;
//			this.roles = new ArrayList<>();
//			Role role = new Role(UserRoles.ROLE_USER);
//			roles.add(role);
//			this.username = this.email;
//		}

}
