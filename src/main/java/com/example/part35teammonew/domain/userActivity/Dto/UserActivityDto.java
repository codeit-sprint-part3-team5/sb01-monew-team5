package com.example.part35teammonew.domain.userActivity.Dto;

import java.time.Instant;
import java.util.LinkedList;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserActivityDto {

  //private ObjectId id;
  private UUID userId;
  private String nickName;
  private String email;
  private Instant createdAt;
  private Set<InterestView> subscriptions;
  private LinkedList<RecentCommentView> recentcomments;
  private LinkedList<LikeCommentView> likeComment;
  private LinkedList<ArticleInfoView> articleViews;
}
