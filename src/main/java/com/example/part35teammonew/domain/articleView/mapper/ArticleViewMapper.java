package com.example.part35teammonew.domain.articleView.mapper;

import com.example.part35teammonew.domain.articleView.dto.ArticleViewDto;
import com.example.part35teammonew.domain.articleView.entity.ArticleView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ArticleViewMapper {

  @Mapping(source = "articleId", target = "articleId")
  @Mapping(source = "count", target = "count")
  @Mapping(source = "readUserIds", target = "readUserIds")
  ArticleViewDto toDto(ArticleView articleView);

}
