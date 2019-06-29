package com.piotics.model;

import java.util.Date;

import org.springframework.data.annotation.Id;

import org.springframework.data.mongodb.core.mapping.Document;

import com.piotics.common.TokenType;

@Document(value = "token")
public class Token {

	@Id
	private String id;
	private String userId;
	private String username;
	private String token;
	private Date creationDate;
	private TokenType tokenType;

	public Token(String userId, String username, String token, TokenType tokenType, Date currentTimestamp) {
		this.userId = userId;
		this.username = username;
		this.token = token;
		this.tokenType = tokenType;
		this.creationDate = currentTimestamp;
	}

	public Token(String username, String token, TokenType tokenType) {
		this.username = username;
		this.token = token;
		this.tokenType = tokenType;
	}

	public Token() {
	}

	public Token(String username, String token, TokenType emailverification, Date date) {
		
		this.username = username;
		this.token = token;
		this.tokenType = tokenType;
		this.creationDate = date;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public TokenType getTokenType() {
		return tokenType;
	}

	public void setTokenType(TokenType tokenType) {
		this.tokenType = tokenType;
	}

}
