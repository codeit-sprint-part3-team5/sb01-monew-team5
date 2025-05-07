package com.example.part35teammonew.exeception.errorcode;

import com.example.part35teammonew.exeception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ArticleViewErrorCode implements ErrorCode {
  ARTICLE_VIEW_CREATE_ERROR(HttpStatus.NOT_FOUND, "ARTICLE_VIEW_001", "아티클 뷰 생성 실패."),
  ARTICLE_VIEW_UPDATE_ERROR(HttpStatus.NOT_FOUND, "ARTICLE_VIEW_002", "아티클 뷰 업데이트 실패."),
  ARTICLE_VIEW_COUNT_ERROR(HttpStatus.NOT_FOUND, "ARTICLE_VIEW_003", "아티클 뷰 조회 실패.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;
}
