package test.piotics.builder;

import com.piotics.model.PasswordResetResource;

public class PasswordResetResourceBuilder implements Builder<PasswordResetResource> {

	private String password = "pass";
	private String newPassword = "passpass";

	public PasswordResetResource build() {
		
		PasswordResetResource passwordResetResource =new PasswordResetResource();
		passwordResetResource.setPassword(password);
		passwordResetResource.setNewPassword(newPassword);
		return passwordResetResource;
	}
	
	public static PasswordResetResourceBuilder aPasswordResetResource() {
		
		return new PasswordResetResourceBuilder();
	}

	public PasswordResetResourceBuilder withPassword(String password) {
		this.password = password;
		return this;
	}
	
	public PasswordResetResourceBuilder withNewPassword(String newPassword) {
		
		this.newPassword =newPassword ;
		return this;
	}
	public PasswordResetResourceBuilder but() {
		return new PasswordResetResourceBuilder()
				.withNewPassword(newPassword)
				.withPassword(password);
	}
}
