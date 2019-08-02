package com.piotics.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.piotics.model.Activity;
import com.piotics.model.ActivityMarker;
import com.piotics.model.Post;

public interface ActivityMongoRepository extends MongoRepository<Activity, String> {

	void deleteAllByActivityMarker(Post post);

	Activity findOneByActivityMarker(ActivityMarker activityMarker);
}
