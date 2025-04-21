package com.example.part35teammonew.domain.article.service;

import com.example.part35teammonew.domain.article.dto.ArticleBaseDto;
import java.util.List;
import java.util.UUID;

public interface ArticleService {
  UUID save(ArticleBaseDto dto);
  ArticleBaseDto findById(UUID id);
  List<ArticleBaseDto> findAll();
  List<ArticleBaseDto> findByTitleOrSummary(String title, String summary);
  List<ArticleBaseDto> findBySourceAndDateAndInterests(String source, String date, String interests);
  void deletePhysical(UUID id);
  void deleteLogical(UUID id);
  void backup();
}

