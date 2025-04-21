package com.example.part35teammonew.domain.userActivity.service;

import com.example.part35teammonew.domain.userActivity.Dto.ArticleInfoView;
import com.example.part35teammonew.domain.userActivity.Dto.InterestView;
import com.example.part35teammonew.domain.userActivity.Dto.LikeCommentView;
import com.example.part35teammonew.domain.userActivity.Dto.RecentCommentView;
import com.example.part35teammonew.domain.userActivity.Dto.UserInfoDto;
import com.example.part35teammonew.domain.userActivity.Dto.UserServiceActivityDto;
import org.bson.types.ObjectId;

public interface userActivityServiceInterface {

  UserServiceActivityDto createUserActivity(ObjectId id);

  UserServiceActivityDto getUserActivity(ObjectId id);

  void addInterestView(ObjectId id, InterestView interestView);

  void subtractInterestView(ObjectId id, InterestView interestView);

  UserServiceActivityDto updateUserInformation(ObjectId id, UserInfoDto userInfoDto);

  void addRecentCommentView(ObjectId id, RecentCommentView recentCommentView);

  void addLikeCommentView(ObjectId id, LikeCommentView likeCommentView);

  void addArticleInfoView(ObjectId id, ArticleInfoView articleInfoView);

  void deleteUserActivity(ObjectId id);


}
