package com.example.part35teammonew.domain.notification;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.part35teammonew.domain.notification.Enum.NotificationType;
import com.example.part35teammonew.domain.notification.entity.Notification;
import com.example.part35teammonew.domain.notification.repository.NotificationRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import com.example.part35teammonew.domain.notification.repository.NotificationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class NotificationRepositoryTest {

  @Autowired
  private NotificationRepository noticeRepository;

  @Test
  @DisplayName("댓글 생성 및 저장 테스트 ")
  void saveCommentNotice_success() {
    UUID userId = UUID.randomUUID();
    UUID resourceId = UUID.randomUUID();
    Instant time=Instant.now();
    Notification notification = Notification.createCommentNotice(userId, "댓글", resourceId,time);

    Notification saved = noticeRepository.save(notification);

    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getContent()).isEqualTo("댓글");
    assertThat(saved.getType()).isEqualTo(NotificationType.COMMENT);
    assertThat(saved.getResourceId()).isEqualTo(resourceId);
    assertThat(saved.isConfirmed()).isFalse();
  }

  @Test
  @DisplayName("알림 조회 테스트")
  void findNoticeById_success() {
    UUID userId = UUID.randomUUID();
    UUID resourceId = UUID.randomUUID();
    Instant time=Instant.now();
    Notification saved = noticeRepository.save(
        Notification.createNewsNotice(userId, "뉴스", resourceId,time));

    Optional<Notification> found = noticeRepository.findById(saved.getId());

    assertThat(found).isPresent();
    assertThat(found.get().getContent()).isEqualTo("뉴스");
    assertThat(found.get().getType()).isEqualTo(NotificationType.NEWS);
  }

  @Test
  @DisplayName("userId로 알림 조회")
  void findAllByUserIdAndConfirmedIsFalse_success() {
    UUID userId = UUID.randomUUID();
    Instant time=Instant.now();
    Notification n1 = Notification.createNewsNotice(userId, "뉴스1", UUID.randomUUID(),time);
    Notification n2 = Notification.createCommentNotice(userId, "댓글2", UUID.randomUUID(),time);
    n1.confirmedRead();
    noticeRepository.save(n1);
    noticeRepository.save(n2);

    var result = noticeRepository.findAllByUserIdAndConfirmedIsFalse(userId);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getContent()).isEqualTo("댓글2");
    assertThat(result.get(0).isConfirmed()).isFalse();
  }

}
