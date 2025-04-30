package com.example.part35teammonew.domain.notification.repository;

import com.example.part35teammonew.domain.notification.entity.Notification;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepositoryCustom {

  private final MongoTemplate mongoTemplate;

  @Override
  public List<Notification> findAllUnreadByUserIdWithCursor(UUID userId, Instant after, Instant before, int limit) {
    Criteria criteria = new Criteria();
    criteria.and("userId").is(userId);
    criteria.and("confirmed").is(false);

    if (after != null && before != null) {
      criteria.and("createdAt").gt(after).lt(before);
    } else if (after != null) {
      criteria.and("createdAt").gt(after);
    } else if (before != null) {
      criteria.and("createdAt").lt(before);
    }

    Query query = new Query(criteria);
    query.limit(limit+1);
    query.with(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));

    return mongoTemplate.find(query, Notification.class);
  }
}
