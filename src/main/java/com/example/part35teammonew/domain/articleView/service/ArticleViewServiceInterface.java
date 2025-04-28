package com.example.part35teammonew.domain.articleView.service;

import com.example.part35teammonew.domain.article.entity.Direction;
import com.example.part35teammonew.domain.articleView.Dto.ArticleViewDto;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface ArticleViewServiceInterface {

  ArticleViewDto createArticleView(UUID articleId);

  boolean addReadUser(UUID userId);

  Long countReadUser();

  List<UUID> getSortByViewCountPageNation(Long cursor, Pageable pageable, String direction);
}
