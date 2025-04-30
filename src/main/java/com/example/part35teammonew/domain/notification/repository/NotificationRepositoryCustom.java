package com.example.part35teammonew.domain.notification.repository;

import com.example.part35teammonew.domain.notification.entity.Notification;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface NotificationRepositoryCustom {
  List<Notification> findAllUnreadByUserIdWithCursor(UUID userId, Instant after, Instant before, int limit);
}
