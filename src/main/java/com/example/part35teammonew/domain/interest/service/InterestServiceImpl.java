package com.example.part35teammonew.domain.interest.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.part35teammonew.domain.interest.dto.request.InterestCreateRequest;
import com.example.part35teammonew.domain.interest.dto.request.InterestPageRequest;
import com.example.part35teammonew.domain.interest.dto.response.InterestDto;
import com.example.part35teammonew.domain.interest.dto.response.PageResponse;
import com.example.part35teammonew.domain.interest.entity.Interest;
import com.example.part35teammonew.domain.interest.repository.InterestRepository;
import com.example.part35teammonew.domain.interestUserList.service.InterestUserListServiceInterface;
import com.example.part35teammonew.domain.userActivity.maper.InterestViewMapper;
import com.example.part35teammonew.domain.userActivity.service.UserActivityServiceInterface;
import com.example.part35teammonew.exeception.DuplicateInterestNameException;
import com.example.part35teammonew.exeception.InterestNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InterestServiceImpl implements InterestService {

	private final InterestRepository interestRepository;
	private static final LevenshteinDistance DISTANCE = LevenshteinDistance.getDefaultInstance();
	private final InterestUserListServiceInterface interestUserListServiceInterface;
	private final UserActivityServiceInterface userActivityServiceInterface;
	private final InterestViewMapper interestViewMapper;

	@Override
	public boolean isNameTooSimilar(String name) {

		List<String> existingNames = interestRepository.findAllNames();

		String newName = name.strip().toLowerCase();

		for (String raw : existingNames) {
			String existing = raw.strip().toLowerCase();

			int max = Math.max(existing.length(), newName.length());
			int allowedDistance = Math.max(1, (int)Math.floor(max * 0.2));
			int distance = DISTANCE.apply(existing, newName);

			if (distance <= allowedDistance) {
				return true;
			}
		}

		return false;

	}

	@Override
	@Transactional
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
		interestUserListServiceInterface.createInterestList(savedInterest.getId());

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
	@Transactional
	public InterestDto updateKeywords(UUID interestId, List<String> newKeywords) {
		Interest interest = interestRepository.findById(interestId)
			.orElseThrow(() -> new InterestNotFoundException("관심사를 찾을 수 없습니다: id 오류"));

		interest.setKeywords(String.join(",", newKeywords));
		Interest saved = interestRepository.save(interest);
		InterestDto interestDto = InterestDto.toDto(interest);

		//여기서 구독중인 모든 유저 어기
		List<UUID> userListNowSub = interestUserListServiceInterface.getAllUserNowSubscribe(interestId);
		//구독중인 모든 유저 활동내역 변경
		for (UUID userId : userListNowSub) {
			userActivityServiceInterface.subtractInterestView(userId, interestViewMapper.toDto(interestDto));
			userActivityServiceInterface.addInterestView(userId, interestViewMapper.toDto(interestDto));
		}

		return InterestDto.toDto(saved);
	}

	@Override
	@Transactional
	public void deleteInterest(UUID interestId) {
		Interest interest = interestRepository.findById(interestId)
			.orElseThrow(() -> new InterestNotFoundException("관심사를 찾을 수 없습니다: id 오류"));
		interestRepository.delete(interest);
	}

	@Override
	public InterestDto getInterestById(UUID interestId, UUID userId) {
		//		Interest interest = interestRepository.findById(interestId)
		//			.orElseThrow(() -> new InterestNotFoundException("관심사를 찾을 수 없습니다: id 오류"));
		//
		////		InterestUserList list = userListRepository.findByInterest(interestId)
		////			.orElseGet(() -> InterestUserList.setUpNewInterestUserList(interestId));
		//
		//		long subscriberCount = list.getUserCount();
		//		boolean subscribedByMe = userId != null && list.findUser(userId);
		//
		//		interest.setSubscriberCount(subscriberCount);
		//		interest.setSubscribedMe(subscribedByMe);
		//
		//		return InterestDto.toDto(interest);
		return null;
	}

	@Override
	public PageResponse<InterestDto> listInterests(InterestPageRequest req) {
		Sort.Direction direction = req.direction().equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
		String orderBy = normalizeOrderBy(req.orderBy());

		Sort sort = Sort.by(direction, orderBy).and(Sort.by(direction, "createdAt"));

		Pageable pageable = PageRequest.of(0, req.limit(), sort);

		List<Interest> interests;
		LocalDateTime nextAfter = null;
		Long nextIdAfter = null;
		String nextCursor = null;
		boolean hasNext = false;
		long totalElements = interestRepository.count();

		//  검색어 기반 조회
		if (req.keyword() != null && !req.keyword().isBlank()) {
			Page<Interest> page = interestRepository.searchByNameOrKeyword(req.keyword(), pageable);
			interests = page.getContent();
			totalElements = page.getTotalElements();
			hasNext = page.hasNext();
		} else {
			//  커서 기반 조회
			if (req.orderBy().equalsIgnoreCase("name")) {
				interests = interestRepository.findByNameAfter(req.cursor(), req.after(), pageable);
			} else if (req.orderBy().equalsIgnoreCase("subscriberCount")) {
				Long countCursor = req.cursor() != null ? Long.parseLong(req.cursor()) : null;
				interests = interestRepository.findBySubscriberCountAfter(countCursor, req.after(), pageable);
			} else {
				throw new IllegalArgumentException("지원하지 않는 정렬 기준입니다: " + req.orderBy());
			}
			hasNext = interests.size() == req.limit();

			if (hasNext && !interests.isEmpty()) {
				Interest last = interests.get(interests.size() - 1);
				nextAfter = last.getCreatedAt();
				if (req.orderBy().equalsIgnoreCase("subscriberCount")) {
					long subscriberCount = last.getSubscriberCount();
				} else if (req.orderBy().equalsIgnoreCase("name")) {
					nextCursor = last.getName();
				}
			}
		}

		// 3. DTO 변환 (구독 정보 포함)
		List<InterestDto> content = interests.stream()
			.map(i -> mapToDtoWithSubscription(i, req.userId()))
			.toList();

		return new PageResponse<>(
			content,
			nextCursor,
			nextAfter,
			content.size(),
			totalElements,
			hasNext
		);
	}

	@Override
	public void subscribe(UUID interestId, UUID userId) {
		Interest interest = interestRepository.findById(interestId).orElseThrow(
			() -> new InterestNotFoundException("관심사를 찾을 수 없습니다: id 오류"));

		if (interestUserListServiceInterface.addSubscribedUser(interestId, userId)) {
			interest.setSubscriberCount(interest.getSubscriberCount() + 1);
			InterestDto interestDto = InterestDto.toDto(interest);
			userActivityServiceInterface.addInterestView(userId, interestViewMapper.toDto(interestDto));
		} else {
			throw new InterestNotFoundException("관심사를 찾을 수 없습니다: 관심사 id , user id 오류");
		}
		interestRepository.save(interest);
	}

	@Override
	public void unsubscribe(UUID interestId, UUID userId) {
		Interest interest = interestRepository.findById(interestId)
			.orElseThrow(() -> new InterestNotFoundException("관심사를 찾을 수 없습니다: id 오류"));

		if (interestUserListServiceInterface.subtractSubscribedUser(interestId, userId)) {
			long count = interest.getSubscriberCount();
			interest.setSubscriberCount(Math.max(0, count - 1));
			InterestDto interestDto = InterestDto.toDto(interest);
			userActivityServiceInterface.subtractInterestView(userId, interestViewMapper.toDto(interestDto));
		} else {
			throw new InterestNotFoundException("관심사를 찾을 수 없습니다: 관심사 id , userId 오류");
		}

		interestRepository.save(interest);
	}

	@Override
	public boolean isSubscribed(UUID interestId, UUID userId) {
		return interestUserListServiceInterface.checkUserSubscribe(interestId, userId);
	}

	@Override
	public long countSubscribers(UUID interestId) {
		return interestUserListServiceInterface.countSubscribedUser(interestId);
	}

	//Interest 엔티티 + mongodb 데이터 조합 -> Dto 로 변환
	private InterestDto mapToDtoWithSubscription(Interest entity, UUID userId) {
		UUID interestId = entity.getId();

		boolean subscribedByMe = interestUserListServiceInterface.checkUserSubscribe(interestId, userId);

		long subscriberCount = interestUserListServiceInterface.countSubscribedUser(interestId);

		List<String> keywordList = entity.getKeywords() != null
			? Arrays.stream(entity.getKeywords().split(",")).map(String::strip).toList()
			: List.of();

		return InterestDto.builder()
			.id(entity.getId())
			.name(entity.getName())
			.keywords(keywordList)
			.subscriberCount(subscriberCount)
			.subscribedByMe(subscribedByMe)
			.build();
	}

	private String normalizeOrderBy(String orderBy) {
		if (orderBy == null) {
			throw new IllegalArgumentException("정렬 기준이 누락되었습니다.");
		}
		orderBy = orderBy.replace("\"", "").trim(); // 쌍따옴표, 공백 제거

		if (orderBy.equalsIgnoreCase("name") || orderBy.equalsIgnoreCase("subscriberCount")) {
			return orderBy;
		}
		throw new IllegalArgumentException("지원하지 않는 정렬 기준입니다: " + orderBy);
	}

}
