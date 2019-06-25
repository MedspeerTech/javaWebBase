package com.piotics.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.piotics.constants.FileType;
import com.piotics.model.Conversion;

public interface ConversionMongoRepository extends MongoRepository<Conversion, String>{

	List<Conversion> findByFileTypeAndLogNull(FileType fileType);

}
