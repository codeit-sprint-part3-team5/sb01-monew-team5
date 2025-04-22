package com.example.part35teammonew.notification;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.part35teammonew.domain.notification.Enum.NotificationType;
import com.example.part35teammonew.domain.notification.entity.Notification;
import com.example.part35teammonew.domain.notification.repository.NotificationRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

@SpringBootTest
public class NotificationRepositoryTest {

  @Autowired
  private NotificationRepository noticeRepository;


  @Test
  @DisplayName("댓글 생성 및 저장 테스트")
  void saveCommentNotice_success() {
    // given
    UUID userId = UUID.randomUUID();
    UUID resourceId = UUID.randomUUID();
    Notification notification = Notification.createCommentNotice(userId, "댓글", resourceId);

    // when
    Notification saved = noticeRepository.save(notification);

    // then
    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getContent()).isEqualTo("댓글");
    assertThat(saved.getType()).isEqualTo(NotificationType.COMMENT);
    assertThat(saved.getResourceId()).isEqualTo(resourceId);
    assertThat(saved.isConfirmed()).isFalse();
  }

  @Test
  @DisplayName("알림 조회 테스트")
  void findNoticeById_success() {
    // given
    UUID userId = UUID.randomUUID();
    UUID resourceId = UUID.randomUUID();
    Notification saved = noticeRepository.save(
        Notification.createNewsNotice(userId, "뉴스", resourceId));

    // when
    Optional<Notification> found = noticeRepository.findById(saved.getId());

    // then
    assertThat(found).isPresent();
    assertThat(found.get().getContent()).isEqualTo("뉴스");
    assertThat(found.get().getType()).isEqualTo(NotificationType.NEWS);
  }

  @Test
  @DisplayName("userId로 미확인 알림 조회 테스트")
  void findAllByUserIdAndConfirmedIsFalse_success() {
    // given
    UUID userId = UUID.randomUUID();
    Notification n1 = Notification.createNewsNotice(userId, "뉴스1", UUID.randomUUID());
    Notification n2 = Notification.createCommentNotice(userId, "댓글2", UUID.randomUUID());
    n1.confirmedRead();
    noticeRepository.save(n1);
    noticeRepository.save(n2);

    // when
    var result = noticeRepository.findAllByUserIdAndConfirmedIsFalse(userId);

    // then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getContent()).isEqualTo("댓글2");
    assertThat(result.get(0).isConfirmed()).isFalse();
  }

  @Test
  @DisplayName("페이징으로 미확인 알림 조회 테스트")
  void findAllByUserIdAndConfirmedIsFalseWithPaging_success() {
    // given
    UUID userId = UUID.randomUUID();
    for (int i = 0; i < 10; i++) {
      Notification n = Notification.createNewsNotice(userId, "뉴스" + i, UUID.randomUUID());
      if (i % 2 == 0) {
        n.confirmedRead();
      }
      noticeRepository.save(n);
    }

    // when
    var pageable = PageRequest.of(0, 3);
    var page = noticeRepository.findAllByUserIdAndConfirmedIsFalse(userId, pageable);

    // then
    assertThat(page.getContent().size()).isEqualTo(3);
    assertThat(page.getTotalElements()).isEqualTo(5);
  }

}
