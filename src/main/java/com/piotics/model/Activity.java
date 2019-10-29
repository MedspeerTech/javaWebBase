package com.piotics.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.piotics.common.utils.CustomDeserializer;
import com.piotics.constants.Targets;

@Document(value = "#{@tenantManager.getTenantId()}"+"activity")
//@JsonDeserialize(using = ActivityDeserializer.class)
@JsonDeserialize(using = CustomDeserializer.class)

public class Activity implements Serializable{

	@Id
	@Field(value="id")
	private String id;
	private String ownerId; // creator id from post/article etc..
	@DBRef
	private UserShort actor;
	private Date publishedOn;
	private List<Comment> comments;
	private Targets target;
	@DBRef
//	@JsonDeserialize(using = CustomDeserializer.class)
	private ActivityMarker activityMarker;

	// used on post creation
	
	public Activity(Post post, Targets target) {

		this.ownerId = post.getCreator().getId();
		this.actor = post.getCreator();
		this.publishedOn = post.getCreatedOn();
		this.target = target;
		this.activityMarker = post;
	}

	public Activity() {
	}

	public Activity(String id, UserShort actor2, ActivityMarker activityMarker2) {
		this.id = id;
		this.actor = actor2;
		this.activityMarker = activityMarker2;

	}


//	public Activity(String id, String ownerId, String target, UserShort actor, Date publishedOn, List<Comment> comments,
//			ActivityMarker activityMarker) {
//		
//		this.id = id;
//		this.target = Targets.POST;
//		this.actor = actor;
//		this.publishedOn = publishedOn;
//		this.comments = comments;
//		this.activityMarker = activityMarker;
//		this.ownerId = ownerId;
//	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public UserShort getActor() {
		return actor;
	}

	public void setActor(UserShort actor) {
		this.actor = actor;
	}

	public Date getPublishedOn() {
		return publishedOn;
	}

	public void setPublishedOn(Date publishedOn) {
		this.publishedOn = publishedOn;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public Targets getTarget() {
		return target;
	}

	public void setTarget(Targets target) {
		this.target = target;
	}

	public ActivityMarker getActivityMarker() {
		return activityMarker;
	}

	public void setActivityMarker(ActivityMarker activityMarker) {
		this.activityMarker = activityMarker;
	}

}
