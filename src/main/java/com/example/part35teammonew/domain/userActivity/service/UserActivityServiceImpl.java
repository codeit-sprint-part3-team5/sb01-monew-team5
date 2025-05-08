package com.example.part35teammonew.domain.userActivity.service;

import com.example.part35teammonew.exeception.RestApiException;
import com.example.part35teammonew.exeception.errorcode.CommentErrorCode;
import com.example.part35teammonew.exeception.errorcode.UserActivityErrorCode;
import com.example.part35teammonew.exeception.userActivity.UserActivityErrorUpdate;
import com.example.part35teammonew.exeception.userActivity.UserActivityNotFoundException;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.part35teammonew.domain.userActivity.Dto.ArticleInfoView;
import com.example.part35teammonew.domain.userActivity.Dto.InterestView;
import com.example.part35teammonew.domain.userActivity.Dto.LikeCommentView;
import com.example.part35teammonew.domain.userActivity.Dto.RecentCommentView;
import com.example.part35teammonew.domain.userActivity.Dto.UserActivityDto;
import com.example.part35teammonew.domain.userActivity.Dto.UserInfoDto;
import com.example.part35teammonew.domain.userActivity.entity.UserActivity;
import com.example.part35teammonew.domain.userActivity.maper.UserActivityMapper;
import com.example.part35teammonew.domain.userActivity.repository.UserActivityRepository;

@Slf4j
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
  public UserActivityDto createUserActivity(LocalDateTime createdAt, UUID userId, String nickName,
      String email) {
    try {
      UserActivity userActivity = UserActivity.setUpNewUserActivity(createdAt, userId, nickName, email);
      userActivityRepository.save(userActivity);
      log.info("{}의 유저 활동 생성", userActivity.getUserId());
      return userActivityMapper.toDto(userActivity);
    } catch (Exception e) {
      log.error("유저 활동 생성 중 예외 발생: {}", userId, e);
      throw new RestApiException(UserActivityErrorCode.USER_ACTIVITY_CREATE_ERROR, "유저 활동 생성 중 오류가 발생했습니다.");
    }

  }

  @Override
  @Transactional(readOnly = true)//관심사 정보 찾기
  public UserActivityDto getUserActivity(UUID id) {
    UserActivity userActivity = userActivityRepository.findByUserId(id)
        .orElseThrow(() -> new RestApiException(UserActivityErrorCode.USER_ACTIVITY_NOT_FOUND, "해당 유저활동이 존재하지 않습니다."));
    return userActivityMapper.toDto(userActivity);
  }

  @Override
  @Transactional //유저가 관심사 구독하면 실행
  public void addInterestView(UUID id, InterestView interestView) {
    UserActivity userActivity = getEntityOrThrow(id);
    try {
      userActivity.updateSubscriptions(interestView);
      userActivityRepository.save(userActivity);
    } catch (Exception e) {
      log.error("관심사 추가 중 예외 발생: {}", id, e);
      throw new RestApiException(UserActivityErrorCode.USER_ACTIVITY_UPDATE_ERROR, "관심사 구독 중 오류가 발생했습니다.");
    }
  }

  @Override
  @Transactional //유저가 관심사 구독 취소할떄
  public void subtractInterestView(UUID id, InterestView interestView) {
    UserActivity userActivity = getEntityOrThrow(id);
    try {
      userActivity.subtractSubscriptions(interestView);
      userActivityRepository.save(userActivity);
    } catch (Exception e) {
      log.error("관심사 제거 중 예외 발생: {}", id, e);
      throw new RestApiException(UserActivityErrorCode.USER_ACTIVITY_UPDATE_ERROR, "관심사 구취소 중 오류가 발생했습니다.");
    }
  }

  @Override
  @Transactional //일단은 닉네임 바꿀떄 실행
  public UserActivityDto updateUserInformation(UUID id, UserInfoDto userInfoDto) {
    UserActivity userActivity = getEntityOrThrow(id);
    try {
      userActivity.updateUserInfo(userInfoDto);
      userActivityRepository.save(userActivity);
      return userActivityMapper.toDto(userActivity);
    } catch (Exception e) {
      log.error("유저 정보 업데이트 중 예외 발생: {}", id, e);
      throw new RestApiException(UserActivityErrorCode.USER_ACTIVITY_UPDATE_ERROR, "유저 정보를 유저활동에 업데이트 중 오류가 발생했습니다.");
    }
  }

  @Override
  @Transactional //유저가 댓글쓰면 실행
  public void addRecentCommentView(UUID id, RecentCommentView recentCommentView) {
    UserActivity userActivity = getEntityOrThrow(id);
    try {
      userActivity.updateComments(recentCommentView);
      userActivityRepository.save(userActivity);
    } catch (Exception e) {
      log.error("댓글 정보 추가 중 예외 발생: {}", id, e);
      throw new RestApiException(UserActivityErrorCode.USER_ACTIVITY_UPDATE_ERROR, "댓글 정보를 유저활동에 추가 중 오류가 발생했습니다.");
    }
  }

  @Override
  @Transactional
  public void addLikeCommentView(UUID id, LikeCommentView likeCommentView) {
    UserActivity userActivity = getEntityOrThrow(id);
    try {
      userActivity.updateCommentLikes(likeCommentView);
      userActivityRepository.save(userActivity);
    } catch (UserActivityErrorUpdate e) {
      throw new RestApiException(UserActivityErrorCode.USER_ACTIVITY_UPDATE_ERROR, "댓글 좋아요 업데이트 실패");
    } catch (Exception e) {
      log.error("댓글 좋아요 추가 중 예외 발생: {}", id, e);
      throw new RestApiException(UserActivityErrorCode.USER_ACTIVITY_UPDATE_ERROR, "댓글 좋아요를 유저활동에 추가 중 오류가 발생했습니다.");
    }
  }

  @Override
  @Transactional//유저가 기사보면 실행
  public void addArticleInfoView(UUID id, ArticleInfoView articleInfoView) {
    UserActivity userActivity = getEntityOrThrow(id);
    try {
      userActivity.updateArticleViews(articleInfoView);
      userActivityRepository.save(userActivity);
    } catch (Exception e) {
      log.error("기사 정보 추가 중 예외 발생: {}", id, e);
      throw new RestApiException(UserActivityErrorCode.USER_ACTIVITY_UPDATE_ERROR, "기사 정보를 유저활동에 추가 중 오류가 발생했습니다.");
    }
  }

  @Override
  @Transactional
  public void deleteUserActivity(UUID id) {
    boolean exists = userActivityRepository.existsByUserId(id);
    if (!exists) {
      throw new RestApiException(UserActivityErrorCode.USER_ACTIVITY_NOT_FOUND, "해당 유저활동이 존재하지 않습니다.");
    }
    try {
      userActivityRepository.deleteByUserId(id);
      log.info("유저 활동 삭제 완료: {}", id);
    } catch (Exception e) {
      log.error("유저 활동 삭제 중 예외 발생: {}", id, e);
      throw new RestApiException(UserActivityErrorCode.USER_ACTIVITY_DELETE_FOUND, "기사 정보 추가 중 오류가 발생했습니다.");
    }


  }

  private UserActivity getEntityOrThrow(UUID id) {
    return userActivityRepository.findByUserId(id)
        .orElseThrow(() -> new RestApiException(UserActivityErrorCode.USER_ACTIVITY_NOT_FOUND, "해당 유저활동이 존재하지 않습니다."));
  }
}
