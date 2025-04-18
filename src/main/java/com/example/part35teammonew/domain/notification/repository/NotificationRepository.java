package com.example.part35teammonew.domain.notification.repository;

import com.example.part35teammonew.domain.notification.entity.Notification;
import java.util.List;
import java.util.UUID;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationRepository extends MongoRepository<Notification, ObjectId>,
    NoticeRepository {

  List<Notification> findByUserId(UUID userId);

  List<Notification> findByConfirmedFalseAndUserId(UUID userId);


}
