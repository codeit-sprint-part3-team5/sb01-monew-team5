package com.example.part35teammonew.domain.notification.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "notification")
@Getter
public class Notification {

  @Id
  private ObjectId id;
  private final UUID userId;
  private List<Notice> noticeList;

  @Builder
  private Notification(UUID userId) {
    this.userId = userId;
    noticeList = new ArrayList<>();
  }

  public static Notification setUpNotification(UUID userId) {
    return Notification.builder()
        .userId(userId)
        .build();
  }

  public void addNotice(Notice newNotice) {
    noticeList.add(newNotice);
  }

}
