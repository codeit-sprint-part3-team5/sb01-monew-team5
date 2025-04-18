package com.example.part35teammonew.domain.ArticleView.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ArticleView")
@Getter
public class ArticleView {

  @Id
  private ObjectId id;

  private final UUID articleId;
  private Set<UUID> readUserIds;


  @Builder
  private ArticleView(UUID articleId) {
    this.articleId = articleId;
    this.readUserIds = new HashSet<>();
  }

  public static ArticleView setUpNewArticleView(UUID articleId) {
    return ArticleView.builder()
        .articleId(articleId)
        .build();
  }

  public void addNewReader(UUID readerId) {
    if (!readUserIds.contains(readerId)) {
      readUserIds.add(readerId);
    }
  }

}
