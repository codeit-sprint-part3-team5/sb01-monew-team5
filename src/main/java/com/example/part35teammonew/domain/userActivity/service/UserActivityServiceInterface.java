package com.example.part35teammonew.domain.userActivity.service;

import com.example.part35teammonew.domain.userActivity.Dto.ArticleInfoView;
import com.example.part35teammonew.domain.userActivity.Dto.InterestView;
import com.example.part35teammonew.domain.userActivity.Dto.LikeCommentView;
import com.example.part35teammonew.domain.userActivity.Dto.RecentCommentView;
import com.example.part35teammonew.domain.userActivity.Dto.UserActivityDto;
import com.example.part35teammonew.domain.userActivity.Dto.UserInfoDto;
import java.util.UUID;
import org.bson.types.ObjectId;

public interface UserActivityServiceInterface {

  UserActivityDto createUserActivity(UUID id);

  UserActivityDto getUserActivity(UUID id);

  void addInterestView(ObjectId id, InterestView interestView);

  void subtractInterestView(ObjectId id, InterestView interestView);

  UserActivityDto updateUserInformation(ObjectId id, UserInfoDto userInfoDto);

  void addRecentCommentView(ObjectId id, RecentCommentView recentCommentView);

  void addLikeCommentView(ObjectId id, LikeCommentView likeCommentView);

  void addArticleInfoView(ObjectId id, ArticleInfoView articleInfoView);

  void deleteUserActivity(ObjectId id);


}
