package com.example.part35teammonew.exception;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;


@JsonPropertyOrder({"timestamp", "status", "message", "details"})
public record ErrorResponse(LocalDateTime timestamp, int status, String message, String details) {
	@Builder
	public ErrorResponse {
	}

}
