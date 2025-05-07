package com.example.part35teammonew.exeception.errorcode;

import com.example.part35teammonew.exeception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NotificationErrorCode implements ErrorCode {

  NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTIFICATION_001", "해당 알람이 존재하지 않습니다."),
  WRONG_USER_ID(HttpStatus.NOT_FOUND, "NOTIFICATION_002", "해당 유저와 맞지 않는 id 입니다."),
  NOTIFICATION_CREATE_ERROR(HttpStatus.NOT_FOUND, "NOTIFICATION_003", "알람 생성 실패."),
  NOTIFICATION_UPDATE_ERROR(HttpStatus.NOT_FOUND, "NOTIFICATION_004", "알람 업데이트 실패."),
  NOTIFICATION_DELETE_ERROR(HttpStatus.NOT_FOUND, "NOTIFICATION_005", "알람 삭제 실패."),
  NOTIFICATION_FETCH_ERROR(HttpStatus.NOT_FOUND, "NOTIFICATION_006", "알람 조회 실패.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

}
