package com.example.part35teammonew.domain.article.dto;

import com.example.part35teammonew.domain.article.entity.Article;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;
import org.springframework.web.ErrorResponse.Builder;

@Data
public class ArticleBaseDto {
  private UUID id;

  @NotBlank
  private String title;

  @NotBlank
  private String summary;

  @NotBlank
  private String link;

  @NotBlank
  private String source;

  @NotNull
  private LocalDateTime date;

  private LocalDateTime createdAt;

  @NotNull
  private int commentCount;

  Long viewCount;

  public ArticleBaseDto(Article article) {
    this.id = article.getId();
    this.title = article.getTitle();
    this.summary = article.getSummary();
    this.link = article.getLink();
    this.source = article.getSource();
    this.date = article.getDate();
    this.createdAt = article.getCreatedAt();
    this.commentCount = article.getCommentCount();
  }

  // 생성자
  public ArticleBaseDto(UUID id, String title, String summary, String link, String source, LocalDateTime date, LocalDateTime createdAt, int commentCount, Long viewCount) {
    this.id = id;
    this.title = title;
    this.summary = summary;
    this.link = link;
    this.source = source;
    this.date = date;
    this.createdAt = createdAt;
    this.commentCount = commentCount;
    this.viewCount = viewCount;
  }
  // 저장 Dto
  public ArticleBaseDto(String title, String summary, String link, String source,
      LocalDateTime date, int commentCount) {
    this.title = title;
    this.summary = summary;
    this.link = link;
    this.source = source;
    this.date = date;
    this.commentCount = commentCount;
  }
}
