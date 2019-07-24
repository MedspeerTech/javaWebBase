package com.piotics.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.piotics.model.Notification;

public interface NotificationMongoRepository extends MongoRepository<Notification, String> {

	List<Notification> findTop10ByUserToNotifyIdOrderByCreatedOnDesc(String id, Pageable pageable);
	
	List<Notification> findTop10ByUserToNotifyIdAndReadFalseOrderByCreatedOnDesc(String id, Pageable pageable);

	Notification findByUserToNotifyIdAndId(String userToNotifyId, String id);

}
 