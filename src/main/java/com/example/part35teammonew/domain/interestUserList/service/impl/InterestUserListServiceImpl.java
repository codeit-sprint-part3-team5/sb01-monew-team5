package com.example.part35teammonew.domain.interestUserList.service.impl;

import static com.example.part35teammonew.domain.interestUserList.entity.InterestUserList.setUpNewInterestUserList;

import com.example.part35teammonew.domain.interestUserList.dto.InterestUserListDto;
import com.example.part35teammonew.domain.interestUserList.entity.InterestUserList;
import com.example.part35teammonew.domain.interestUserList.mapper.InterestUserListMapper;
import com.example.part35teammonew.domain.interestUserList.repository.InterestUserListRepository;
import com.example.part35teammonew.domain.interestUserList.service.InterestUserListServiceInterface;
import com.example.part35teammonew.exception.RestApiException;
import com.example.part35teammonew.exception.errorcode.InterestUserListErrorCode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class InterestUserListServiceImpl implements InterestUserListServiceInterface {


  private final InterestUserListRepository interestUserListRepository;
  private final InterestUserListMapper interestUserListMapper;

  InterestUserListServiceImpl(
      @Autowired InterestUserListRepository interestUserListRepository,
      @Autowired InterestUserListMapper interestUserListMapper
  ) {
    this.interestUserListRepository = interestUserListRepository;
    this.interestUserListMapper = interestUserListMapper;
  }

  @Override
  @Transactional//관심사 만들어 질떄 호출
  public InterestUserListDto createInterestList(UUID interest) {
    try {
      InterestUserList interestUserList = setUpNewInterestUserList(interest);
      interestUserListRepository.save(interestUserList);
      return interestUserListMapper.toDto(interestUserList);
    } catch (Exception e) {
      log.error("관심사 리스트 생성 중 오류 발생: {} ", interest, e);
      throw new RestApiException(InterestUserListErrorCode.INTEREST_USER_LIST_CREATE_ERROR, "관심사 리스트 생성 중 오류 발생");
    }
  }

  @Override
  @Transactional//유저가 관심사 구독하면 작동
  public boolean addSubscribedUser(UUID interestId, UUID userId) {
    InterestUserList interestUserList = findByInterestOrThrow(interestId);
    try {
      interestUserList.addUser(userId);
      interestUserListRepository.save(interestUserList);
      return true;
    } catch (Exception e) {
      log.error("관심사 구독 추가 중 오류 발생: {}  {}", interestId, userId,e);
      throw new RestApiException(InterestUserListErrorCode.INTEREST_USER_LIST_UPDATE_ERROR, "관심사 구독 추가 중 오류 발생");
    }
  }

  @Override
  @Transactional // 유저가 관심사 구독 취소하면 작동
  public boolean subtractSubscribedUser(UUID id, UUID user) {
    InterestUserList interestUserList = findByInterestOrThrow(id);
    try {
      interestUserList.subtractUser(user);
      interestUserListRepository.save(interestUserList);
      return true;
    } catch (Exception e) {
      log.error("관심사 구독 취소 중 오류 발생: {}  {}", id, user,e);
      throw new RestApiException(InterestUserListErrorCode.INTEREST_USER_LIST_UPDATE_ERROR, "관심사 구독 취소 중 오류 발생");
    }
  }

  @Override
  @Transactional(readOnly = true) //유저가 구독중인지 확인
  public boolean checkUserSubscribe(UUID interestId, UUID userId) {
    InterestUserList interestUserList = findByInterestOrThrow(interestId);
    try {
      return interestUserList.findUser(userId);
    } catch (Exception e) {
      log.error("관심사 구독 확인 중 오류 발생: {}  {}", interestId, userId,e);
      throw new RestApiException(InterestUserListErrorCode.INTEREST_USER_LIST_CHECK_ERROR, "관심사 구독 확인 중 오류 발생");
    }
  }

  @Override
  @Transactional(readOnly = true)
  public List<UUID> getAllUserNowSubscribe(UUID interestId) {
    InterestUserList interestUserList = findByInterestOrThrow(interestId);
    try {
      return new ArrayList<>(interestUserList.getSubscribedUser());
    } catch (Exception e) {
      log.error("관심사 구독자 목록 조회 중 오류 발생: {}", interestId ,e);
      throw new RestApiException(InterestUserListErrorCode.INTEREST_USER_LIST_READ_ERROR, "관심사 구독자 목록 조회 중 오류 발생");
    }
  }

  @Override
  @Transactional(readOnly = true) // 관심사 구독중인 사람 수 체크
  public Long countSubscribedUser(UUID interest) {
    InterestUserList interestUserList = findByInterestOrThrow(interest);
    try {
      return interestUserList.getUserCount();
    } catch (Exception e) {
      log.error("관심사 구독자 수 조회 중 오류 발생 : {}",interest);
      throw new RestApiException(InterestUserListErrorCode.INTEREST_USER_LIST_READ_ERROR, "관심사 구독자 수 조회 중 오류 발생");
    }
  }

  @Override
  @Transactional //관심사 물리적 삭제시
  public void deleteInterestList(UUID interest) {
    try {
      interestUserListRepository.deleteByInterest(interest);
    } catch (Exception e) {
      log.error("관심사 리스트 삭제 중 오류 발생 : {}",interest);
      throw new RestApiException(InterestUserListErrorCode.INTEREST_USER_LIST_DELETE_ERROR, "관심사 리스트 삭제 중 오류 발생");
    }
  }

  private InterestUserList findByInterestOrThrow(UUID interestId) {
    return interestUserListRepository.findByInterest(interestId)
        .orElseThrow(() -> new RestApiException(InterestUserListErrorCode.INTEREST_USER_LIST_NOT_FOUND, "해당 관심사 구독 정보가 존재하지 않습니다."));
  }
}
