package com.example.part35teammonew.domain.UserActivity.Dto;

import java.math.BigInteger;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LikeComentView {

  private UUID id;
  private Instant createdAt;

  private UUID commentId;
  private UUID articleId;
  private String articleTitle;

  private UUID commentUserId;
  private String commentUserNickname;

  private String commentContent;
  private BigInteger commentLikeCount;
  private Instant commentCreatedAt;
}
