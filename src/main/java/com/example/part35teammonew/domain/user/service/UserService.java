package com.example.part35teammonew.domain.user.service;

import com.example.part35teammonew.domain.user.dto.UserDto;
import com.example.part35teammonew.domain.user.entity.User;
import java.util.UUID;

public interface UserService {

    // 회원가입
    UserDto create(User user);

    // 닉네임 수정
    UserDto update(UUID userId, String nickname);

    // 회원 삭제 (물리 삭제 여부 false -> 논리 삭제)
    void delete(UUID userId, boolean isPhysical);

    // 로그인 (JWT 토큰 반환)
    String login(User user);

}
