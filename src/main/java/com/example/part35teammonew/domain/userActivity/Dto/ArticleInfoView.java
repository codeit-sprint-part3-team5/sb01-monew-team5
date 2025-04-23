package com.example.part35teammonew.domain.userActivity.Dto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ArticleInfoView {

  private UUID id;//기사 조회 id
  private UUID viewedBy;//본사람
  private Instant createdAt;//본 시간

  private UUID articleId;//가서 아이디
  private String source;
  private String sourceUrl;

  private String articleTitle;
  private LocalDateTime articlePublishedDate;
  private String articleSummary;

  private int articleCommentCount;
  private int articleViewCount;
}
