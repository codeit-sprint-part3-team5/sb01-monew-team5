package com.example.part35teammonew.domain.notification.service;


import com.example.part35teammonew.domain.notification.Dto.CursorPageRequest;
import com.example.part35teammonew.domain.notification.Dto.CursorPageResponse;
import com.example.part35teammonew.domain.notification.Dto.NotificationDto;
import java.util.UUID;
import org.bson.types.ObjectId;

public interface NotificationServiceInterface {

  NotificationDto addNewsNotice(UUID userId, String content, UUID resourceId);

  NotificationDto addCommentNotice(UUID userId, String content, UUID resourceId);

  boolean confirmedReadNotice(ObjectId id, UUID userId);

  boolean confirmedReadAllNotice(UUID userId);

  boolean deleteOldConfirmedNotice();

  CursorPageResponse<NotificationDto> getNoticePage(UUID userId, CursorPageRequest pageRequest);


}
