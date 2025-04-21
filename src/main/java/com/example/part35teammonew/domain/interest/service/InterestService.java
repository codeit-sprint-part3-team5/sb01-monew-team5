package com.example.part35teammonew.domain.interest.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;

import com.example.part35teammonew.domain.interest.Enum.SortBy;
import com.example.part35teammonew.domain.interest.dto.InterestCreateRequest;
import com.example.part35teammonew.domain.interest.dto.InterestDto;

public interface InterestService {

	/**
	 * 관심사 이름 유사도 검사 수행 및 boolean 값 반환
	 * @param name 유사도 측정 대상 관심사 이름
	 * @return 유사도 검사 수행 후 80% 이상 유사할 경우 true
	 */
	boolean isNameTooSimilar(String name);

	/**
	 * 관심사 생성
	 * @param request 관심사 이름, 키워드 리스트
	 * @return id, 관심사 이름, 키워드 리스트, 구독자 수, 구독 여부
	 */
	InterestDto createInterest(InterestCreateRequest request);

	/**
	 * 관심사 키워드 수정
	 * @param interestId 관심사 id
	 * @param newKeywords 수정할 키워드 리스트
	 * @return id, 관심사 이름, 키워드 리스트, 구독자 수, 구독 여부
	 */
	InterestDto updateKeywords(UUID interestId, List<String> newKeywords);

	/**
	 * 관심사 삭제
	 * @param interestId 관심사 id
	 */
	void deleteInterest(UUID interestId);

	/**
	 * 관심사 단건 조회
	 * @param interestId 관심사 id
	 * @param userId 현재 user id
	 * @return id, 관심사 이름, 키워드 리스트, 구독자 수, 구독 여부
	 */
	InterestDto getInterestById(UUID interestId, UUID userId);

	Page<InterestDto> listInterests(String search, String cursorValue, SortBy sortBy, int size, UUID userId);

	/**
	 * 관심사 구독
	 */
	void subscribe(UUID interestId, UUID userId);

	/**
	 * 관심사 구독 해제
	 */
	void unsubscribe(UUID interestId, UUID userId);

	/**
	 *관심사에 대한 구독 여부 조회
	 */
	boolean isSubscribed(UUID interestId, UUID userId);

	/**
	 *특정 관심사의 총 구독자 수 조회
	 */
	long countSubscribers(UUID interestId);

}
