package com.example.part35teammonew.domain.UserActivity.Dto;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecentComent {

  private UUID id;
  private UUID articleId;
  private String articleTitle;

  private UUID userId;
  private String userNickname;

  private String content;
  private long likeCount;
  private Instant createdAt;
}
