package com.example.part35teammonew.domain.user.controller.docs;

import com.example.part35teammonew.domain.user.dto.UserDto;
import com.example.part35teammonew.domain.user.dto.UserLoginRequest;
import com.example.part35teammonew.domain.user.dto.UserRegisterRequest;
import com.example.part35teammonew.domain.user.dto.UserUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@Tag(name = "Users", description = "사용자 관리 API")
public interface UserApi {

    @Operation(summary = "회원가입", description = "신규 사용자를 등록합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공",
                    content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "요청 형식 오류 또는 입력값 검증 실패",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content)
    })
    ResponseEntity<UserDto> registerUser(
            @Valid @RequestBody
            @Parameter(description = "회원가입 요청", required = true)
            UserRegisterRequest request
    );

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 사용자 인증을 수행합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 로그인 정보",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content)
    })
    ResponseEntity<UserDto> login(
            @RequestBody
            @Parameter(description = "로그인 요청", required = true)
            UserLoginRequest request,

            HttpServletRequest httpRequest
    );

    @Operation(summary = "닉네임 수정", description = "사용자의 닉네임을 수정합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "404", description = "사용자 정보 없음",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content)
    })
    ResponseEntity<UserDto> updateUser(
            @PathVariable
            @Parameter(description = "사용자 ID", required = true)
            UUID userId,

            @RequestBody
            @Parameter(description = "수정 요청", required = true)
            UserUpdateRequest request
    );

    @Operation(summary = "회원 논리 삭제", description = "사용자를 논리적으로 삭제합니다 (isDeleted=true)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공", content = @Content),
            @ApiResponse(responseCode = "404", description = "사용자 정보 없음", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content)
    })
    ResponseEntity<Void> deleteUserLogical(
            @PathVariable
            @Parameter(description = "사용자 ID", required = true)
            UUID userId
    );

    @Operation(summary = "회원 물리 삭제", description = "사용자를 물리적으로 삭제합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공", content = @Content),
            @ApiResponse(responseCode = "404", description = "사용자 정보 없음", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content)
    })
    ResponseEntity<Void> deleteUserPhysical(
            @PathVariable
            @Parameter(description = "사용자 ID", required = true)
            UUID userId
    );
}
