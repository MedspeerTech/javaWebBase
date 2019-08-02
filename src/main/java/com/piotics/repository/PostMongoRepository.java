package com.piotics.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.piotics.model.Post;

public interface PostMongoRepository extends MongoRepository<Post, String>{

}
