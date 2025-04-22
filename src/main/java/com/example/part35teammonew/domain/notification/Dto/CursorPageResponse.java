package com.example.part35teammonew.domain.notification.Dto;

import java.util.List;

public class CursorPageResponse<T> {

  private final List<T> data;
  private final String nextCursor;
  private final boolean hasNext;

  public CursorPageResponse(List<T> data, String nextCursor, boolean hasNext) {
    this.data = data;
    this.nextCursor = nextCursor;
    this.hasNext = hasNext;
  }

  public List<T> getData() {
    return data;
  }

  public String getNextCursor() {
    return nextCursor;
  }

  public boolean isHasNext() {
    return hasNext;
  }
}
