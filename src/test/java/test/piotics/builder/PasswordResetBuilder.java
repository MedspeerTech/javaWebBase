package test.piotics.builder;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.piotics.model.PasswordReset;

public class PasswordResetBuilder implements Builder<PasswordReset> {

	private String username = "user@test.com";
	private String password = "pass";
	private String token = "jhp5nhmes355hs28pt850vgok0";

	public PasswordReset build() {

		PasswordReset passwordReset = new PasswordReset(username, password, token);
		return passwordReset;
	}

	public static PasswordResetBuilder aPasswordResetBuilder() {

		return new PasswordResetBuilder();
	}

	public PasswordResetBuilder withUserName(String username) {
		this.username = username;
		return this;
	}

	public PasswordResetBuilder withPassword(String password) {
		this.password = password;
		return this;
	}

	public PasswordResetBuilder withToken(String token) {
		this.token = token;
		return this;
	}

	public PasswordResetBuilder but() {

		return new PasswordResetBuilder()
				.withUserName(username)
				.withPassword(password)
				.withToken(token);
	}

}
