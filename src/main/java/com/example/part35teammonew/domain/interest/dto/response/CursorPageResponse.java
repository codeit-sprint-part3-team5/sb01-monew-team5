package com.example.part35teammonew.domain.interest.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record CursorPageResponse<T>(
	List<T> content,
	LocalDateTime nextCursor,
	Long nextIdAfter,
	int size,
	long totalElements,
	boolean hasNext
) {
}