package com.example.part35teammonew.domain.article.controller;

import com.example.part35teammonew.domain.article.controller.docs.ArticleApi;
import com.example.part35teammonew.domain.article.dto.ArticleBaseDto;
import com.example.part35teammonew.domain.article.dto.ArticleEnrollmentResponse;
<<<<<<< HEAD
import com.example.part35teammonew.domain.article.dto.ArticleSourceAndDateAndInterestsRequest;
import com.example.part35teammonew.domain.article.dto.ArticlesRequestDto;
=======
>>>>>>> 503e9752aa197baedf124638cd2bacc572828887
import com.example.part35teammonew.domain.article.dto.ArticlesResponse;
import com.example.part35teammonew.domain.article.service.ArticleService;
import com.example.part35teammonew.domain.articleView.service.ArticleViewServiceInterface;
<<<<<<< HEAD
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
=======
import com.example.part35teammonew.domain.comment.dto.CommentPageResponse;
import com.example.part35teammonew.domain.comment.service.CommentService;
import com.example.part35teammonew.domain.userActivity.mapper.ArticleInfoViewMapper;
import com.example.part35teammonew.domain.userActivity.service.UserActivityServiceInterface;
import com.example.part35teammonew.exception.RestApiException;
import com.example.part35teammonew.exception.errorcode.ArticleErrorCode;

>>>>>>> 503e9752aa197baedf124638cd2bacc572828887
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
<<<<<<< HEAD
=======
import org.springframework.beans.factory.annotation.Qualifier;
>>>>>>> 503e9752aa197baedf124638cd2bacc572828887
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
<<<<<<< HEAD
@RequiredArgsConstructor
public class ArticleController {

  private final ArticleService articleService;
  private final ArticleViewServiceInterface articleViewService;
=======
@RequestMapping("/api/articles")
public class ArticleController implements ArticleApi {

  private final ArticleService articleService;
>>>>>>> 503e9752aa197baedf124638cd2bacc572828887
  private final JobLauncher jobLauncher;
  private final Job backupJob;
  private final ArticleViewServiceInterface articleViewServiceInterface;
  private final UserActivityServiceInterface userActivityServiceInterface;
  private final ArticleInfoViewMapper articleInfoViewMapper;
  private final CommentService commentService;

<<<<<<< HEAD
  @PostMapping("/api/articles/{articleId}/article-views")
  public ResponseEntity<ArticleEnrollmentResponse> articleViewEnrollment(
      @PathVariable UUID articleId, @RequestHeader String monewRequestUserId) {

    ArticleBaseDto articleBaseDto = articleService.findById(articleId);
    ArticleEnrollmentResponse articleEnrollmentResponse = new ArticleEnrollmentResponse();

    articleEnrollmentResponse.setArticleId(articleId);
    articleEnrollmentResponse.setArticleTitle(articleBaseDto.getTitle());
    articleEnrollmentResponse.setArticlePublishedDate(articleBaseDto.getDate());
    articleEnrollmentResponse.setCreatedAt(articleBaseDto.getCreatedAt());
    articleEnrollmentResponse.setArticleSummary(articleBaseDto.getSummary());
    articleEnrollmentResponse.setArticleViewCount(articleViewService.countReadUser(articleId));
    articleEnrollmentResponse.setSource(articleBaseDto.getSource());
    articleEnrollmentResponse.setSourceUrl(articleBaseDto.getLink());
    articleEnrollmentResponse.setArticleCommentCount(articleBaseDto.getCommentCount());
    articleEnrollmentResponse.setViewdBy(UUID.fromString(monewRequestUserId));

    return ResponseEntity.ok(articleEnrollmentResponse);
  }

  @GetMapping("/api/articles")
  public ResponseEntity<ArticlesResponse> articles(
      @RequestBody ArticlesRequestDto articlesRequestDto) {
    ArticlesResponse articlesResponse = new ArticlesResponse();

    String keyword = articlesRequestDto.getKeyword();
    System.out.println("keyword = " + keyword);
    SortField sortField = SortField.valueOf(articlesRequestDto.getOrderBy());
    Direction direction = Direction.valueOf(articlesRequestDto.getDirection());
    String cursor = articlesRequestDto.getCursor();
    int limit = articlesRequestDto.getLimit();
    String publishDateFrom = articlesRequestDto.getPublishDateFrom();
    String publishDateTo = articlesRequestDto.getPublishDateTo();
    String[] sourceIn = articlesRequestDto.getSourceIn();
    String monewRequestUserId = articlesRequestDto.getMonew_Request_User_ID();
    System.out.println("monewRequestUserId = " + monewRequestUserId);

    ArticleCursorRequest articleCursorRequest = new ArticleCursorRequest(cursor, sortField, limit,
        direction);

    ArticleSourceAndDateAndInterestsRequest articleSourceAndDateAndInterestsRequest = new ArticleSourceAndDateAndInterestsRequest();
    if (sourceIn != null) {
      articleSourceAndDateAndInterestsRequest.setSourceIn(sourceIn);
    }
    if (publishDateFrom != null) {
      articleSourceAndDateAndInterestsRequest.setPublishDateFrom(publishDateFrom);
    }
    if (publishDateTo != null) {
      articleSourceAndDateAndInterestsRequest.setPublishDateTo(publishDateTo);
    }
    if (keyword != null) {
      articleSourceAndDateAndInterestsRequest.setKeyword(keyword);
    }

    findByCursorPagingResponse byCursorPaging = articleService.findByCursorPaging(
        articleCursorRequest);
    System.out.println("byCursorPaging = " + byCursorPaging);
    System.out.println(
        "byCursorPaging.getArticles().size() = " + byCursorPaging.getArticles().size());

    List<ArticleBaseDto> bySourceAndDateAndInterests = articleService.findBySourceAndDateAndInterests(
        articleSourceAndDateAndInterestsRequest);
    System.out.println("bySourceAndDateAndInterests = " + bySourceAndDateAndInterests);
    System.out.println(
        "bySourceAndDateAndInterests.size() = " + bySourceAndDateAndInterests.size());

    List<ArticleBaseDto> result = new ArrayList<>();
    for (ArticleBaseDto bySourceAndDateAndInterest : bySourceAndDateAndInterests) {
      if (byCursorPaging.getArticles().contains(bySourceAndDateAndInterest)) {
        result.add(bySourceAndDateAndInterest);
      }
=======
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
>>>>>>> 503e9752aa197baedf124638cd2bacc572828887
    }
    //같은게 있으면 제목순
    ArticlesResponse result = articleService.getPageArticle(keyword, interestId,
        sourceIn, publishDateFrom, publishDateTo, orderBy, direction, cursor, after, limit, userId);

    return ResponseEntity.ok(result);

<<<<<<< HEAD
    if (publishDateFrom != null) {
      String onlyDate = publishDateFrom.substring(0, 10);
      LocalDateTime DateFrom = LocalDate.parse(onlyDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
          .atStartOfDay();
      System.out.println("DateFrom = " + DateFrom);
      System.out.println("byCursorPaging.getNextAfter() = " + byCursorPaging.getNextAfter());
      if (byCursorPaging.getNextAfter() != null && DateFrom.isBefore(
          byCursorPaging.getNextAfter())) {
        articlesResponse.setNextAfter(String.valueOf(byCursorPaging.getNextAfter()));
      } else {
        articlesResponse.setNextAfter("false");
      }
    } else {
      articlesResponse.setNextAfter("false");
    }

    if (publishDateTo != null) {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
      LocalDateTime dateTime = LocalDateTime.parse(publishDateTo, formatter);
      /*String onlyDate = publishDateTo.substring(0, 10); // "2025-04-24"
      LocalDateTime localDate = LocalDate.parse(onlyDate,
          DateTimeFormatter.ofPattern("yyyy-MM-dd")).plusDays(1).atStartOfDay();
      System.out.println("publishDateTo = " + publishDateTo);
      System.out.println("localDate = " + localDate);*/
      if (byCursorPaging.getNextAfter() != null && !articlesResponse.getNextAfter().equals("false")
          && dateTime.isAfter(byCursorPaging.getNextAfter())) {
        articlesResponse.setHasNext("true");
      } else {
        articlesResponse.setHasNext("false");
      }
    } else {
      articlesResponse.setHasNext("false");
    }
    articlesResponse.setNextCursor(byCursorPaging.getNextCursor());
    articlesResponse.setContent(result);
    articlesResponse.setSize(limit); //필요성 의문 //몇 개씩 보기라는 게 없음

    return ResponseEntity.ok(articlesResponse);
=======
>>>>>>> 503e9752aa197baedf124638cd2bacc572828887
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

<<<<<<< HEAD
  @DeleteMapping("/api/articles/{articleId}/hard")
=======
  @DeleteMapping("/{articleId}/hard")
>>>>>>> 503e9752aa197baedf124638cd2bacc572828887
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