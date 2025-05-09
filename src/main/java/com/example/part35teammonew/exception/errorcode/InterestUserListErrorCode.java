package com.example.part35teammonew.exception.errorcode;

import com.example.part35teammonew.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum InterestUserListErrorCode implements ErrorCode {
  INTEREST_USER_LIST_CREATE_ERROR(HttpStatus.NOT_FOUND, "INTEREST_USER_LIST_001", "interestUserList 생성 실패."),
  INTEREST_USER_LIST_UPDATE_ERROR(HttpStatus.NOT_FOUND, "INTEREST_USER_LIST_002", "interestUserList 변경 실패."),
  INTEREST_USER_LIST_CHECK_ERROR(HttpStatus.NOT_FOUND, "INTEREST_USER_LIST_003", "interestUserList 확인 살패."),
  INTEREST_USER_LIST_READ_ERROR(HttpStatus.NOT_FOUND, "INTEREST_USER_LIST_004", "interestUserList 조회 실해."),
  INTEREST_USER_LIST_DELETE_ERROR(HttpStatus.NOT_FOUND, "INTEREST_USER_LIST_005", "interestUserList 삭제 실패."),
  INTEREST_USER_LIST_NOT_FOUND(HttpStatus.NOT_FOUND, "INTEREST_USER_LIST_006", "interestUserList 확인 실패.");
  private final HttpStatus httpStatus;
  private final String code;
  private final String message;
}
