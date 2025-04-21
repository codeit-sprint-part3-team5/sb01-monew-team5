package com.example.part35teammonew.domain.article.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.part35teammonew.domain.article.dto.ArticleBaseDto;
import java.time.LocalDateTime;
import java.util.UUID;
import jdk.jfr.Name;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ArticleServiceTest {
  @Autowired
  private ArticleService articleService;
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
  @Test
  @Name("기사 생성")
  void saveArticle() {
    ArticleBaseDto hello = createSampleArticle("hello");
    //
    UUID savedId = articleService.save(hello);
    ArticleBaseDto articleBaseDto = articleService.findById(savedId);
    //
    assertThat(savedId).isEqualTo(articleBaseDto.getId());
  }
  @Test
  @Name("기사 중복 생성")
  void DuplicatedArticle() {
    ArticleBaseDto hello = createSampleArticle("hello");
    UUID savedId = articleService.save(hello);

    assertThatThrownBy(() -> articleService.save(hello))
        .isInstanceOf(IllegalArgumentException.class) // 예외 타입
        .hasMessageContaining("중복 저장되었습니다."); // 예외 메시지 확인
  }
  @Test
  @Name("기사 삭제")
  void deleteArticle() {
    ArticleBaseDto hello = createSampleArticle("hello");
    UUID savedId = articleService.save(hello);
    //
    articleService.delete(savedId);
    //
    assertThatThrownBy(() -> articleService.findById(savedId))
        .isInstanceOf(IllegalArgumentException.class) // 예외 타입
        .hasMessageContaining("해당 ID의 기사를 찾을 수 없습니다."); // 예외 메시지 확인
  }
  @Test
  @Name("기사가 없는데 삭제하려고 함")
  void deleteNotExistArticle() {
    assertThatThrownBy(() -> articleService.delete(UUID.randomUUID()))
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
    UUID savedId = articleService.save(hello);
    UUID savedId2 = articleService.save(hello2);
    UUID savedId3 = articleService.save(hello3);
    //
    assertThat(articleService.findAll().size()).isEqualTo(3);
  }

  

}