package com.example.part35teammonew.domain.notification.dto;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CursorPageRequest {

  private final Instant  cursor;
  private final Instant  after;
  private final int limit;

  public CursorPageRequest(Instant  cursor, Instant after, int limit) {
    this.cursor = cursor;
    this.after = after;
    this.limit = limit;
  }


}