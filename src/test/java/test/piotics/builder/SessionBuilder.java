package test.piotics.builder;

import com.piotics.constants.UserRoles;
import com.piotics.model.Session;

public class SessionBuilder implements Builder<Session>{

	private String id;

	private String email;
	private UserRoles role;
	
	@Override
	public Session build() {
		return new Session(id,email,role.toString());
	}
	
	public SessionBuilder aSession() {
		return new SessionBuilder();
	}
	
	public SessionBuilder withId(String id) {
		this.id = id;
		return this;
	}
	
	public SessionBuilder withEmail(String email) {
		this.email = email;
		 return this;
	}
	
	public SessionBuilder withRole(UserRoles role) {
		this.role = role;
		return this;
	}

}
