package com.example.part35teammonew.domain.notification.service;

import com.example.part35teammonew.domain.notification.Dto.CursorPageRequest;
import com.example.part35teammonew.domain.notification.Dto.CursorPageResponse;
import com.example.part35teammonew.domain.notification.Dto.NotificationDto;
import com.example.part35teammonew.domain.notification.entity.Notification;
import com.example.part35teammonew.domain.notification.repository.NotificationRepository;
import com.example.part35teammonew.exeception.RestApiException;
import com.example.part35teammonew.exeception.errorcode.NotificationErrorCode;
import com.example.part35teammonew.exeception.notification.WrongUserNotification;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationServiceInterface {


  private final NotificationRepository notificationRepository;
  private final MongoTemplate mongoTemplate;

  public NotificationServiceImpl(@Autowired NotificationRepository notificationRepository,
      MongoTemplate mongoTemplate) {
    this.notificationRepository = notificationRepository;
    this.mongoTemplate = mongoTemplate;
  }

  @Transactional
  @Override//뉴스 알림 추가할떄
  public NotificationDto addNewsNotice(UUID userId, String content, UUID resourceId) {
    try {
      Notification notification = Notification.createNewsNotice(userId, content, resourceId, Instant.now());
      notificationRepository.save(notification);
      log.info("뉴스 알림 생성됨: {}", notification.getId());
      return new NotificationDto(notification);
    } catch (Exception e) {
      log.error("뉴스 알림 생성 중 오류 발생", e);
      throw new RestApiException(NotificationErrorCode.NOTIFICATION_CREATE_ERROR, "뉴스 알림 생성 중 오류가 발생했습니다.");
    }
  }

  @Transactional
  @Override//댓글 관련 알림 추가할떄
  public NotificationDto addCommentNotice(UUID userId, String content, UUID resourceId) {
    try {
      Notification notification = Notification.createCommentNotice(userId, content, resourceId, Instant.now());
      notificationRepository.save(notification);
      log.info("댓글 알림 생성됨: {}", notification.getId());
      return new NotificationDto(notification);
    } catch (Exception e) {
      log.error("댓글 알림 생성 중 오류 발생", e);
      throw new RestApiException(NotificationErrorCode.NOTIFICATION_CREATE_ERROR, "댓글 알림 생성 중 오류가 발생했습니다.");
    }
  }

  @Transactional
  @Override//알림 아이디로 읽음 처리하기, 유저 아이디로로 교차 검증
  public boolean confirmedReadNotice(ObjectId id, UUID userId) {
    Optional<Notification> optionalNotification = notificationRepository.findById(id);
    if (optionalNotification.isEmpty()) {
      return false;
    }

    Notification notification = optionalNotification.get();

    if (!notification.getUserId().equals(userId)) {
      throw new RestApiException(NotificationErrorCode.WRONG_USER_ID, "요청 id와 알람 주인의 id가 다릅니다");
    }

    boolean wasChanged = notification.confirmedRead();
    if (wasChanged) {
      notificationRepository.save(notification);
      log.info("{} 알림 읽음",id);
    }

    return wasChanged;
  }

  @Transactional
  @Override// 유저의 알림 다 읽음처리
  public boolean confirmedReadAllNotice(UUID userId) {
    try {
      List<Notification> notificationList =
          notificationRepository.findAllByUserIdAndConfirmedIsFalse(userId);

      if (notificationList.isEmpty()) {
        return false;
      }

      notificationList.forEach(Notification::confirmedRead);
      notificationRepository.saveAll(notificationList);
      log.info("{}의 현재 알림 모두 읽음",userId);

      return true;
    } catch (Exception e) {
      log.error("모든 알림 읽음 처리 중 오류 발생: {}", userId, e);
      throw new RestApiException(NotificationErrorCode.NOTIFICATION_UPDATE_ERROR, "전체 알림 읽음 처리 중 오류가 발생했습니다.");
    }
  }


  @Transactional
  @Override // 일주일 지난거 삭제, 따로 실행하는 서비스 있음
  public boolean deleteOldConfirmedNotice() {
    try {
      Instant threshold = Instant.now().minus(7, ChronoUnit.DAYS);
      notificationRepository.deleteAllByConfirmedIsTrueAndCreatedAtBefore(threshold);
      return true;
    } catch (Exception e) {
      log.error("오래된 알림 삭제 중 오류 발생", e);
      throw new RestApiException(NotificationErrorCode.NOTIFICATION_DELETE_ERROR, "오래된 알림 삭제 중 오류가 발생했습니다.");
    }
  }

  @Transactional(readOnly = true)
  @Override
  public CursorPageResponse<NotificationDto> getNoticePage(UUID userId, CursorPageRequest pageRequest) {
    try {
      Instant cursor = pageRequest.getCursor();
      Instant after = pageRequest.getAfter();
      int limit = pageRequest.getLimit();

      Sort.Direction sortDirection = Sort.Direction.ASC;
      Query query = new Query();
      Criteria criteria = Criteria.where("userId").is(userId).and("confirmed").is(false);

      if (cursor != null && after != null && cursor.equals(after)) {
        criteria = criteria.and("createdAt").gt(cursor);
      } else {
        if (cursor != null) {
          criteria = criteria.and("createdAt").gt(cursor);
        }
        if (after != null) {
          criteria = criteria.and("createdAt").gt(after);
        }
      }

      query.addCriteria(criteria);
      query.with(Sort.by(sortDirection, "createdAt"));
      query.limit(limit + 1);

      List<Notification> results = mongoTemplate.find(query, Notification.class);

      boolean hasNext = results.size() > limit;
      String nextCursor = null;
      String nextAfter = null;

      if (hasNext) {
        Notification last = results.get(limit);
        nextCursor = last.getCreatedAt().toString();
        results = results.subList(0, limit);
        nextAfter = results.get(results.size() - 1).getCreatedAt().toString();
      }

      List<NotificationDto> dtoList = results.stream()
          .map(NotificationDto::new)
          .toList();

      long total = notificationRepository.countByUserIdAndConfirmedIsFalse(userId);
      long size = dtoList.size();

      return new CursorPageResponse<>(
          dtoList,
          nextCursor,
          nextAfter,
          hasNext,
          size,
          total
      );
    } catch (Exception e) {
      log.error("알림 목록 조회 중 오류 발생", e);
      throw new RestApiException(NotificationErrorCode.NOTIFICATION_FETCH_ERROR, "알림 목록 조회 중 오류가 발생했습니다.");
    }
  }

}

