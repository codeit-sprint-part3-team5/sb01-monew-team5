package com.example.part35teammonew.domain.notification.service;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationServiceInterface {


  private final NotificationRepository notificationRepository;
  private final NotificationMapper notificationMapper;

  public NotificationServiceImpl(@Autowired NotificationRepository notificationRepository
      , @Autowired NotificationMapper notificationMapper) {
    this.notificationRepository = notificationRepository;
    this.notificationMapper = notificationMapper;
  }

  @Override
  public NotificationDto addNewsNotice(UUID userId, String content, UUID resourceId) {
    Notification notification = Notification.createNewsNotice(userId, content, resourceId);
    notificationRepository.save(notification);
    return notificationMapper.toDto(notification);
  }

  @Override
  public NotificationDto addCommentNotice(UUID userId, String content, UUID resourceId) {
    Notification notification = Notification.createCommentNotice(userId, content, resourceId);
    notificationRepository.save(notification);
    return notificationMapper.toDto(notification);
  }

  @Override
  public boolean confirmedReadNotice(ObjectId id) {
    Optional<Notification> optionalNotification = notificationRepository.findById(id);
    if (optionalNotification.isEmpty()) {
      return false;
    }

    Notification notification = optionalNotification.get();
    boolean wasChanged = notification.confirmedRead();
    if (wasChanged) {
      notificationRepository.save(notification);
    }

    return wasChanged;
  }

  @Override
  public void confirmedReadAllNotice(UUID userId) {
    List<Notification> notificationList = notificationRepository.findAllByUserIdAndConfirmedIsFalse(
        userId);
    notificationList.forEach(Notification::confirmedRead);
    notificationRepository.saveAll(notificationList);
  }


  @Override
  public boolean deleteOldConfirmedNotice() {
    Instant threshold = Instant.now().minus(7, ChronoUnit.DAYS);
    notificationRepository.deleteAllByConfirmedIsTrueAndCreatedAtBefore(threshold);
    return true;
  }

  @Override
  public Page<NotificationDto> getNoticePage(UUID userId, Pageable pageable) {
    Page<Notification> notifications = notificationRepository.findAllByUserIdAndConfirmedIsFalse(
        userId, pageable);
    return notifications.map(notificationMapper::toDto);
  }
}
