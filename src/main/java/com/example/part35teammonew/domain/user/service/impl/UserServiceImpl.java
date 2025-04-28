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
import org.springframework.stereotype.Service;

import java.util.UUID;

// TODO 로그, 커스텀 예외 처리
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    // 회원가입
    @Override
    public UserDto register(UserRegisterRequest request) {

        User user = User.builder()
                        .email(request.getEmail())
                        .nickname(request.getNickname())
                        .password(request.getPassword())
                        .build();
        userRepository.save(user);

        return UserDto.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .build();
    }

    // 닉네임 수정
    @Transactional
    @Override
    public UserDto update(UUID userId, UserUpdateRequest request){
        User user = userRepository.findById(userId)
                .orElseThrow(); // TODO 커스텀 예외 추가 후 수정 예정
        user.updateNickname(request.getNickname());
        return UserDto.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();
    }

    // 회원 논리 삭제
    @Override
    public void deleteLogical(UUID userId){
        User user = userRepository.findById(userId)
                .orElseThrow();
        user.deleteLogical();
    }

    // 회원 물리 삭제
    @Override
    public void deletePhysical(UUID userId){
            userRepository.deleteById(userId); // TODO cascade 옵션을 통해 (?) 연관관계 객체도 삭제
    }
}
