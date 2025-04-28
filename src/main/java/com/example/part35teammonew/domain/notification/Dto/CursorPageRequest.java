package com.example.part35teammonew.domain.notification.Dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

@Getter
@Setter
public class CursorPageRequest {

  private final String cursor;
  private final LocalDateTime after;
  private final int limit;

  public CursorPageRequest(String cursor, LocalDateTime after, int limit) {
    this.cursor = cursor;
    this.after = after;
    this.limit = limit;
  }

  public ObjectId getCursorObjectId() {
    if (cursor == null || cursor.isBlank()) {
      return null;
    }
    try {
      return new ObjectId(cursor);
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

}
