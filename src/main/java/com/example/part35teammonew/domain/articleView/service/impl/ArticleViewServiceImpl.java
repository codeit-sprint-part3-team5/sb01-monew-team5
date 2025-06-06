package com.example.part35teammonew.domain.articleView.service.impl;

import static com.example.part35teammonew.domain.articleView.entity.ArticleView.setUpNewArticleView;

import com.example.part35teammonew.domain.articleView.dto.ArticleViewDto;
import com.example.part35teammonew.domain.articleView.entity.ArticleView;
import com.example.part35teammonew.domain.articleView.mapper.ArticleViewMapper;
import com.example.part35teammonew.domain.articleView.repository.ArticleViewRepository;
import com.example.part35teammonew.domain.articleView.service.ArticleViewServiceInterface;
import com.example.part35teammonew.exception.RestApiException;
import com.example.part35teammonew.exception.errorcode.ArticleViewErrorCode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ArticleViewServiceImpl implements ArticleViewServiceInterface {

  private final ArticleViewRepository articleViewRepository;
  private final ArticleViewMapper articleViewMapper;

  public ArticleViewServiceImpl(
      @Autowired ArticleViewRepository articleViewRepository,
      @Autowired ArticleViewMapper articleViewMapper) {
    this.articleViewRepository = articleViewRepository;
    this.articleViewMapper = articleViewMapper;
  }

  @Override
  @Transactional  //기사가 만들어 질떄 호출
  public ArticleViewDto createArticleView(UUID articleId) {
    try {
      ArticleView articleView = setUpNewArticleView(articleId);
      ArticleView saved = articleViewRepository.save(articleView);
      return articleViewMapper.toDto(saved);
    } catch (Exception e) {
      throw new RestApiException(ArticleViewErrorCode.ARTICLE_VIEW_CREATE_ERROR, "기사 조회 기록 생성 중 오류가 발생했습니다.");
    }
  }

  @Override
  @Transactional //유저가 기사 읽을떄 호출
  public boolean addReadUser(UUID articleId, UUID userId) {
    try {
      return articleViewRepository.findByArticleId(articleId)
          .map(articleView -> {
            articleView.addNewReader(userId);
            articleViewRepository.save(articleView);
            return true;
          })
          .orElse(false);
    } catch (Exception e) {
      throw new RestApiException(ArticleViewErrorCode.ARTICLE_VIEW_UPDATE_ERROR, "기사 읽음 정보 추가 중 오류가 발생했습니다.");
    }
  }

  @Override
  @Transactional(readOnly = true) //기사의 조회수 호출
  public Long countReadUser(UUID articleId) {
    try {
      return articleViewRepository.findByArticleId(articleId)
          .map(ArticleView::getCount)
          .orElse(0L);
    } catch (Exception e) {
      throw new RestApiException(ArticleViewErrorCode.ARTICLE_VIEW_COUNT_ERROR, "기사 조회수 조회 중 오류가 발생했습니다.");
    }
  }

  @Override
  @Transactional(readOnly = true) //조회순으로 테스트 그리고 같으면 id순으로  direction는 asc 아니면 desc 
  //디폴트는 desc임
  public List<UUID> getSortByVewCountPageNation(Long cursor, Pageable pageable, String direction) {
    Sort.Order countOrder;
    if (direction.equalsIgnoreCase("asc")) {
      countOrder = Sort.Order.asc("count");
    } else {
      countOrder = Sort.Order.desc("count");
    }

    Sort.Order idOrder;
    if (direction.equalsIgnoreCase("asc")) {
      idOrder = Sort.Order.asc("_id"); // MongoDB의 기본 ObjectId 필드
    } else {
      idOrder = Sort.Order.desc("_id");
    }

    Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
        Sort.by(countOrder, idOrder));

    Page<ArticleView> page = articleViewRepository.findAll(sortedPageable);
    List<ArticleView> views = page.getContent();

    if (cursor != null) {
      List<ArticleView> filteredViews = new ArrayList<>();

      for (ArticleView view : views) {
        Long count = view.getCount();
        if (direction.equalsIgnoreCase("asc")) {
          if (count > cursor) {
            filteredViews.add(view);
          }
        } else {
          if (count < cursor) {
            filteredViews.add(view);
          }
        }
      }

      views = filteredViews;
    }
    return views.stream()
        .map(ArticleView::getArticleId)
        .toList();
  }


}
