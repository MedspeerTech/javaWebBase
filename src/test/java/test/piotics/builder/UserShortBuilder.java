package test.piotics.builder;

import com.piotics.model.UserShort;

public class UserShortBuilder implements Builder<UserShort>{

	private String id = "abcdefges355hs28pt8501256j";
	private String username = "user";
	private String fileId = "bhg658nges355hs28pt85564opi";
	
	public UserShort build() {
		return new UserShort(id, username, fileId);
	}
	
	public static UserShortBuilder aUserShort() {
		return new UserShortBuilder();
	}
	
	public UserShortBuilder withId(String id) {
		this.id = id;
		return this;
	}
	
	public UserShortBuilder withUserName(String username) {
		this.username = username;
		return this;
	}
	
	public UserShortBuilder withFileId(String fileId) {
		this.fileId = fileId;
		return this;
	}
	
	public UserShortBuilder but() {
		return new UserShortBuilder()
				.withId(id)
				.withUserName(username)
				.withFileId(fileId);
	}

}
