package com.piotics.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class SignUpUser {

	@NotNull(message = "username should not be null")
	@NotEmpty(message = "username should not be empty")
	private String userName;
//	@NotNull(message = "password should not")
//	@NotEmpty
	private String password;
	private Token token;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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
