package com.piotics.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class SignUpUser {

	@NotNull(message = "username should not be null")
	@NotEmpty(message = "username should not be empty")
	private String username;
//	@NotNull(message = "password should not")
//	@NotEmpty
	private String password;
	private Token token;

	
	public SignUpUser(String email, String password) {

		this.username = email;
		this.password = password;
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

	public Token getToken() {
		return token;
	}

	public void setToken(Token token) {
		this.token = token;
	}

}
