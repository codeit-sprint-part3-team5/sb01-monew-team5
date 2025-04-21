package com.example.part35teammonew.domain.article.dto;

import com.example.part35teammonew.domain.article.entity.Article;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

  // 기본 생성자 금지
  public ArticleBaseDto(Article article) {
    this.id = article.getId();
    this.title = article.getTitle();
    this.summary = article.getSummary();
    this.link = article.getLink();
    this.source = article.getSource();
    this.date = article.getDate();
  }

  // 생성자
  public ArticleBaseDto(UUID id, String title, String summary, String link, String source,
      LocalDateTime date) {
    this.id = id;
    this.title = title;
    this.summary = summary;
    this.link = link;
    this.source = source;
    this.date = date;
  }
  // 저장 Dto
  public ArticleBaseDto(String title, String summary, String link, String source,
      LocalDateTime date) {
    this.title = title;
    this.summary = summary;
    this.link = link;
    this.source = source;
    this.date = date;
  }

}
