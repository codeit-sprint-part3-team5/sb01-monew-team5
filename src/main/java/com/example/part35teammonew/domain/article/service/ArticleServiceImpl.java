package com.example.part35teammonew.domain.article.service;

import com.example.part35teammonew.domain.article.dto.ArticleBaseDto;
import com.example.part35teammonew.domain.article.entity.Article;
import com.example.part35teammonew.domain.article.repository.ArticleRepository;
import com.example.part35teammonew.domain.interest.entity.Interest;
import jakarta.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ArticleServiceImpl implements ArticleService {

  ArticleRepository articleRepository;

  public ArticleServiceImpl(ArticleRepository articleRepository) {
    this.articleRepository = articleRepository;
  }

  // 기사 저장
  public UUID save(ArticleBaseDto dto) {
    if (dto.getTitle() == null || dto.getTitle().isBlank() || dto.getDate() == null) {
      throw new IllegalArgumentException("제목과 날짜는 필수입니다.");
    }
    if (articleRepository.findByTitleAndDate(dto.getTitle(), dto.getDate()) != null) {
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

  @Override
  public List<ArticleBaseDto> findByTitleOrSummary(@Nullable String title, @Nullable String summary) {
    if (title == null && summary == null) {
      throw new IllegalArgumentException("제목과 요약 내용 중 하나는 채워주세요.");
    }
    //추후 Querydsl 시도해보자
    List<Article> articles;
    if (title == null && summary != null) {
      articles= articleRepository.findBySummary(summary);
      return articles.stream().map(ArticleBaseDto::new).collect(Collectors.toList());
    }else if(title != null && summary == null){
      articles = articleRepository.findByTitle(title);
      return articles.stream().map(ArticleBaseDto::new).collect(Collectors.toList());
    }
    return articleRepository.findByTitleAndSummary(title, summary).stream().map(ArticleBaseDto::new)
        .toList();
  }

  @Override
  public List<ArticleBaseDto> findBySourceAndDateAndInterests(String source, String date, String interests) {
    List<Article> articles = articleRepository.findBySourceAndDateAndInterests(source, date, interests);
    Set<String> requestedKeywords = new HashSet<>(Arrays.asList(interests.split(",")));

    List<ArticleBaseDto> result = new ArrayList<>();
    Set<UUID> addedArticleIds = new HashSet<>(); // 중복 방지용

    for (Article article : articles) {
      for (Interest interest : article.getInterests()) {
        for (String keyword : interest.getKeywords().split(",")) {
          if (requestedKeywords.contains(keyword.trim()) && !addedArticleIds.contains(article.getId())) {
            result.add(new ArticleBaseDto(article));
            addedArticleIds.add(article.getId()); // 중복 방지
            break; // 키워드 하나라도 매칭되면 다음 기사로
          }
        }
      }
    }

    return result;
  }


  // 기사 삭제
  public void deletePhysical(UUID id) {
    if (articleRepository.findById(id).isPresent()) {
      articleRepository.deleteById(id);
      return;
    }
    throw new IllegalArgumentException("해당 ID의 기사를 찾을 수 없습니다.");
  }

  @Override
  public void deleteLogical(UUID id) {

  }

  // 기사 백업 및 복구 (S3)
  @Override
  public void backup() {

  }

}
