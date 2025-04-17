package com.example.part35teammonew.domain.notification.entity;

import com.example.part35teammonew.domain.notification.Enum.NotificationType;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "notification")
@Getter
@Builder
public class Notification {

  @Id
  private ObjectId id;
  private final Instant createdAt;
  private Instant updateAt;
  private boolean confirmed;
  private final UUID userId;
  private final String content;
  private NotificationType type;
  private final UUID resourceId;

  @Builder
  private Notification(UUID userId, String content, NotificationType type, UUID resourceId) {
    this.createdAt = Instant.now();
    this.updateAt = Instant.MIN;
    this.confirmed = false;
    this.userId = userId;
    this.content = content;
    this.type = type;
    this.resourceId = resourceId;

  }

  public static Notification setUpCommentNotification(UUID userId, String content,
      UUID resourceId) {
    return Notification.builder()
        .userId(userId)
        .content(content)
        .type(NotificationType.COMMENT)
        .resourceId(resourceId)
        .build();
  }

  public static Notification setUpNewsNotification(UUID userId, String content, UUID resourceId) {
    return Notification.builder()
        .userId(userId)
        .content(content)
        .type(NotificationType.NEWS)
        .resourceId(resourceId)
        .build();
  }

  public void updateConfirmedRead() {
    this.confirmed = true;
    this.updateAt = Instant.now();
  }
  
  public boolean isExpired() {
    return createdAt.isBefore(Instant.now().minus(Duration.ofDays(7)));
  }

}
