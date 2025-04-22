package com.example.part35teammonew.domain.notification.repository;

import com.example.part35teammonew.domain.notification.entity.Notification;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationRepository extends MongoRepository<Notification, ObjectId> {

  List<Notification> findAllByUserIdAndConfirmedIsFalse(UUID userId);

  void deleteAllByConfirmedIsTrueAndCreatedAtBefore(Instant threshold);

  Page<Notification> findAllByUserIdAndConfirmedIsFalse(UUID userId, Pageable pageable);

}
