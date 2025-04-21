package com.example.part35teammonew.domain.article.repository;

import com.example.part35teammonew.domain.article.dto.ArticleBaseDto;
import com.example.part35teammonew.domain.article.entity.Article;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<Article, UUID> {

  Article findByTitleAndDate(@NotNull String title, @NotNull LocalDateTime date);

  @Query("SELECT a From Article a where a.title LIKE %:title% OR a.summary LIKE %:summary%")
  List<Article> findByTitleAndSummary(String title, String summary);

  @Query("SELECT a From Article a where a.summary LIKE %:summary%")
  List<Article> findBySummary(String summary);

  @Query("SELECT a From Article a where a.title LIKE %:title%")
  List<Article> findByTitle(String title);

  @Query("""
  SELECT a FROM Article a
  WHERE a.source = :source AND FUNCTION('DATE', a.date) = :date
""")
  List<Article> findBySourceAndDate(
      @Param("source") String source,
      @Param("date") LocalDate date
  );

  List<Article> findBySource(@NotNull String source);

  @Query("select a from Article a where FUNCTION('DATE', a.date) = :date")
  List<Article> findByDate(@Param("date") LocalDate date);

}
