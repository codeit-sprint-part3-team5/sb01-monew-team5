package com.example.part35teammonew.domain.UserActivity.entity;

import com.example.part35teammonew.domain.UserActivity.Dto.ArticleDto;
import com.example.part35teammonew.domain.UserActivity.Dto.InterestDTO;
import com.example.part35teammonew.domain.UserActivity.Dto.LikeComentDto;
import com.example.part35teammonew.domain.UserActivity.Dto.RecentComent;
import java.time.Instant;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "UserActivity")
@Getter
public class UserActivity {

  @Id
  private ObjectId id;
  private final UUID userId;
  private String nickName;
  private String email;
  private Instant createdAt;
  private Set<InterestDTO> subscriptions;
  public Queue<RecentComent> recentcomments;
  public Queue<LikeComentDto> likeComment;
  public Queue<ArticleDto> articleViews;

  @Builder
  private UserActivity(Instant createdAt, UUID userId, String nickName, String email) {
    this.userId = userId;
    this.nickName = nickName;
    this.email = email;
    this.createdAt = createdAt;
    this.subscriptions = new HashSet<>();
    this.recentcomments = new LinkedList<>();
    this.likeComment = new LinkedList<>();
    this.articleViews = new LinkedList<>();
  }

  public static UserActivity setUpNewUserActivity(Instant createdAt, UUID userId, String nickName,
      String email) {
    return UserActivity.builder()
        .createdAt(createdAt)
        .userId(userId)
        .nickName(nickName)
        .email(email)
        .build();
  }

  public void updateSubscriptions(InterestDTO interest) {
    if (!subscriptions.contains(interest)) {
      subscriptions.add(interest);
    } else {
      //예외처리
    }
  }

  public void updateComments(RecentComent coment) {
    if (recentcomments.size() >= 10) {
      recentcomments.poll();
    }
    recentcomments.add(coment);
  }

  public void updateCommentLikes(LikeComentDto coment) {
    if (likeComment.size() >= 10) {
      likeComment.poll();
    }
    likeComment.add(coment);
  }

  public void updateArticleViews(ArticleDto article) {
    if (articleViews.size() >= 10) {
      articleViews.poll();
    }
    articleViews.add(article);
  }

}
