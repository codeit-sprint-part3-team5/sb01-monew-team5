package com.example.part35teammonew.domain.UserActivity.entity;

import java.time.Instant;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
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
  private Set<Interest> subscriptions;
  public Queue<Coment> comments;
  public Queue<Coment> commentLikes;
  public Queue<Article> articleViews;

  @Builder
  private UserActivity(Instant createdAt, UUID userId, String nickName, String email) {
    this.userId = userId;
    this.nickName = nickName;
    this.email = email;
    this.createdAt = createdAt;
    this.subscriptions = new HashSet<>();
    this.comments = new LinkedList<>();
    this.commentLikes = new LinkedList<>();
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

  public void updateSubscriptions(Interest interest) {
    if (!subscriptions.contains(interest)) {
      subscriptions.add(readerId);
    } else {
      //예외처리
    }
  }

  public void updateComments(Coment coment) {
    if (comments.size() >= 10) {
      comments.poll();
    }
    comments.add(comment);
  }

  public void updateCommentLikes(Coment coment) {
    if (commentLikes.size() >= 10) {
      commentLikes.poll();
    }
    commentLikes.add(comment);
  }

  public void updateArticleViews(Coment coment) {
    if (articleViews.size() >= 10) {
      articleViews.poll();
    }
    articleViews.add(comment);
  }

}
