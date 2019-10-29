package com.piotics.model;

import com.piotics.common.utils.UtilityManager;
import com.piotics.custom.validator.ObjectValidation;
import com.piotics.custom.validator.Validatable;

@ObjectValidation(message = "object not valid")
public class SignUpUser implements Validatable {

	private String username;
	private String password;
	private Token token;

	public SignUpUser(String email, String password) {

		this.username = email;
		this.password = password;
	}

	public SignUpUser() {
	}

	@Override
	public boolean isValid() {

		UtilityManager utilityManager = new UtilityManager();

		if (username == null)
			return false;
		return (utilityManager.isEmail(username) && password != null);
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
