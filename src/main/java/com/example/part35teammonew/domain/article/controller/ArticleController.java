package com.example.part35teammonew.domain.article.controller;

import com.example.part35teammonew.domain.article.dto.ArticleBaseDto;
import com.example.part35teammonew.domain.article.dto.ArticleCursorRequest;
import com.example.part35teammonew.domain.article.dto.ArticleEnrollmentResponse;
import com.example.part35teammonew.domain.article.dto.ArticleSourceAndDateAndInterestsRequest;
import com.example.part35teammonew.domain.article.dto.ArticlesRequestDto;
import com.example.part35teammonew.domain.article.dto.ArticlesResponse;
import com.example.part35teammonew.domain.article.dto.findByCursorPagingResponse;
import com.example.part35teammonew.domain.article.entity.Direction;
import com.example.part35teammonew.domain.article.entity.SortField;
import com.example.part35teammonew.domain.article.service.ArticleService;
import com.example.part35teammonew.domain.articleView.service.ArticleViewServiceInterface;
import com.example.part35teammonew.domain.userActivity.Dto.ArticleInfoView;
import com.example.part35teammonew.domain.userActivity.maper.ArticleInfoViewMapper;
import com.example.part35teammonew.domain.userActivity.service.UserActivityServiceInterface;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ArticleController {
  private final ArticleService articleService;
  private final ArticleViewServiceInterface articleViewService;
  private final JobLauncher jobLauncher;
  private final Job backupJob;
  private final ArticleViewServiceInterface articleViewServiceInterface;
  private final UserActivityServiceInterface userActivityServiceInterface;
  private final ArticleInfoViewMapper articleInfoViewMapper;

  @PostMapping("/api/articles/{articleId}/article-views")
  public ResponseEntity<ArticleEnrollmentResponse> articleViewEnrollment(@PathVariable UUID articleId, @RequestHeader("Monew-Request-User-ID") String userId ) { //,
    ArticleEnrollmentResponse articleEnrollmentResponse = new ArticleEnrollmentResponse();
    ArticleBaseDto articleBaseDto = articleService.findById(articleId);

    articleViewServiceInterface.addReadUser(articleId,UUID.fromString(userId));

    articleEnrollmentResponse.setId(articleId);
    articleEnrollmentResponse.setViewdBy(articleId);
    articleEnrollmentResponse.setCreatedAt(articleBaseDto.getCreatedAt().toLocalDate());
    articleEnrollmentResponse.setArticleId(articleId);
    articleEnrollmentResponse.setSource(articleBaseDto.getSource());
    articleEnrollmentResponse.setSourceUrl(articleBaseDto.getLink());
    articleEnrollmentResponse.setArticleTitle(articleBaseDto.getTitle());
    articleEnrollmentResponse.setArticlePublishedDate(articleBaseDto.getDate());
    articleEnrollmentResponse.setArticleSummary(articleBaseDto.getSummary());
    articleEnrollmentResponse.setArticleCommentCount(articleBaseDto.getCommentCount());
    articleEnrollmentResponse.setArticleViewCount(articleViewService.countReadUser(articleId));
    System.out.println("userId = " + userId);
    System.out.println("articleEnrollmentResponse = " + articleEnrollmentResponse);

    userActivityServiceInterface.addArticleInfoView(UUID.fromString(userId),
        articleInfoViewMapper.toDto(articleEnrollmentResponse,UUID.fromString(userId)) );

    //monewRequestUserId의 역할?
    return ResponseEntity.ok(articleEnrollmentResponse);
  }
  @GetMapping("/api/articles")
  public ResponseEntity<ArticlesResponse> articles(
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) String[] sourceIn,
      @RequestParam(required = false) String publishDateFrom,
      @RequestParam(required = false) String publishDateTo,
      @RequestParam String orderBy,
      @RequestParam String direction,
      @RequestParam(required = false) String cursor,
      @RequestParam int limit,
      @RequestParam(required = false) String interestId,
      @RequestParam(required = false, name = "monew_Request_User_ID") String monewRequestUserId
  ) {
    ArticlesResponse articlesResponse = new ArticlesResponse();

    SortField sortField = SortField.valueOf(orderBy);
    Direction sortDirection = Direction.valueOf(direction);

    System.out.println("sortDirection = " + sortDirection);
    System.out.println("sortDirection = " + sortDirection);
    System.out.println("limit = " + limit);

    ArticleCursorRequest articleCursorRequest = new ArticleCursorRequest(cursor, sortField, limit, sortDirection);

    ArticleSourceAndDateAndInterestsRequest articleSourceAndDateAndInterestsRequest = new ArticleSourceAndDateAndInterestsRequest();
    if(sourceIn != null) articleSourceAndDateAndInterestsRequest.setSourceIn(sourceIn);
    if(publishDateFrom != null) articleSourceAndDateAndInterestsRequest.setPublishDateFrom(publishDateFrom);
    if(publishDateTo != null) articleSourceAndDateAndInterestsRequest.setPublishDateTo(publishDateTo);
    if(keyword != null) articleSourceAndDateAndInterestsRequest.setKeyword(keyword);


    findByCursorPagingResponse byCursorPaging = articleService.findByCursorPaging(articleCursorRequest);
    System.out.println("byCursorPaging = " + byCursorPaging);
    System.out.println("byCursorPaging.getArticles().size() = " + byCursorPaging.getArticles().size());

    List<ArticleBaseDto> bySourceAndDateAndInterests = articleService.findBySourceAndDateAndInterests(articleSourceAndDateAndInterestsRequest);
    System.out.println("bySourceAndDateAndInterests = " + bySourceAndDateAndInterests);
    System.out.println("bySourceAndDateAndInterests.size() = " + bySourceAndDateAndInterests.size());

    List<ArticleBaseDto> result = new ArrayList<>();
    for (ArticleBaseDto bySourceAndDateAndInterest : bySourceAndDateAndInterests) {
      if(byCursorPaging.getArticles().contains(bySourceAndDateAndInterest)){
        result.add(bySourceAndDateAndInterest);
      }
    }

    System.out.println("result = " + result);
    System.out.println("result = " + result.size());


    if(publishDateFrom != null){
      String onlyDate = publishDateFrom.substring(0, 10);
      LocalDateTime DateFrom = LocalDate.parse(onlyDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();
      System.out.println("DateFrom = " + DateFrom);
      System.out.println("byCursorPaging.getNextAfter() = " + byCursorPaging.getNextAfter());
      if(byCursorPaging.getNextAfter()!=null && DateFrom.isBefore(byCursorPaging.getNextAfter())){
        articlesResponse.setNextAfter(String.valueOf(byCursorPaging.getNextAfter()));
      }else {
        articlesResponse.setNextAfter("false");
      }
    }else {
      articlesResponse.setNextAfter("false");
    }

    if(publishDateTo != null){
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
      LocalDateTime dateTime = LocalDateTime.parse(publishDateTo, formatter);
      if(byCursorPaging.getNextAfter()!=null && !articlesResponse.getNextAfter().equals("false") && dateTime.isAfter(byCursorPaging.getNextAfter())){
        articlesResponse.setHasNext("true");
      }else {
        articlesResponse.setHasNext("false");
      }
    }else {
      articlesResponse.setHasNext("false");
    }
    articlesResponse.setNextCursor(byCursorPaging.getNextCursor());
    articlesResponse.setContent(result);
    articlesResponse.setSize(limit); //필요성 의문 //몇 개씩 보기라는 게 없음

    return ResponseEntity.ok(articlesResponse);
  }
  @GetMapping("/api/articles2")
  public ResponseEntity<ArticlesResponse> articles2(@RequestBody ArticlesRequestDto articlesRequestDto) {
    ArticlesResponse articlesResponse = new ArticlesResponse();

    String keyword = articlesRequestDto.getKeyword();
    SortField sortField = SortField.valueOf(articlesRequestDto.getOrderBy());
    Direction direction = Direction.valueOf(articlesRequestDto.getDirection());
    String cursor = articlesRequestDto.getCursor();
    int limit = articlesRequestDto.getLimit();
    String publishDateFrom = articlesRequestDto.getPublishDateFrom();
    String publishDateTo = articlesRequestDto.getPublishDateTo();
    String[] sourceIn = articlesRequestDto.getSourceIn();
    String monewRequestUserId = articlesRequestDto.getMonew_Request_User_ID();

    ArticleCursorRequest articleCursorRequest = new ArticleCursorRequest(cursor, sortField, limit, direction);

    ArticleSourceAndDateAndInterestsRequest articleSourceAndDateAndInterestsRequest = new ArticleSourceAndDateAndInterestsRequest();
    if(sourceIn != null) articleSourceAndDateAndInterestsRequest.setSourceIn(sourceIn);
    if(publishDateFrom != null) articleSourceAndDateAndInterestsRequest.setPublishDateFrom(publishDateFrom);
    if(publishDateTo != null) articleSourceAndDateAndInterestsRequest.setPublishDateTo(publishDateTo);
    if(keyword != null) articleSourceAndDateAndInterestsRequest.setKeyword(keyword);


    findByCursorPagingResponse byCursorPaging = articleService.findByCursorPaging(articleCursorRequest);
    System.out.println("byCursorPaging = " + byCursorPaging);
    System.out.println("byCursorPaging.getArticles().size() = " + byCursorPaging.getArticles().size());

    List<ArticleBaseDto> bySourceAndDateAndInterests = articleService.findBySourceAndDateAndInterests(articleSourceAndDateAndInterestsRequest);
    System.out.println("bySourceAndDateAndInterests = " + bySourceAndDateAndInterests);
    System.out.println("bySourceAndDateAndInterests.size() = " + bySourceAndDateAndInterests.size());

    List<ArticleBaseDto> result = new ArrayList<>();
    for (ArticleBaseDto bySourceAndDateAndInterest : bySourceAndDateAndInterests) {
      if(byCursorPaging.getArticles().contains(bySourceAndDateAndInterest)){
        result.add(bySourceAndDateAndInterest);
      }
    }

    System.out.println("result = " + result);
    System.out.println("result = " + result.size());


    if(publishDateFrom != null){
      String onlyDate = publishDateFrom.substring(0, 10);
      LocalDateTime DateFrom = LocalDate.parse(onlyDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();
      System.out.println("DateFrom = " + DateFrom);
      System.out.println("byCursorPaging.getNextAfter() = " + byCursorPaging.getNextAfter());
      if(byCursorPaging.getNextAfter()!=null && DateFrom.isBefore(byCursorPaging.getNextAfter())){
        articlesResponse.setNextAfter(String.valueOf(byCursorPaging.getNextAfter()));
      }else {
        articlesResponse.setNextAfter("false");
      }
    }else {
      articlesResponse.setNextAfter("false");
    }

    if(publishDateTo != null){
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
      LocalDateTime dateTime = LocalDateTime.parse(publishDateTo, formatter);
      /*String onlyDate = publishDateTo.substring(0, 10); // "2025-04-24"
      LocalDateTime localDate = LocalDate.parse(onlyDate,
          DateTimeFormatter.ofPattern("yyyy-MM-dd")).plusDays(1).atStartOfDay();
      System.out.println("publishDateTo = " + publishDateTo);
      System.out.println("localDate = " + localDate);*/
      if(byCursorPaging.getNextAfter()!=null && !articlesResponse.getNextAfter().equals("false") && dateTime.isAfter(byCursorPaging.getNextAfter())){
        articlesResponse.setHasNext("true");
      }else {
        articlesResponse.setHasNext("false");
      }
    }else {
      articlesResponse.setHasNext("false");
    }
    articlesResponse.setNextCursor(byCursorPaging.getNextCursor());
    articlesResponse.setContent(result);
    articlesResponse.setSize(limit); //필요성 의문 //몇 개씩 보기라는 게 없음

    return ResponseEntity.ok(articlesResponse);
  }

  @GetMapping("/api/articles/restore")
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

  @DeleteMapping("/api/articles/{articleId}")
  public ResponseEntity<Void> articlesDelete(@PathVariable UUID articleId) {
    articleService.deleteLogical(articleId);
    return ResponseEntity.noContent().build();
  }
  @DeleteMapping("/api/articles/{articleId}/hard")
  public ResponseEntity<Void> articlesDeleteHard(@PathVariable UUID articleId) {
    articleService.deletePhysical(articleId);
    return ResponseEntity.noContent().build();
  }
}
