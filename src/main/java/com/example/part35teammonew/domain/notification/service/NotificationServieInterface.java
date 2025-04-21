package com.example.part35teammonew.domain.notification.service;


import com.example.part35teammonew.domain.notification.Dto.NotificationeDto;
import java.util.UUID;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationServieInterface {

  NotificationeDto addNewsNotice(UUID userId, NotificationeDto noticeDto);

  NotificationeDto addCommentNotice(UUID userId, NotificationeDto noticeDto);

  boolean confirmedReadNotice(ObjectId Id);

  boolean confirmedReadAllNotice(ObjectId userId);

  boolean deleteOldConfirmedNotice();

  Page<NotificationeDto> getNoticePage(UUID userId, Pageable pageable);


}
