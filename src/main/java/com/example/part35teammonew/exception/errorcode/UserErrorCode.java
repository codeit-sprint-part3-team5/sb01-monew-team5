package com.example.part35teammonew.exception.errorcode;

import com.example.part35teammonew.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
    USER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "USER_001", "이미 등록된 회원입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_002", "존재하지 않는 회원입니다."),
    WRONG_PASSWORD(HttpStatus.BAD_REQUEST, "USER_003", "비밀번호가 일치하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
