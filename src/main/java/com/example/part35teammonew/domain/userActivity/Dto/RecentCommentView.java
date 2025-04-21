package com.example.part35teammonew.domain.userActivity.Dto;

import java.math.BigInteger;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecentCommentView {

  private UUID id;
  private UUID articleId;
  private String articleTitle;

  private UUID userId;
  private String userNickname;

  private String content;
  private BigInteger likeCount;
  private Instant createdAt;
}
