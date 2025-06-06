package com.example.part35teammonew.domain.article.repository;

import com.example.part35teammonew.domain.article.entity.Article;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<Article, UUID> {

  Article findByTitleAndDate(@NotNull String title, @NotNull LocalDateTime date);

  Optional<Article> findById(@NotNull UUID id);


  @Query("""
      SELECT a FROM Article a
      WHERE a.deletedAt IS NULL
        AND (:keyword IS NULL OR a.title LIKE :keyword)
        AND (:interestId IS NULL OR a.interestId = :interestId)
        AND (:from IS NULL OR a.date >= :from)
        AND (:to IS NULL OR a.date <= :to)
        AND (:sources IS NULL OR a.source IN :sources)
      """)
  Page<Article> searchByKeywordInterestSources(
      @Param("keyword") String keyword,
      @Param("interestId") UUID interestId,
      @Param("from") LocalDateTime from,
      @Param("to") LocalDateTime to,
      @Param("sources") List<String> sources,
      Pageable pageable
  );


  @Query("""
        SELECT a FROM Article a
        WHERE a.deletedAt IS NULL
          AND a.title LIKE :keyword
          AND a.date >= :from
          AND a.date <= :to
      """)
  Page<Article> searchByKeywordOnly(
      @Param("keyword") String keyword,
      @Param("from") LocalDateTime from,
      @Param("to") LocalDateTime to,
      Pageable pageable);

  @Query("""
        SELECT a FROM Article a
        WHERE a.deletedAt IS NULL
          AND a.date >= :from
          AND a.date <= :to
      """)
  Page<Article> searchByDateOnly(
      @Param("from") LocalDateTime from,
      @Param("to") LocalDateTime to,
      Pageable pageable);
  //
  @Query("""
    SELECT a FROM Article a
    WHERE a.deletedAt IS NULL
      AND a.title LIKE :keyword
      AND a.interestId = :interestId
      AND a.date >= :from
      AND a.date <= :to
""")
  Page<Article> searchByKeywordAndInterest(
      @Param("keyword") String keyword,
      @Param("interestId") UUID interestId,
      @Param("from") LocalDateTime from,
      @Param("to") LocalDateTime to,
      Pageable pageable
  );
  @Query("""
    SELECT a FROM Article a
    WHERE a.deletedAt IS NULL
      AND a.title LIKE :keyword
      AND a.source IN :sources
      AND a.date >= :from
      AND a.date <= :to
""")
  Page<Article> searchByKeywordAndSources(
      @Param("keyword") String keyword,
      @Param("from") LocalDateTime from,
      @Param("to") LocalDateTime to,
      @Param("sources") List<String> sources,
      Pageable pageable
  );
  @Query("""
    SELECT a FROM Article a
    WHERE a.deletedAt IS NULL
      AND a.interestId = :interestId
      AND a.source IN :sources
      AND a.date >= :from
      AND a.date <= :to
""")
  Page<Article> searchByInterestAndSources(
      @Param("interestId") UUID interestId,
      @Param("from") LocalDateTime from,
      @Param("to") LocalDateTime to,
      @Param("sources") List<String> sources,
      Pageable pageable
  );
  @Query("""
    SELECT a FROM Article a
    WHERE a.deletedAt IS NULL
      AND a.interestId = :interestId
      AND a.date >= :from
      AND a.date <= :to
""")
  Page<Article> searchByInterestOnly(
      @Param("interestId") UUID interestId,
      @Param("from") LocalDateTime from,
      @Param("to") LocalDateTime to,
      Pageable pageable
  );
  @Query("""
    SELECT a FROM Article a
    WHERE a.deletedAt IS NULL
      AND a.source IN :sources
      AND a.date >= :from
      AND a.date <= :to
""")
  Page<Article> searchBySourcesOnly(
      @Param("from") LocalDateTime from,
      @Param("to") LocalDateTime to,
      @Param("sources") List<String> sources,
      Pageable pageable
  );
}
