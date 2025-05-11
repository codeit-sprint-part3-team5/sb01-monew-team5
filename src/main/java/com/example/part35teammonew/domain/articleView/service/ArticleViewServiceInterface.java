package com.example.part35teammonew.domain.articleView.service;

import com.example.part35teammonew.domain.articleView.dto.ArticleViewDto;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface ArticleViewServiceInterface {

  ArticleViewDto createArticleView(UUID articleId);//기사 만들어 질때 해결

  boolean addReadUser(UUID articleId, UUID userId);//유저가 기사 읽을때, //뭔가 문제 생김

  Long countReadUser(UUID articleId);// 조회수 읽기

  List<UUID> getSortByVewCountPageNation(Long cursor, Pageable pageable, String direction);//조회수 기반 페이지네이션
}
