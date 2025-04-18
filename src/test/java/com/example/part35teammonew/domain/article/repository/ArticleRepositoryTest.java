package com.example.part35teammonew.domain.article.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.example.part35teammonew.domain.article.dto.ArticleCreateDto;
import com.example.part35teammonew.domain.article.entity.Article;
import java.time.Instant;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ArticleRepositoryTest {
  @Autowired
  ArticleRepository articleRepository;
  @Test
  public void save(){
    ArticleCreateDto articleCreateDto = new ArticleCreateDto("title", "summary","link","source",
        Instant.now());
    Article article = new Article(articleCreateDto);
    //
    articleRepository.save(article);
    //
    Assertions.assertThat(article.getId()).isNotNull();
    Assertions.assertThat(article.getTitle()).isEqualTo("title");

  }
}