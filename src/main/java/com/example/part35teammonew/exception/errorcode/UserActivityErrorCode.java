package com.example.part35teammonew.exception.errorcode;

import com.example.part35teammonew.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserActivityErrorCode implements ErrorCode {
  USER_ACTIVITY_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_ACTIVITY_001", "해당 유저활동이 존재하지 않습니다."),
  USER_ACTIVITY_UPDATE_ERROR(HttpStatus.NOT_FOUND, "USER_ACTIVITY_002", "해당 유저 활동 업데이트 오류."),
  USER_ACTIVITY_CREATE_ERROR(HttpStatus.NOT_FOUND, "USER_ACTIVITY_003", "해당 유저 활동 생성 오류."),
  USER_ACTIVITY_DELETE_FOUND(HttpStatus.NOT_FOUND, "USER_ACTIVITY_004", "해당 유저 활동 삭제 오류.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;
}
