package com.example.part35teammonew.domain.interest.controller.docs;

import com.example.part35teammonew.domain.interest.dto.InterestCreateRequest;
import com.example.part35teammonew.domain.interest.dto.InterestUpdateRequest;
import com.example.part35teammonew.domain.interest.dto.InterestDto;
import com.example.part35teammonew.domain.interest.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Interests", description = "관심사 관리 API")
public interface InterestApi {

  @Operation(summary = "관심사 생성", description = "관심사를 생성합니다.")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201", description = "생성 성공",
          content = @Content(schema = @Schema(implementation = InterestDto.class))
      ),
      @ApiResponse(
          responseCode = "400", description = "잘못된 요청(입력값 검증 실패)",
          content = @Content()
      ),
      @ApiResponse(
          responseCode = "409", description = "유사 관심사 중복",
          content = @Content
      ),
      @ApiResponse(
          responseCode = "500", description = "서버 내부 오류",
          content = @Content
      )
  })
  ResponseEntity<InterestDto> create(
      @Valid @RequestBody @Parameter(description = "관심사 생성 요청", required = true) InterestCreateRequest request);


  @Operation(summary = "관심사 정보 수정", description = "관심사 키워드를 수정합니다")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "수정 성공",
          content = @Content(schema = @Schema(implementation = InterestDto.class))
      ),
      @ApiResponse(
          responseCode = "400", description = "잘못된 요청(입력값 검증 실패)",
          content = @Content(schema = @Schema(implementation = InterestDto.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "관심사 정보 없음",
          content = @Content(schema = @Schema(implementation = InterestDto.class))
      ),
      @ApiResponse(
          responseCode = "500", description = "서버 내부 오류",
          content = @Content(schema = @Schema(implementation = InterestDto.class))
      )
  })
  @PatchMapping("{interestId}")
  ResponseEntity<InterestDto> updateKeywords(
      @PathVariable @Parameter(description = "관심사 ID", required = true) UUID interestId,
      @RequestBody @Valid @Parameter(required = true) InterestUpdateRequest request);


  @Operation(summary = "관심사 물리 삭제", description = "관심사를 물리적으로 삭제합니다.")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "204", description = "삭제 성공",
          content = @Content
      ),
      @ApiResponse(
          responseCode = "404", description = "관심사 정보 없음",
          content = @Content
      ),
      @ApiResponse(
          responseCode = "500", description = "서버 내부 오류",
          content = @Content
      )
  })
  @DeleteMapping("{interestId}")
  ResponseEntity<InterestDto> delete(
      @PathVariable @Parameter(description = "관심사 ID", required = true) UUID interestId);


  @Operation(summary = "관심사 구독", description = "관심사를 구독합니다.")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "구독 성공",
          content = @Content(schema = @Schema(implementation = InterestDto.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "관심사 정보 없음",
          content = @Content(schema = @Schema(implementation = InterestDto.class))
      ),
      @ApiResponse(
          responseCode = "500", description = "서버 내부 오류",
          content = @Content(schema = @Schema(implementation = InterestDto.class))
      )
  })
  @PostMapping("{interestId}/subscriptions")
  ResponseEntity<Void> subscribe(
      @PathVariable @Parameter(description = "관심사 ID", required = true) UUID interestId,
      @RequestHeader("Monew-Request-User-ID") @Parameter(description = "요청자 ID", required = true) UUID userId);


  @Operation(summary = "관심사 목록 조회", description = "조건에 맞는 관심사 목록을 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "조회 성공",
          content = @Content(schema = @Schema(implementation = PageResponse.class))
      ),
      @ApiResponse(
          responseCode = "400", description = "잘못된 요청(정렬 기준 오류, 페이지네이션 오류 등)",
          content = @Content(schema = @Schema(implementation = PageResponse.class))
      ),
      @ApiResponse(
          responseCode = "500", description = "서버 내부 오류",
          content = @Content(schema = @Schema(implementation = PageResponse.class))
      )
  })
  @GetMapping
  ResponseEntity<PageResponse<InterestDto>> listInterests(
      @RequestParam(required = false) @Parameter(description = "검색어(관심사 이름 키워드)") String keyword,
      @RequestParam @Parameter(description = "정렬 속성 이름", required = true) String orderBy,
      @RequestParam @Parameter(description = "정렬 방향(ASC, DESC)", required = true) String direction,
      @RequestParam(required = false) @Parameter(description = "커서 값") String cursor,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @Parameter(description = "보조 커서(createdAt) 값") LocalDateTime after,
      @RequestParam @Parameter(description = "커서 페이지 크기", required = true) int limit,
      @RequestHeader("Monew-Request-User-ID") @Parameter(description = "요청자 ID", required = true) UUID userId
  );


  @Operation(summary = "관심사 구독", description = "관심사를 구독합니다.")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "구독 성공",
          content = @Content
      ),
      @ApiResponse(
          responseCode = "404", description = "관심사 정보 없음",
          content = @Content
      ),
      @ApiResponse(
          responseCode = "500", description = "서버 내부 오류",
          content = @Content
      )
  })
  @DeleteMapping("{interestId}/subscriptions")
  ResponseEntity<Void> unsubscribe(
      @PathVariable @Parameter(description = "관심사 ID", required = true) UUID interestId,
      @RequestHeader("Monew-Request-User-ID") @Parameter(description = "요청자 ID", required = true) UUID userId);
}
