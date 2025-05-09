package com.example.part35teammonew.domain.userActivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.example.part35teammonew.domain.userActivity.dto.ArticleInfoView;
import com.example.part35teammonew.domain.userActivity.dto.InterestView;
import com.example.part35teammonew.domain.userActivity.dto.LikeCommentView;
import com.example.part35teammonew.domain.userActivity.dto.RecentCommentView;
import com.example.part35teammonew.domain.userActivity.entity.UserActivity;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserActivityTest {

  private UserActivity userActivity;
  private UUID userId;
  private LocalDateTime createdAt;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    createdAt = LocalDateTime.now();
    userActivity = UserActivity.setUpNewUserActivity(createdAt, userId, "구황작물",
        "감자@고구마.com");
  }

  @Test
  @DisplayName("생성시 제대로 되어있는지 확인")
  void createUserActivity() {
    assertThat(userActivity.getUserId()).isEqualTo(userId);
    assertThat(userActivity.getNickName()).isEqualTo("구황작물");
    assertThat(userActivity.getEmail()).isEqualTo("감자@고구마.com");
    assertThat(userActivity.getSubscriptions()).isEmpty();
    assertThat(userActivity.getRecentcomments()).isEmpty();
    assertThat(userActivity.getLikeComment()).isEmpty();
    assertThat(userActivity.getArticleViews()).isEmpty();
  }

  @Test
  @DisplayName("subscriptions에 추가 되는지")
  void updateSubscriptions_addNewInterest() {
    InterestView interest = InterestView.builder()
        .interestId(UUID.randomUUID())
        .interestName("아스파라거스")
        .build();

    userActivity.updateSubscriptions(interest);

    assertThat(userActivity.getSubscriptions()).contains(interest);
  }

  @Test
  @DisplayName("최신댓글 최대 갯수 10개 유지")
  void updateRecentComments_limitTo10() {
    for (int i = 0; i < 12; i++) {
      RecentCommentView comment = RecentCommentView.builder()
          .id(UUID.randomUUID())
          .content("댓글" + i)
          .createdAt(LocalDateTime.now())
          .build();
      userActivity.updateComments(comment);
    }

    assertThat(userActivity.getRecentcomments()).hasSize(10);
    assertThat(userActivity.getRecentcomments().peek().getContent()).isEqualTo("댓글2");
  }

  @Test
  @DisplayName("좋아요 댓글 10개 유지")
  void updateCommentLikes_limitTo10() {
    for (int i = 0; i < 20; i++) {
      LikeCommentView like = LikeCommentView.builder()
          .id(UUID.randomUUID())
          .commentContent("댓글 " + i)
          .createdAt(LocalDateTime.now())
          .build();
      userActivity.updateCommentLikes(like);
    }

    assertThat(userActivity.getLikeComment()).hasSize(10);
    assertThat(userActivity.getLikeComment().peek().getCommentContent()).isEqualTo("댓글 10");
  }

  @Test
  @DisplayName("조회 글 10개 유지")
  void updateArticleViews_limitTo10() {
    for (int i = 0; i < 11; i++) {
      ArticleInfoView view = ArticleInfoView.builder()
          .id(UUID.randomUUID())
          .articleId(UUID.randomUUID())
          .articleTitle("기사 " + i)
          .createdAt(Instant.now())
          .build();
      userActivity.updateArticleViews(view);
    }

    assertThat(userActivity.getArticleViews()).hasSize(10);
    assertThat(userActivity.getArticleViews().peek().getArticleTitle()).isEqualTo("기사 1");
  }
}