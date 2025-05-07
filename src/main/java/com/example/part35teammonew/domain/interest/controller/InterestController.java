package com.example.part35teammonew.domain.interest.controller;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.part35teammonew.domain.interest.dto.request.InterestCreateRequest;
import com.example.part35teammonew.domain.interest.dto.request.InterestPageRequest;
import com.example.part35teammonew.domain.interest.dto.response.PageResponse;
import com.example.part35teammonew.domain.interest.dto.response.InterestDto;
import com.example.part35teammonew.domain.interest.dto.request.InterestUpdateRequest;
import com.example.part35teammonew.domain.interest.service.InterestService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Slf4j
@RestController
@RequestMapping("/api/interests")
@RequiredArgsConstructor
@Validated
public class InterestController {

  private final InterestService interestService;

  @PostMapping
  public ResponseEntity<InterestDto> create(@RequestBody @Valid InterestCreateRequest request) {
    log.info("POST /api/interests - 관심사 생성 요청 : {} ", request.getName());
    InterestDto dto = interestService.createInterest(request);
    log.info("관심사 생성 완료 - ID : {}", dto.getId());
    return new ResponseEntity<>(dto, HttpStatus.CREATED);
  }

  @PatchMapping("{interestId}")
  public ResponseEntity<InterestDto> updateKeywords(@PathVariable UUID interestId,
      @RequestBody @Valid InterestUpdateRequest request) {
    log.info("PATCH /api/interests/{} - 키워드 수정 요청 : {} ", interestId, request.getKeywords());
    InterestDto dto = interestService.updateKeywords(interestId, request.getKeywords());
    log.info("키워드 수정 완료 - 관심사 ID : {}", dto.getId());
    return new ResponseEntity<>(dto, HttpStatus.OK);
  }

  @DeleteMapping("{interestId}")
  public ResponseEntity<InterestDto> delete(@PathVariable UUID interestId) {
    log.info("DELETE /api/interests/{} - 관심사 삭제 요청", interestId);
    interestService.deleteInterest(interestId);
    log.info("관심사 삭제 완료 - 관심사 ID : {}", interestId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @PostMapping("{interestId}/subscriptions")
  public ResponseEntity<Void> subscribe(@PathVariable UUID interestId,
      @RequestHeader("Monew-Request-User-ID") UUID userId) {
    log.info("POST /api/interests/{}/subscriptions - 구독 요청 user ID : {}", interestId, userId);
    interestService.subscribe(interestId, userId);
    log.info("구독 완료 - 관심사 ID : {}, 유저 ID : {}", interestId, userId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping
  public ResponseEntity<PageResponse<InterestDto>> listInterests(
      @RequestParam(required = false) String keyword,
      @RequestParam String orderBy,
      @RequestParam String direction,
      @RequestParam(required = false) String cursor,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime after,
      @RequestParam int limit,
      @RequestHeader("Monew-Request-User-ID") UUID userId
  ) {
    log.info("GET /api/interests - 목록 조회 요청: keyword={}, orderBy={}, direction={}, cursor={}, after={}, limit={}",
        keyword, orderBy, direction, cursor, after, limit);

    InterestPageRequest request = new InterestPageRequest(
        keyword,
        orderBy,
        direction,
        cursor,
        after,
        limit,
        userId
    );
    PageResponse<InterestDto> result = interestService.listInterests(request);
    log.info("목록 조회 완료 - 반환 건수: {}, 다음 커서: {}", result.size(), result.nextCursor());
    return ResponseEntity.ok(result);
  }

  @DeleteMapping("{interestId}/subscriptions")
  public ResponseEntity<Void> unsubscribe(@PathVariable UUID interestId,
      @RequestHeader("Monew-Request-User-ID") UUID userId) {
    log.info("POST /api/interests/{}/subscriptions - 구독 해제 요청 user ID : {}", interestId, userId);
    interestService.unsubscribe(interestId, userId);
    log.info("구독 취소 완료 - 관심사 ID: {}, 유저 ID: {}", interestId, userId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
