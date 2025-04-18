package com.example.part35teammonew.domain.notification.entity;

import com.example.part35teammonew.domain.notification.Enum.NotificationType;
import jakarta.persistence.Id;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import org.bson.types.ObjectId;

@Getter
@Builder
public class Notice {

  @Id
  private ObjectId id;
  private final Instant createdAt;
  private Instant updateAt;
  private boolean confirmed;
  private final String content;
  private NotificationType type;
  private final UUID resourceId;

  @Builder
  private Notice(String content, NotificationType type, UUID resourceId) {
    this.createdAt = Instant.now();
    this.updateAt = Instant.MIN;
    this.confirmed = false;
    this.content = content;
    this.type = type;
    this.resourceId = resourceId;
  }

  public static Notice createCommentNotice(String content, UUID resourceId) {
    return Notice.builder()
        .content(content)
        .type(NotificationType.COMMENT)
        .resourceId(resourceId)
        .build();
  }

  public static Notice createNewsNotice(String content, UUID resourceId) {
    return Notice.builder()
        .content(content)
        .type(NotificationType.NEWS)
        .resourceId(resourceId)
        .build();
  }

}
