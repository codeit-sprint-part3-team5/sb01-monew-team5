package com.example.part35teammonew.domain.notification.repository;

import com.example.part35teammonew.domain.notification.entity.Notification;
import java.time.Instant;
import java.util.UUID;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
public class NoticeRepositoryImpl implements NoticeRepository {

  private final MongoTemplate mongoTemplate;

  public NoticeRepositoryImpl(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  @Override
  public void markNoticeAsRead(UUID userId, ObjectId noticeId) {
    Query query = new Query(Criteria.where("_id").is(userId)
        .and("noticeList._id").is(noticeId));

    Update update = new Update()
        .set("noticeList.$.confirmed", true)
        .set("noticeList.$.updateAt", Instant.now());

    mongoTemplate.updateFirst(query, update, Notification.class);
  }
}
