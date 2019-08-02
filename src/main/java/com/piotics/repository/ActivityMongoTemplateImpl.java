package com.piotics.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.model.MappingInstantiationException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.piotics.model.Activity;
import com.piotics.model.Post;
import com.piotics.service.ActivityService;
import com.piotics.service.PostService;

@Repository
public class ActivityMongoTemplateImpl implements ActivityMongoTemplate {

	@Autowired
	MongoTemplate mongoTemplate;

	@Autowired
	PostService postService;
	
	@Autowired
	ActivityService activityService;

	@Override
	public void updatePostActivities(Post post) {

		Post dbPost = postService.getPost(post.getId());

		Activity activity = new Activity();
		activity.setActivityMarker(dbPost);
		Query query = new Query(Criteria.where("activityMarker").is(activity.getActivityMarker()));

		Update update = new Update().set("activityMarker.message", post.getMessage())
				.set("activityMarker.fileMeta", post.getFileMeta())
				.set("activityMarker.fileType", post.getFileMeta().getType());

		try {
			
			mongoTemplate.findAndModify(query, update, Activity.class);

		} catch (MappingInstantiationException e) {

			mongoTemplate.findAndModify(query, update, Activity.class);
		}
	}

}
