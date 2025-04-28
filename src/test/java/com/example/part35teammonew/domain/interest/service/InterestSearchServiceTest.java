package com.example.part35teammonew.domain.interest.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.part35teammonew.domain.interest.dto.response.InterestDto;
import com.example.part35teammonew.domain.interest.entity.Interest;
import com.example.part35teammonew.domain.interest.repository.InterestRepository;
import com.example.part35teammonew.domain.interestUserList.entity.InterestUserList;
import com.example.part35teammonew.domain.interestUserList.repository.InterestUserListRepository;
import com.example.part35teammonew.exeception.InterestNotFoundException;

@ExtendWith(MockitoExtension.class)
public class InterestSearchServiceTest {
	@Mock
	private InterestRepository interestRepository;

	@Mock
	private InterestUserListRepository userListRepository;

	@InjectMocks
	private InterestServiceImpl interestService;

	@Test
	@DisplayName("없는 아이디 조회 시 실패 예외")
	public void getInterest_notFound() {
		//given
		UUID interestId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		given(interestRepository.findById(interestId)).willReturn(Optional.empty());

		//when then
		assertThatThrownBy(() -> interestService.getInterestById(interestId, userId))
			.isInstanceOf(InterestNotFoundException.class)
			.hasMessageContaining("관심사를 찾을 수 없습니다: id 오류");

		verify(userListRepository,never()).findByInterest(any());

	}

	@Test
	@DisplayName("단건 조회시 성공 테스트")
	void getInterest_get_success() {
		//given
		UUID interestId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();

		//jpa 에서 꺼낼 엔티티
		Interest interest = new Interest();
		interest.setId(interestId);
		interest.setName("여행");
		interest.setKeywords("제주도,서울,부산");
		given(interestRepository.findById(interestId)).willReturn(Optional.of(interest));

		//mongodb 에서 꺼낼 구독 리스트
		InterestUserList list = InterestUserList.setUpNewInterestUserList(interestId);
		list.addUser(userId);
		list.addUser(UUID.randomUUID());
		given(userListRepository.findByInterest(interestId)).willReturn(Optional.of(list));

		//when
		InterestDto dto = interestService.getInterestById(interestId, userId);

		//then
		assertThat(dto.getId()).isEqualTo(interestId);
		assertThat(dto.getName()).isEqualTo(interest.getName());
		assertThat(dto.getKeywords()).contains("제주도", "서울", "부산");
		assertThat(dto.getSubscriberCount()).isEqualTo(2);
		assertThat(dto.isSubscribedByMe()).isEqualTo(true);

	}

}
