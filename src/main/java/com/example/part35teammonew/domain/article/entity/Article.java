package com.example.part35teammonew.domain.article.entity;

import com.example.part35teammonew.domain.article.dto.ArticleBaseDto;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
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
  @Column(length = 500)
  private String title;

  @NotNull
  @Column(length = 2000)
  private String summary;

  @NotNull
  @Column(length = 1000)
  private String link;

  @NotNull
  @Column(length = 500)
  private String source;

  @NotNull
  private LocalDateTime date;

  @NotNull
  private LocalDateTime createdAt;

  private LocalDateTime deletedAt;

  @NotNull
  private int commentCount = 0;

  @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Comment> comments = new ArrayList<>();

  @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<CommentLike> commentLikes = new ArrayList<>();

  @ManyToMany
  @JoinTable(name = "article_interests", joinColumns = @JoinColumn(name = "article_id"), inverseJoinColumns = @JoinColumn(name = "interest_id"))
  private List<Interest> interests = new ArrayList<>();

  public Article(ArticleBaseDto articleBaseDto) {
    this.title = articleBaseDto.getTitle();
    this.summary = articleBaseDto.getSummary();
    this.link = articleBaseDto.getLink();
    this.source = articleBaseDto.getSource();
    this.date = articleBaseDto.getDate();
    this.createdAt = LocalDateTime.now();
    this.commentCount = articleBaseDto.getCommentCount();
  }
  public Article update(ArticleBaseDto articleUpdateDto) {
    if(articleUpdateDto.getTitle() != null) { this.title = articleUpdateDto.getTitle(); }
    if(articleUpdateDto.getSummary() != null) { this.summary = articleUpdateDto.getSummary(); }
    if(articleUpdateDto.getLink() != null) { this.link = articleUpdateDto.getLink(); }
    if(articleUpdateDto.getSource() != null) { this.source = articleUpdateDto.getSource(); }
    if(articleUpdateDto.getDate() != null) { this.date = articleUpdateDto.getDate(); }
    if(articleUpdateDto.getCommentCount() > 0) { this.commentCount = articleUpdateDto.getCommentCount(); }
    return this;
  }
  public void logicalDelete(LocalDateTime logicalDeleted) {
    this.deletedAt = logicalDeleted;
  }
  public boolean isNotLogicallyDeleted() {
    return this.getDeletedAt() == null;
  }


  @Override
  public String toString() {
    return "Article{" +
        "id=" + id +
        ", title='" + title + '\'' +
        ", summary='" + summary + '\'' +
        ", link='" + link + '\'' +
        ", source='" + source + '\'' +
        ", date=" + date +
        ", createdAt=" + createdAt +
        ", deletedAt=" + deletedAt +
        ", commentCount=" + commentCount +
        ", comments=" + comments +
        ", commentLikes=" + commentLikes +
        ", interests=" + interests +
        '}';
  }
}
