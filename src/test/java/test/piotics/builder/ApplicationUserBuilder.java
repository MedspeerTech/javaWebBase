package test.piotics.builder;

import org.springframework.data.mongodb.core.mapping.DBRef;

import com.piotics.constants.UserRoles;
import com.piotics.model.ApplicationUser;
import com.piotics.model.Tenant;

public class ApplicationUserBuilder implements Builder<ApplicationUser> {

	private String id = "5d38540043cb13581a800525";
	private String username = "dijo";
	private String password = "pass";
	private String email = "dijofrancis01@gmail.com";
	private String phone = "8525016985";
	private String countryCode = "+91";
	private Tenant company = new Tenant();
	private UserRoles role = UserRoles.ROLE_USER;
	private boolean enabled = true;
	private int attempts = 0;
	private boolean accountNonExpired = true;
	private boolean accountNonLocked = true;
	private boolean credentialsNonExpired = true;

	public static ApplicationUserBuilder anApplicationUser() {

		return new ApplicationUserBuilder();
	}

	public ApplicationUser build() {

		ApplicationUser applicationUser = new ApplicationUser(email, password, role, enabled);
		applicationUser.setId(id);
		applicationUser.setUsername(username);
		applicationUser.setEmail(email);
		applicationUser.setPhone(phone);
		applicationUser.setCountryCode(countryCode);
		applicationUser.setCompany(company);
		applicationUser.setRole(role);
		applicationUser.setAttempts(attempts);
		applicationUser.setAccountNonExpired(accountNonExpired);
		applicationUser.setCredentialsNonExpired(credentialsNonExpired);
		applicationUser.setAccountNonLocked(accountNonLocked);

		return applicationUser;
	}

	public ApplicationUserBuilder withCompany(Tenant company) {

		this.company = company;
		return this;
	}

	public ApplicationUserBuilder withUsername(String username) {

		this.username = username;
		return this;
	}

	public ApplicationUserBuilder withPassword(String password) {
		this.password = password;
		return this;
	}

	public ApplicationUserBuilder withRole(UserRoles role) {
		this.role = role;
		return this;
	}

	public ApplicationUserBuilder withEnabled(boolean enabled) {

		this.enabled = enabled;
		return this;
	}

	public ApplicationUserBuilder withEmail(String email) {
		this.email = email;
		return this;
	}

	public ApplicationUserBuilder withId(String id) {
		this.id =id;
		return this;
	}

	public ApplicationUserBuilder withPhone(String phone) {
		this.phone = phone;
		return this;
	}

}
