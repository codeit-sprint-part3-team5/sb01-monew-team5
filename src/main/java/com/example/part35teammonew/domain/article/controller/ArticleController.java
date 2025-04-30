package com.example.part35teammonew.domain.article.controller;

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
import java.time.LocalDate;
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
  private final CommentService commentService;


  @PostMapping("/api/articles/{articleId}/article-views")
  public ResponseEntity<ArticleEnrollmentResponse> articleViewEnrollment(@PathVariable UUID articleId, @RequestHeader("Monew-Request-User-ID") String userId ) {
    ArticleEnrollmentResponse articleEnrollmentResponse = new ArticleEnrollmentResponse();
    ArticleBaseDto articleBaseDto = articleService.findById(articleId);

    UUID requestUserId = null;
    try {
      requestUserId = UUID.fromString(userId);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("유효하지 않은 사용자 ID 형식입니다");
    }

    articleViewServiceInterface.addReadUser(articleId, requestUserId);

    articleEnrollmentResponse.setId(articleId);
    articleEnrollmentResponse.setViewedBy(articleId);
    articleEnrollmentResponse.setCreatedAt(articleBaseDto.getCreatedAt().toLocalDate());
    articleEnrollmentResponse.setArticleId(articleId);
    articleEnrollmentResponse.setSource(articleBaseDto.getSource());
    articleEnrollmentResponse.setSourceUrl(articleBaseDto.getSourceUrl());
    articleEnrollmentResponse.setArticleTitle(articleBaseDto.getTitle());
    articleEnrollmentResponse.setArticlePublishedDate(articleBaseDto.getPublishDate());
    articleEnrollmentResponse.setArticleSummary(articleBaseDto.getSummary());

    CommentPageResponse comments = commentService.getComments(articleId, null, null, null, null,
        null, null);
    System.out.println("comments.getSize() = " + comments.getSize());
    //articleEnrollmentResponse.setArticleCommentCount(articleBaseDto.getCommentCount()); //기사 코멘트 읽기 여기서 문제인가?
    articleEnrollmentResponse.setArticleCommentCount(comments.getSize());

    articleEnrollmentResponse.setArticleViewCount(articleViewService.countReadUser(articleId));
    //articleEnrollmentResponse.setArticleViewCount(100L);

    System.out.println("userId = " + userId);
    System.out.println("articleEnrollmentResponse = " + articleEnrollmentResponse);

    userActivityServiceInterface.addArticleInfoView(requestUserId,
        articleInfoViewMapper.toDto(articleEnrollmentResponse, requestUserId));

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
      @RequestHeader("Monew-Request-User-ID") String userId
  ) {
    ArticlesResponse articlesResponse = new ArticlesResponse();

    // 유효한 파라미터인지 검증
    SortField sortField;
    try {
      sortField = SortField.valueOf(orderBy);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("유효하지 않은 정렬 필드입니다: " + orderBy);
    }

    Direction sortDirection;
    try {
      sortDirection = Direction.valueOf(direction);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("유효하지 않은 정렬 방향입니다: " + direction);
    }


    ArticleSourceAndDateAndInterestsRequest articleSourceAndDateAndInterestsRequest = new ArticleSourceAndDateAndInterestsRequest();
    if(sourceIn != null) articleSourceAndDateAndInterestsRequest.setSourceIn(sourceIn);
    if(publishDateFrom != null) articleSourceAndDateAndInterestsRequest.setPublishDateFrom(publishDateFrom);
    if(publishDateTo != null) articleSourceAndDateAndInterestsRequest.setPublishDateTo(publishDateTo);
    if(keyword != null) articleSourceAndDateAndInterestsRequest.setKeyword(keyword);

    List<ArticleBaseDto> bySourceAndDateAndInterests = articleService.findBySourceAndDateAndInterests(articleSourceAndDateAndInterestsRequest);
    for (ArticleBaseDto bySourceAndDateAndInterest : bySourceAndDateAndInterests) {
      String title = bySourceAndDateAndInterest.getTitle();
      System.out.println("title = " + title);
      LocalDateTime publishDate = bySourceAndDateAndInterest.getPublishDate();
      System.out.println("publishDate = " + publishDate);
    }
    System.out.println("bySourceAndDateAndInterests.size() = " + bySourceAndDateAndInterests.size());




    // 커서 정규화 - ISO 형식으로 변환 시도
    if (sortField==SortField.publishDate && cursor != null && cursor.contains("T")) {
      try {
        // ISO 형식 체크
        LocalDateTime.parse(cursor, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
      } catch (DateTimeParseException e) {
        // 형식이 잘못된 경우, 표준 ISO 형식으로 변환 시도
        try {
          // 시간 부분 추출
          String[] parts = cursor.split("T");
          if (parts.length == 2) {
            String date = parts[0];
            String time = parts[1];

            // 시간 부분에 한 자리 시간이 있다면 두 자리로 변환
            if (time.length() < 8) {
              String[] timeParts = time.split(":");
              if (timeParts.length >= 2) {
                time = String.format("%02d:%02d",
                    Integer.parseInt(timeParts[0]),
                    Integer.parseInt(timeParts[1]));

                if (timeParts.length > 2) {
                  time += ":" + timeParts[2];
                } else {
                  time += ":00";
                }
              }
            }

            cursor = date + "T" + time;
          }
        } catch (Exception ex) {
          // 변환 실패 시 현재 시간 사용
          cursor = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
      }
    }
    ArticleCursorRequest articleCursorRequest = new ArticleCursorRequest(cursor, sortField, limit, sortDirection, bySourceAndDateAndInterests);
    findByCursorPagingResponse byCursorPaging = articleService.findByCursorPaging(articleCursorRequest);
    System.out.println("byCursorPaging.getArticles().size() = " + byCursorPaging.getArticles().size());

    /*// 표준 ISO 형식 사용
    System.out.println("이번 탐색에 사용할_cursor = " + cursor);
    cursor = articleCursorRequest.getCursor();
    System.out.println("커서 null 값 체크_cursor = " + cursor);

    LocalDateTime newCursor;
    if(cursor.contains("T")){
      newCursor = LocalDateTime.parse(cursor, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    } else {
      // 날짜만 있는 경우 현재 시간을 포함한 ISO 형식으로 변환
      newCursor = LocalDateTime.parse(cursor + "T00:00:00");
    }
    System.out.println("String값에서 localDateTime 으로 : newCursor = " + newCursor);


    LocalDateTime newNextCursor = null;
    ArticleBaseDto newNextAfter = null;
    List<ArticleBaseDto> newResult = new ArrayList<>();

    if(Direction.valueOf(direction) == Direction.ASC){
      bySourceAndDateAndInterests.sort(Comparator.comparing(ArticleBaseDto::getPublishDate));
      for (ArticleBaseDto bySourceAndDateAndInterest : bySourceAndDateAndInterests) {
        if( bySourceAndDateAndInterest.getPublishDate().isAfter(newCursor)){
          newResult.add(bySourceAndDateAndInterest);
        }
      }
      if( newResult.size() >= limit+1 ){
        newNextAfter = newResult.get(limit+1);
        newNextCursor = newResult.get(limit).getPublishDate();
        newResult = newResult.subList(0, limit);

        System.out.println("newNextAfter = " + newNextAfter);
        System.out.println("newNextCursor = " + newNextCursor);
        System.out.println("newResult.size() = " + newResult.size());
      }else {
        newNextAfter = null;
        if(newResult.isEmpty()){
          newNextCursor = newCursor;
        }else {
          newNextCursor = newResult.get(newResult.size()-1).getPublishDate();
        }
      }
    }
    if (Direction.valueOf(direction) == Direction.DESC) {
      bySourceAndDateAndInterests.sort(Comparator.comparing(ArticleBaseDto::getPublishDate).reversed());
      for (ArticleBaseDto bySourceAndDateAndInterest : bySourceAndDateAndInterests) {
        if (bySourceAndDateAndInterest.getPublishDate().isBefore(newCursor)) {
          newResult.add(bySourceAndDateAndInterest);
        }
      }
      if (newResult.size() >= limit+1) {
        newNextAfter = newResult.get(limit+1);
        newNextCursor = newResult.get(limit).getPublishDate();
        newResult = newResult.subList(0, limit);

        System.out.println("newNextAfter = " + newNextAfter);
        System.out.println("newNextCursor = " + newNextCursor);
        System.out.println("newResult.size() = " + newResult.size());
      }else {
        newNextAfter = null;
        if(newResult.isEmpty()){
          newNextCursor = newCursor;
        }else {
          newNextCursor = newResult.get(newResult.size()-1).getPublishDate();
        }
      }
    }

    if(newNextAfter != null){
      articlesResponse.setHasNext("true");
      articlesResponse.setNextAfter(newNextAfter.getPublishDate().toString());
      articlesResponse.setNextCursor(newNextCursor.toString());
    }else {
      articlesResponse.setHasNext("false");
      articlesResponse.setNextCursor(newNextCursor.toString());
    }*/



    List<ArticleBaseDto> result = new ArrayList<>();
    List<ArticleBaseDto> articles = byCursorPaging.getArticles();

    for (ArticleBaseDto bySourceAndDateAndInterest : articles) {
      //if(byCursorPaging.getArticles().contains(bySourceAndDateAndInterest)){
        //ViewCount 조정
        bySourceAndDateAndInterest.setViewCount(articleViewService.countReadUser(bySourceAndDateAndInterest.getId()));
        //
        result.add(bySourceAndDateAndInterest);
      //}
    }
    //result = sorting(result,direction,sortField);

    System.out.println("result = " + result);
    System.out.println("result.size() = " + result.size());
    /*
    if(publishDateFrom != null){
      try {
        String onlyDate = publishDateFrom.substring(0, 10);
        LocalDateTime DateFrom = LocalDate.parse(onlyDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();
        System.out.println("DateFrom = " + DateFrom);
        System.out.println("byCursorPaging.getNextAfter() = " + byCursorPaging.getNextAfter());
        if(byCursorPaging.getNextAfter()!=null && DateFrom.isBefore(byCursorPaging.getNextAfter())){
          articlesResponse.setNextAfter(String.valueOf(byCursorPaging.getNextAfter()));
        }else {
          articlesResponse.setNextAfter("false");
        }
      } catch (Exception e) {
        articlesResponse.setNextAfter("false");
      }
    }else {
      articlesResponse.setNextAfter("false");
    }

    if(publishDateTo != null){
      try {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(publishDateTo, formatter);
        if(byCursorPaging.getNextAfter()!=null && !articlesResponse.getNextAfter().equals("false") && dateTime.isAfter(byCursorPaging.getNextAfter())){
          articlesResponse.setHasNext("true");
        }else {
          articlesResponse.setHasNext("false");
        }
      } catch (Exception e) {
        articlesResponse.setHasNext("false");
      }
    }else {
      articlesResponse.setHasNext("false");
    }
*/
    LocalDateTime newNextAfter = byCursorPaging.getNextAfter();
    String nextCursor = byCursorPaging.getNextCursor();
    if(!byCursorPaging.getHasNext().equals("false")) {
      articlesResponse.setHasNext("true");
      articlesResponse.setNextAfter(newNextAfter.toString());
      articlesResponse.setNextCursor(nextCursor);
    }else {
      articlesResponse.setHasNext("false");
      articlesResponse.setNextCursor(nextCursor);
    }
    articlesResponse.setContent(result);
    articlesResponse.setSize(limit);

    return ResponseEntity.ok(articlesResponse);
  }

  private List<ArticleBaseDto> sorting(List<ArticleBaseDto> result, String direction,
      SortField sortField) {
    if (result == null || result.isEmpty()) return result;
    Comparator<ArticleBaseDto> comparator;
    switch (sortField) {
      case publishDate -> comparator = Comparator.comparing(ArticleBaseDto::getPublishDate);
      case commentCount -> comparator = Comparator.comparing(ArticleBaseDto::getCommentCount);
      //case viewCount -> comparator = Comparator.comparing(ArticleBaseDto::get, String.CASE_INSENSITIVE_ORDER);
      default -> throw new IllegalArgumentException("Unknown sort field: " + sortField);
    }
    if ("DESC".equalsIgnoreCase(direction)) {
      comparator = comparator.reversed();
    }
    return result.stream()
        .sorted(comparator)
        .toList();
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