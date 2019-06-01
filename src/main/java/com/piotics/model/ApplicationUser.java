package com.piotics.model;

import java.util.Collection;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.piotics.constants.UserRoles;

@Document(value = "user_security")
public class ApplicationUser implements UserDetails {

	@Id
	private String id;
//	@NotNull(message="username.notnull")
//	@Column(unique=true)
	private String username;
	private String password;
	private String email;
	private Company company;
	private UserRoles role;
	private boolean enabled = false;
	private int attempts;
	private boolean accountNonExpired = true;
	private boolean accountNonLocked = true;
	private boolean credentialsNonExpired = true;

	public ApplicationUser() {
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

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public UserRoles getRole() {
		return role;
	}

	public void setRole(UserRoles role) {
		this.role = role;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int getAttempts() {
		return attempts;
	}

	public void setAttempts(int attempts) {
		this.attempts = attempts;
	}

	public void setAccountNonExpired(boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}

	public void setAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	public void setCredentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}

	public String getUsername() {
		return username;
	}

//	@Override
//	public Collection<? extends GrantedAuthority> getAuthorities() {
//
//		Collection<Authority> authorities = new ArrayList<>();
//		Iterator<Role> itr = this.roles.iterator();
//		while (itr.hasNext()) {
//			Role role = itr.next();
//			authorities.addAll(role.getAuthorities());
//		}
//
//		return authorities;
//	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return null;
	}

//	public void setNewUser() {
//		this.accountNonExpired = true;
//		this.credentialsNonExpired = true;
//		this.accountNonLocked = true;
//		this.roles = new ArrayList<>();
//		Role role = new Role(UserRoles.ROLE_USER);
//		roles.add(role);
//		this.username = this.email;
//	}

}
