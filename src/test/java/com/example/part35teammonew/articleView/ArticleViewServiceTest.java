package com.example.part35teammonew.articleView;


import static org.assertj.core.api.Assertions.assertThat;

import com.example.part35teammonew.domain.articleView.entity.ArticleView;
import com.example.part35teammonew.domain.articleView.mapper.ArticleViewMapper;
import com.example.part35teammonew.domain.articleView.repository.ArticleViewRepository;
import com.example.part35teammonew.domain.articleView.service.ArticleViewServiceImp;
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
    articleId = UUID.randomUUID();
    userId = UUID.randomUUID();
    userId1 = UUID.randomUUID();
    userId3 = UUID.randomUUID();

    ArticleView articleView = ArticleView.setUpNewArticleView(articleId);
    articleViewRepository.save(articleView);
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


  @TestConfiguration
  static class MapperMockConfig {

    @Bean
    public ArticleViewMapper articleViewMapper() {
      return Mockito.mock(ArticleViewMapper.class);
    }
  }
}
