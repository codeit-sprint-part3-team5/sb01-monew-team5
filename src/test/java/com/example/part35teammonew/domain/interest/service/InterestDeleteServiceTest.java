package com.example.part35teammonew.domain.interest.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.part35teammonew.domain.interest.dto.request.InterestCreateRequest;
import com.example.part35teammonew.domain.interest.dto.InterestDto;
import com.example.part35teammonew.domain.interest.entity.Interest;
import com.example.part35teammonew.domain.interest.repository.InterestRepository;
import com.example.part35teammonew.exeception.DuplicateInterestNameException;

@ExtendWith(MockitoExtension.class)
public class InterestDeleteServiceTest {
	@Mock
	private InterestRepository interestRepository;

	@InjectMocks
	private InterestServiceImpl interestService;
}
