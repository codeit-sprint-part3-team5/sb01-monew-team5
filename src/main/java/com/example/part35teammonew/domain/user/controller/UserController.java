package com.example.part35teammonew.domain.user.controller;

import com.example.part35teammonew.domain.user.dto.UserDto;
import com.example.part35teammonew.domain.user.dto.UserLoginRequest;
import com.example.part35teammonew.domain.user.dto.UserRegisterRequest;
import com.example.part35teammonew.domain.user.dto.UserUpdateRequest;
import com.example.part35teammonew.domain.user.service.UserService;
import com.example.part35teammonew.domain.user.service.impl.CustomUserDetailsService;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("api/users")
public class UserController {

    private final UserService userService;
    private final CustomUserDetailsService userDetailsService;

    // 회원가입
    @PostMapping(consumes = "application/json", produces = "application/json")
    ResponseEntity<UserDto> registerUser(@RequestBody @Valid UserRegisterRequest request){
        UserDto createdUserDto = userService.register(request);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(createdUserDto);
    }

    // 로그인
    @PostMapping("/login")
    ResponseEntity<UserDto> login(@RequestBody UserLoginRequest request, HttpServletRequest httpRequest){
        UserDto userDto = userService.login(request);

        // 인증 정보 생성
        UserDetails userDetails = userDetailsService.loadUserByUsername(userDto.getEmail());

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        // SecurityContext에 설정
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        // 세션에 SecurityContext 저장 = 세션 생성
        HttpSession session = httpRequest.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);


        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Monew-Request-User-ID", userDto.getId().toString())
                .body(userDto);
    }

    // 닉네임 수정
    @PatchMapping("/{userId}")
    ResponseEntity<UserDto> updateUser(@PathVariable(value = "userId") UUID userId, @RequestBody UserUpdateRequest request) {
        UserDto updatedUserDto = userService.update(userId, request);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(updatedUserDto);
    }

    // 회원 논리 삭제
    @DeleteMapping("/{userId}")
    ResponseEntity<Void> deleteUserLogical(@PathVariable(value = "userId") UUID userId){
        userService.deleteLogical(userId);
        return ResponseEntity
            .status(HttpStatus.OK)
            .build();
    }

    // 회원 물리 삭제
    @DeleteMapping("/{userId}/hard")
    ResponseEntity<Void> deleteUserPhysical(@PathVariable(value = "userId") UUID userId){
        userService.deletePhysical(userId);
        return ResponseEntity
            .status(HttpStatus.OK)
            .build();
    }
}
