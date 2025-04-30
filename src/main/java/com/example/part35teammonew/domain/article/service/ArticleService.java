package com.example.part35teammonew.domain.article.service;

import com.example.part35teammonew.domain.article.dto.ArticleBaseDto;
import com.example.part35teammonew.domain.article.dto.ArticleCursorRequest;
import com.example.part35teammonew.domain.article.dto.ArticleSourceAndDateAndInterestsRequest;
import com.example.part35teammonew.domain.article.dto.findByCursorPagingResponse;
import java.util.List;
import java.util.UUID;

public interface ArticleService {
  UUID save(ArticleBaseDto dto);
  ArticleBaseDto findById(UUID id);
  List<ArticleBaseDto> findAll();
  List<ArticleBaseDto> findByIds(List<UUID> ids);
  List<ArticleBaseDto> findByTitleOrSummary(String title, String summary);
  List<ArticleBaseDto> findBySourceAndDateAndInterests(
      ArticleSourceAndDateAndInterestsRequest articleSourceAndDateAndInterestsRequest);
  void deletePhysical(UUID id);
  void deleteLogical(UUID id);
  findByCursorPagingResponse findByCursorPaging(ArticleCursorRequest req);
  List<UUID> backup(String from, String to);
}

