package com.example.part35teammonew.exeception;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.example.part35teammonew.exeception.errorcode.GlobalErrorCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
		MethodArgumentNotValidException ex,
		HttpHeaders headers,
		HttpStatusCode status,
		WebRequest request
	) {
		log.error("유효성 검사 실패 : {}", ex.getMessage());

		ErrorResponse errorResponse = ErrorResponse.builder()
			.timestamp(LocalDateTime.now())
			.status(HttpStatus.BAD_REQUEST.value())
			.message("유효성 검사에 실패하셨습니다.")
			.details(ex.getObjectName() + " : " + Objects.requireNonNull(ex.getBindingResult().getFieldError())
				.getDefaultMessage())
			.build();

		return handleExceptionInternal(errorResponse);

	}

	@ExceptionHandler(RestApiException.class)
	public ResponseEntity<Object> handleCustomException(RestApiException ex) {

		log.error("에러 코드 : {}, 에러 발생 : {} ({})", ex.getMessage(), ex.getMessage(), ex.getDetails());

		ErrorResponse errorResponse = ErrorResponse.builder()
			.timestamp(LocalDateTime.now())
			.status(ex.getErrorCode().getHttpStatus().value())
			.message(ex.getMessage())
			.details(ex.getDetails())
			.build();

		return handleExceptionInternal(errorResponse);

	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleBadRequestException(Exception ex) {

		log.error("서버 에러 발생 : {}", ex.getMessage());

		ErrorResponse errorResponse = ErrorResponse.builder()
			.timestamp(LocalDateTime.now())
			.status(GlobalErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus().value())
			.message(GlobalErrorCode.INTERNAL_SERVER_ERROR.getMessage())
			.build();

		return handleExceptionInternal(errorResponse);
	}

	private ResponseEntity<Object> handleExceptionInternal(ErrorResponse errorResponse) {
		return ResponseEntity
			.status(errorResponse.status())
			.body(errorResponse);

	}

}
