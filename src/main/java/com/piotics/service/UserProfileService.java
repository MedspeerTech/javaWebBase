package com.piotics.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.piotics.model.UserProfile;
import com.piotics.repository.UserProfileMongoRepository;

@Service
public class UserProfileService {
	
	@Autowired
	UserProfileMongoRepository userProfileMongoRepository;

	public UserProfile save(UserProfile userProfile) {

		return userProfileMongoRepository.save(userProfile);	
	}
	
	public UserProfile getProfile(String id) {

		UserProfile userProfile = userProfileMongoRepository.findById(id).get();
		return userProfile;
	}
}
