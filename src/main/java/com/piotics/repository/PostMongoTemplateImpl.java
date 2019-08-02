package com.piotics.repository;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.piotics.model.Post;
import com.piotics.model.UserProfile;
import com.piotics.resources.NotificationDocument;

@Repository
public class PostMongoTemplateImpl implements PostMongoTemplate {

	@Autowired
	MongoTemplate mongoTemplate;

	@Override
	public Post updatePost(Post post) {

		Query query = new Query(Criteria.where("id").is(post.getId()));
		Update update = new Update()
				.set("message", post.getMessage()).set("fileMeta", post.getFileMeta())
				.set("fileType", post.getFileMeta().getType());
		post.setFileType(post.getFileMeta().getType());

		mongoTemplate.findAndModify(query, update, Post.class);
		return post;
	}

}
