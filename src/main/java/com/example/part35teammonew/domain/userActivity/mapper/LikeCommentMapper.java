package com.example.part35teammonew.domain.userActivity.mapper;

import com.example.part35teammonew.domain.comment.dto.CommentDto;
import com.example.part35teammonew.domain.comment.dto.CommentLikeResponse;
import com.example.part35teammonew.domain.userActivity.dto.LikeCommentView;
import java.time.Instant;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class LikeCommentMapper {

  public LikeCommentView toDto(CommentDto comment,
      CommentLikeResponse commentLike) {
    return LikeCommentView.builder()
        .id(comment.getId())
        .createdAt(comment.getCreatedAt())
        .articleId(comment.getArticleId())
        .articleTitle(comment.getArticleTitle())
        .commentUserId(comment.getUserId())
        .commentUserNickname(comment.getUserNickname())
        .commentContent(comment.getContent())
        .commentLikeCount(comment.getLikeCount())
        .commentCreatedAt(Instant.now())
        .build();

  }

}
