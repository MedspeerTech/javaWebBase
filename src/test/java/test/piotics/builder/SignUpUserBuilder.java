package test.piotics.builder;

import com.piotics.model.SignUpUser;
import com.piotics.model.Token;

public class SignUpUserBuilder implements Builder<SignUpUser> {

	private String username = "dijofrancis01@gmail.com";
	private String password = "pass";
	private Token token;
	
	public SignUpUser build() {
		SignUpUser signUpUser = new SignUpUser(username, password);
		signUpUser.setToken(token);
		return signUpUser;
	}

	public static SignUpUserBuilder aSignUpUser() {

		return new SignUpUserBuilder();
	}

	public SignUpUserBuilder withUserName(String username) {

		this.username = username;
		return this;
	}

	public SignUpUserBuilder withPassword(String password) {

		this.password = password;
		return this;
	}

	public SignUpUserBuilder withToken(Token token) {
		this.token = token;
		return this;

	}

	public SignUpUserBuilder but() {

		return new SignUpUserBuilder().withUserName(username).withPassword(password).withToken(token);
	}
}
