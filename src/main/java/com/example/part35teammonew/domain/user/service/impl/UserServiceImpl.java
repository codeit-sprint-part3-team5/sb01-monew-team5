package com.example.part35teammonew.domain.user.service.impl;

import java.util.UUID;

import com.example.part35teammonew.exeception.RestApiException;
import com.example.part35teammonew.exeception.errorcode.UserErrorCode;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.part35teammonew.domain.user.dto.UserDto;
import com.example.part35teammonew.domain.user.dto.UserLoginRequest;
import com.example.part35teammonew.domain.user.dto.UserRegisterRequest;
import com.example.part35teammonew.domain.user.dto.UserUpdateRequest;
import com.example.part35teammonew.domain.user.entity.User;
import com.example.part35teammonew.domain.user.repository.UserRepository;
import com.example.part35teammonew.domain.user.service.UserService;
import com.example.part35teammonew.domain.userActivity.Dto.UserInfoDto;
import com.example.part35teammonew.domain.userActivity.service.UserActivityServiceInterface;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final UserActivityServiceInterface userActivityServiceInterface;

	// 회원가입
	@Override
	public UserDto register(UserRegisterRequest request) {
		log.info("회원가입 시도: {}", request.getEmail());

		// TODO custom
		if (userRepository.existsByEmailAndIsDeletedFalse(request.getEmail())) {
			throw new RestApiException(UserErrorCode.USER_ALREADY_EXISTS, "이미 등록된 회원입니다.");
		}

		String encryptedPassword = passwordEncoder.encode(request.getPassword());

		User user = User.builder()
				.email(request.getEmail())
				.nickname(request.getNickname())
				.password(encryptedPassword)
				.build();
		User savedUser = userRepository.save(user); // repository save된 버전 = DB에 저장된 버전을 return해야 반환값에 id랑 createdAt이 채워진다

		userActivityServiceInterface.createUserActivity(savedUser.getCreatedAt(), savedUser.getId(),
				savedUser.getNickname(), savedUser.getEmail());// 유저 생성에 맞춰 유저 활동내역 생성

		log.info("회원가입 성공: {}", savedUser.getEmail());

		return UserDto.fromEntity(savedUser);
	}

	// 로그인
	@Transactional(readOnly = true)
	@Override
	public UserDto login(UserLoginRequest request) {
		log.info("로그인 시도: {}", request.getEmail());

		// 이메일로 사용자 검색 (논리 삭제가 되지 않은 사용자여야 함)
		User user = userRepository.findByEmailAndIsDeletedFalse(request.getEmail())
				.orElseThrow(() -> {
					log.warn("로그인 실패 - 존재하지 않는 이메일: {}", request.getEmail());
					throw new RestApiException(UserErrorCode.USER_NOT_FOUND, "존재하지 않는 회원입니다.");
				}); // TODO custom

		// 비밀번호 검증
		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			log.warn("로그인 실패 - 비밀번호 불일치: {}", request.getEmail());
			throw new RestApiException(UserErrorCode.WRONG_PASSWORD, "비밀번호가 일치하지 않습니다."); // TODO custom
		}

		log.info("로그인 성공: {}", request.getEmail());

		return UserDto.fromEntity(user);
	}

	// 닉네임 수정
	@Transactional
	@Override
	public UserDto update(UUID userId, UserUpdateRequest request) {
		log.info("닉네임 변경 시도 - UserId: {}", userId);

		User user = userRepository.findById(userId)
				.orElseThrow(() -> {
					log.warn("닉네임 변경 실패 - 존재하지 않는 사용자: {}", userId);
					throw new RestApiException(UserErrorCode.USER_NOT_FOUND, "존재하지 않는 회원입니다."); // TODO custom: UserNotFoundException으로 변경
				});
		user.updateNickname(request.getNickname());

		userActivityServiceInterface.updateUserInformation(userId, new UserInfoDto(request.getNickname()));

		log.info("닉네임 변경 성공 - UserId: {}, NewNickname: {}", userId, request.getNickname());

		return UserDto.fromEntity(user);
	}

	// 회원 논리 삭제
	@Transactional
	@Override
	public void deleteLogical(UUID userId) {
		log.info("회원 논리 삭제 시도 - UserId: {}", userId);

		User user = userRepository.findById(userId)
				.orElseThrow(() -> {
					log.warn("논리 삭제 실패 - 존재하지 않는 사용자: {}", userId);
					throw new RestApiException(UserErrorCode.USER_NOT_FOUND, "존재하지 않는 회원입니다."); // TODO custom: UserNotFoundException으로 변경
				});
		user.deleteLogical();
		userRepository.save(user);

		log.info("회원 논리 삭제 성공 - UserId: {}", userId);
	}

	// 회원 물리 삭제
	@Transactional
	@Override
	public void deletePhysical(UUID userId) {
		log.info("회원 물리 삭제 시도 - UserId: {}", userId);

		userRepository.deleteById(userId);
		userActivityServiceInterface.deleteUserActivity(userId);

		log.info("회원 물리 삭제 성공 - UserId: {}", userId);
	}
}