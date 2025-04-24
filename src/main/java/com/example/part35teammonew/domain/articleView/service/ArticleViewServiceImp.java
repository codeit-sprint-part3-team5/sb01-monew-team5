package com.example.part35teammonew.domain.articleView.service;

import static com.example.part35teammonew.domain.articleView.entity.ArticleView.setUpNewArticleView;

import com.example.part35teammonew.domain.articleView.Dto.ArticleViewDto;
import com.example.part35teammonew.domain.articleView.entity.ArticleView;
import com.example.part35teammonew.domain.articleView.mapper.ArticleViewMapper;
import com.example.part35teammonew.domain.articleView.repository.ArticleViewRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ArticleViewServiceImp implements ArticleViewServiceInterface {

  private final ArticleViewRepository articleViewRepository;
  private final ArticleViewMapper articleViewMapper;

  public ArticleViewServiceImp(
      @Autowired ArticleViewRepository articleViewRepository,
      @Autowired ArticleViewMapper articleViewMapper) {
    this.articleViewRepository = articleViewRepository;
    this.articleViewMapper = articleViewMapper;
  }

  @Override
  @Transactional
  public ArticleViewDto createArticleView(UUID articleId) {
    ArticleView articleView = setUpNewArticleView(articleId);
    articleViewRepository.save(articleView);
    return articleViewMapper.toDto(articleView);
  }

  @Override
  @Transactional
  public boolean addReadUser(UUID articleId, UUID userId) {
    return articleViewRepository.findByArticleId(articleId)
        .map(articleView -> {
          articleView.addNewReader(userId);
          articleViewRepository.save(articleView);
          return true;
        })
        .orElse(false);
  }

  @Override
  @Transactional(readOnly = true)
  public Long countReadUser(UUID articleId) {
    return articleViewRepository.findByArticleId(articleId)
        .map(ArticleView::getCount)
        .orElse(0L);
  }

  @Override
  @Transactional(readOnly = true) //조회순으로 테스트 그리고 같으면 id순으로
  public List<UUID> getSortByVewCountPageNation(Long cursor, Pageable pageable, String direction) {
    Sort.Order countOrder = direction.equalsIgnoreCase("asc")
        ? Sort.Order.asc("count")
        : Sort.Order.desc("count");

    Sort.Order idOrder = direction.equalsIgnoreCase("asc")
        ? Sort.Order.asc("_id")
        : Sort.Order.desc("_id");

    Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
        Sort.by(countOrder, idOrder));

    List<ArticleView> views = articleViewRepository.findAllOrderByCountDesc(sortedPageable);

    if (cursor != null) {
      views = views.stream()
          .filter(view -> {
            if (direction.equalsIgnoreCase("asc")) {
              return view.getCount() > cursor;
            } else {
              return view.getCount() < cursor;
            }
          })
          .toList();
    }

    return views.stream()
        .map(ArticleView::getArticleId)
        .toList();
  }


}
