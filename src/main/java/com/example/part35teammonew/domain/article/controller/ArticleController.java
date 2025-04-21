package com.example.part35teammonew.domain.article.controller;

import com.example.part35teammonew.domain.article.dto.ArticleEnrollmentResponse;
import com.example.part35teammonew.domain.article.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ArticleController {
  @Autowired
  private ArticleService articleService;

  @PostMapping("/api/articles/{articleId}/article-views")
  public ResponseEntity<ArticleEnrollmentResponse> articleViewEnrollment(@PathVariable int articleId, @RequestHeader String monewRequestUserId) {

    return ResponseEntity.ok(null);
  }

}
