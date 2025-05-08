package com.example.part35teammonew.exeception.errorcode;

import com.example.part35teammonew.exeception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ArticleErrorCode implements ErrorCode {
  ARTICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "ARTICLE_001", "해당 댓글이 존재하지 않습니다."),
  ARTICLE_MiISSING_ARTICLE_FIELD_Exception(HttpStatus.BAD_REQUEST, "ARTICLE_002", "제목과 날짜는 필수입니다."),
  ARTICLE_DUPLICATED_SAVED(HttpStatus.BAD_REQUEST, "ARTICLE_003", "중복 저장되었습니다."),
  ARTICLE_CURSOR_IS_NUMBER(HttpStatus.BAD_REQUEST, "ARTICLE_004", "커서는 숫자여야 합니다."),
  ARTICLE_PARSE_UUID(HttpStatus.BAD_REQUEST, "ARTICLE_005", "유효하지 않은 사용자 ID 형식입니다"),
  S3_FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "S3_001", "S3 Bucket에 파일이 존재하지 않습니다."),
  S3_FILE_IO_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S3_002", "FILE IO 작업 중 에러 발생했습니다."),
  S3_FAIL_TO_UPLOAD(HttpStatus.INTERNAL_SERVER_ERROR, "S3_003", "FILE 처리 중 오류 발생"),
  S3_FAIL_TO_DELETE_FILE(HttpStatus.INTERNAL_SERVER_ERROR, "S3_004", "S3 파일 삭제 중 오류 발생"),
  S3_FAIL_TO_DOWNLOAD_FILE(HttpStatus.INTERNAL_SERVER_ERROR, "S3_004", "S3 파일 다운로드 중 오류 발생");
  private final HttpStatus httpStatus;
  private final String code;
  private final String message;
}
