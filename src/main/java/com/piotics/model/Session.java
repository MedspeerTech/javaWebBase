package com.piotics.model;

import java.util.Collection;

import org.springframework.beans.BeanUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.piotics.constants.UserRoles;
import com.piotics.resources.SocialUser;

public class Session implements UserDetails {

	private String id;

	private String email;
	private UserRoles role;

	public Session() {
	}

	public Session(SocialUser socialUser, UserRoles role) {
		BeanUtils.copyProperties(socialUser, this);
		this.role = role;
	}

	public Session(String id,String email, String roles) {
		this.id = id;
		this.email = email;
		this.role = UserRoles.valueOf(roles);
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
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
