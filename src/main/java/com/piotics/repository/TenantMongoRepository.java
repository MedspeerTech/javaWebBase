package com.piotics.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.piotics.model.Tenant;

public interface TenantMongoRepository extends MongoRepository<Tenant, String>{

}
