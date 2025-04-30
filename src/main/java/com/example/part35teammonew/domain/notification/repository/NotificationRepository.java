package com.example.part35teammonew.domain.notification.repository;

import com.example.part35teammonew.domain.notification.entity.Notification;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationRepository extends MongoRepository<Notification, ObjectId>, NotificationRepositoryCustom  {


  Optional<Notification> findByUserId(UUID id);

  List<Notification> findAllByUserIdAndConfirmedIsFalse(UUID userId);

  void deleteAllByConfirmedIsTrueAndCreatedAtBefore(Instant threshold);


  List<Notification> findAllByUserIdAndConfirmedIsFalseOrderByCreatedAtDesc(UUID userId);

  List<Notification> findAllByUserIdAndConfirmedIsFalseAndCreatedAtBeforeOrderByCreatedAtDesc(UUID userId, Instant cursor);

  List<Notification> findAllByUserIdAndConfirmedIsFalseAndCreatedAtAfterOrderByCreatedAtDesc(UUID userId, Instant after);

  List<Notification> findAllByUserIdAndConfirmedIsFalseAndCreatedAtAfterAndCreatedAtBeforeOrderByCreatedAtDesc(
      UUID userId, Instant after, Instant cursor);

  long countByUserIdAndConfirmedIsFalse(UUID userId);
}
