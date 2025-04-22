package com.example.part35teammonew.domain.userActivity.service;

import com.example.part35teammonew.domain.userActivity.Dto.ArticleInfoView;
import com.example.part35teammonew.domain.userActivity.Dto.InterestView;
import com.example.part35teammonew.domain.userActivity.Dto.LikeCommentView;
import com.example.part35teammonew.domain.userActivity.Dto.RecentCommentView;
import com.example.part35teammonew.domain.userActivity.Dto.UserActivityDto;
import com.example.part35teammonew.domain.userActivity.Dto.UserInfoDto;
import java.time.Instant;
import java.util.UUID;

public interface UserActivityServiceInterface {

  UserActivityDto createUserActivity(Instant createdAt, UUID userId, String nickName,
      String email);

  UserActivityDto getUserActivity(UUID id);

  void addInterestView(UUID id, InterestView interestView);

  void subtractInterestView(UUID id, InterestView interestView);

  UserActivityDto updateUserInformation(UUID id, UserInfoDto userInfoDto);

  void addRecentCommentView(UUID id, RecentCommentView recentCommentView);

  void addLikeCommentView(UUID id, LikeCommentView likeCommentView);

  void addArticleInfoView(UUID id, ArticleInfoView articleInfoView);

  void deleteUserActivity(UUID id);


}
