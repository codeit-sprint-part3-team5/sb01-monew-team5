package com.example.part35teammonew.domain.articleView.service;

import com.example.part35teammonew.domain.articleView.Dto.ArticleViewDto;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface ArticleViewServiceInterface {

  ArticleViewDto createArticleView(UUID articleId);

  boolean addReadUser(UUID articleId, UUID userId);

  Long countReadUser(UUID articleId);

  List<UUID> getSortByVewCountPageNation(Long cursor, Pageable pageable, String direction);//하기
}
