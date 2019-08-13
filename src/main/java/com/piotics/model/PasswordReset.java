package com.piotics.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class PasswordReset{
	
	@NotEmpty
	@NotNull
	private String username;
	@NotEmpty
	@NotNull
	private String password;
    private String token;

    public PasswordReset() {}
    
	public PasswordReset(String username, String password, String token) {
		this.username = username;
		this.password = password;
		this.token = token;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}

}
