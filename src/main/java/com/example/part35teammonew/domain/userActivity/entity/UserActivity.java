package com.example.part35teammonew.domain.userActivity.entity;

import com.example.part35teammonew.domain.userActivity.Dto.ArticleView;
import com.example.part35teammonew.domain.userActivity.Dto.InterestView;
import com.example.part35teammonew.domain.userActivity.Dto.LikeComentView;
import com.example.part35teammonew.domain.userActivity.Dto.RecentComentView;
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
  private Set<InterestView> subscriptions;
  public Queue<RecentComentView> recentcomments;
  public Queue<LikeComentView> likeComment;
  public Queue<ArticleView> articleViews;

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

  public void updateSubscriptions(InterestView interest) {
    if (!subscriptions.contains(interest)) {
      subscriptions.add(interest);
    } else {
      //예외처리
    }
  }

  public void updateComments(RecentComentView comment) {
    if (recentcomments.size() >= 10) {
      recentcomments.poll();
    }
    recentcomments.add(comment);
  }

  public void updateCommentLikes(LikeComentView comment) {
    if (likeComment.size() >= 10) {
      likeComment.poll();
    }
    likeComment.add(comment);
  }

  public void updateArticleViews(ArticleView article) {
    if (articleViews.size() >= 10) {
      articleViews.poll();
    }
    articleViews.add(article);
  }

}
