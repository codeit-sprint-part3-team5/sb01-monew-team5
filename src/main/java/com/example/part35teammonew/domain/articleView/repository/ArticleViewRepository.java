package com.example.part35teammonew.domain.articleView.repository;

import com.example.part35teammonew.domain.articleView.entity.ArticleView;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;


public interface ArticleViewRepository extends MongoRepository<ArticleView, ObjectId> {

  Optional<ArticleView> findByArticleId(UUID articleId);

  @Query("{}")
  List<ArticleView> findAllOrderByCountDesc(Pageable pageable);

}
