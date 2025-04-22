package com.example.part35teammonew.notification;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.part35teammonew.domain.notification.Dto.NotificationDto;
import com.example.part35teammonew.domain.notification.entity.Notification;
import com.example.part35teammonew.domain.notification.repository.NotificationRepository;
import com.example.part35teammonew.domain.notification.service.NotificationServiceImpl;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class NotificationServiceImplTest {

  @Autowired
  private NotificationServiceImpl notificationService;

  @Autowired
  private NotificationRepository notificationRepository;

  @Test
  @DisplayName("뉴스 알림 추가 테스트")
  void addNewsNotice_success() {
    UUID userId = UUID.randomUUID();
    UUID resourceId = UUID.randomUUID();

    NotificationDto dto = notificationService.addNewsNotice(userId, "뉴스", resourceId);

    assertThat(dto).isNotNull();
    assertThat(dto.content()).isEqualTo("뉴스");
  }

  @Test
  @DisplayName("댓글 알림 추가 테스트")
  void addCommentNotice_success() {
    UUID userId = UUID.randomUUID();
    UUID resourceId = UUID.randomUUID();

    NotificationDto dto = notificationService.addCommentNotice(userId, "댓글", resourceId);

    assertThat(dto).isNotNull();
    assertThat(dto.content()).isEqualTo("댓글");
  }

  @Test
  @DisplayName("알림 읽음")
  void confirmedReadNotice_success() {
    Notification saved = notificationRepository.save(Notification.createNewsNotice(
        UUID.randomUUID(), "읽음", UUID.randomUUID()));

    boolean result = notificationService.confirmedReadNotice(saved.getId());

    assertThat(result).isTrue();
    assertThat(notificationRepository.findById(saved.getId()).get().isConfirmed()).isTrue();
  }

  @Test
  @DisplayName("전체 읽음 ")
  void confirmedReadAllNotice_success() {
    UUID userId = UUID.randomUUID();
    notificationRepository.save(Notification.createNewsNotice(userId, "1", UUID.randomUUID()));
    notificationRepository.save(Notification.createCommentNotice(userId, "2", UUID.randomUUID()));

    notificationService.confirmedReadAllNotice(userId);

    List<Notification> all = notificationRepository.findAllByUserIdAndConfirmedIsFalse(userId);
    assertThat(all).isEmpty();
  }

  @Test
  @DisplayName("알림 페이징 조회 테스트")
  void getNoticePage_success() {
    UUID userId = UUID.randomUUID();
    for (int i = 0; i < 7; i++) {
      notificationRepository.save(
          Notification.createNewsNotice(userId, "알림 " + i, UUID.randomUUID()));
    }

    Page<NotificationDto> page = notificationService.getNoticePage(userId, PageRequest.of(0, 5));

    assertThat(page.getContent().size()).isEqualTo(5);
    assertThat(page.getTotalElements()).isEqualTo(7);
  }


}