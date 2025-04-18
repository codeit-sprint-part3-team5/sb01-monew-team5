package com.example.part35teammonew.notification;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.example.part35teammonew.domain.notification.entity.Notification;
import com.example.part35teammonew.domain.notification.repository.NotificationRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

@DataMongoTest
class NotificationRepositoryTest {

  @Autowired
  private NotificationRepository notificationRepository;

  @Test
  @DisplayName("Notification을 조회")
  void findByUserId_success() {
    UUID userId = UUID.randomUUID();
    Notification notification = Notification.setUpNotification(userId);
    notificationRepository.save(notification);

    List<Notification> result = notificationRepository.findByUserId(userId);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getUserId()).isEqualTo(userId);
  }

  @Test
  @DisplayName("userId로 필터링 조회")
  void findByUserId_filtersCorrectly() {
    UUID user1 = UUID.randomUUID();
    UUID user2 = UUID.randomUUID();

    notificationRepository.save(Notification.setUpNotification(user1));
    notificationRepository.save(Notification.setUpNotification(user2));
    notificationRepository.save(Notification.setUpNotification(user1));

    List<Notification> result = notificationRepository.findByUserId(user1);

    assertThat(result).hasSize(2);
    assertThat(result).allMatch(n -> n.getUserId().equals(user1));
  }

  @Test
  @DisplayName("없는거 조회하면 빈거 반환")
  void findByUserId_notFound() {
    List<Notification> result = notificationRepository.findByUserId(UUID.randomUUID());

    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("저장 테스트)")
  void createNotification_success() {
    UUID userId = UUID.randomUUID();
    Notification saved = notificationRepository.save(Notification.setUpNotification(userId));

    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getUserId()).isEqualTo(userId);
  }


  @Test
  @DisplayName("삭제 테스트")
  void deleteNotification_success() {
    UUID userId = UUID.randomUUID();
    Notification notification = notificationRepository.save(Notification.setUpNotification(userId));

    notificationRepository.delete(notification);

    Optional<Notification> result = notificationRepository.findById(notification.getId());
    assertThat(result).isNotPresent();
  }
}