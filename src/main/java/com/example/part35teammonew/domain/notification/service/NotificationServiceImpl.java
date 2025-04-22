package com.example.part35teammonew.domain.notification.service;

import com.example.part35teammonew.domain.notification.Dto.CursorPageRequest;
import com.example.part35teammonew.domain.notification.Dto.CursorPageResponse;
import com.example.part35teammonew.domain.notification.Dto.NotificationDto;
import com.example.part35teammonew.domain.notification.entity.Notification;
import com.example.part35teammonew.domain.notification.mapper.NotificationMapper;
import com.example.part35teammonew.domain.notification.repository.NotificationRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationServiceImpl implements NotificationServiceInterface {


  private final NotificationRepository notificationRepository;
  private final NotificationMapper notificationMapper;

  public NotificationServiceImpl(@Autowired NotificationRepository notificationRepository
      , @Autowired NotificationMapper notificationMapper) {
    this.notificationRepository = notificationRepository;
    this.notificationMapper = notificationMapper;
  }

  @Transactional
  @Override
  public NotificationDto addNewsNotice(UUID userId, String content, UUID resourceId) {
    Notification notification = Notification.createNewsNotice(userId, content, resourceId);
    notificationRepository.save(notification);
    return notificationMapper.toDto(notification);
  }

  @Transactional
  @Override
  public NotificationDto addCommentNotice(UUID userId, String content, UUID resourceId) {
    Notification notification = Notification.createCommentNotice(userId, content, resourceId);
    notificationRepository.save(notification);
    return notificationMapper.toDto(notification);
  }

  @Transactional
  @Override
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
  @Override
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
  @Override
  public boolean deleteOldConfirmedNotice() {
    Instant threshold = Instant.now().minus(7, ChronoUnit.DAYS);
    notificationRepository.deleteAllByConfirmedIsTrueAndCreatedAtBefore(threshold);
    return true;
  }

  @Override
  @Transactional(readOnly = true)
  public CursorPageResponse<NotificationDto> getNoticePage(UUID userId,
      CursorPageRequest pageRequest) {
    ObjectId cursorId = pageRequest.getCursorObjectId();
    int limit = pageRequest.getLimit() + 1; // 다음 페이지 여부 확인 위해 +1

    List<Notification> notifications = (cursorId != null)
        ? notificationRepository.findAllByUserIdAndIdLessThanOrderByIdDesc(userId, cursorId)
        : notificationRepository.findAllByUserIdOrderByIdDesc(userId);

    boolean hasNext = notifications.size() > pageRequest.getLimit();
    if (hasNext) {
      notifications = notifications.subList(0, pageRequest.getLimit());
    }

    List<NotificationDto> dtoList = notifications.stream()
        .map(notificationMapper::toDto)
        .toList();

    String nextCursor = hasNext
        ? notifications.get(notifications.size() - 1).getId().toHexString()
        : null;

    return new CursorPageResponse<>(dtoList, nextCursor, hasNext);
  }
}
