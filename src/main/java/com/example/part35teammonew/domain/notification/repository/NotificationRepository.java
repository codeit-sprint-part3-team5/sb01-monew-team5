package com.example.part35teammonew.domain.notification.repository;

import com.example.part35teammonew.domain.notification.entity.Notification;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationRepository extends MongoRepository<Notification, ObjectId> {

}
