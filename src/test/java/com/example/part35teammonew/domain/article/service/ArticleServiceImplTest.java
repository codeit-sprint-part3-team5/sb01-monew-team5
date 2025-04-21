package com.example.part35teammonew.domain.article.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.part35teammonew.domain.article.dto.ArticleBaseDto;
import com.example.part35teammonew.domain.interest.entity.Interest;
import com.example.part35teammonew.domain.interest.repository.InterestRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import jdk.jfr.Name;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ArticleServiceImplTest {
  @Autowired
  private ArticleServiceImpl articleServiceImpl;
  @Autowired
  private InterestRepository interestRepository;

  private ArticleBaseDto createArticleBaseDto(String title) {
    ArticleBaseDto dto = new ArticleBaseDto(
        title,
        "summary",
        "link",
        "source",
        LocalDateTime.now()
    );
    return dto;
  }
  private ArticleBaseDto createArticleBaseDto(String title, String summary) {
    ArticleBaseDto dto = new ArticleBaseDto(
        title,
        summary,
        "link",
        "source",
        LocalDateTime.now()
    );
    return dto;
  }
  private ArticleBaseDto createArticleBaseDto(String title, String summary, String source, LocalDateTime dateTime) {
    ArticleBaseDto dto = new ArticleBaseDto(
        title,
        summary,
        "link",
        source,
        dateTime
    );
    return dto;
  }
  @Test
  @Name("기사 생성")
  void saveArticle() {
    ArticleBaseDto hello = createArticleBaseDto("hello");
    //
    UUID savedId = articleServiceImpl.save(hello);
    ArticleBaseDto articleBaseDto = articleServiceImpl.findById(savedId);
    //
    assertThat(savedId).isEqualTo(articleBaseDto.getId());
  }
  @Test
  @Name("기사 중복 생성")
  void DuplicatedArticle() {
    ArticleBaseDto hello = createArticleBaseDto("hello");
    UUID savedId = articleServiceImpl.save(hello);

    assertThatThrownBy(() -> articleServiceImpl.save(hello))
        .isInstanceOf(IllegalArgumentException.class) // 예외 타입
        .hasMessageContaining("중복 저장되었습니다."); // 예외 메시지 확인
  }
  @Test
  @Name("기사 삭제")
  void deleteArticle() {
    ArticleBaseDto hello = createArticleBaseDto("hello");
    UUID savedId = articleServiceImpl.save(hello);
    //
    articleServiceImpl.deletePhysical(savedId);
    //
    assertThatThrownBy(() -> articleServiceImpl.findById(savedId))
        .isInstanceOf(IllegalArgumentException.class) // 예외 타입
        .hasMessageContaining("해당 ID의 기사를 찾을 수 없습니다."); // 예외 메시지 확인
  }
  @Test
  @Name("기사가 없는데 삭제하려고 함")
  void deleteNotExistArticle() {
    assertThatThrownBy(() -> articleServiceImpl.deletePhysical(UUID.randomUUID()))
        .isInstanceOf(IllegalArgumentException.class) // 예외 타입
        .hasMessageContaining("해당 ID의 기사를 찾을 수 없습니다."); // 예외 메시지 확인
  }
  @Test
  @Name("기사 목록 테스트")
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
  @Name("제목과 요약 내용으로 기사 찾기")
  void findByTitleOrSummary(){
    //참고 : 타이틀과 기사 작성 시간이 같으면 중복
    ArticleBaseDto hello = createArticleBaseDto("hello1", "summary1");
    ArticleBaseDto hello2 = createArticleBaseDto("hello2", "summary1");
    ArticleBaseDto hello3 = createArticleBaseDto("hello3", "summary1");
    ArticleBaseDto hello4 = createArticleBaseDto("hello4", "summary1");
    ArticleBaseDto hello5 = createArticleBaseDto("hello5", "summary1");
    //
    articleServiceImpl.save(hello);    articleServiceImpl.save(hello2);
    articleServiceImpl.save(hello3);    articleServiceImpl.save(hello4);
    articleServiceImpl.save(hello5);
    //
    assertThat(articleServiceImpl.findByTitleOrSummary("ello", "summary").size()).isEqualTo(5);
    assertThat(articleServiceImpl.findByTitleOrSummary("hello5", "summary1").size()).isEqualTo(5);
    assertThat(articleServiceImpl.findByTitleOrSummary(null, "summary1").size()).isEqualTo(5);
    assertThat(articleServiceImpl.findByTitleOrSummary("ello4", null).size()).isEqualTo(1);
  }
  @Test
  @Name("작성 시간, 소스, 관심사로 기사 찾기")
  void findBySourceAndDateAndInterests(){

    ArticleBaseDto hello1 = createArticleBaseDto("Spring과 Java를 활용한 REST API 개발 실무 가이드", "summary1", "source1", LocalDateTime.now());
    ArticleBaseDto hello2 = createArticleBaseDto("Java 개발자를 위한 최신 Spring Security 가이드", "summary1", "source1", LocalDateTime.now());
    ArticleBaseDto hello3 = createArticleBaseDto("Spring 기반 마이크로서비스 아키텍처 개요", "summary1", "source1", LocalDateTime.now());
    ArticleBaseDto python1 = createArticleBaseDto("AI 모델 개발을 위한 파이썬 기초 튜토리얼", "summary1", "source1", LocalDateTime.now());
    ArticleBaseDto python2 = createArticleBaseDto("Python 입문자를 위한 머신러닝 시작 가이드", "summary1", "source1", LocalDateTime.now());
    ArticleBaseDto python3 = createArticleBaseDto("AI 시대, 파이썬이 여전히 인기인 이유", "summary1", "source1", LocalDateTime.now());
    ArticleBaseDto boot1 = createArticleBaseDto("Spring Boot로 빠르게 만드는 웹 애플리케이션 가이드", "summary1", "source1", LocalDateTime.now());
    ArticleBaseDto boot2 = createArticleBaseDto("Spring Boot 3.x 업그레이드 체크리스트", "summary1", "source1", LocalDateTime.now());
    ArticleBaseDto boot3 = createArticleBaseDto("부트캠프 출신 개발자의 Spring Boot 실전 후기", "summary1", "source1", LocalDateTime.now());

    Interest i1 = new Interest();
    i1.setName("Java Spring");
    i1.setKeywords("java,spring");
    interestRepository.save(i1);

    Interest i2 = new Interest();
    i2.setName("Python Basics");
    i2.setKeywords("python,AI");
    interestRepository.save(i2);

    Interest i3 = new Interest();
    i3.setName("Spring Boot");
    i3.setKeywords("boot,spring");
    interestRepository.save(i3);
    //
    articleServiceImpl.save(hello1);    articleServiceImpl.save(hello2);  articleServiceImpl.save(hello3);
    articleServiceImpl.save(python1);   articleServiceImpl.save(python2);   articleServiceImpl.save(python3);
    articleServiceImpl.save(boot1);   articleServiceImpl.save(boot2);   articleServiceImpl.save(boot3);
    //
    assertThat(articleServiceImpl.findBySourceAndDateAndInterests("source1", null, null).size()).isEqualTo(9);
    assertThat(articleServiceImpl.findBySourceAndDateAndInterests(null, String.valueOf(LocalDate.now()), null).size()).isEqualTo(9);
    assertThat(articleServiceImpl.findBySourceAndDateAndInterests("source1", String.valueOf(LocalDate.now()), null).size()).isEqualTo(9);
    assertThat(articleServiceImpl.findBySourceAndDateAndInterests(null, String.valueOf(LocalDate.now()), "Java").size()).isEqualTo(2);
    assertThat(articleServiceImpl.findBySourceAndDateAndInterests(null, String.valueOf(LocalDate.now()), "java").size()).isEqualTo(2);
    assertThat(articleServiceImpl.findBySourceAndDateAndInterests(null, String.valueOf(LocalDate.now()), "가이드").size()).isEqualTo(4);

    assertThatThrownBy(() -> articleServiceImpl.findBySourceAndDateAndInterests(null, null, null))
        .isInstanceOf(IllegalArgumentException.class) // 예외 타입
        .hasMessageContaining("소스와 날짜 중 하나의 파라미터는 채워져야 합니다."); // 예외 메시지 확인
  }

  

}