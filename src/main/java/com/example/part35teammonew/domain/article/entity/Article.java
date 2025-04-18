package com.example.part35teammonew.domain.article.entity;

import com.example.part35teammonew.domain.article.dto.ArticleCreateDto;
import com.example.part35teammonew.domain.comment.entity.Comment;
import com.example.part35teammonew.domain.comment.entity.CommentLike;
import com.example.part35teammonew.domain.interest.entity.Interest;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Table(name = "articles")
public class Article {

  @Id
  @GeneratedValue
  @Column(name = "article_id")
  private UUID id;

  @NotNull
  private String title;

  @NotNull
  private String summary;

  @NotNull
  private String link;

  @NotNull
  private String source;

  @NotNull
  private Instant date;

  @NotNull
  private Instant createdAt;

  private Instant deletedAt;

  @NotNull
  private int commentCount = 0;

  @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Comment> comments = new ArrayList<>();

  @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<CommentLike> commentLikes = new ArrayList<>();

  @ManyToMany
  @JoinTable(name = "article_interest", joinColumns = @JoinColumn(name = "article_id"), inverseJoinColumns = @JoinColumn(name = "interest_id"))
  private List<Interest> interests = new ArrayList<>();

  public Article(ArticleCreateDto articleCreateDto) {
    this.title = articleCreateDto.getTitle();
    this.summary = articleCreateDto.getSummary();;
    this.link = articleCreateDto.getTitle();;
    this.source = articleCreateDto.getSource();;
    this.date = articleCreateDto.getDate();;
    this.createdAt = Instant.now();;
    this.commentCount = 0;
  }
}
