package com.example.part35teammonew.domain.ArticleView.entity;

import java.math.BigInteger;
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
  private BigInteger count;


  @Builder
  private ArticleView(UUID articleId) {
    this.articleId = articleId;
    this.readUserIds = new HashSet<>();
    this.count = new BigInteger("0");
  }

  public static ArticleView setUpNewArticleView(UUID articleId) {
    return ArticleView.builder()
        .articleId(articleId)
        .build();
  }

  public void addNewReader(UUID readerId) {
    if (!readUserIds.contains(readerId)) {
      this.count = count.add(new BigInteger("1"));
      readUserIds.add(readerId);
    }
  }

}
