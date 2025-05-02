package com.example.part35teammonew.exeception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RestApiException extends RuntimeException {
	private final ErrorCode errorCode;
	private final String details;
}
