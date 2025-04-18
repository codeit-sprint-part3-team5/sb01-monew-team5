package com.example.part35teammonew.domain.notification.repository;

import java.util.UUID;
import org.bson.types.ObjectId;

public interface NoticeRepository {

  void markNoticeAsRead(UUID userId, ObjectId noticeId);
}
