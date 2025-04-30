package com.example.part35teammonew.domain.notification.service;

import com.example.part35teammonew.domain.notification.Dto.CursorPageRequest;
import com.example.part35teammonew.domain.notification.Dto.CursorPageResponse;
import com.example.part35teammonew.domain.notification.Dto.NotificationDto;
import com.example.part35teammonew.domain.notification.entity.Notification;
import com.example.part35teammonew.domain.notification.repository.NotificationRepository;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationServiceInterface {


  private final NotificationRepository notificationRepository;

  public NotificationServiceImpl(@Autowired NotificationRepository notificationRepository) {
    this.notificationRepository = notificationRepository;
  }

  @Transactional
  @Override//뉴스 알림 추가할떄
  public NotificationDto addNewsNotice(UUID userId, String content, UUID resourceId) {
    Notification notification = Notification.createNewsNotice(userId, content, resourceId);
    notificationRepository.save(notification);
    return new NotificationDto(notification);
  }

  @Transactional
  @Override//댓글 관련 알림 추가할떄
  public NotificationDto addCommentNotice(UUID userId, String content, UUID resourceId) {
    Notification notification = Notification.createCommentNotice(userId, content, resourceId);
    notificationRepository.save(notification);
    return new NotificationDto(notification);
  }

  @Transactional
  @Override//알림 아이디로 읽음 처리하기, 유저 아이디로로 교차 검증
  public boolean confirmedReadNotice(ObjectId id, UUID userId) {
    Optional<Notification> optionalNotification = notificationRepository.findById(id);
    if (optionalNotification.isEmpty()) {
      return false;
    }

    Notification notification = optionalNotification.get();

    if (!notification.getUserId().equals(userId)) {
      return false;
    }

    boolean wasChanged = notification.confirmedRead();
    if (wasChanged) {
      notificationRepository.save(notification);
    }

    return wasChanged;
  }

  @Transactional
  @Override// 유저의 알림 다 읽음처리
  public boolean confirmedReadAllNotice(UUID userId) {
    List<Notification> notificationList =
        notificationRepository.findAllByUserIdAndConfirmedIsFalse(userId);

    if (notificationList.isEmpty()) {
      return false;
    }

    notificationList.forEach(Notification::confirmedRead);
    notificationRepository.saveAll(notificationList);

    return true;
  }


  @Transactional
  @Override // 일주일 지난거 삭제, 따로 실행하는 서비스 있음
  public boolean deleteOldConfirmedNotice() {
    Instant threshold = Instant.now().minus(7, ChronoUnit.DAYS);
    notificationRepository.deleteAllByConfirmedIsTrueAndCreatedAtBefore(threshold);
    return true;
  }

  @Transactional(readOnly = true)
  @Override
  public CursorPageResponse<NotificationDto> getNoticePage(UUID userId, CursorPageRequest pageRequest) {
    Instant cursorInstant = pageRequest.getCursor();
    Instant afterInstant = pageRequest.getAfter();

    List<Notification> notifications;

    if (afterInstant != null) {
      if (cursorInstant != null) {
        // ✨ 여기! Custom Repository 메서드 사용
        notifications = notificationRepository.findAllUnreadByUserIdWithCursor(
            userId, afterInstant, cursorInstant, pageRequest.getLimit()
        );
      } else {
        notifications = notificationRepository
            .findAllByUserIdAndConfirmedIsFalseAndCreatedAtAfterOrderByCreatedAtDesc(
                userId, afterInstant);
      }
    } else {
      if (cursorInstant != null) {
        notifications = notificationRepository
            .findAllByUserIdAndConfirmedIsFalseAndCreatedAtBeforeOrderByCreatedAtDesc(
                userId, cursorInstant);
      } else {
        notifications = notificationRepository
            .findAllByUserIdAndConfirmedIsFalseOrderByCreatedAtDesc(userId);
      }
    }

    boolean hasNext = notifications.size() > pageRequest.getLimit();
    if (hasNext) {
      notifications = notifications.subList(0, pageRequest.getLimit());
    }

    List<NotificationDto> dtoList = notifications.stream()
        .map(NotificationDto::new)
        .toList();

    String nextCursor;
    if (hasNext) {
      nextCursor = notifications.get(notifications.size() - 1).getCreatedAt().toString();
    } else {
      nextCursor = null;
    }

    long totalElement = notificationRepository.countByUserIdAndConfirmedIsFalse(userId);
    long size = dtoList.size();

    return new CursorPageResponse<>(dtoList, nextCursor, Boolean.toString(hasNext), hasNext, size, totalElement);
  }


}
