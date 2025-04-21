package com.example.part35teammonew.domain.article.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class ArticleEnrollmentResponse {
  //Vaildation 필요
  private UUID id;
  private UUID viewdBy;
  private LocalDateTime createdAt;
  private UUID articleId;
  private String source;
  private String sourceUrl;
  private String articleTitle;
  private LocalDateTime articlePublishedDate;
  private String articleSummary;
  private int articleCommentCount;
  private int articleViewCount;

  public ArticleEnrollmentResponse(UUID id, UUID viewdBy, LocalDateTime createdAt, UUID articleId,
      String source, String sourceUrl, String articleTitle, LocalDateTime articlePublishedDate,
      String articleSummary, int articleCommentCount, int articleViewCount) {
    this.id = id;
    this.viewdBy = viewdBy;
    this.createdAt = createdAt;
    this.articleId = articleId;
    this.source = source;
    this.sourceUrl = sourceUrl;
    this.articleTitle = articleTitle;
    this.articlePublishedDate = articlePublishedDate;
    this.articleSummary = articleSummary;
    this.articleCommentCount = articleCommentCount;
    this.articleViewCount = articleViewCount;
  }
}
