package com.example.part35teammonew.exception.errorcode;

import org.springframework.http.HttpStatus;

import com.example.part35teammonew.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommentErrorCode implements ErrorCode {
  COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT_001", "해당 댓글이 존재하지 않습니다."),
  COMMENT_UPDATE_UNAUTHORIZED(HttpStatus.FORBIDDEN, "COMMENT_002", "댓글 작성자만 수정할 수 있습니다."),
  COMMENT_DELETE_UNAUTHORIZED(HttpStatus.FORBIDDEN, "COMMENT_003", "댓글 작성자만 삭제할 수 있습니다."),
  COMMENT_LIKE_CONFLICT(HttpStatus.CONFLICT, "COMMENT_004", "이미 좋아요를 누른 댓글입니다."),
  COMMENT_LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT_005", "해당 댓글에 좋아요를 누르지 않았습니다."),

  // 추가된 에러 코드
  COMMENT_UPDATE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMENT_006", "댓글 수정 중 오류가 발생했습니다."),
  COMMENT_DELETE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMENT_007", "댓글 삭제 중 오류가 발생했습니다."),
  COMMENT_FIND_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMENT_008", "댓글 조회 중 오류가 발생했습니다."),
  COMMENT_LIKE_COUNT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMENT_009", "댓글 좋아요 수 조회 중 오류가 발생했습니다.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;
}