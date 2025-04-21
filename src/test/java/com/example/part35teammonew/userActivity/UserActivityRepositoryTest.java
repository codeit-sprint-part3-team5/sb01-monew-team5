package com.example.part35teammonew.userActivity;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.part35teammonew.domain.userActivity.Dto.ArticleInfoView;
import com.example.part35teammonew.domain.userActivity.Dto.InterestView;
import com.example.part35teammonew.domain.userActivity.Dto.LikeComentView;
import com.example.part35teammonew.domain.userActivity.Dto.RecentComentView;
import com.example.part35teammonew.domain.userActivity.entity.UserActivity;
import com.example.part35teammonew.domain.userActivity.repository.UserActivityRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

@DataMongoTest
class UserActivityRepositoryTest {

  @Autowired
  private UserActivityRepository repository;

  @Test
  @DisplayName("저장하고 조회")
  void saveAndFindByUserId_success() {
    UUID userId = UUID.randomUUID();
    UserActivity activity = UserActivity.setUpNewUserActivity(
        Instant.now(), userId, "짜장", "짬뽕@탕수육.com"
    );
    repository.save(activity);

    Optional<UserActivity> result = repository.findByUserId(userId);

    assertThat(result).isPresent();
    assertThat(result.get().getUserId()).isEqualTo(userId);
    assertThat(result.get().getNickName()).isEqualTo("짜장");
  }

  @Test
  @DisplayName("없는거 조회하면 빈거 반환")
  void findByUserId_notFound() {
    Optional<UserActivity> result = repository.findByUserId(UUID.randomUUID());

    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("subscriptions의 업데이트 확인")
  void updateUserActivity_success() {
    UUID userId = UUID.randomUUID();
    UserActivity activity = UserActivity.setUpNewUserActivity(
        Instant.now(), userId, "짜장", "짬뽕@탕수육.com"
    );
    repository.save(activity);

    InterestView interest = InterestView.builder()
        .interestId(UUID.randomUUID())
        .interestName("군만두")
        .build();
    activity.updateSubscriptions(interest);
    repository.save(activity);

    Optional<UserActivity> result = repository.findByUserId(userId);

    assertThat(result).isPresent();
    assertThat(result.get().getSubscriptions()).contains(interest);
  }

  @Test
  @DisplayName("RecentComentView 업데이트 후 확인")
  void updateRecentComments_success() {
    UUID userId = UUID.randomUUID();
    UserActivity activity = UserActivity.setUpNewUserActivity(
        Instant.now(), userId, "짜장", "짬뽕@탕수육.com"
    );

    RecentComentView comment = RecentComentView.builder()
        .id(UUID.randomUUID())
        .content("테스트")
        .createdAt(Instant.now())
        .build();

    activity.updateComments(comment);
    repository.save(activity);

    Optional<UserActivity> result = repository.findByUserId(userId);

    assertThat(result).isPresent();
    assertThat(result.get().recentcomments).hasSize(1);
    assertThat(result.get().recentcomments.peek().getContent()).isEqualTo("테스트");
  }

  @Test
  @DisplayName("LikeComentView 업데이트 확인")
  void updateLikeComments_success() {
    UUID userId = UUID.randomUUID();
    UserActivity activity = UserActivity.setUpNewUserActivity(
        Instant.now(), userId, "짜장", "짬뽕@탕수육.com"
    );

    LikeComentView like = LikeComentView.builder()
        .commentContent("테스트트트틑")
        .createdAt(Instant.now())
        .build();

    activity.updateCommentLikes(like);
    repository.save(activity);

    Optional<UserActivity> result = repository.findByUserId(userId);

    assertThat(result).isPresent();
    assertThat(result.get().likeComment).hasSize(1);
    assertThat(result.get().likeComment.peek().getCommentContent()).isEqualTo("테스트트트틑");
  }

  @Test
  @DisplayName("ArticleInfoView 업데이트 후 조회")
  void updateArticleViews_success() {
    UUID userId = UUID.randomUUID();
    UserActivity activity = UserActivity.setUpNewUserActivity(
        Instant.now(), userId, "짜짱", "짬뽕@탕수육.com"
    );

    ArticleInfoView article = ArticleInfoView.builder()
        .articleId(UUID.randomUUID())
        .articleTitle("기사사사")
        .createdAt(Instant.now())
        .build();

    activity.updateArticleViews(article);
    repository.save(activity);

    Optional<UserActivity> result = repository.findByUserId(userId);

    assertThat(result).isPresent();
    assertThat(result.get().articleViews).hasSize(1);
    assertThat(result.get().articleViews.peek().getArticleTitle()).isEqualTo("기사사사");
  }

  @Test
  @DisplayName("삭제 확인")
  void deleteUserActivity_success() {
    UUID userId = UUID.randomUUID();
    UserActivity activity = UserActivity.setUpNewUserActivity(
        Instant.now(), userId, "과자", "썬칩@새우깡.com"
    );
    repository.save(activity);

    repository.delete(activity);

    Optional<UserActivity> result = repository.findByUserId(userId);
    assertThat(result).isEmpty();
  }
}