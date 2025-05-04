package com.example.part35teammonew.exeception.errorcode;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GlobalErrorCode {

	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GLOBAL_001", "서버 내부 오류가 발생했습니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
