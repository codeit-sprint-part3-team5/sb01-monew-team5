package com.example.part35teammonew.domain.article.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import lombok.Data;

@Data
public class ArticleCreateDto {

  @NotBlank
  private String title;

  @NotBlank
  private String summary;

  @NotBlank
  private String link;

  @NotBlank
  private String source;

  @NotNull
  private Instant date;

  // 기본 생성자 금지
  private ArticleCreateDto() {}

  // 생성자
  public ArticleCreateDto(String title, String summary, String link, String source, Instant date) {
    this.title = title;
    this.summary = summary;
    this.link = link;
    this.source = source;
    this.date = date;
  }
}
