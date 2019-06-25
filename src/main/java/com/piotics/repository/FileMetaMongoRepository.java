package com.piotics.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.piotics.model.FileMeta;

public interface FileMetaMongoRepository extends MongoRepository<FileMeta, String> {

	FileMeta findByPath(String path);

	Optional<FileMeta> findById(Integer id);

}
