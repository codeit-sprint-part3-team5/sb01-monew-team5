package com.example.part35teammonew.notification;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.part35teammonew.domain.notification.Dto.CursorPageRequest;
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
    UUID user = UUID.randomUUID();
    Notification saved = notificationRepository.save(Notification.createNewsNotice(
        user, "읽음", UUID.randomUUID()));

    boolean result = notificationService.confirmedReadNotice(saved.getId(), user);

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
  @DisplayName(" 페이징 조회")
  void getNoticePage_success() {
    UUID userId = UUID.randomUUID();
    UUID resourceId = UUID.randomUUID();

    Notification n1 = notificationRepository.save(
        Notification.createNewsNotice(userId, "알림1", resourceId));
    sleep(20); // createdAt 간격을 확실히 벌림
    Notification n2 = notificationRepository.save(
        Notification.createNewsNotice(userId, "알림2", resourceId));
    sleep(20);
    Notification n3 = notificationRepository.save(
        Notification.createNewsNotice(userId, "알림3", resourceId));

    CursorPageRequest pageRequest = new CursorPageRequest(null, 2);
    var page1 = notificationService.getNoticePage(userId, pageRequest);

    assertThat(page1.getData()).hasSize(2);
    assertThat(page1.isHasNext()).isTrue();
    assertThat(page1.getNextCursor()).isNotBlank();

    CursorPageRequest nextPageRequest = new CursorPageRequest(page1.getNextCursor(), 2);
    var page2 = notificationService.getNoticePage(userId, nextPageRequest);

    assertThat(page2.getData()).hasSize(1);
    assertThat(page2.isHasNext()).isFalse();
  }

  @Test
  @DisplayName("알림 없음")
  void getNoticePage_empty() {
    UUID userId = UUID.randomUUID();

    CursorPageRequest request = new CursorPageRequest(null, 10);
    var response = notificationService.getNoticePage(userId, request);

    assertThat(response.getData()).isEmpty();
    assertThat(response.getNextCursor()).isNull();
    assertThat(response.isHasNext()).isFalse();
  }

  @Test
  @DisplayName("알림 딱맞게 조회")
  void getNoticePage_exactLimit_noNext() {
    UUID userId = UUID.randomUUID();
    UUID resourceId = UUID.randomUUID();

    notificationRepository.save(Notification.createNewsNotice(userId, "1", resourceId));
    sleep(10);
    notificationRepository.save(Notification.createNewsNotice(userId, "2", resourceId));

    CursorPageRequest request = new CursorPageRequest(null, 2);
    var response = notificationService.getNoticePage(userId, request);

    assertThat(response.getData()).hasSize(2);
    assertThat(response.isHasNext()).isFalse();
    assertThat(response.getNextCursor()).isNull();
  }

  private void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }


}