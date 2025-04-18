package com.example.part35teammonew.notification;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.example.part35teammonew.domain.notification.Enum.NotificationType;
import com.example.part35teammonew.domain.notification.entity.Notice;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NoticeTest {

  @Test
  @DisplayName("댓글 알림 생성 확인")
  void createCommentNoticeTest() {
    String content = "그르륷";
    UUID resourceId = UUID.randomUUID();

    Notice notice = Notice.createCommentNotice(content, resourceId);

    assertThat(notice.getType()).isEqualTo(NotificationType.COMMENT);
    assertThat(notice.getContent()).isEqualTo(content);
    assertThat(notice.getResourceId()).isEqualTo(resourceId);
    assertThat(notice.isConfirmed()).isFalse();
    assertThat(notice.getUpdateAt()).isEqualTo(Instant.EPOCH);
    assertThat(notice.getCreatedAt()).isBeforeOrEqualTo(Instant.now());
  }

  @Test
  @DisplayName("뉴스 알림 생성 확인")
  void createNewsNoticeTest() {
    String content = "크르릉컹";
    UUID resourceId = UUID.randomUUID();

    Notice notice = Notice.createNewsNotice(content, resourceId);

    assertThat(notice.getType()).isEqualTo(NotificationType.NEWS);
    assertThat(notice.getContent()).isEqualTo(content);
    assertThat(notice.getResourceId()).isEqualTo(resourceId);
    assertThat(notice.isConfirmed()).isFalse();
    assertThat(notice.getUpdateAt()).isEqualTo(Instant.EPOCH);
    assertThat(notice.getCreatedAt()).isBeforeOrEqualTo(Instant.now());
  }
}