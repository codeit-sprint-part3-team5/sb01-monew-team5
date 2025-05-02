package com.example.part35teammonew.exeception.errorcode;

import org.springframework.http.HttpStatus;

import com.example.part35teammonew.exeception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InterestErrorCode implements ErrorCode {
	INTEREST_NOT_FOUND(HttpStatus.NOT_FOUND, "INTEREST_001", "해당 관심사가 존재하지 않습니다."),
	SIMILAR_INTEREST_NAME(HttpStatus.BAD_REQUEST, "INTEREST_002", "해당 관심사 이름의 유사도가 80프로 이상입니다."),
	DUPLICATE_SUBSCRIBED_INTEREST(HttpStatus.BAD_REQUEST,"INTEREST_003", "해당 관심사를 이미 구독하고 있습니다."),
	UNSUBSCRIBED_INTEREST(HttpStatus.BAD_REQUEST,"INTEREST_004", "해당 관심사는 이미 구독되지 않고 있습니다.");


	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
