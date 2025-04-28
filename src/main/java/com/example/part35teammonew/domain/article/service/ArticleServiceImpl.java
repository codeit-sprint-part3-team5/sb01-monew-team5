package com.example.part35teammonew.domain.article.service;

import com.example.part35teammonew.domain.article.batch.S3UploadArticle;
import com.example.part35teammonew.domain.article.dto.ArticleBaseDto;
import com.example.part35teammonew.domain.article.dto.ArticleCursorRequest;
import com.example.part35teammonew.domain.article.dto.ArticleSourceAndDateAndInterestsRequest;
import com.example.part35teammonew.domain.article.dto.findByCursorPagingResponse;
import com.example.part35teammonew.domain.article.entity.Article;
import com.example.part35teammonew.domain.article.entity.Direction;
import com.example.part35teammonew.domain.article.repository.ArticleRepository;
import com.example.part35teammonew.domain.articleView.service.ArticleViewServiceInterface;
import jakarta.annotation.Nullable;
import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

  private final ArticleRepository articleRepository;
  private final ArticleViewServiceInterface articleViewServiceInterface;
  private final S3UploadArticle s3UploadArticle;

  // 기사 저장
  @Override
  public UUID save(ArticleBaseDto dto) {
    if (dto.getTitle() == null || dto.getTitle().isBlank() || dto.getDate() == null) {
      throw new IllegalArgumentException("제목과 날짜는 필수입니다.");
    }
    if (articleRepository.findByTitleAndDate(dto.getTitle(), dto.getDate()) != null) {
      throw new IllegalArgumentException("중복 저장되었습니다.");
    }

    Article article = new Article(dto);
    Article saved = articleRepository.save(article);
    articleViewServiceInterface.createArticleView(article.getId()); //아티클 뷰 만듬
    return saved.getId();
  }

  // Id로 기사 단건 조회
  @Override
  public ArticleBaseDto findById(UUID id) {
    return articleRepository.findById(id)
        .filter(Article::isNotLogicallyDeleted)
        .map(ArticleBaseDto::new)
        .orElseThrow(() -> new IllegalArgumentException("해당 ID의 기사를 찾을 수 없습니다."));
  }

  @Override
  public List<ArticleBaseDto> findAll() {
    List<ArticleBaseDto> articles = new ArrayList<>();
    articleRepository.findAll().stream().filter(Article::isNotLogicallyDeleted)
        .map(ArticleBaseDto::new).forEach(articles::add);
    return articles;
  }

  @Override
  public List<ArticleBaseDto> findByIds(List<UUID> ids) {
    List<ArticleBaseDto> articles = new ArrayList<>();
    for (UUID id : ids) {
      articles.add(findById(id));
    }
    return articles;
  }


  @Override
  public List<ArticleBaseDto> findByTitleOrSummary(@Nullable String title,
      @Nullable String summary) {
    if (title == null && summary == null) {
      throw new IllegalArgumentException("제목과 요약 내용 중 하나는 채워주세요.");
    }
    //추후 Querydsl 시도해보자
    List<Article> articles;
    if (title == null && summary != null) {
      articles = articleRepository.findBySummary(summary);
      return articles.stream().filter(Article::isNotLogicallyDeleted).map(ArticleBaseDto::new)
          .collect(Collectors.toList());
    } else if (title != null && summary == null) {
      articles = articleRepository.findByTitle(title);
      return articles.stream().filter(Article::isNotLogicallyDeleted).map(ArticleBaseDto::new)
          .collect(Collectors.toList());
    }
    return articleRepository.findByTitleAndSummary(title, summary).stream()
        .filter(Article::isNotLogicallyDeleted).map(ArticleBaseDto::new)
        .toList();
  }

  @Override
  public List<ArticleBaseDto> findBySourceAndDateAndInterests(
      ArticleSourceAndDateAndInterestsRequest articleSourceAndDateAndInterestsRequest) {
    System.out.println(
        "articleSourceAndDateAndInterestsRequest = " + articleSourceAndDateAndInterestsRequest);
    String[] sources = articleSourceAndDateAndInterestsRequest.getSourceIn(); //추후 처리 고려해야 할 듯
    String startDate = articleSourceAndDateAndInterestsRequest.getPublishDateFrom();
    String endDate = articleSourceAndDateAndInterestsRequest.getPublishDateTo();
    String interests = articleSourceAndDateAndInterestsRequest.getKeyword();

    if (sources == null && startDate == null && endDate == null) {
      throw new IllegalArgumentException("소스와 날짜 중 하나의 파라미터는 채워져야 합니다.");
    }
    List<Article> articles = new ArrayList<>();
    if (sources != null && startDate == null && endDate == null) {
      for (String source : sources) {
        articles.addAll(articleRepository.findBySource(source));
      }
      System.out.println("findBySource_articles = " + articles);
      System.out.println("articles.size() = " + articles.size());

    } else if (sources == null && startDate != null && endDate == null) { //startDate만 존재
      System.out.println(" } else if (sources == null && startDate != null && endDate == null) { //startDate만 존재");

      LocalDateTime startDateTime = LocalDate.parse(startDate.substring(0, 10), DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();
      LocalDateTime endDateTime = LocalDate.now().plusDays(1).atStartOfDay(); //오늘보다 하루 뒤
      System.out.println("startDateTime = " + startDateTime);
      System.out.println("endDateTime = " + endDateTime);

      articles = articleRepository.findByStartEndDate(startDateTime, endDateTime);

      for (Article article : articles) {
        System.out.println("findByStartEndDate_article = " + article);
      }
      System.out.println("articles.size() = " + articles.size());

    } else if (sources == null && startDate == null && endDate != null) { //endDate만 존재
      System.out.println("  } else if (sources == null && startDate == null && endDate != null)");
      LocalDate DateFrom = LocalDate.of(1970, 1, 1); //시작일 기준
      LocalDate localDate = LocalDate.parse(endDate.substring(0, 10), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
          .plusDays(1);
      System.out.println("startDateTime = " + DateFrom);
      System.out.println("endDateTime = " + localDate);
      articles = articleRepository.findByStartEndDate(DateFrom.atStartOfDay(),
          localDate.atStartOfDay());
      for (Article article : articles) {
        System.out.println("findByStartEndDate_article = " + article);
      }
      System.out.println("articles.size() = " + articles.size());

    } else if(sources == null && startDate != null && endDate != null){
      System.out.println("startDate = " + startDate);
      System.out.println("endDate = " + endDate);
      System.out.println("else if(sources == null && startDate != null && endDate != null)");
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
      String onlyDate = startDate.substring(0, 10); // "2025-04-24"
      LocalDateTime DateFrom = LocalDate.parse(onlyDate,
          DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();
      onlyDate = endDate.substring(0, 10); // "2025-04-24"
      LocalDateTime localDate = LocalDate.parse(onlyDate,
          DateTimeFormatter.ofPattern("yyyy-MM-dd")).plusDays(1).atStartOfDay();

      //LocalDateTime DateFrom = LocalDateTime.parse(startDate, formatter);
      //LocalDateTime localDate = LocalDateTime.parse(endDate, formatter).plusDays(1);

      /*LocalDate DateFrom =  LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
      LocalDate localDate = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
          .plusDays(1);*/
      System.out.println("startDateTime = " + DateFrom);
      System.out.println("endDateTime = " + localDate);
      articles = articleRepository.findByStartEndDate(DateFrom,
          localDate);
      for (Article article : articles) {
        System.out.println("findByStartEndDate_article = " + article);
      }
      System.out.println("articles.size() = " + articles.size());

    }else if (sources != null && startDate != null && endDate == null) { //source, startDate 존재
      System.out.println("    } else if (sources != null && startDate != null && endDate == null)");
      LocalDate localStartDate = LocalDate.parse(startDate.substring(0, 10), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
      LocalDate plus = LocalDate.now().plusDays(1); //오늘보다 하루 뒤
      LocalDate localEndDate = LocalDate.parse(plus.toString(),
          DateTimeFormatter.ofPattern("yyyy-MM-dd"));
      System.out.println("startDateTime = " + localStartDate);
      System.out.println("plus = " + plus);
      System.out.println("endDateTime = " + localEndDate);
      for (String source : sources) {
        articles.addAll(articleRepository.findBySourceAndDate(source, localStartDate.atStartOfDay(),
            localEndDate.atStartOfDay()));
      }
      for (Article article : articles) {
        System.out.println("findBySourceAndDate_article = " + article);
      }

    } else if (sources != null && startDate == null && endDate != null) { //source, endDate 존재
      System.out.println("else if (sources != null && startDate == null && endDate != null)");
      LocalDate DateFrom = LocalDate.of(1970, 1, 1);
      LocalDate localDate = LocalDate.parse(endDate.substring(0, 10), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
          .plusDays(1); //endDate보다 하루 뒤
      for (String source : sources) {
        articles.addAll(articleRepository.findBySourceAndDate(source, DateFrom.atStartOfDay(),
            localDate.atStartOfDay()));
      }
      for (Article article : articles) {
        System.out.println("findBySourceAndDate_article = " + article);
      }


    } else if (sources != null) { //source, startDate, endDate 모두 존재
      System.out.println("   } else if (sources != null) { //source, startDate, endDate 모두 존재");
      LocalDate localStartDate = LocalDate.parse(startDate.substring(0, 10),
          DateTimeFormatter.ofPattern("yyyy-MM-dd"));
      LocalDate localEndDate = LocalDate.parse(endDate.substring(0, 10), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
          .plusDays(1); //endDate보다 하루 뒤
      for (String source : sources) {
        articles.addAll(articleRepository.findBySourceAndDate(source, localStartDate.atStartOfDay(),
            localEndDate.atStartOfDay()));
      }
      for (Article article : articles) {
        System.out.println("findBySourceAndDate_article = " + article);
      }
    }
    //논리삭제여부
    articles = articles.stream().filter(Article::isNotLogicallyDeleted).toList();
    for (Article article : articles) {
      System.out.println("isNotLogicallyDeleted_article = " + article);
    }
    System.out.println("articles.size() = " + articles.size());

    if (interests != null && !interests.isEmpty()) {
      //System.out.println("interests = " + interests);
      Set<String> requestedKeywords = new HashSet<>();
      if (interests.contains(",")) {
        requestedKeywords = new HashSet<>(Arrays.asList(interests.split(",")));
      } else {
        requestedKeywords.add(interests);
      }
      List<ArticleBaseDto> result = new ArrayList<>();
      for (Article article : articles) {
        for (String keyword : requestedKeywords) {
          String articleTitle = article.getTitle().toLowerCase();
          String articleSummary = article.getSummary().toLowerCase();
          if (article.getTitle().contains(keyword) || articleTitle.contains(keyword)
              || article.getSummary().contains(keyword) || articleSummary.contains(keyword)) {
            result.add(new ArticleBaseDto(article));
          }
        }
      }
      return result;
    }
    return articles.stream().map(ArticleBaseDto::new).collect(Collectors.toList());
  }

  // 기사 삭제
  @Override
  public void deletePhysical(UUID id) {
    if (articleRepository.findById(id).isPresent()) {
      articleRepository.deleteById(id);
      return;
    }
    throw new IllegalArgumentException("해당 ID의 기사를 찾을 수 없습니다.");
  }

  @Override
  public void deleteLogical(UUID id) {
    //deletedAt 존재하며 현재 시간보다 이전이면 제거된 것으로 침
    //다른 검색 메서드에서도 포함시켜야할 듯
    Optional<Article> article = articleRepository.findById(id);
    if (article.isPresent()) {
      article.get().logicalDelete(LocalDateTime.now());
    } else {
      throw new IllegalArgumentException("해당 ID의 기사를 찾을 수 없습니다.");
    }
  }

  /**
   * 기사 조회
   *
   * @param req cursor, sort, size, direction
   * @return id, 기사 타이틀, 요약, 링크, 출처, 날짜, 댓글 수
   */
  @Override
  public findByCursorPagingResponse findByCursorPaging(ArticleCursorRequest req) {
    findByCursorPagingResponse response = new findByCursorPagingResponse();
    List<Article> articles = null;
    Pageable pageable = PageRequest.of(0, req.getSize()+1);

    switch (req.getSortField()) {
      case DATE -> {
        LocalDateTime cursor = req.getCursor() != null ? LocalDateTime.parse(req.getCursor()) : LocalDateTime.now();
        articles = req.getDirection() == Direction.ASC
            ? articleRepository.findByDateCursorAsc(cursor, pageable)
            : articleRepository.findByDateCursorDesc(cursor, pageable);
        LocalDateTime nextCursor;
        if(req.getDirection() == Direction.ASC){
          nextCursor = articles.get(0).getDate();
        }else {
          nextCursor = articles.get(articles.size()-1).getDate();
        }
        /*List<Article> articleList =
            req.getDirection() == Direction.ASC ? articleRepository.findByDateCursorAsc(nextCursor,
                pageable) : articleRepository.findByDateCursorDesc(nextCursor, pageable);
                response.setNextAfter(articleList.isEmpty() ?null:articleList.get(0).getDate());
        response.setNextCursor(nextCursor.toString());
        */
        if(articles.size() < req.getSize()){
          response.setNextAfter(null);
        }else {
          response.setNextAfter(articles.get(articles.size()-1).getDate());
        }
        response.setNextCursor(nextCursor.toString());
      }
      case COMMENT_COUNT -> {
        int cursor = req.getCursor() != null ? Integer.parseInt(req.getCursor()) : 0;
        int nextCursor = req.getCursor() != null ? Integer.parseInt(req.getCursor())+1 : 0;
        articles = req.getDirection() == Direction.ASC
            ? articleRepository.findByCommentCursorAsc(cursor, pageable)
            : articleRepository.findByCommentCursorDesc(cursor, pageable);
        Article nextArticle = req.getDirection() == Direction.ASC
            ? articleRepository.findByCommentCursorAsc(nextCursor, pageable).get(0)
            : articleRepository.findByCommentCursorDesc(nextCursor, pageable).get(0);
        response.setNextAfter(nextArticle.getDate());
        response.setNextCursor(String.valueOf(nextCursor));
      }
      case VIEW_COUNT -> {
        Long cursor = req.getCursor() != null ? Long.parseLong(req.getCursor()) : 0L;
        Long nextCursor = req.getCursor() != null ? Long.parseLong(req.getCursor())+1 : 0;
        //articles = req.getDirection() == Direction.ASC ? articleRepository.findByViewCursorAsc(cursor, pageable) : articleRepository.findByViewCursorDesc(cursor, pageable);
        List<UUID> sortByVewCountPageNation = articleViewServiceInterface.getSortByVewCountPageNation(cursor, pageable, req.getDirection().toString());
        LocalDateTime nextAfter = findById( articleViewServiceInterface.getSortByVewCountPageNation(nextCursor, pageable, req.getDirection().toString()).get(0)).getDate();
        response.setArticles(findByIds(sortByVewCountPageNation));
        response.setNextAfter(nextAfter);
        response.setNextCursor(String.valueOf(nextCursor));
        response.setLimit(req.getSize());
        return response;
      }
      default -> throw new IllegalArgumentException("정렬 조건이 잘못되었습니다.");
    }
    response.setArticles(articles.stream().filter(Article::isNotLogicallyDeleted).map(ArticleBaseDto::new).toList());
    response.setLimit(req.getSize());
    return response;
  }
  @Override
  public List<UUID> backup(String from, String to) {
    LocalDate localStartDate = LocalDate.parse(from.substring(0, 10), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    LocalDate localEndDate = LocalDate.parse(to.substring(0, 10), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    List<UUID> result = new ArrayList<>();
    Queue<Article> queue = new LinkedList<>();
    while (localStartDate.isBefore(localEndDate)) {
      System.out.println("localStartDate = " + localStartDate);
      System.out.println("localEndDate = " + localEndDate);
      File file = new File("articles_" + localStartDate + "temp.json");
      JSONArray jsonArray;
      try {
        if(s3UploadArticle.exists("articles_"+localStartDate+".json")){
          s3UploadArticle.download(file);
          String content = Files.readString(file.toPath());
          jsonArray = new JSONArray(content);
        } else {
          throw new IllegalArgumentException("S3 Bucket에 파일이 존재하지 않습니다.");
        }
        for (int i = 0; i < jsonArray.length(); i++) {
          JSONObject obj = jsonArray.getJSONObject(i);
          Article article = new Article(new ArticleBaseDto(
              obj.getString("title"),
              obj.getString("summary"),
              obj.getString("link"),
              obj.getString("source"),
              LocalDateTime.parse(obj.getString("date")),
              obj.getInt("commentCount")
          ));
          queue.add(article);
        }
      }catch (Exception e){

      }
      System.out.println("articleBackupReader_queue = " + queue.size());
      while(!queue.isEmpty()){
        Article article = queue.poll();
        article = articleRepository.findByTitleAndDate(article.getTitle(), article.getDate()) != null ? null : article;
        if(article != null){
          save(new ArticleBaseDto(article));
          result.add(article.getId());
          System.out.println("article = " + article);
        }
      }
    }
    return result;
  }


}
