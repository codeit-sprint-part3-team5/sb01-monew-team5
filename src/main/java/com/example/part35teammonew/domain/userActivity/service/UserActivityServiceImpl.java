package com.example.part35teammonew.domain.userActivity.service;

import com.example.part35teammonew.domain.userActivity.Dto.ArticleInfoView;
import com.example.part35teammonew.domain.userActivity.Dto.InterestView;
import com.example.part35teammonew.domain.userActivity.Dto.LikeCommentView;
import com.example.part35teammonew.domain.userActivity.Dto.RecentCommentView;
import com.example.part35teammonew.domain.userActivity.Dto.UserActivityDto;
import com.example.part35teammonew.domain.userActivity.Dto.UserInfoDto;
import com.example.part35teammonew.domain.userActivity.entity.UserActivity;
import com.example.part35teammonew.domain.userActivity.maper.UserActivityMapper;
import com.example.part35teammonew.domain.userActivity.repository.UserActivityRepository;
import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserActivityServiceImpl implements UserActivityServiceInterface {

  private final UserActivityRepository userActivityRepository;
  private final UserActivityMapper userActivityMapper;

  UserActivityServiceImpl(@Autowired UserActivityRepository userActivityRepository,
      @Autowired UserActivityMapper userActivityMapper) {
    this.userActivityRepository = userActivityRepository;
    this.userActivityMapper = userActivityMapper;
  }

  @Override
  @Transactional
  public UserActivityDto createUserActivity(Instant createdAt, UUID userId, String nickName,
      String email) {
    UserActivity userActivity = UserActivity.setUpNewUserActivity(createdAt, userId, nickName,
        email);
    userActivityRepository.save(userActivity);
    return userActivityMapper.toDto(userActivity);

  }

  @Override
  @Transactional
  public UserActivityDto getUserActivity(UUID id) {
    Optional<UserActivity> userActivity = userActivityRepository.findByUserId(id);
    return userActivity
        .map(userActivityMapper::toDto)
        .orElse(null);
  }

  @Override
  @Transactional
  public void addInterestView(UUID id, InterestView interestView) {
    UserActivity userActivity = userActivityRepository.findByUserId(id)
        .orElseThrow(() -> new NoSuchElementException("null"));

    userActivity.updateSubscriptions(interestView);

    userActivityRepository.save(userActivity);
  }

  @Override
  @Transactional
  public void subtractInterestView(UUID id, InterestView interestView) {
    UserActivity userActivity = userActivityRepository.findByUserId(id)
        .orElseThrow(() -> new NoSuchElementException("null"));
    userActivity.subtractSubscriptions(interestView);
    userActivityRepository.save(userActivity);
  }

  @Override
  @Transactional
  public UserActivityDto updateUserInformation(UUID id, UserInfoDto userInfoDto) {
    return null;
  }

  @Override
  @Transactional
  public void addRecentCommentView(UUID id, RecentCommentView recentCommentView) {

  }

  @Override
  @Transactional
  public void addLikeCommentView(UUID id, LikeCommentView likeCommentView) {

  }

  @Override
  @Transactional
  public void addArticleInfoView(UUID id, ArticleInfoView articleInfoView) {

  }

  @Override
  @Transactional
  public void deleteUserActivity(UUID id) {

  }
}
