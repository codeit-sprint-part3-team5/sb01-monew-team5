package com.example.part35teammonew.domain.interest.dto;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterestDto {
	private UUID id;

	private String name;

	private List<String> keywords;

	private long subscriberCount;

	private boolean subscribedByMe;
}
