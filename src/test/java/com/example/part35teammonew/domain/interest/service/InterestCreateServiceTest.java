package com.example.part35teammonew.domain.interest.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import com.example.part35teammonew.domain.interest.service.impl.InterestServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.part35teammonew.domain.interest.dto.InterestDto;
import com.example.part35teammonew.domain.interest.dto.InterestCreateRequest;
import com.example.part35teammonew.domain.interest.entity.Interest;
import com.example.part35teammonew.domain.interest.repository.InterestRepository;
import com.example.part35teammonew.domain.interestUserList.dto.InterestUserListDto;
import com.example.part35teammonew.domain.interestUserList.service.InterestUserListServiceInterface;
import com.example.part35teammonew.exception.RestApiException;

@ExtendWith(MockitoExtension.class)
public class InterestCreateServiceTest {

	@Mock
	private InterestRepository interestRepository;

	@Mock
	private InterestUserListServiceInterface userListService;

	@InjectMocks
	private InterestServiceImpl interestService;

	@Test
	@DisplayName("유사도가 충분히 통과시 -> 관심사 생성 성공")
	public void createInterest_success() {
		//given
		InterestCreateRequest req = new InterestCreateRequest("여행", List.of("제주도", "서울", "부산"));

		Interest saved = new Interest();
		UUID interestId = UUID.randomUUID();
		saved.setId(interestId);
		saved.setName("여행");
		saved.setKeywords("제주도,서울,부산");
		InterestUserListDto interestUserListDto = new InterestUserListDto(saved.getId(), new HashSet<UUID>());
		given(interestRepository.save(any(Interest.class))).willReturn(saved);
		given(userListService.createInterestList(saved.getId())).willReturn(interestUserListDto);

		//when
		InterestDto dto = interestService.createInterest(req);

		//then
		assertThat(dto.getId()).isEqualTo(interestId);
		assertThat(dto.getName()).isEqualTo("여행");
		assertThat(dto.getKeywords()).contains("제주도", "서울", "부산");

		assertThat(dto.getSubscriberCount()).isEqualTo(0);
		assertThat(dto.isSubscribedByMe()).isFalse();

		// 올바르게 파싱 됐는지 확인
		ArgumentCaptor<Interest> captor = ArgumentCaptor.forClass(Interest.class);
		verify(interestRepository).save(captor.capture());
		Interest interest = captor.getValue();
		assertThat(interest.getName()).isEqualTo("여행");
		assertThat(interest.getKeywords()).isEqualTo("제주도,서울,부산");

	}

	@Test
	@DisplayName("유사도가 80% 이상 유사하면 createInterest 에서 예외처리")
	void createInterest_duplicateName() {
		//given
		given(interestRepository.findAllNames()).willReturn(List.of("제주도여행"));

		InterestCreateRequest req = new InterestCreateRequest("제주도여행1", List.of("제주도"));

		//when & then
		assertThatThrownBy(() -> interestService.createInterest(req))
			.isInstanceOf(RestApiException.class);

		verify(interestRepository, never()).save(any(Interest.class));
	}

}
