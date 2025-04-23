package com.example.part35teammonew.notification;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.example.part35teammonew.domain.notification.Enum.NotificationType;
import com.example.part35teammonew.domain.notification.entity.Notification;
import java.util.Optional;
import java.util.UUID;

import com.example.part35teammonew.domain.notification.repository.NotificationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

@DataMongoTest
public class NotificationRepositoryTest {

  @Autowired
  private NotificationRepository noticeRepository;

  @Test
  @DisplayName("댓글 알림 생성 및 저장 테스트")
  void saveCommentNotice_success() {
    // given
    UUID resourceId = UUID.randomUUID();
    Notification notification = Notification.createCommentNotice("댓글 알림입니다.", resourceId);

    // when
    Notification saved = noticeRepository.save(notification);

    // then
    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getContent()).isEqualTo("댓글 알림입니다.");
    assertThat(saved.getType()).isEqualTo(NotificationType.COMMENT);
    assertThat(saved.getResourceId()).isEqualTo(resourceId);
    assertThat(saved.isConfirmed()).isFalse();
  }

  @Test
  @DisplayName("알림 조회 테스트")
  void findNoticeById_success() {
    // given
    UUID resourceId = UUID.randomUUID();
    Notification saved = noticeRepository.save(
        Notification.createNewsNotice("뉴스 알림입니다.", resourceId));

    // when
    Optional<Notification> found = noticeRepository.findById(saved.getId());

    // then
    assertThat(found).isPresent();
    assertThat(found.get().getContent()).isEqualTo("뉴스 알림입니다.");
    assertThat(found.get().getType()).isEqualTo(NotificationType.NEWS);
  }

  @Test
  @DisplayName("알림 삭제 테스트")
  void deleteNotice_success() {
    // given
    Notification notification = Notification.createCommentNotice("삭제 테스트용 알림", UUID.randomUUID());
    Notification saved = noticeRepository.save(notification);

    // when
    noticeRepository.deleteById(saved.getId());

    // then
    Optional<Notification> deleted = noticeRepository.findById(saved.getId());
    assertThat(deleted).isNotPresent();
  }
}
