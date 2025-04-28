package com.example.part35teammonew.domain.articleView.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ArticleView")
@CompoundIndex(name = "count_id_desc_idx", def = "{'count': -1, '_id': -1}")//페이지 네이션 애들로 인덱스 삼음
@Getter
public class ArticleView {

  @Id
  private ObjectId id;
  private final UUID articleId;
  private Long count;
  private Set<UUID> readUserIds;


  @Builder
  private ArticleView(UUID articleId) {
    this.articleId = articleId;
    this.readUserIds = new HashSet<>();
    count = 0L;
  }

  public static ArticleView setUpNewArticleView(UUID articleId) {
    return ArticleView.builder()
        .articleId(articleId)
        .build();
  }

  public void addNewReader(UUID readerId) {
    if (!readUserIds.contains(readerId)) {
      readUserIds.add(readerId);
      count++;
    }
  }

}
