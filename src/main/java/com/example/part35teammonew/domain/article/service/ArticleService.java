package com.example.part35teammonew.domain.article.service;

import com.example.part35teammonew.domain.article.dto.ArticleBaseDto;
import com.example.part35teammonew.domain.article.entity.Article;
import com.example.part35teammonew.domain.article.repository.ArticleRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ArticleService {
  ArticleRepository articleRepository;

  public ArticleService(ArticleRepository articleRepository) {
    this.articleRepository = articleRepository;
  }
  // 기사 저장
  public UUID save(ArticleBaseDto dto) {
    //기존에 저장 됐는지 확인 - 기사 제목/작성일 비교?

    if (dto.getTitle() == null || dto.getTitle().isBlank() || dto.getDate() == null) {
      throw new IllegalArgumentException("제목과 날짜는 필수입니다.");
    }
    if( articleRepository.findByTitleAndDate(dto.getTitle(), dto.getDate()) != null){
      throw new IllegalArgumentException("중복 저장되었습니다.");
    }

    Article article = new Article(dto);
    System.out.println("article = " + article);
    Article saved = articleRepository.save(article);
    return saved.getId();
  }

  // Id로 기사 단건 조회
  public ArticleBaseDto findById(UUID id) {
    return articleRepository.findById(id)
        .map(ArticleBaseDto::new)
        .orElseThrow(() -> new IllegalArgumentException("해당 ID의 기사를 찾을 수 없습니다."));
  }
  public List<ArticleBaseDto> findAll() {
    List<ArticleBaseDto> articles = new ArrayList<>();
    articleRepository.findAll().stream().map(ArticleBaseDto::new).forEach(articles::add);
    return articles;
  }

  // 기사 삭제
  public void delete(UUID id) {
    if(articleRepository.findById(id).isPresent()){
      articleRepository.deleteById(id);
      return;
    }
    throw new IllegalArgumentException("해당 ID의 기사를 찾을 수 없습니다.");
  }
  // 기사 백업 및 복구 (S3)

}
