package com.example.part35teammonew.domain.user.service.impl;

import com.example.part35teammonew.domain.user.dto.UserDto;
import com.example.part35teammonew.domain.user.dto.UserRegisterRequest;
import com.example.part35teammonew.domain.user.dto.UserUpdateRequest;
import com.example.part35teammonew.domain.user.entity.User;
import com.example.part35teammonew.domain.user.repository.UserRepository;
import com.example.part35teammonew.domain.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

// TODO 로그, 커스텀 예외 처리
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // 회원가입
    @Override
    public UserDto register(UserRegisterRequest request) {
        String encryptedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
            .email(request.getEmail())
            .nickname(request.getNickname())
            .password(encryptedPassword)
            .build();
        User savedUser = userRepository.save(user); // repository save된 버전 = DB에 저장된 버전을 return해야 반환값에 id랑 createdAt이 채워진다

        return UserDto.fromEntity(savedUser);
    }

    // 닉네임 수정
    @Transactional
    @Override
    public UserDto update(UUID userId, UserUpdateRequest request){
        User user = userRepository.findById(userId)
            .orElseThrow(); // TODO 커스텀 예외 추가 후 수정 예정
        user.updateNickname(request.getNickname());
        return UserDto.fromEntity(user);
    }

    // 회원 논리 삭제
    @Transactional
    @Override
    public void deleteLogical(UUID userId){
        User user = userRepository.findById(userId)
            .orElseThrow();
        user.deleteLogical();
        userRepository.save(user);
    }

    // 회원 물리 삭제 - TODO 수정 필요 (Cascade)
    @Transactional
    @Override
    public void deletePhysical(UUID userId){
        userRepository.deleteById(userId);
    }
}
