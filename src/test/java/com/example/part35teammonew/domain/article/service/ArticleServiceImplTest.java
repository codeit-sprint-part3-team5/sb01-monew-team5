package com.example.part35teammonew.domain.article.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.part35teammonew.domain.article.dto.ArticleBaseDto;
import com.example.part35teammonew.domain.interest.entity.Interest;
import com.example.part35teammonew.domain.interest.repository.InterestRepository;
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

  private ArticleBaseDto createSampleArticle(String title) {
    ArticleBaseDto dto = new ArticleBaseDto(
        title,
        "summary",
        "link",
        "source",
        LocalDateTime.now()
    );
    return dto;
  }
  private ArticleBaseDto createTitleAndSummaryArticle(String title, String summary) {
    ArticleBaseDto dto = new ArticleBaseDto(
        title,
        summary,
        "link",
        "source",
        LocalDateTime.now()
    );
    return dto;
  }
  @Test
  @Name("기사 생성")
  void saveArticle() {
    ArticleBaseDto hello = createSampleArticle("hello");
    //
    UUID savedId = articleServiceImpl.save(hello);
    ArticleBaseDto articleBaseDto = articleServiceImpl.findById(savedId);
    //
    assertThat(savedId).isEqualTo(articleBaseDto.getId());
  }
  @Test
  @Name("기사 중복 생성")
  void DuplicatedArticle() {
    ArticleBaseDto hello = createSampleArticle("hello");
    UUID savedId = articleServiceImpl.save(hello);

    assertThatThrownBy(() -> articleServiceImpl.save(hello))
        .isInstanceOf(IllegalArgumentException.class) // 예외 타입
        .hasMessageContaining("중복 저장되었습니다."); // 예외 메시지 확인
  }
  @Test
  @Name("기사 삭제")
  void deleteArticle() {
    ArticleBaseDto hello = createSampleArticle("hello");
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
    ArticleBaseDto hello = createSampleArticle("hello");
    ArticleBaseDto hello2 = createSampleArticle("hello2");
    ArticleBaseDto hello3 = createSampleArticle("hello3");
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
    ArticleBaseDto hello = createTitleAndSummaryArticle("hello1", "summary1");
    ArticleBaseDto hello2 = createTitleAndSummaryArticle("hello2", "summary1");
    ArticleBaseDto hello3 = createTitleAndSummaryArticle("hello3", "summary1");
    ArticleBaseDto hello4 = createTitleAndSummaryArticle("hello4", "summary1");
    ArticleBaseDto hello5 = createTitleAndSummaryArticle("hello5", "summary1");
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

    ArticleBaseDto hello = createTitleAndSummaryArticle("hello1", "summary1");
    ArticleBaseDto hello2 = createTitleAndSummaryArticle("hello2", "summary1");

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
    articleServiceImpl.save(hello);
    articleServiceImpl.save(hello2);


    //
    assertThat(articleServiceImpl.findByTitleOrSummary("ello", "summary").size()).isEqualTo(5);
    assertThat(articleServiceImpl.findByTitleOrSummary("hello5", "summary1").size()).isEqualTo(5);
    assertThat(articleServiceImpl.findByTitleOrSummary(null, "summary1").size()).isEqualTo(5);
    assertThat(articleServiceImpl.findByTitleOrSummary("ello4", null).size()).isEqualTo(1);
  }

  

}