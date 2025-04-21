package com.example.part35teammonew.notification;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.example.part35teammonew.domain.notification.entity.Notice;
import com.example.part35teammonew.domain.notification.entity.Notification;
import com.example.part35teammonew.domain.notification.repository.NoticeRepositoryImpl;
import java.time.Instant;
import java.util.UUID;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@DataMongoTest
class NoticeRepositoryImplTest {

  @Autowired
  private MongoTemplate mongoTemplate;

  private void setPrivateId(Notice notice, ObjectId id) {
    try {
      var field = Notice.class.getDeclaredField("id");
      field.setAccessible(true);
      field.set(notice, id);
    } catch (Exception e) {
      throw new RuntimeException("실패시", e);
    }
  }

  private Notification findNotificationByUserId(UUID userId) {
    return mongoTemplate.findOne(
        Query.query(Criteria.where("userId").is(userId)),
        Notification.class
    );
  }


  @Test
  @DisplayName("ObjectId 잘못된거 업데이트")
  void markNoticeAsRead_wrongNoticeId() {
    UUID userId = UUID.randomUUID();

    Notice notice = Notice.createCommentNotice("댓글", UUID.randomUUID());
    ObjectId correctId = new ObjectId();
    ObjectId wrongId = new ObjectId();
    setPrivateId(notice, correctId);

    Notification notification = Notification.setUpNotification(userId);
    notification.addNotice(notice);
    mongoTemplate.save(notification);

    NoticeRepositoryImpl repository = new NoticeRepositoryImpl(mongoTemplate);
    repository.markNoticeAsRead(userId, wrongId);

    Notification updated = findNotificationByUserId(userId);
    assertThat(updated).isNotNull();

    Notice unchanged = updated.getNoticeList().get(0);
    assertThat(unchanged.isConfirmed()).isFalse();
    assertThat(unchanged.getUpdateAt()).isEqualTo(Instant.EPOCH);
  }

  @Test
  @DisplayName("userId잘못된거 업데이트")
  void markNoticeAsRead_wrongUserId() {
    UUID correctUserId = UUID.randomUUID();
    UUID wrongUserId = UUID.randomUUID();

    Notice notice = Notice.createNewsNotice("알림", UUID.randomUUID());
    ObjectId noticeId = new ObjectId();
    setPrivateId(notice, noticeId);

    Notification notification = Notification.setUpNotification(correctUserId);
    notification.addNotice(notice);
    mongoTemplate.save(notification);

    NoticeRepositoryImpl repository = new NoticeRepositoryImpl(mongoTemplate);
    repository.markNoticeAsRead(wrongUserId, noticeId);

    Notification updated = findNotificationByUserId(correctUserId);
    assertThat(updated).isNotNull();

    Notice unchanged = updated.getNoticeList().get(0);
    assertThat(unchanged.isConfirmed()).isFalse();
    assertThat(unchanged.getUpdateAt()).isEqualTo(Instant.EPOCH);
  }

  @Test
  @DisplayName("없는거 처리")
  void markNoticeAsRead_emptyNoticeList() {
    UUID userId = UUID.randomUUID();

    Notification emptyNotification = Notification.setUpNotification(userId);
    mongoTemplate.save(emptyNotification);

    NoticeRepositoryImpl repository = new NoticeRepositoryImpl(mongoTemplate);
    repository.markNoticeAsRead(userId, new ObjectId());

    Notification updated = findNotificationByUserId(userId);
    assertThat(updated).isNotNull();
    assertThat(updated.getNoticeList()).isEmpty();
  }
}