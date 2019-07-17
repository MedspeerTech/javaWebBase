package com.piotics.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class PasswordResetResource {

	@NotNull(message = "password should not be null")
	@NotEmpty(message = "password should not be empty")
	private String password;
	
	@NotNull(message = "newPassword should not be null")
	@NotEmpty(message = "newPassword should not be empty")
	private String newPassword;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newpassword) {
		this.newPassword = newpassword;
	}

}
