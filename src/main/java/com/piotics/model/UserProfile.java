package com.piotics.model;

import javax.validation.constraints.NotEmpty;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.google.firebase.database.annotations.NotNull;
import com.piotics.constants.Gender;

@Document(collection = "user")
public class UserProfile {

	@Id
	@NotNull(value = "id should not be null")
	@NotEmpty(message = "id should not be empty")
	private String id;
	@NotNull(value = "username Should not be null")
	@NotEmpty(message = "username should not be empty")
	private String username;
	private String fileId;
	private String email;
	private String phone;
	private Gender gender;
	private String about;
	private String designation;
	private String location;
	private int newNotifications;

	public UserProfile() {

	}

	public UserProfile(String email, String id) {
		this.id = id;
		this.email = email;
	}

	public UserProfile(String id, String email, String phone) {

		this.id = id;
		this.email = email;
		this.phone = phone;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getNewNotifications() {
		return newNotifications;
	}

	public void setNewNotifications(int newNotifications) {
		this.newNotifications = newNotifications;
	}

}
