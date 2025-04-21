package com.example.part35teammonew.domain.articleView.service;

import static com.example.part35teammonew.domain.articleView.entity.ArticleView.setUpNewArticleView;

import com.example.part35teammonew.domain.articleView.Dto.ArticleViewDto;
import com.example.part35teammonew.domain.articleView.entity.ArticleView;
import com.example.part35teammonew.domain.articleView.mapper.ArticleViewMapper;
import com.example.part35teammonew.domain.articleView.repository.ArticleViewRepository;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;

public class ArticleViewServiceImp implements ArticleViewServiceInterface {

  private final ArticleViewRepository articleViewRepository;
  private final ArticleViewMapper articleViewMapper;

  public ArticleViewServiceImp(
      @Autowired ArticleViewRepository articleViewRepository,
      @Autowired ArticleViewMapper articleViewMapper) {
    this.articleViewRepository = articleViewRepository;
    this.articleViewMapper = articleViewMapper;
  }

  @Override
  public ArticleViewDto createArticleView(UUID articleId) {
    ArticleView articleView = setUpNewArticleView(articleId);
    articleViewRepository.save(articleView);
    return articleViewMapper.toDto(articleView);
  }

  @Override
  public boolean addReadUser(UUID articleId, UUID userId) {
    return articleViewRepository.findByArticleId(articleId)
        .map(articleView -> {
          articleView.addNewReader(userId);
          articleViewRepository.save(articleView);
          return true;
        })
        .orElse(false);
  }

  @Override
  public Long countReadUser(UUID articleId) {
    return articleViewRepository.findByArticleId(articleId)
        .map(ArticleView::getCount)
        .orElse(0L);
  }
}
