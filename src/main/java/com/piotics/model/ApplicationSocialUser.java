package com.piotics.model;

import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.piotics.resources.SocialUser;

@Document(collection = "applicationSocialUser")
public class ApplicationSocialUser {

	@Id
	private String id;

	private String provider;
	private String socialId;
	private String email;
	private String name;
	private String image;
	private String token;

	public ApplicationSocialUser() {
	}

	public ApplicationSocialUser(SocialUser socialUser) {
		BeanUtils.copyProperties(socialUser, this);
		this.id = null;
		this.socialId = socialUser.getId();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getSocialId() {
		return socialId;
	}

	public void setSocialId(String socialId) {
		this.socialId = socialId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
