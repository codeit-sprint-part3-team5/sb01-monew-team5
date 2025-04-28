package com.example.part35teammonew.domain.articleView.service;

import com.example.part35teammonew.domain.article.entity.Direction;
import com.example.part35teammonew.domain.articleView.Dto.ArticleViewDto;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ArticleViewService implements ArticleViewServiceInterface {

  @Override
  public ArticleViewDto createArticleView(UUID articleId) {
    return null;
  }

  @Override
  public boolean addReadUser(UUID userId) {
    return false;
  }

  @Override
  public Long countReadUser() {
    return 0L;
  }

  @Override
  public List<UUID> getSortByViewCountPageNation(Long cursor, Pageable pageable, String direction) {
    return List.of();
  }

}
