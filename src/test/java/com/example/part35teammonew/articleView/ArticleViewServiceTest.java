package com.example.part35teammonew.articleView;


import static org.assertj.core.api.Assertions.assertThat;

import com.example.part35teammonew.domain.articleView.entity.ArticleView;
import com.example.part35teammonew.domain.articleView.mapper.ArticleViewMapper;
import com.example.part35teammonew.domain.articleView.repository.ArticleViewRepository;
import com.example.part35teammonew.domain.articleView.service.ArticleViewServiceImp;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;

@DataMongoTest
@Import({ArticleViewServiceImp.class, ArticleViewServiceTest.MapperMockConfig.class})
class ArticleViewServiceTest {

  @Autowired
  private ArticleViewServiceImp articleViewService;

  @Autowired
  private ArticleViewRepository articleViewRepository;

  private UUID articleId;
  private UUID userId;
  private UUID userId1;
  private UUID userId3;

  @BeforeEach
  void setUp() {
    articleViewRepository.deleteAll();
    articleId = UUID.randomUUID();
    userId = UUID.randomUUID();
    userId1 = UUID.randomUUID();
    userId3 = UUID.randomUUID();

    ArticleView articleView = ArticleView.setUpNewArticleView(articleId);
    articleViewRepository.save(articleView);
  }

  @TestConfiguration
  static class MapperMockConfig {

    @Bean
    public ArticleViewMapper articleViewMapper() {
      return Mockito.mock(ArticleViewMapper.class);
    }
  }

  @Test
  @DisplayName("addReadUser 호출")
  void addReadUser_success() {
    boolean result = articleViewService.addReadUser(articleId, userId);

    ArticleView updated = articleViewRepository.findByArticleId(articleId).get();

    assertThat(result).isTrue();
    assertThat(updated.getReadUserIds()).contains(userId);
  }

  @Test
  @DisplayName("읽은 유저수 반환")
  void countReadUser_success() {
    articleViewService.addReadUser(articleId, userId);
    articleViewService.addReadUser(articleId, userId1);
    long count = articleViewService.countReadUser(articleId);

    assertThat(count).isEqualTo(2);
  }

  @Test
  @DisplayName("읽은 유저수 반환")
  void duplicateReadUser_success() {
    articleViewService.addReadUser(articleId, userId);
    articleViewService.addReadUser(articleId, userId1);
    articleViewService.addReadUser(articleId, userId1);
    articleViewService.addReadUser(articleId, userId3);
    long count = articleViewService.countReadUser(articleId);

    assertThat(count).isEqualTo(3);
  }

  @Test
  @DisplayName("조회수 동일할 때 ObjectId 기준 내림차순 정렬 검증")
  void getArticles_sameCount_sortByObjectIdDescending() {
    UUID articleId1 = UUID.randomUUID();
    UUID articleId2 = UUID.randomUUID();

    ArticleView v1 = ArticleView.setUpNewArticleView(articleId1);
    v1.addNewReader(UUID.randomUUID());

    try {
      Thread.sleep(10);
    } catch (InterruptedException ignored) {
    }

    ArticleView v2 = ArticleView.setUpNewArticleView(articleId2);
    v2.addNewReader(UUID.randomUUID());

    articleViewRepository.saveAll(List.of(v1, v2));

    List<UUID> actual = articleViewService.getSortByVewCountPageNation(null, Pageable.ofSize(2),
        "next");

    List<ArticleView> sorted = articleViewRepository.findAll().stream()
        .filter(v -> v.getCount().equals(1L))
        .sorted(
            (a, b) -> b.getId().toHexString().compareTo(a.getId().toHexString()))
        .toList();

    List<UUID> expected = sorted.stream()
        .map(ArticleView::getArticleId)
        .toList();
    assertThat(actual).containsExactlyElementsOf(expected);
  }

  @Test
  @DisplayName("조회수 내림차순 정렬 + limit 적용 확인")
  void getArticles_sortedByCount_limitApplied() {
    articleViewRepository.deleteAll();

    UUID a1 = UUID.randomUUID();
    UUID a2 = UUID.randomUUID();
    UUID a3 = UUID.randomUUID();

    ArticleView v1 = ArticleView.setUpNewArticleView(a1);
    v1.addNewReader(UUID.randomUUID()); // count = 1

    ArticleView v2 = ArticleView.setUpNewArticleView(a2);
    v2.addNewReader(UUID.randomUUID());
    v2.addNewReader(UUID.randomUUID()); // count = 2

    ArticleView v3 = ArticleView.setUpNewArticleView(a3);
    // count = 0

    articleViewRepository.saveAll(List.of(v1, v2, v3));

    List<UUID> result = articleViewService.getSortByVewCountPageNation(null, Pageable.ofSize(2),
        "next");

    assertThat(result).hasSize(2);
    assertThat(result).containsExactly(v2.getArticleId(), v1.getArticleId());
  }

  @Test
  @DisplayName("커서 값보다 count가 작은 항목만 반환되는지")
  void getArticles_filteredByCursorCount() {
    articleViewRepository.deleteAll();

    UUID a1 = UUID.randomUUID();
    UUID a2 = UUID.randomUUID();
    UUID a3 = UUID.randomUUID();

    ArticleView v1 = ArticleView.setUpNewArticleView(a1);
    v1.addNewReader(UUID.randomUUID()); // count = 1

    ArticleView v2 = ArticleView.setUpNewArticleView(a2);
    v2.addNewReader(UUID.randomUUID());
    v2.addNewReader(UUID.randomUUID()); // count = 2

    ArticleView v3 = ArticleView.setUpNewArticleView(a3);
    // count = 0

    articleViewRepository.saveAll(List.of(v1, v2, v3));

    // 커서 count = 1 ⇒ 0인 항목만 나와야 함
    List<UUID> result = articleViewService.getSortByVewCountPageNation(1L, Pageable.ofSize(5),
        "desc");

    assertThat(result).containsExactly(v3.getArticleId());
  }

  @Test
  @DisplayName("조회수 오름차순 정렬 + limit 적용 확인")
  void getArticles_sortedByCountAscending() {
    articleViewRepository.deleteAll();

    UUID a1 = UUID.randomUUID();
    UUID a2 = UUID.randomUUID();
    UUID a3 = UUID.randomUUID();

    ArticleView v1 = ArticleView.setUpNewArticleView(a1);
    v1.addNewReader(UUID.randomUUID()); // count = 1

    ArticleView v2 = ArticleView.setUpNewArticleView(a2);
    v2.addNewReader(UUID.randomUUID());
    v2.addNewReader(UUID.randomUUID()); // count = 2

    ArticleView v3 = ArticleView.setUpNewArticleView(a3);
    // count = 0

    articleViewRepository.saveAll(List.of(v1, v2, v3));

    // 실제 결과
    List<UUID> result = articleViewService.getSortByVewCountPageNation(null, Pageable.ofSize(3),
        "asc");

    // 저장된 엔티티들을 count 오름차순, id 오름차순으로 정렬하여 기대값 생성
    List<UUID> expected = articleViewRepository.findAll().stream()
        .sorted((x, y) -> {
          int cmp = Long.compare(x.getCount(), y.getCount());
          if (cmp != 0) {
            return cmp;
          }
          return x.getId().toHexString().compareTo(y.getId().toHexString());
        })
        .map(ArticleView::getArticleId)
        .limit(3)
        .toList();

    assertThat(result).containsExactlyElementsOf(expected);
  }
}
