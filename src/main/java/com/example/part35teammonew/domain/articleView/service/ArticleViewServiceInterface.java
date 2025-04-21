package com.example.part35teammonew.domain.articleView.service;

import com.example.part35teammonew.domain.articleView.Dto.ArticleViewDto;
import java.util.UUID;

public interface ArticleViewServiceInterface {

  ArticleViewDto createArticleView(UUID articleId);

  boolean addReadUser(UUID articleId, UUID userId);

  Long countReadUser(UUID articleId);
}
