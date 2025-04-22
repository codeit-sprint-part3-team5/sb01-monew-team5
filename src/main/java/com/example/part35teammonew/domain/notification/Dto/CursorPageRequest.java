package com.example.part35teammonew.domain.notification.Dto;

import org.bson.types.ObjectId;

public class CursorPageRequest {

  private final String cursor;
  private final int limit;

  public CursorPageRequest(String cursor, int limit) {
    this.cursor = cursor;
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

  public int getLimit() {
    return limit;
  }

  public String getCursor() {
    return cursor;
  }
}
