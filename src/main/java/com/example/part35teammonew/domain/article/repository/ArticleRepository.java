package com.example.part35teammonew.domain.article.repository;

import com.example.part35teammonew.domain.article.entity.Article;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<Article, UUID> {

  Article findByTitleAndDate(@NotNull String title, @NotNull LocalDateTime date);
}
