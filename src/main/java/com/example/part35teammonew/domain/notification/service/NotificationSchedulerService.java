package com.example.part35teammonew.domain.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationSchedulerService {

  private final NotificationServiceInterface notificationService;

  public NotificationSchedulerService(@Autowired NotificationServiceInterface notificationService) {
    this.notificationService = notificationService;
  }

  @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul")
  public void runDeleteOldConfirmedNotice() {
    boolean result = notificationService.deleteOldConfirmedNotice();
    if (result) {
      log.info("일주일 지난 확인된 알림 삭제 완료");
    } else {
      log.info("알림 없음");
    }
  }
}
