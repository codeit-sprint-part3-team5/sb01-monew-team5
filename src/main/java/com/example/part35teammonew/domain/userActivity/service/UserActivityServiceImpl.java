package com.example.part35teammonew.domain.userActivity.service;

import com.example.part35teammonew.domain.userActivity.Dto.ArticleInfoView;
import com.example.part35teammonew.domain.userActivity.Dto.InterestView;
import com.example.part35teammonew.domain.userActivity.Dto.LikeCommentView;
import com.example.part35teammonew.domain.userActivity.Dto.RecentCommentView;
import com.example.part35teammonew.domain.userActivity.Dto.UserActivityDto;
import com.example.part35teammonew.domain.userActivity.Dto.UserInfoDto;
import java.util.UUID;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

@Service
public class UserActivityServiceImpl implements UserActivityServiceInterface {

  @Override
  public UserActivityDto createUserActivity(UUID id) {
    return null;
  }

  @Override
  public UserActivityDto getUserActivity(UUID id) {
    return null;
  }

  @Override
  public void addInterestView(ObjectId id, InterestView interestView) {

  }

  @Override
  public void subtractInterestView(ObjectId id, InterestView interestView) {

  }

  @Override
  public UserActivityDto updateUserInformation(ObjectId id, UserInfoDto userInfoDto) {
    return null;
  }

  @Override
  public void addRecentCommentView(ObjectId id, RecentCommentView recentCommentView) {

  }

  @Override
  public void addLikeCommentView(ObjectId id, LikeCommentView likeCommentView) {

  }

  @Override
  public void addArticleInfoView(ObjectId id, ArticleInfoView articleInfoView) {

  }

  @Override
  public void deleteUserActivity(ObjectId id) {

  }
}
