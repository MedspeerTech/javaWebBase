package com.piotics.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user")
public class UserShort {

	private String id;
	private String username;
	private String fileId;

	public UserShort() {}

	public UserShort(String id, String username, String fileId) {
		super();
		this.id = id;
		this.username = username;
		this.fileId = fileId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

}
