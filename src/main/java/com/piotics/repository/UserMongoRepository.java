package com.piotics.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.piotics.constants.UserRoles;
import com.piotics.model.ApplicationUser;
import com.piotics.model.UserShort;

public interface UserMongoRepository extends MongoRepository<ApplicationUser, String> {

	ApplicationUser findByUsername(String username);

	ApplicationUser findByUsernameAndPassword(String Username, String password);

	ApplicationUser findByEmail(String email);
	ApplicationUser findByPhone(String phone);

	List<ApplicationUser> findByRole(UserRoles userRole);
	
}
