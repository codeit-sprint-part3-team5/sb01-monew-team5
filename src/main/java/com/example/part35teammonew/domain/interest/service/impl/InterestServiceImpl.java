package com.example.part35teammonew.domain.interest.service.impl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import com.example.part35teammonew.domain.interest.service.InterestService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.part35teammonew.domain.interest.dto.InterestCreateRequest;
import com.example.part35teammonew.domain.interest.dto.InterestPageRequest;
import com.example.part35teammonew.domain.interest.dto.InterestDto;
import com.example.part35teammonew.domain.interest.dto.PageResponse;
import com.example.part35teammonew.domain.interest.entity.Interest;
import com.example.part35teammonew.domain.interest.repository.InterestRepository;
import com.example.part35teammonew.domain.interestUserList.service.InterestUserListServiceInterface;
import com.example.part35teammonew.domain.userActivity.mapper.InterestViewMapper;
import com.example.part35teammonew.domain.userActivity.service.UserActivityServiceInterface;
import com.example.part35teammonew.exception.RestApiException;
import com.example.part35teammonew.exception.errorcode.InterestErrorCode;

import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterestServiceImpl implements InterestService {

  private final InterestRepository interestRepository;
  private static final LevenshteinDistance DISTANCE = LevenshteinDistance.getDefaultInstance();
  private final InterestUserListServiceInterface interestUserListServiceInterface;
  private final UserActivityServiceInterface userActivityServiceInterface;
  private final InterestViewMapper interestViewMapper;

  @Override
  public boolean isNameTooSimilar(String name) {

    log.debug("관심사 이름 유사도 측정 : {}", name);
    List<String> existingNames = interestRepository.findAllNames();

    String newName = name.strip().toLowerCase();

    for (String raw : existingNames) {
      String existing = raw.strip().toLowerCase();

      int max = Math.max(existing.length(), newName.length());
      int allowedDistance = Math.max(1, (int) Math.floor(max * 0.2));
      int distance = DISTANCE.apply(existing, newName);

      if (distance <= allowedDistance) {
        return true;
      }
    }

    return false;

  }

  @Override
  @Transactional
  public InterestDto createInterest(InterestCreateRequest request) {
    String name = request.getName().strip();

    //유사도 검증
    if (isNameTooSimilar(name)) {
      log.error("관심사 생성에 실패 했습니다 : name = {}", name);
      throw new RestApiException(InterestErrorCode.SIMILAR_INTEREST_NAME, "관심사 생성에 실패 했습니다.");
    }

    //List keywords -> DB 저장용 String 으로 변환
    String joinedKeywords = String.join(",", request.getKeywords());

    //엔티티 생성
    Interest toSave = new Interest();
    toSave.setName(name);
    toSave.setKeywords(joinedKeywords);

    //저장
    Interest savedInterest = interestRepository.save(toSave);
    interestUserListServiceInterface.createInterestList(savedInterest.getId());

    log.info("관심사 생성 성공 : interestId = {}", savedInterest.getId());

    return InterestDto.toDto(savedInterest);

  }

  @Override
  @Transactional
  public InterestDto updateKeywords(UUID interestId, List<String> newKeywords) {
    Interest interest = interestRepository.findById(interestId)
        .orElseThrow(() -> {
          log.error("존재하지 않는 관심사 : {}", interestId);
          return new RestApiException(InterestErrorCode.INTEREST_NOT_FOUND, "키워드 수정에 실패 했습니다.");
        });

    interest.setKeywords(String.join(",", newKeywords));
    Interest saved = interestRepository.save(interest);
    InterestDto interestDto = InterestDto.toDto(interest);

    //여기서 구독중인 모든 유저 확인
    List<UUID> userListNowSub = interestUserListServiceInterface.getAllUserNowSubscribe(interestId);
    //구독중인 모든 유저 활동내역 변경
    for (UUID userId : userListNowSub) {
      userActivityServiceInterface.subtractInterestView(userId,
          interestViewMapper.toDto(interestDto));
      userActivityServiceInterface.addInterestView(userId, interestViewMapper.toDto(interestDto));
    }
    log.info("관심사 업데이트 완료 : interestId = {}, 새로운 키워드 = {}", saved.getId(), newKeywords);
    return InterestDto.toDto(saved);
  }

  @Override
  @Transactional
  public void deleteInterest(UUID interestId) {
    Interest interest = interestRepository.findById(interestId)
        .orElseThrow(() -> {
          log.error("존재하지 않는 관심사 : {}", interestId);
          return new RestApiException(InterestErrorCode.INTEREST_NOT_FOUND, "관심사 삭제에 실패 했습니다.");
        });

    interestRepository.delete(interest);
  }

  @Override
  public List<Pair<String, UUID>> getInterestList() {
    List<Interest> interestList = interestRepository.findAll();

    return interestList.stream()
        .flatMap(interest -> {
          if (interest.getKeywords() == null || interest.getKeywords().isBlank()) {
            log.error("키워드가 비워져 있습니다 : interestId = {}", interest.getId());
            throw new IllegalArgumentException("키워드는 비어 있을 수 없습니다");
          }
          List<String> keywords = Stream.of(interest.getKeywords().split(","))
              .map(String::trim)
              .toList();
          log.info("관심사 전체 리스트 반환 완료");
          // 이름  키워드를 하나의 스트림으로
          return Stream.concat(
              Stream.of(interest.getName().strip()),
              keywords.stream()
          ).map(value -> Pair.of(value, interest.getId()));
        })
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<InterestDto> listInterests(InterestPageRequest req) {

    Sort.Direction direction =
        req.direction().equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
    String orderBy = normalizeOrderBy(req.orderBy());

    Sort sort = Sort.by(
        new Sort.Order(direction, orderBy),
        new Sort.Order(direction, "createdAt")
    );

    String keyword = req.keyword();
    boolean isSearching = keyword != null && !keyword.trim().isEmpty();

    log.info("커서 요청 수신 - 관심사 목록 조회: 정렬 = {}, 방향 = {}, 검색어 = {}, 커서 = {}", orderBy, direction,
        keyword, req.safeCursor());

    int limit;
    if (isSearching) {
      limit = req.limit() > 0 ? req.limit() : 10;
    } else {
      limit = Integer.MAX_VALUE;
    }

    Pageable pageable = PageRequest.of(0, limit, sort);

    List<Interest> interests;
    LocalDateTime nextAfter = null;
    String nextCursor = null;
    boolean hasNext = false;
    long totalElements = interestRepository.count();

    if (isSearching) {
      Page<Interest> page = interestRepository.searchByNameOrKeyword(keyword.trim(), pageable);
      interests = page.getContent();
      totalElements = page.getTotalElements();
      hasNext = page.hasNext();
    } else {
      String rawCursor = req.safeCursor();

      boolean isBlankCursor = rawCursor == null || rawCursor.isBlank();
      String nameCursor = isBlankCursor ? null : rawCursor;
      Long countCursor =
          (isBlankCursor || !orderBy.equals("subscriberCount")) ? null : Long.parseLong(rawCursor);

      LocalDateTime after = (rawCursor == null) ? null : req.after();

      log.debug("커서 기반 페이지네이션 적용 - orderBy = {}, cursor = {}, after = {}", orderBy, rawCursor,
          after);

      if (orderBy.equalsIgnoreCase("name")) {
        interests = direction == Sort.Direction.ASC
            ? interestRepository.findByNameAfter(nameCursor, after, pageable)
            : interestRepository.findByNameBefore(nameCursor, after, pageable);
      } else if (orderBy.equalsIgnoreCase("subscriberCount")) {
        interests = direction == Sort.Direction.ASC
            ? interestRepository.findBySubscriberCountAfter(countCursor, after, pageable)
            : interestRepository.findBySubscriberCountBefore(countCursor, after, pageable);
      } else {
        log.error("정렬기준이 올바르지 않습니다 : orderBy = {}", req.orderBy());
        throw new IllegalArgumentException("지원하지 않는 정렬 기준입니다: " + req.orderBy());
      }

      hasNext = interests.size() == req.limit();

      if (hasNext && !interests.isEmpty()) {
        Interest last = interests.get(interests.size() - 1);
        nextAfter = last.getCreatedAt();
        if (orderBy.equalsIgnoreCase("subscriberCount")) {
          nextCursor = String.valueOf(last.getSubscriberCount());
        } else if (orderBy.equalsIgnoreCase("name")) {
          nextCursor = last.getName();
        }
      }
    }

    List<InterestDto> content = interests.stream()
        .map(i -> mapToDtoWithSubscription(i, req.userId()))
        .toList();

    log.info("관심사 조회 완료 - 조회 건수 = {}, hasNext = {}, nextCursor = {}", content.size(), hasNext,
        nextCursor);
    return new PageResponse<>(
        content,
        nextCursor,
        nextAfter,
        content.size(),
        totalElements,
        hasNext
    );
  }

  @Override
  public void subscribe(UUID interestId, UUID userId) {
    Interest interest = interestRepository.findById(interestId).orElseThrow(() -> {
      log.error("존재하지 않는 관심사 : interestId = {}", interestId);
      return new RestApiException(InterestErrorCode.INTEREST_NOT_FOUND, "구독에 실패 했습니다.");
    });

    if (interestUserListServiceInterface.addSubscribedUser(interestId, userId)) {
      interest.setSubscriberCount(interest.getSubscriberCount() + 1);
      InterestDto interestDto = InterestDto.toDto(interest);
      userActivityServiceInterface.addInterestView(userId, interestViewMapper.toDto(interestDto));
    } else {
      log.error("존재하지 않는 관심사 : interestId = {}", interestId);
      throw new RestApiException(InterestErrorCode.INTEREST_NOT_FOUND, "구독에 실패 했습니다.");
    }
    log.info("관심사 구독 완료 : interestId = {}, userId = {}", interestId, userId);
    interestRepository.save(interest);
  }

  @Override
  public void unsubscribe(UUID interestId, UUID userId) {
    Interest interest = interestRepository.findById(interestId).orElseThrow(() -> {
      log.error("존재하지 않는 관심사 : interestId = {}", interestId);
      return new RestApiException(InterestErrorCode.INTEREST_NOT_FOUND, "구독 해제에 실패 했습니다.");
    });

    if (interestUserListServiceInterface.subtractSubscribedUser(interestId, userId)) {
      long count = interest.getSubscriberCount();
      interest.setSubscriberCount(Math.max(0, count - 1));
      InterestDto interestDto = InterestDto.toDto(interest);
      userActivityServiceInterface.subtractInterestView(userId,
          interestViewMapper.toDto(interestDto));
    } else {
      log.error("존재하지 않는 관심사 : interestId = {}", interestId);
      throw new RestApiException(InterestErrorCode.INTEREST_NOT_FOUND, "구독 해제에 실패 했습니다.");
    }
    log.info("관심사 해제 구독 완료 : interestId = {}, userId = {}", interestId, userId);
    interestRepository.save(interest);
  }

  @Override
  public boolean isSubscribed(UUID interestId, UUID userId) {
    return interestUserListServiceInterface.checkUserSubscribe(interestId, userId);
  }

  @Override
  public long countSubscribers(UUID interestId) {
    return interestUserListServiceInterface.countSubscribedUser(interestId);
  }

  //Interest 엔티티 + mongodb 데이터 조합 -> Dto 로 변환
  private InterestDto mapToDtoWithSubscription(Interest entity, UUID userId) {
    UUID interestId = entity.getId();

    boolean subscribedByMe = interestUserListServiceInterface.checkUserSubscribe(interestId,
        userId);

    long subscriberCount = interestUserListServiceInterface.countSubscribedUser(interestId);

    List<String> keywordList = entity.getKeywords() != null
        ? Arrays.stream(entity.getKeywords().split(",")).map(String::strip).toList()
        : List.of();

    return InterestDto.builder()
        .id(entity.getId())
        .name(entity.getName())
        .keywords(keywordList)
        .subscriberCount(subscriberCount)
        .subscribedByMe(subscribedByMe)
        .build();
  }

  private String normalizeOrderBy(String orderBy) {
    if (orderBy == null) {
      throw new IllegalArgumentException("정렬 기준이 누락되었습니다.");
    }
    orderBy = orderBy.replace("\"", "").strip();

    if (orderBy.equalsIgnoreCase("name") || orderBy.equalsIgnoreCase("subscriberCount")) {
      return orderBy;
    }
    throw new IllegalArgumentException("지원하지 않는 정렬 기준입니다: " + orderBy);
  }

}