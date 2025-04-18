package com.example.part35teammonew.domain.ArticleView.repository;

import com.example.part35teammonew.domain.ArticleView.entity.ArticleView;
import java.util.Optional;
import java.util.UUID;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ArticleViewRepository extends MongoRepository<ArticleView, ObjectId> {

  Optional<ArticleView> findByArticleId(UUID articleId);

}
