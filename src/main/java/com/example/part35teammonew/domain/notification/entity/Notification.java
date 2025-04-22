package com.example.part35teammonew.domain.notification.entity;

import com.example.part35teammonew.domain.notification.Enum.NotificationType;
import jakarta.persistence.Id;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document(collection = "notification")
@CompoundIndexes({
    @CompoundIndex(name = "userId_createdAt_idx", def = "{'userId': 1, 'createdAt': -1}")
})
public class Notification {

  @Id
  private ObjectId id;
  private UUID userId;
  private final Instant createdAt;
  private Instant updateAt;
  private boolean confirmed;
  private final String content;
  private NotificationType type;
  private final UUID resourceId;

  @Builder
  private Notification(UUID userId, String content, NotificationType type, UUID resourceId) {
    this.userId = userId;
    this.createdAt = Instant.now();
    this.updateAt = Instant.EPOCH;
    this.confirmed = false;
    this.content = content;
    this.type = type;
    this.resourceId = resourceId;
  }

  public static Notification createCommentNotice(UUID userId, String content, UUID resourceId) {
    return Notification.builder()
        .userId(userId)
        .content(content)
        .type(NotificationType.COMMENT)
        .resourceId(resourceId)
        .build();
  }

  public static Notification createNewsNotice(UUID userId, String content, UUID resourceId) {
    return Notification.builder()
        .userId(userId)
        .content(content)
        .type(NotificationType.NEWS)
        .resourceId(resourceId)
        .build();
  }

  public boolean confirmedRead() {
    if (!this.confirmed) {
      this.confirmed = true;
      return true;
    }
    return false;
  }

}
