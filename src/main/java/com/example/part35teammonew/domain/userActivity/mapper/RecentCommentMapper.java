package com.example.part35teammonew.domain.userActivity.mapper;

import com.example.part35teammonew.domain.comment.entity.Comment;
import com.example.part35teammonew.domain.userActivity.dto.RecentCommentView;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class RecentCommentMapper {

  public RecentCommentView toDto(Comment comment) {
    return RecentCommentView.builder()
        .id(comment.getId())
        .articleId(comment.getArticle().getId())
        .articleTitle(comment.getArticle().getTitle())
        .userId(comment.getUser().getId())
        .userNickname(comment.getUserNickname())
        .content(comment.getContent())
        .likeCount(comment.getLikeCount())
        .createdAt(LocalDateTime.now())
        .build();
  }
}
