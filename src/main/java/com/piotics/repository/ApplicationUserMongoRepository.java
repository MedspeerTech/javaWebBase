//package com.piotics.repository;
//
//import com.piotics.model.ApplicationUser;
//
//import java.util.Optional;
//
////import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.mongodb.repository.MongoRepository;
//
//public interface ApplicationUserMongoRepository extends MongoRepository<ApplicationUser, String> {
//	ApplicationUser findByUsername(String username);
//
//	Optional<ApplicationUser> findById(String id);
//	
//	ApplicationUser findByEmail(String email);
//	ApplicationUser findByPhone(String phone);
//
//	ApplicationUser findByUsernameAndPassword(String Username, String password);
//
//}
