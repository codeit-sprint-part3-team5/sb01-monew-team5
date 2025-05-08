package com.example.part35teammonew.domain.article.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.part35teammonew.domain.article.dto.ArticleBaseDto;
import com.example.part35teammonew.domain.article.dto.ArticlesResponse;
import com.example.part35teammonew.exeception.RestApiException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ArticleServiceImplTest {

  @Autowired
  private ArticleServiceImpl articleServiceImpl;

  private ArticleBaseDto createArticleBaseDto(String title) {
    ArticleBaseDto dto = new ArticleBaseDto(null, title, "summary", "link", "source",
        LocalDateTime.now(),null, 0, 0L);
    return dto;
  }

  private ArticleBaseDto createArticleBaseDto(String title, String summary) {
    ArticleBaseDto dto = new ArticleBaseDto(title, summary, "link", "source", LocalDateTime.now(), 0);
    return dto;
  }

  private ArticleBaseDto createArticleBaseDto(String title, String summary, String[] sources,
      LocalDateTime dateTime) {
    ArticleBaseDto dto = new ArticleBaseDto(title, summary, "link", sources[0], dateTime, 0);
    return dto;
  }

  @Test
  @DisplayName("기사 생성")
  void saveArticle() {
    ArticleBaseDto hello = createArticleBaseDto("hello");
    //
    UUID savedId = articleServiceImpl.save(hello);
    ArticleBaseDto articleBaseDto = articleServiceImpl.findById(savedId);
    //
    assertThat(savedId).isEqualTo(articleBaseDto.getId());
  }

  @Test
  @DisplayName("기사 중복 생성")
  void DuplicatedArticle() {
    ArticleBaseDto hello = createArticleBaseDto("hello");
    UUID savedId = articleServiceImpl.save(hello);

    assertThatThrownBy(() -> articleServiceImpl.save(hello)).isInstanceOf(
        RestApiException.class); // 예외 타입
  }

  @Test
  @DisplayName("기사 삭제")
  void deleteArticle() {
    ArticleBaseDto hello = createArticleBaseDto("hello");
    UUID savedId = articleServiceImpl.save(hello);
    //
    try {
      articleServiceImpl.deletePhysical(savedId);
    }catch (RestApiException e) {
      System.out.println("delete physical failed");
    }
    //
    assertThatThrownBy(() -> articleServiceImpl.findById(savedId)).isInstanceOf(
        RestApiException.class); // 예외 타입
  }

  @Test
  @DisplayName("기사가 없는데 삭제하려고 함")
  void deleteNotExistArticle() {
    assertThatThrownBy(() -> articleServiceImpl.deletePhysical(UUID.randomUUID())).isInstanceOf(
        RestApiException.class); // 예외 타입
  }

  @Test
  @DisplayName("기사 목록 테스트")
  void savedArticles() {
    ArticleBaseDto hello = createArticleBaseDto("hello");
    ArticleBaseDto hello2 = createArticleBaseDto("hello2");
    ArticleBaseDto hello3 = createArticleBaseDto("hello3");
    //
    UUID savedId = articleServiceImpl.save(hello);
    UUID savedId2 = articleServiceImpl.save(hello2);
    UUID savedId3 = articleServiceImpl.save(hello3);
    //
    assertThat(articleServiceImpl.findAll().size()).isEqualTo(3);
  }

  @Test
  @DisplayName("논리적 삭제된 기사는 조회되지 않아야 한다")
  void logicallyDeletedArticleShouldNotBeFound() {
    // given
    ArticleBaseDto article = createArticleBaseDto("삭제될 기사");
    UUID savedId = articleServiceImpl.save(article);

    // when: 논리적 삭제 수행
    articleServiceImpl.deleteLogical(savedId);

    // then: findById 시 예외 발생해야 함
    assertThatThrownBy(() -> articleServiceImpl.findById(savedId)).isInstanceOf(
        RestApiException.class);

    // and: findAll 결과에도 포함되지 않아야 함
    List<ArticleBaseDto> articles = articleServiceImpl.findAll();
    assertThat(articles.stream().noneMatch(a -> savedId.equals(a.getId()))).isTrue();
  }

  @Test
  @DisplayName("source, date, interests 조합에 따른 기사 조회 테스트 (논리삭제 포함)")
  void findBySourceDateInterests_withLogicalDeleteCheck() {
    String[] source = {"source1"};
    LocalDate now = LocalDate.now();
    LocalDate yesterday = now.minusDays(1);

    String startDate = yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    String endDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

    ArticleBaseDto article1 = createArticleBaseDto("Spring Boot Guide", "내용1", source, now.atTime(11,50));
    ArticleBaseDto article2 = createArticleBaseDto("Python ML Intro", "내용2", source, now.atTime(11,50));
    ArticleBaseDto article3 = createArticleBaseDto("No match title", "내용3", source, now.atTime(11,50));

    UUID id1 = articleServiceImpl.save(article1);
    UUID id2 = articleServiceImpl.save(article2);
    UUID id3 = articleServiceImpl.save(article3);

    // when: 일부 기사 논리적 삭제
    articleServiceImpl.deleteLogical(id3);

    // then
    ArticlesResponse pageArticle = articleServiceImpl.getPageArticle(null, null, null,
        LocalDateTime.now().toString(), null, "publishDate", "DESC", "0",
        null, 50, null);
    System.out.println("pageArticle = " + pageArticle);

  }

  @Test
  @DisplayName("source, date에 따른 기사 조회 테스트 (논리삭제 포함)")
  void findBySourceDateInterests_withLogicalDeleteCheck2() {
    String[] source = {"source1"};
    LocalDateTime now = LocalDate.now().atStartOfDay();
    LocalDateTime yesterday = now.minusDays(1);
    LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);

    String startDate = yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    String endDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    String tomorrowDate = tomorrow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

    System.out.println("now = " + now);
    System.out.println("yesterday = " + yesterday);
    System.out.println("tomorrow = " + tomorrow);
    System.out.println("startDate = " + startDate);
    System.out.println("endDate = " + endDate);
    System.out.println("tomorrowDate = " + tomorrowDate);

    ArticleBaseDto article1 = createArticleBaseDto("Spring Boot Guide", "내용1", source, now);
    ArticleBaseDto article2 = createArticleBaseDto("Python ML Intro", "내용2", source, now);
    ArticleBaseDto article3 = createArticleBaseDto("No match title", "내용3", source, now);

    UUID id1 = articleServiceImpl.save(article1);
    UUID id2 = articleServiceImpl.save(article2);
    UUID id3 = articleServiceImpl.save(article3);

    // when: 일부 기사 논리적 삭제
    articleServiceImpl.deleteLogical(id3);

    //then
    assertThat(articleServiceImpl.findAll().size()).isEqualTo(2);

  }
  @Test
  @DisplayName("다양한 기사 데이터를 바탕으로 모든 조합을 테스트")
  void testFindBySourceAndDateAndInterests_withRealisticArticles() {
    String[] source = {"thelec"};
    String[] altSource = {"zdnet"};
    String interestKeyword = "AI";
    String noMatchKeyword = "quantum";

    LocalDate today = LocalDate.now();
    LocalDate yesterday = today.minusDays(1);
    LocalDate twoDaysAgo = today.minusDays(2);

    // 다양한 날짜, 제목, source로 기사 저장 //10개
    List<ArticleBaseDto> articles = List.of(
        createArticleBaseDto("AI takes over development", "내용", source, twoDaysAgo.atTime(9, 0)),
        createArticleBaseDto("Spring Boot Guide", "내용", source, yesterday.atTime(10, 0)),
        createArticleBaseDto("Python AI Tutorial", "내용", source, yesterday.atTime(11, 0)),
        createArticleBaseDto("Cloud computing trends", "내용", altSource, yesterday.atTime(12, 0)),
        createArticleBaseDto("AI for Healthcare", "내용", source, today.atTime(8, 30)),
        createArticleBaseDto("Cybersecurity update", "내용", source, today.atTime(14, 15)),
        createArticleBaseDto("AI in Automotive", "내용", altSource, today.atTime(13, 0)),
        createArticleBaseDto("Legacy Systems", "내용", source, today.atTime(17, 45)),
        createArticleBaseDto("No match content", "내용", source, today.atTime(20, 0)),
        createArticleBaseDto("Spring Security Intro", "내용", source, today.atTime(22, 0))
    );

    // 저장 후 ID 보관
    List<UUID> articleIds = articles.stream()
        .map(articleServiceImpl::save)
        .toList();

    // 하나는 논리 삭제
    articleServiceImpl.deleteLogical(articleIds.get(8)); // "No match content"

    //이틀 전 날짜로 개수 찾기
    assertThat(
        articleServiceImpl.getPageArticle(null, null, null, twoDaysAgo.atStartOfDay().toString(), null, "publishDate", "ASC", "0", null, 50, null
        ).getContent().size()).isEqualTo(9);
    //어제 날짜로 개수 찾기
    assertThat(
        articleServiceImpl.getPageArticle(null, null, null, yesterday.atStartOfDay().toString(), null, "publishDate", "ASC", "0", null, 50, null
        ).getContent().size()).isEqualTo(8);
    //키워드 찾기
    assertThat(
        articleServiceImpl.getPageArticle(interestKeyword, null, null, yesterday.atStartOfDay().toString(), null, "publishDate", "ASC", "0", null, 50, null
        ).getContent().size()).isEqualTo(3);


    //source[] 로 찾기 //이거 NAVER 처리로 묶음 ( 6 -> 8 )
    assertThat(
        articleServiceImpl.getPageArticle(null, null, new String[]{"thelec"}, yesterday.atStartOfDay().toString(), null, "publishDate", "DESC", "0", null, 50, null
        ).getContent().size()).isEqualTo(8);

  }

  @Test
  @DisplayName("커서 기반 페이징 - DATE 및 COMMENT_COUNT 정렬 조건 테스트")
  void testCursorPaginationWithoutViewCount() {
    // Given
    String source = "codeit";
    LocalDateTime baseTime =LocalDate.now().atTime(10, 0);
    int total = 100;

    for (int i = 0; i < total; i++) {
      ArticleBaseDto articleBaseDto = new ArticleBaseDto(
          null, "Spring" + i, "내용" + i, "link", source,
          baseTime.minusHours(i), null, i, (long) i
      );
      articleServiceImpl.save(articleBaseDto);
    }

    // When - 첫 페이지 요청
    ArticlesResponse responsePage1 = articleServiceImpl.getPageArticle(
        null, null, new String[]{"codeit"},
        LocalDate.now().atStartOfDay().toString(), null,
        "publishDate", "DESC",
        "0", null,
        10, null
    );
    System.out.println("responsePage1.getContent() = " + responsePage1.getContent());

    // Then - 검증
    assertEquals(10, responsePage1.getContent().size());
    assertEquals("Spring0", responsePage1.getContent().get(0).getTitle()); // 가장 최신
    assertEquals("Spring9", responsePage1.getContent().get(9).getTitle()); // 가장 오래된 (10개 중에서)

    // When - 두 번째 페이지 요청
    ArticlesResponse responsePage2 = articleServiceImpl.getPageArticle(
        null, null, null,
        LocalDate.now().minusDays(1).atStartOfDay().toString(), null,
        "publishDate", "DESC",
        "1", null,
        10, null
    );

    // Then - 검증
    assertEquals(10, responsePage2.getContent().size());
    assertEquals("Spring10", responsePage2.getContent().get(0).getTitle());
    assertEquals("Spring19", responsePage2.getContent().get(9).getTitle());

    // When - commentCount 기준 정렬 테스트
    ArticlesResponse commentCountSorted = articleServiceImpl.getPageArticle(
        null, null, null,
        LocalDate.now().minusDays(1).atStartOfDay().toString(), null,
        "commentCount", "DESC",
        "0", null,
        10, null
    );

    // Then - commentCount가 높은 순서로 정렬되어 있는지 확인
    List<ArticleBaseDto> sortedByCommentCount = commentCountSorted.getContent();
    for (int i = 1; i < sortedByCommentCount.size(); i++) {
      assertTrue(
          sortedByCommentCount.get(i - 1).getCommentCount()
              >= sortedByCommentCount.get(i).getCommentCount()
      );
    }
  }


}