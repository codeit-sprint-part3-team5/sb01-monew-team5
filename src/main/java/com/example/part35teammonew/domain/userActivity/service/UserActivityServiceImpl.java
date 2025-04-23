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
    UserActivity userActivity = userActivityRepository.findByUserId(id)
        .orElseThrow(() -> new NoSuchElementException("예외ㅣㅣㅣㅣ"));
    return userActivityMapper.toDto(userActivity);
  }

  @Override
  @Transactional
  public void addInterestView(UUID id, InterestView interestView) {
    UserActivity userActivity = userActivityRepository.findByUserId(id)
        .orElseThrow(() -> new NoSuchElementException("예외ㅣㅣㅣㅣ"));

    userActivity.updateSubscriptions(interestView);

    userActivityRepository.save(userActivity);
  }

  @Override
  @Transactional
  public void subtractInterestView(UUID id, InterestView interestView) {
    UserActivity userActivity = userActivityRepository.findByUserId(id)
        .orElseThrow(() -> new NoSuchElementException("예외ㅣㅣㅣㅣ"));
    userActivity.subtractSubscriptions(interestView);
    userActivityRepository.save(userActivity);
  }

  @Override
  @Transactional
  public UserActivityDto updateUserInformation(UUID id, UserInfoDto userInfoDto) {
    UserActivity userActivity = userActivityRepository.findByUserId(id)
        .orElseThrow(() -> new NoSuchElementException("예외ㅣㅣㅣㅣ"));
    userActivity.updateUserInfo(userInfoDto);
    userActivityRepository.save(userActivity);
    return userActivityMapper.toDto(userActivity);
  }

  @Override
  @Transactional
  public void addRecentCommentView(UUID id, RecentCommentView recentCommentView) {
    UserActivity userActivity = userActivityRepository.findByUserId(id)
        .orElseThrow(() -> new NoSuchElementException("예외ㅣㅣㅣㅣ"));
    userActivity.updateComments(recentCommentView);
    userActivityRepository.save(userActivity);
  }

  @Override
  @Transactional
  public void addLikeCommentView(UUID id, LikeCommentView likeCommentView) {
    UserActivity userActivity = userActivityRepository.findByUserId(id)
        .orElseThrow(() -> new NoSuchElementException("예외ㅣㅣㅣㅣ"));
    userActivity.updateCommentLikes(likeCommentView);
    userActivityRepository.save(userActivity);
  }

  @Override
  @Transactional
  public void addArticleInfoView(UUID id, ArticleInfoView articleInfoView) {
    UserActivity userActivity = userActivityRepository.findByUserId(id)
        .orElseThrow(() -> new NoSuchElementException("예외ㅣㅣㅣㅣ"));
    userActivity.updateArticleViews(articleInfoView);
    userActivityRepository.save(userActivity);
  }

  @Override
  @Transactional
  public void deleteUserActivity(UUID id) {
    userActivityRepository.deleteByUserId(id);
  }

  private UserActivity getUserActivityOrThrow(UUID userId) {
    return userActivityRepository.findByUserId(userId)
        .orElseThrow(
            () -> new NoSuchElementException("UserActivity not found for userId: " + userId));
  }
}
