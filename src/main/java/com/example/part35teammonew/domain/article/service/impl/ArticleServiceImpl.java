package com.example.part35teammonew.domain.article.service.impl;

import com.example.part35teammonew.domain.article.batch.S3UploadArticle;
import com.example.part35teammonew.domain.article.dto.ArticleBaseDto;
import com.example.part35teammonew.domain.article.dto.ArticlesResponse;
import com.example.part35teammonew.domain.article.entity.Article;
import com.example.part35teammonew.domain.article.entity.SortField;
import com.example.part35teammonew.domain.article.repository.ArticleRepository;
import com.example.part35teammonew.domain.article.service.ArticleService;
import com.example.part35teammonew.domain.articleView.dto.ArticleViewDto;
import com.example.part35teammonew.domain.articleView.service.ArticleViewServiceInterface;
import com.example.part35teammonew.domain.interest.service.InterestService;
import com.example.part35teammonew.domain.interestUserList.service.InterestUserListServiceInterface;
import com.example.part35teammonew.domain.notification.service.NotificationServiceInterface;
import com.example.part35teammonew.exception.RestApiException;
import com.example.part35teammonew.exception.errorcode.ArticleErrorCode;
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
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleServiceImpl implements ArticleService {

  private final ArticleRepository articleRepository;
  private final ArticleViewServiceInterface articleViewServiceInterface;
  private final S3UploadArticle s3UploadArticle;
  private final NotificationServiceInterface notificationServiceInterface;
  private final InterestService interestService;
  private final InterestUserListServiceInterface interestUserListServiceInterface;

  // 기사 저장
  @Override
  @Transactional
  public UUID save(ArticleBaseDto dto) {
    if (dto.getTitle() == null || dto.getTitle().isBlank() || dto.getPublishDate() == null) {
      log.error("제목과 날짜는 필수입니다.");
      throw new RestApiException(ArticleErrorCode.ARTICLE_MiISSING_ARTICLE_FIELD_Exception, "제목과 날짜는 필수입니다.");
    }
    if (articleRepository.findByTitleAndDate(dto.getTitle(), dto.getPublishDate()) != null) {
      log.error("기사가 중복 저장되었습니다.");
      throw new RestApiException(ArticleErrorCode.ARTICLE_DUPLICATED_SAVED,"중복 저장되었습니다.");
    }

    Article article = new Article(dto);
    Article saved = articleRepository.save(article);//저장

    ArticleViewDto articleViewDto = articleViewServiceInterface.createArticleView(
        saved.getId());//뷰테이블 만듬

    //관심사, 키워드 추출
    String articleTitle= saved.getTitle();
    UUID articleId=saved.getId();

    List<Pair<String,UUID>> getInterest=interestService.getInterestList();
    Set<UUID> containedId =new HashSet<>();
    Set<UUID> targetUserID=new HashSet<>();//Set<UUID> 유저아이디: 구독중인 유저들

    //title.contains()// 안돼면 확인
    for(Pair<String,UUID> pair:getInterest){
      if(articleTitle.toLowerCase().contains(pair.getLeft().toLowerCase())){
        containedId.add(pair.getRight());//Set<UUID> 관심사id 들 : 관심사 x 제목 x 키워드로 거른
        saved.setInterestId(pair.getRight());
      }
    }


    //관심사, 키워드를 구독중인 유저 얼아내기
    for (UUID interestId : containedId) {
      List<UUID> findUser = interestUserListServiceInterface.getAllUserNowSubscribe(interestId);
      targetUserID.addAll(findUser);
    }

    //찾은 유저에게 알람보내기

    for (UUID userId : targetUserID){
      notificationServiceInterface.addNewsNotice(userId,articleTitle+" 라는 관심있는 뉴스가 등록되었습니다",articleId);
    }

    return saved.getId();
  }

  // Id로 기사 단건 조회
  @Override
  public ArticleBaseDto findById(UUID id) {
    return articleRepository.findById(id)
        .filter(Article::isNotLogicallyDeleted)
        .map(ArticleBaseDto::new)
        .orElseThrow(() -> new RestApiException(ArticleErrorCode.ARTICLE_NOT_FOUND, "해당 ID의 기사를 찾을 수 없습니다."));
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

  // 기사 삭제
  @Override
  @Transactional
  public void deletePhysical(UUID id) {
    Optional<Article> articleOptional = articleRepository.findById(id);
    if (articleOptional.isPresent()) {
      Article article = articleOptional.get();

      // DB 삭제
      articleRepository.deleteById(id);

      // S3 삭제
      String today = LocalDate.now().toString();
      File file = new File("articles_" + today + ".json");
      // 1. 다운로드
      try {
        //S3가 연결될 수 있는가?
        s3UploadArticle.removeArticleFromS3Json(article.getTitle());
      }catch (Exception e){
        log.error("S3 Bucket에 파일이 존재하지 않습니다.", e);
        throw new RestApiException(ArticleErrorCode.S3_FILE_NOT_FOUND, "S3 Bucket에 파일이 존재하지 않습니다.");
      }
      return;
    }
    log.error("해당 ID의 기사를 찾을 수 없습니다.");
    throw new RestApiException(ArticleErrorCode.ARTICLE_NOT_FOUND, "해당 ID의 기사를 찾을 수 없습니다.");
  }

  @Override
  @Transactional
  public void deleteLogical(UUID id) {
    Optional<Article> article = articleRepository.findById(id);
    if (article.isPresent()) {
      article.get().logicalDelete(LocalDateTime.now());
    } else {
      log.error("해당 ID의 기사를 찾을 수 없습니다.");
      throw new RestApiException(ArticleErrorCode.ARTICLE_NOT_FOUND, "해당 ID의 기사를 찾을 수 없습니다.");
    }
  }

  @Override
  public void increaseCountReadUser(UUID id) {
    Article article = articleRepository.findById(id).
        orElseThrow(() -> new NoSuchElementException("기사 없음"));
    article.increaseReadCount();
  }

  @Override
  public ArticlesResponse getPageArticle(String keyword, String interestId, String[] sourceIn,
      String publishDateFrom, String publishDateTo, String orderBy, String direction, String cursor,
      String after, int limit, String userId) {

    sourceIn = null;

    Sort sort = switch (SortField.valueOf(orderBy)) {
      case publishDate -> Sort.by(
          Sort.Order.by("date").with(Sort.Direction.fromString(direction)),
          Sort.Order.by("title").with(Sort.Direction.ASC) // 보조 정렬
      );
      case commentCount -> Sort.by(
          Sort.Order.by("commentCount").with(Sort.Direction.fromString(direction)),
          Sort.Order.by("title").with(Sort.Direction.ASC)
      );
      case viewCount -> Sort.by(
          Sort.Order.by("viewCount").with(Sort.Direction.fromString(direction)),
          Sort.Order.by("title").with(Sort.Direction.ASC)
      );
    };

    int page = 0;
    if (cursor != null && !cursor.isBlank()) {
      try {
        page = Integer.parseInt(cursor);
      } catch (NumberFormatException e) {
        log.error("커서는 숫자여야 합니다.", e);
        throw new RestApiException(ArticleErrorCode.ARTICLE_CURSOR_IS_NUMBER,"커서는 숫자여야 합니다.");
      }
    }

    Pageable pageable = PageRequest.of(page, limit, sort);

    UUID interestUUID = null;
    if (interestId != null && !interestId.isBlank()) {
      interestUUID = UUID.fromString(interestId);
    }

    LocalDateTime from = null;
    if (publishDateFrom != null && !publishDateFrom.isBlank()) {
      from = LocalDateTime.parse(publishDateFrom);
    } else {
      from = LocalDate.now().atStartOfDay();
    }

    LocalDateTime to = null;
    if (publishDateTo != null && !publishDateTo.isBlank()) {
      to = LocalDateTime.parse(publishDateTo);
    } else {
      to = LocalDate.now().plusDays(1).atStartOfDay();
    }

    List<String> sources =
        (sourceIn != null && sourceIn.length > 0) ? Arrays.asList(sourceIn) : null;
    Page<Article> result;

    if (keyword != null && !keyword.isBlank()) {
      keyword = "%" + keyword + "%";

      if (interestUUID != null) {
        if (sources != null) {
          // 8. keyword + interestId + sources
          result = articleRepository.searchByKeywordInterestSources(keyword, interestUUID, from, to, sources, pageable);
        } else {
          // 5. keyword + interestId
          result = articleRepository.searchByKeywordAndInterest(keyword, interestUUID, from, to, pageable);
        }
      } else {
        if (sources != null) {
          // 6. keyword + sources
          result = articleRepository.searchByKeywordAndSources(keyword, from, to, sources, pageable);
        } else {
          // 2. keyword only
          result = articleRepository.searchByKeywordOnly(keyword, from, to, pageable);
        }
      }

    } else {
      if (interestUUID != null) {
        if (sources != null) {
          // 7. interestId + sources
          result = articleRepository.searchByInterestAndSources(interestUUID, from, to, sources, pageable);
        } else {
          // 3. interestId only
          result = articleRepository.searchByInterestOnly(interestUUID, from, to, pageable);
        }
      } else {
        if (sources != null) {
          // 4. sources only
          result = articleRepository.searchBySourcesOnly(from, to, sources, pageable);
        } else {
          // 1. no filters other than date
          result = articleRepository.searchByDateOnly(from, to, pageable);
        }
      }
    }


    List<ArticleBaseDto> content = result.getContent().stream()
        .map(ArticleBaseDto::new)
        .toList();

    boolean hasNext_ = content.size() >= limit;
    String nextAfter = null;
    if (hasNext_ && orderBy.equals(SortField.publishDate.toString())) {
      nextAfter = content.get(content.size() - 1).getPublishDate().toString();
    }

    ArticlesResponse response = new ArticlesResponse();
    response.setContent(content);
    response.setSize(limit);
    response.setTotalElements((int) result.getTotalElements());
    response.setHasNext(hasNext_);
    response.setNextCursor(hasNext_ ? String.valueOf(page + 1) : null); //변경했는데 확인 못함
    response.setNextAfter(nextAfter);

    return response;
  }

  @Override
  @Transactional
  public List<UUID> backup(String from, String to) {
    LocalDate localStartDate = LocalDate.parse(from.substring(0, 10),
        DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    LocalDate localEndDate = LocalDate.parse(to.substring(0, 10),
        DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    List<UUID> result = new ArrayList<>();
    Queue<Article> queue = new LinkedList<>();
    while (localStartDate.isBefore(localEndDate)) {
      // System.out.println("localStartDate = " + localStartDate);
      // System.out.println("localEndDate = " + localEndDate);
      File file = new File("articles_" + localStartDate + "temp.json");
      JSONArray jsonArray;
      try {
        if (s3UploadArticle.exists("articles_" + localStartDate + ".json")) {
          s3UploadArticle.download(file);
          String content = Files.readString(file.toPath());
          jsonArray = new JSONArray(content);
        } else {
          log.error("S3 Bucket에 파일이 존재하지 않습니다.");
          throw new RestApiException(ArticleErrorCode.S3_FILE_NOT_FOUND, "S3 Bucket에 파일이 존재하지 않습니다.");
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
      } catch (Exception e) {
        log.error("FILE IO 작업 중 에러 발생했습니다.", e);
        throw new RestApiException(ArticleErrorCode.S3_FILE_IO_ERROR, "FILE IO 작업 중 에러 발생했습니다.");

      }
      while (!queue.isEmpty()) {
        Article article = queue.poll();
        article =
            articleRepository.findByTitleAndDate(article.getTitle(), article.getDate()) != null
                ? null : article;
        if (article != null) {
          save(new ArticleBaseDto(article));
          result.add(article.getId());
          System.out.println("article = " + article);
        }
      }
    }
    return result;
  }


}
