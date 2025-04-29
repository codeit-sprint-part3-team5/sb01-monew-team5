package com.example.part35teammonew.domain.notification.Dto;

import java.util.List;

public class CursorPageResponse<T> {

  private final List<T> content;
  private final String nextCursor;
  private final String hasAfter;
  private final Long size;
  private final Long totalElements;
  private final boolean hasNext;

  public CursorPageResponse(List<T> data, String nextCursor, String hasAfter, boolean hasNext,
      Long size, Long totalElement) {
    this.content = data;
    this.nextCursor = nextCursor;
    this.hasAfter = hasAfter;
    this.hasNext = hasNext;
    this.size = size;
    this.totalElements = totalElement;
  }

  public List<T> getContent() {
    return content;
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

  public Long getTotalElements() {
    return totalElements;
  }
}
