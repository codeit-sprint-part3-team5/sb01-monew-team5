package com.example.part35teammonew.domain.notification.Dto;

import java.util.List;

public class CursorPageResponse<T> {

  private final List<T> data;
  private final String nextCursor;
  private final String hasAfter;
  private final boolean hasNext;
  private final Long size;
  private final Long totalElement;

  public CursorPageResponse(List<T> data, String nextCursor, String hasAfter, boolean hasNext,
      Long size, Long totalElement) {
    this.data = data;
    this.nextCursor = nextCursor;
    this.hasAfter = hasAfter;
    this.hasNext = hasNext;
    this.size = size;
    this.totalElement = totalElement;
  }

  public List<T> getData() {
    return data;
  }

  public String getNextCursor() {
    return nextCursor;
  }

  public String getHasAfter() {
    return hasAfter;
  }

  public boolean isHasNext() {
    return hasNext;
  }

  public Long getSize() {
    return size;
  }

  public Long getTotalElement() {
    return totalElement;
  }
}
