package medspeer.tech.model;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import medspeer.tech.constants.UserRoles;

@Entity(name = "user_security")
public class ApplicationUser implements UserDetails {

	@Id
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid",
			strategy = "uuid")
	private String id;
	@NotNull(message="username.notnull")
	@Column(unique=true)
	private String username;
	private String password;
	private String email;

	@ManyToMany(fetch = FetchType.EAGER,
		cascade = {
				CascadeType.PERSIST,
				CascadeType.MERGE
		})
	@JoinTable(
			name = "users_roles",
			joinColumns = @JoinColumn(
					name = "user_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(
					name = "role_id", referencedColumnName = "id"))
	private Collection<Role> roles;
	private boolean enabled = false;
	private int attempts;
	private boolean accountNonExpired =true;
	private boolean accountNonLocked =true;
	private boolean credentialsNonExpired =true;

	public ApplicationUser() {
	}

//	public ApplicationUser(ApplicationSocialUser applicationSocialUser) {
//		BeanUtils.copyProperties(applicationSocialUser,this);
//		this.accountNonExpired=true;
//		this.credentialsNonExpired=true;
//		this.accountNonLocked=true;
//		this.roles=new ArrayList<>();
//		Role role=new Role(UserRoles.ROLE_USER);
//		roles.add(role);
//		this.username=applicationSocialUser.getEmail();
//	}
//
//	public ApplicationUser(SocialUser socialUser, Role role) {
//		BeanUtils.copyProperties(socialUser,this);
//		this.accountNonExpired=true;
//		this.credentialsNonExpired=true;
//		this.accountNonLocked=true;
//		this.roles=new ArrayList<>();
//
//		roles.add(role);
//		this.username=socialUser.getEmail();
//
//
//	}

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

	public Collection<Role> getRoles() {
		return roles;
	}

	public void setRoles(Collection<Role> roles) {
		this.roles = roles;
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


	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

		Collection<Authority> authorities=new ArrayList<>();
		Iterator<Role> itr = this.roles.iterator();
		while (itr.hasNext()){
			Role role = itr.next();
			authorities.addAll(role.getAuthorities());
		}

		return authorities;
	}

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

	public void setNewUser(){
        this.accountNonExpired=true;
        this.credentialsNonExpired=true;
        this.accountNonLocked=true;
        this.roles=new ArrayList<>();
        Role role=new Role(UserRoles.ROLE_USER);
        roles.add(role);
        this.username=this.email;
    }


}
