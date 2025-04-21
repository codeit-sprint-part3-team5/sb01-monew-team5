package com.example.part35teammonew.domain.articleView.mapper;

import com.example.part35teammonew.domain.articleView.Dto.ArticleViewDto;
import com.example.part35teammonew.domain.articleView.entity.ArticleView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ArticleViewMapper {

  ArticleViewMapper INSTANCE = Mappers.getMapper(ArticleViewMapper.class);

  @Mapping(source = "articleId", target = "articleId")
  @Mapping(source = "readUserIds", target = "readUserIds")
  ArticleViewDto toDto(ArticleView articleView);

}
