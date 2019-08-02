package com.piotics.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.piotics.constants.FileType;

@Document(value = "post")
public class Post extends Social implements ActivityMarker {

	@Id
	@Field(value="id")
	private String id;
	@DBRef
	private UserShort creator;
	private Date createdOn;
	private String message;
	@DBRef
	private FileMeta fileMeta;
	private FileType fileType;
	
	
	

	public Post(String id, UserShort creator, Date createdOn, String message, FileMeta fileMeta, FileType fileType) {
		super();
		this.id = id;
		this.creator = creator;
		this.createdOn = createdOn;
		this.message = message;
		this.fileMeta = fileMeta;
		this.fileType = fileType;
	}

	public Post() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public UserShort getCreator() {
		return creator;
	}

	public void setCreator(UserShort creator) {
		this.creator = creator;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public FileMeta getFileMeta() {
		return fileMeta;
	}

	public void setFileMeta(FileMeta fileMeta) {
		this.fileMeta = fileMeta;
	}

	public FileType getFileType() {
		return fileType;
	}

	public void setFileType(FileType fileType) {
		this.fileType = fileType;
	}

}
