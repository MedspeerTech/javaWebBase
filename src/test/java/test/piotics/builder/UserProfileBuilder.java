package test.piotics.builder;

import com.piotics.constants.Gender;
import com.piotics.model.UserProfile;

public class UserProfileBuilder implements Builder<UserProfile>{

	private String id = "5d38540043cb13581a800525";
	private String username = "user";
	private String fileId = "4d38541043cb13581a883526";
	private String email = "user@test.com";
	private String phone = "9249304135";
	private Gender gender = Gender.MALE;
	private String about = "test data";
	private String designation= "developer";
	private String location = "chennai";
	private int newNotifications = 0;
	
	public UserProfile build() {
		
		UserProfile userProfile = new UserProfile(id, email, phone);
		userProfile.setUsername(username);
		userProfile.setFileId(fileId);
		userProfile.setGender(gender);
		userProfile.setAbout(about);
		userProfile.setDesignation(designation);
		userProfile.setLocation(location);
		userProfile.setNewNotifications(newNotifications);
		
		return userProfile;
	}
	
	public static UserProfileBuilder aUserProfile() {
		
		return new UserProfileBuilder();
	}
	
	public UserProfileBuilder withId(String id) {
		
		this.id =id;
		return this;
	}
	
	public UserProfileBuilder withUsername(String username) {
		this.username = username;
		return this;
	}
	
	public UserProfileBuilder withFileId(String fileId) {
		this.fileId = fileId;
		return this;
	}
	
	public UserProfileBuilder withEmail(String email) {
		this.email = email;
		return this;
	}
	
	public UserProfileBuilder withPhone(String phone) {
		this.phone = phone;
		return this;
	}
	
	public UserProfileBuilder withGender(Gender gender) {
		this.gender = gender;
		return this;
	}
	
	public UserProfileBuilder withAbout(String about) {
		this.about = about;
		return this;
	}
	
	public UserProfileBuilder withDesignation(String designation) {
		this.designation = designation;
		return this;
	}
	
	public UserProfileBuilder withLocation(String location) {
		this.location = location;
		return this;
	}
	
	public UserProfileBuilder withNewNotifications(int newNotifications) {
		this.newNotifications = newNotifications;
		return this;
	}
	
	public UserProfileBuilder but() {
		
		return new UserProfileBuilder()
				.withId(id)
				.withUsername(username)
				.withFileId(fileId)
				.withEmail(email)
				.withPhone(phone)
				.withGender(gender)
				.withAbout(about)
				.withDesignation(designation)
				.withLocation(location)
				.withNewNotifications(newNotifications);
	}
	
}
