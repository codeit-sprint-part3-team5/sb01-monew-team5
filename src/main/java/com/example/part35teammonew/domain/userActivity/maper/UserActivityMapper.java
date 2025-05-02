package com.example.part35teammonew.domain.userActivity.maper;

import com.example.part35teammonew.domain.userActivity.Dto.ArticleInfoView;
import com.example.part35teammonew.domain.userActivity.Dto.LikeCommentView;
import com.example.part35teammonew.domain.userActivity.Dto.RecentCommentView;
import com.example.part35teammonew.domain.userActivity.Dto.UserActivityDto;
import com.example.part35teammonew.domain.userActivity.entity.UserActivity;
import java.util.Collections;
import java.util.LinkedList;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserActivityMapper {

  UserActivityMapper INSTANCE = Mappers.getMapper(UserActivityMapper.class);

  @Mapping(target = "comments", ignore = true)
  @Mapping(target = "commentLikes", ignore = true)
  @Mapping(target = "articleViews", ignore = true)
  @Mapping(source = "nickName", target = "nickname")
  UserActivityDto toDto(UserActivity userActivity);


  @AfterMapping
  default void reverseFields(UserActivity userActivity, @MappingTarget UserActivityDto dto) {
    if (userActivity.getRecentcomments() != null) {
      LinkedList<RecentCommentView> reversed = new LinkedList<>(userActivity.getRecentcomments());
      Collections.reverse(reversed);
      dto.setComments(reversed);
    }
    if (userActivity.getLikeComment() != null) {
      LinkedList<LikeCommentView> reversed = new LinkedList<>(userActivity.getLikeComment());
      Collections.reverse(reversed);
      dto.setCommentLikes(reversed);
    }
    if (userActivity.getArticleViews() != null) {
      LinkedList<ArticleInfoView> reversed = new LinkedList<>(userActivity.getArticleViews());
      Collections.reverse(reversed);
      dto.setArticleViews(reversed);
    }
  }


}
