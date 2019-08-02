package com.piotics.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;



public class Comment {

	@Id
	private String id;
	@DBRef
	private Comment parent;
	@DBRef
	private Activity activity;
	private String userId;
	private String comment;
	private Date commentedOn;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Comment getParent() {
		return parent;
	}
	public void setParent(Comment parent) {
		this.parent = parent;
	}
	public Activity getActivity() {
		return activity;
	}
	public void setActivity(Activity activity) {
		this.activity = activity;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Date getCommentedOn() {
		return commentedOn;
	}
	public void setCommentedOn(Date commentedOn) {
		this.commentedOn = commentedOn;
	}
	
	
}
