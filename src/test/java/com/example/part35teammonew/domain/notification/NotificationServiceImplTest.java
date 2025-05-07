package com.example.part35teammonew.domain.notification;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.part35teammonew.domain.notification.Dto.CursorPageRequest;
import com.example.part35teammonew.domain.notification.Dto.NotificationDto;
import com.example.part35teammonew.domain.notification.entity.Notification;
import com.example.part35teammonew.domain.notification.repository.NotificationRepository;
import com.example.part35teammonew.domain.notification.service.NotificationServiceImpl;
import java.time.Instant;
import java.time.LocalDateTime;
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
    assertThat(dto.getContent()).isEqualTo("뉴스");
  }

  @Test
  @DisplayName("댓글 알림 추가 테스트")
  void addCommentNotice_success() {
    UUID userId = UUID.randomUUID();
    UUID resourceId = UUID.randomUUID();

    NotificationDto dto = notificationService.addCommentNotice(userId, "댓글", resourceId);

    assertThat(dto).isNotNull();
    assertThat(dto.getContent()).isEqualTo("댓글");
  }

  @Test
  @DisplayName("알림 읽음")
  void confirmedReadNotice_success() {
    UUID user = UUID.randomUUID();
    Instant time=Instant.now();
    Notification saved = notificationRepository.save(Notification.createNewsNotice(
        user, "읽음", UUID.randomUUID(),time));

    boolean result = notificationService.confirmedReadNotice(saved.getId(), user);

    assertThat(result).isTrue();
    assertThat(notificationRepository.findById(saved.getId()).get().isConfirmed()).isTrue();
  }

  @Test
  @DisplayName("전체 읽음 ")
  void confirmedReadAllNotice_success() {
    UUID userId = UUID.randomUUID();
    Instant time=Instant.now();
    notificationRepository.save(Notification.createNewsNotice(userId, "1", UUID.randomUUID(),time));
    notificationRepository.save(Notification.createCommentNotice(userId, "2", UUID.randomUUID(),time));

    notificationService.confirmedReadAllNotice(userId);

    List<Notification> all = notificationRepository.findAllByUserIdAndConfirmedIsFalse(userId);
    assertThat(all).isEmpty();
  }

  @Test
  @DisplayName("알림 없음")
  void getNoticePage_empty() {
    UUID userId = UUID.randomUUID();

    CursorPageRequest request = new CursorPageRequest(null, Instant.now().minusSeconds(86400), 10);
    var response = notificationService.getNoticePage(userId, request);

    assertThat(response.getContent()).isEmpty();
    assertThat(response.getNextCursor()).isNull();
    assertThat(response.isHasNext()).isFalse();
  }

  @Test
  @DisplayName("알림 딱맞게 조회")
  void getNoticePage_exactLimit_noNext() {
    UUID userId = UUID.randomUUID();
    UUID resourceId = UUID.randomUUID();
    Instant time=Instant.now();

    notificationRepository.save(Notification.createNewsNotice(userId, "1", resourceId,time));
    sleep(5);
    notificationRepository.save(Notification.createNewsNotice(userId, "2", resourceId,time));

    CursorPageRequest request = new CursorPageRequest(null, Instant.now().minusSeconds(86400), 2);
    var response = notificationService.getNoticePage(userId, request);

    assertThat(response.getContent()).hasSize(2);
    assertThat(response.isHasNext()).isFalse();
    assertThat(response.getNextCursor()).isNull();
  }

  @Test
  @DisplayName("다른 유저 알림이 조회되지 않아야 함")
  void getNoticePage_userIsolation() {
    UUID user1 = UUID.randomUUID();
    UUID user2 = UUID.randomUUID();
    UUID resourceId = UUID.randomUUID();
    Instant time=Instant.now();
    notificationRepository.save(Notification.createNewsNotice(user1, "user1 알림", resourceId,time));
    notificationRepository.save(Notification.createNewsNotice(user2, "user2 알림", resourceId,time));

    var response = notificationService.getNoticePage(user1,
        new CursorPageRequest(null, Instant.now().minusSeconds(86400), 10));
    assertThat(response.getContent()).hasSize(1);
    assertThat(response.getContent().get(0).getContent()).isEqualTo("user1 알림");
  }

//  @Test  이거 실행할려면 엔티티 필드 바꾸고 테스트 해야함
//  @DisplayName("after 조건 확인")
//  void getNoticePage_afterConditionWorks() {
//    UUID userId = UUID.randomUUID();
//    UUID resourceId = UUID.randomUUID();
//    Notification old = Notification.createNewsNotice(userId, "1111", resourceId);
//    old.setCreatedAt(LocalDateTime.now().minusDays(1).atZone(ZoneOffset.UTC).toInstant());
//    notificationRepository.save(old);
//
//    LocalDateTime baseTime = LocalDateTime.now();
//
//    Notification recent = Notification.createNewsNotice(userId, "2222", resourceId);
//    recent.setCreatedAt(baseTime.plusSeconds(1).atZone(ZoneOffset.UTC).toInstant());
//    notificationRepository.save(recent);
//
//    CursorPageRequest request = new CursorPageRequest(null, baseTime, 10);
//    var response = notificationService.getNoticePage(userId, request);
//
//    assertThat(response.getData()).hasSize(1);
//    assertThat(response.getData().get(0).content()).isEqualTo("2222");
//  }

  private void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }


}