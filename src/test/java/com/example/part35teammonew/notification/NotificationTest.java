package com.example.part35teammonew.notification;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.part35teammonew.domain.notification.Enum.NotificationType;
import com.example.part35teammonew.domain.notification.entity.Notice;
import com.example.part35teammonew.domain.notification.entity.Notification;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NotificationTest {

  @Test
  @DisplayName("댓글 알림 생성")
  void createCommentNotice() {
    UUID resourceId = UUID.randomUUID();
    String content = "우어어어.";

    Notice notice = Notice.createCommentNotice(content, resourceId);

    assertThat(notice.getType()).isEqualTo(NotificationType.COMMENT);
    assertThat(notice.getContent()).isEqualTo(content);
    assertThat(notice.getResourceId()).isEqualTo(resourceId);
    assertThat(notice.isConfirmed()).isFalse();
    assertThat(notice.getCreatedAt()).isBeforeOrEqualTo(Instant.now());
    assertThat(notice.getUpdateAt()).isEqualTo(Instant.EPOCH);
  }

  @Test
  @DisplayName("뉴스 알림 생성")
  void createNewsNotice() {
    UUID resourceId = UUID.randomUUID();
    String content = "크어어어어.";

    Notice notice = Notice.createNewsNotice(content, resourceId);

    assertThat(notice.getType()).isEqualTo(NotificationType.NEWS);
    assertThat(notice.getContent()).isEqualTo(content);
  }

  @Test
  @DisplayName("알림 추가 확인")
  void createNotificationAndAddNotice() {
    UUID userId = UUID.randomUUID();
    Notification notification = Notification.setUpNotification(userId);

    assertThat(notification.getUserId()).isEqualTo(userId);
    assertThat(notification.getNoticeList()).isEmpty();

    Notice notice = Notice.createCommentNotice("댓글 알림입니다", UUID.randomUUID());
    notification.addNotice(notice);

    assertThat(notification.getNoticeList()).hasSize(1);
    assertThat(notification.getNoticeList().get(0)).isEqualTo(notice);
  }

  @Test
  @DisplayName("처음에 비어야 함")
  void createNotification_initialState() {
    UUID userId = UUID.randomUUID();
    Notification notification = Notification.setUpNotification(userId);

    assertThat(notification.getUserId()).isEqualTo(userId);
    assertThat(notification.getNoticeList()).isEmpty();
  }

  @Test
  @DisplayName("여러개 추가")
  void addMultipleNotices_inOrder() {
    UUID userId = UUID.randomUUID();
    Notification notification = Notification.setUpNotification(userId);

    Notice notice1 = Notice.createCommentNotice("감", UUID.randomUUID());
    Notice notice2 = Notice.createNewsNotice("자", UUID.randomUUID());
    Notice notice3 = Notice.createCommentNotice("탕", UUID.randomUUID());

    notification.addNotice(notice1);
    notification.addNotice(notice2);
    notification.addNotice(notice3);

    assertThat(notification.getNoticeList()).hasSize(3);
    assertThat(notification.getNoticeList().get(0)).isEqualTo(notice1);
    assertThat(notification.getNoticeList().get(1)).isEqualTo(notice2);
    assertThat(notification.getNoticeList().get(2)).isEqualTo(notice3);
  }
}