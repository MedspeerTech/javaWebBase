package com.piotics.repository;

import com.piotics.model.Activity;
import com.piotics.model.Post;

public interface ActivityMongoTemplate {

	void updatePostActivities(Post post);
}
