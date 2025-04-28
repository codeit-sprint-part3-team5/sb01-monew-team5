package com.example.part35teammonew.domain.article.repository;

import com.example.part35teammonew.domain.article.dto.ArticleBaseDto;
import com.example.part35teammonew.domain.article.entity.Article;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<Article, UUID> {
  Article findByTitleAndDate(@NotNull String title, @NotNull LocalDateTime date);

  @Query("SELECT a From Article a where a.title LIKE %:title% OR a.summary LIKE %:summary% AND a.deletedAt IS NULL")
  List<Article> findByTitleAndSummary(String title, String summary);

  @Query("SELECT a From Article a where a.summary LIKE %:summary% AND a.deletedAt IS NULL")
  List<Article> findBySummary(String summary);

  @Query("SELECT a From Article a where a.title LIKE %:title% AND a.deletedAt IS NULL")
  List<Article> findByTitle(String title);

  @Query("SELECT a FROM Article a WHERE a.date BETWEEN :startDate AND :endDate AND a.deletedAt IS NULL")
  List<Article> findByStartEndDate(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

  @Query("SELECT a FROM Article a WHERE a.source = :source AND a.date BETWEEN :startDate AND :endDate AND a.deletedAt IS NULL")
  List<Article> findBySourceAndDate(
      @Param("source") String source,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate
  );

  List<Article> findBySource(@NotNull String source);

  @Query("SELECT a FROM Article a WHERE a.date > :cursor AND a.deletedAt IS NULL ORDER BY a.date ASC")
  List<Article> findByDateCursorAsc(@Param("cursor") LocalDateTime cursor, Pageable pageable);
  @Query("SELECT a FROM Article a WHERE a.date < :cursor AND a.deletedAt IS NULL ORDER BY a.date DESC ")
  List<Article> findByDateCursorDesc(@Param("cursor") LocalDateTime cursor, Pageable pageable);

  @Query("SELECT a FROM Article a  WHERE a.commentCount > :cursor AND a.deletedAt IS NULL ORDER BY a.commentCount ASC")
  List<Article> findByCommentCursorAsc(@Param("cursor") int cursor, Pageable pageable);
  @Query("SELECT a FROM Article a  WHERE  a.commentCount < :cursor AND a.deletedAt IS NULL ORDER BY a.commentCount DESC")
  List<Article> findByCommentCursorDesc(@Param("cursor") int cursor, Pageable pageable);


}
