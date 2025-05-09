package com.example.part35teammonew.domain.article.controller;

import com.example.part35teammonew.domain.article.controller.docs.ArticleApi;
import com.example.part35teammonew.domain.article.dto.ArticleBaseDto;
import com.example.part35teammonew.domain.article.dto.ArticleCursorRequest;
import com.example.part35teammonew.domain.article.dto.ArticleEnrollmentResponse;
import com.example.part35teammonew.domain.article.dto.ArticleSourceAndDateAndInterestsRequest;
import com.example.part35teammonew.domain.article.dto.ArticlesResponse;
import com.example.part35teammonew.domain.article.dto.findByCursorPagingResponse;
import com.example.part35teammonew.domain.article.entity.Direction;
import com.example.part35teammonew.domain.article.entity.SortField;
import com.example.part35teammonew.domain.article.service.ArticleService;
import com.example.part35teammonew.domain.articleView.service.ArticleViewServiceInterface;
import com.example.part35teammonew.domain.comment.dto.CommentPageResponse;
import com.example.part35teammonew.domain.comment.service.CommentService;
import com.example.part35teammonew.domain.userActivity.maper.ArticleInfoViewMapper;
import com.example.part35teammonew.domain.userActivity.service.UserActivityServiceInterface;
import com.example.part35teammonew.exeception.RestApiException;
import com.example.part35teammonew.exeception.errorcode.ArticleErrorCode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/articles")
public class ArticleController implements ArticleApi {

  private final ArticleService articleService;
  private final JobLauncher jobLauncher;
  private final Job backupJob;
  private final ArticleViewServiceInterface articleViewServiceInterface;
  private final UserActivityServiceInterface userActivityServiceInterface;
  private final ArticleInfoViewMapper articleInfoViewMapper;
  private final CommentService commentService;

  public ArticleController(
      ArticleService articleService,
      JobLauncher jobLauncher,
      @Qualifier("backupJob") Job backupJob,
      ArticleViewServiceInterface articleViewServiceInterface,
      UserActivityServiceInterface userActivityServiceInterface,
      ArticleInfoViewMapper articleInfoViewMapper,
      CommentService commentService
  ) {
    this.articleService = articleService;
    this.jobLauncher = jobLauncher;
    this.backupJob = backupJob;
    this.articleViewServiceInterface = articleViewServiceInterface;
    this.userActivityServiceInterface = userActivityServiceInterface;
    this.articleInfoViewMapper = articleInfoViewMapper;
    this.commentService = commentService;
  }

  @PostMapping("/{articleId}/article-views")
  public ResponseEntity<ArticleEnrollmentResponse> articleViewEnrollment(
      @PathVariable UUID articleId, @RequestHeader("Monew-Request-User-ID") String userId) {
    ArticleEnrollmentResponse articleEnrollmentResponse = new ArticleEnrollmentResponse();
    ArticleBaseDto articleBaseDto = articleService.findById(articleId);

    UUID requestUserId = null;
    try {
      requestUserId = UUID.fromString(userId);
    } catch (IllegalArgumentException e) {
      throw new RestApiException(ArticleErrorCode.ARTICLE_PARSE_UUID,"유효하지 않은 사용자 ID 형식입니다");
    }
    if(articleViewServiceInterface.addReadUser(articleId, requestUserId)){
      articleService.increaseCountReadUser(articleId);
    }
    articleEnrollmentResponse.setId(articleId);
    articleEnrollmentResponse.setViewedBy(articleId);
    articleEnrollmentResponse.setCreatedAt(articleBaseDto.getCreatedAt().toLocalDate());
    articleEnrollmentResponse.setArticleId(articleId);
    articleEnrollmentResponse.setSource(articleBaseDto.getSource());
    articleEnrollmentResponse.setSourceUrl(articleBaseDto.getSourceUrl());
    articleEnrollmentResponse.setArticleTitle(articleBaseDto.getTitle());
    articleEnrollmentResponse.setArticlePublishedDate(articleBaseDto.getPublishDate());
    articleEnrollmentResponse.setArticleSummary(articleBaseDto.getSummary());

    CommentPageResponse comments = commentService.getComments(articleId, null, null, null, null, null, null);
    articleEnrollmentResponse.setArticleCommentCount(comments.getSize());
    articleEnrollmentResponse.setArticleViewCount(articleBaseDto.getViewCount());
    userActivityServiceInterface.addArticleInfoView(requestUserId, articleInfoViewMapper.toDto(articleEnrollmentResponse, requestUserId));

    return ResponseEntity.ok(articleEnrollmentResponse);
  }

  @GetMapping
  public ResponseEntity<ArticlesResponse> articles(
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) String interestId,
      @RequestParam(required = false) String[] sourceIn,
      @RequestParam(required = false) String publishDateFrom,
      @RequestParam(required = false) String publishDateTo,
      @RequestParam String orderBy, //코맨트수, 조회수, 시간순
      @RequestParam String direction,
      @RequestParam(required = false) String cursor, //orderby에 따라 달라짐
      @RequestParam (required = false) String after, //시간
      @RequestParam int limit,
      @RequestHeader("Monew-Request-User-ID") String userId
  ) {
    if ( limit == 0 ){
      return ResponseEntity.badRequest().body(null);
    }
    //같은게 있으면 제목순
    ArticlesResponse result = articleService.getPageArticle(keyword, interestId,
        sourceIn, publishDateFrom, publishDateTo, orderBy, direction, cursor, after, limit, userId);

    return ResponseEntity.ok(result);

  }

  @GetMapping("/restore")
  public ResponseEntity<ArticleEnrollmentResponse> articlesRestore(
      @RequestParam("from") String from,
      @RequestParam("to") String to) throws Exception {

    JobParameters jobParameters = new JobParametersBuilder()
        .addString("from", from)
        .addString("to", to)
        .addLong("time", System.currentTimeMillis()) // 중복방지용
        .toJobParameters();

    jobLauncher.run(backupJob, jobParameters);

    return ResponseEntity.ok(null);
  }

  @DeleteMapping("/{articleId}")
  public ResponseEntity<Void> articlesDelete(@PathVariable UUID articleId) {
    articleService.deleteLogical(articleId);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{articleId}/hard")
  public ResponseEntity<Void> articlesDeleteHard(@PathVariable UUID articleId) {
    articleService.deletePhysical(articleId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/sources")
  public ResponseEntity<List<String>> getSources(){
    List<String> hardSources=new ArrayList<>();
    hardSources.add("NAVER");
    return ResponseEntity.ok(hardSources);
  }
}
