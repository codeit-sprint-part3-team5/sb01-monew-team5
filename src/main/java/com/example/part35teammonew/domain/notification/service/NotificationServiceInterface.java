package com.example.part35teammonew.domain.notification.service;


import com.example.part35teammonew.domain.notification.Dto.NotificationDto;
import java.util.UUID;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationServiceInterface {

  NotificationDto addNewsNotice(UUID userId, String content, UUID resourceId);

  NotificationDto addCommentNotice(UUID userId, String content, UUID resourceId);

  boolean confirmedReadNotice(ObjectId Id);

  void confirmedReadAllNotice(UUID userId);

  boolean deleteOldConfirmedNotice();

  Page<NotificationDto> getNoticePage(UUID userId, Pageable pageable);


}
