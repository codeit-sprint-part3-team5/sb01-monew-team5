package com.example.part35teammonew.domain.interest.service;

import java.util.List;
import java.util.UUID;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.part35teammonew.domain.interest.Enum.SortBy;
import com.example.part35teammonew.domain.interest.dto.request.InterestCreateRequest;
import com.example.part35teammonew.domain.interest.dto.InterestDto;
import com.example.part35teammonew.domain.interest.entity.Interest;
import com.example.part35teammonew.domain.interest.repository.InterestRepository;
import com.example.part35teammonew.exeception.DuplicateInterestNameException;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InterestServiceImpl implements InterestService {

	private final InterestRepository interestRepository;
	private static final LevenshteinDistance levenshtein = LevenshteinDistance.getDefaultInstance();

	@Override
	public boolean isNameTooSimilar(String name) {

		List<String> existingNames = interestRepository.findAllNames();

		String newName = name.strip().toLowerCase();

		for (String raw : existingNames) {
			String existing = raw.strip().toLowerCase();

			int max = Math.max(existing.length(), newName.length());
			int allowedDistance = Math.max(1, (int)Math.floor(max * 0.2));
			int distance = levenshtein.apply(existing, newName);

			if (distance <= allowedDistance) {
				return true;
			}
		}

		return false;

	}

	@Transactional
	@Override
	public InterestDto createInterest(InterestCreateRequest request) {
		String name = request.getName().strip();

		//유사도 검증
		if (isNameTooSimilar(name)) {
			throw new DuplicateInterestNameException("관심사 이름의 유사도가 80% 이상입니다.");
		}

		//List keywords -> DB 저장용 String 으로 변환
		String joinedKeywords = String.join(",", request.getKeywords());

		//엔티티 생성
		Interest toSave = new Interest();
		toSave.setName(name);
		toSave.setKeywords(joinedKeywords);

		//저장
		Interest savedInterest = interestRepository.save(toSave);

		//DTO 변환 todo: 나중에 mapper 클래스 생성하면 리팩토링
		List<String> keywordsList = request.getKeywords();
		return InterestDto.builder()
			.id(savedInterest.getId())
			.name(savedInterest.getName())
			.keywords(keywordsList)
			.subscriberCount(0)
			.subscribedByMe(false)
			.build();

	}

	@Override
	public InterestDto updateKeywords(UUID interestId, List<String> newKeywords) {
		Interest interest = interestRepository.findById(interestId)
			.orElseThrow(() -> new EntityNotFoundException("관심사를 찾을 수 없습니다: id 오류"));

		interest.setKeywords(String.join(",", newKeywords));
		Interest saved = interestRepository.save(interest);

		return InterestDto.toDto(saved);
	}

	@Override
	public void deleteInterest(UUID interestId) {

	}

	@Override
	public InterestDto getInterestById(UUID interestId, UUID userId) {
		return null;
	}

	@Override
	public Page<InterestDto> listInterests(String search, String cursorValue, SortBy sortBy, int size, UUID userId) {
		return null;
	}

	@Override
	public void subscribe(UUID interestId, UUID userId) {

	}

	@Override
	public void unsubscribe(UUID interestId, UUID userId) {

	}

	@Override
	public boolean isSubscribed(UUID interestId, UUID userId) {
		return false;
	}

	@Override
	public long countSubscribers(UUID interestId) {
		return 0;
	}
}
