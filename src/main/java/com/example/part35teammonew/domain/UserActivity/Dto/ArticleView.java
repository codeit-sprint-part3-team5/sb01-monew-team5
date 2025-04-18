package com.example.part35teammonew.domain.UserActivity.Dto;

import java.math.BigInteger;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ArticleView {

  private UUID id;
  private UUID viewedBy;
  private Instant createdAt;

  private UUID articleId;
  private String source;
  private String sourceUrl;

  private String articleTitle;
  private Instant articlePublishedDate;
  private String articleSummary;

  private BigInteger articleCommentCount;
  private BigInteger articleViewCount;
}
