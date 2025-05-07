package com.example.part35teammonew.domain.notification.controller.docs;

import com.example.part35teammonew.domain.comment.dto.CommentPageResponse;
import com.example.part35teammonew.domain.notification.Dto.CursorPageResponse;
import com.example.part35teammonew.domain.notification.Dto.NotificationDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Instant;
import java.util.UUID;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Noticfication", description = "알람 관리 API")
public interface NoticficationApi {

  @Operation(summary = "알람 목록 조회", description = "알림 목록을 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "조회 성공",
          content = @Content(schema = @Schema(implementation = CursorPageResponse.class))
      ),
      @ApiResponse(
          responseCode = "400", description = "잘못된 요청 (정렬 기준 오류, 페이지네이션 파라미터 오류 등)",
          content = @Content
      ),
      @ApiResponse(
          responseCode = "500", description = "서버 내부 오류",
          content = @Content
      )
  })
  ResponseEntity<CursorPageResponse<NotificationDto>> getNotifications(
      @RequestParam( required = false)
      @Parameter(description = "커서 값", required = false)
      Instant cursor,
      @RequestParam( required = false)
      @Parameter(description = "보조커서 값", required = false)
      Instant after,
      @RequestParam( )
      @Parameter(description = "커서 페이지 크기")
      int limit,
      @RequestHeader("Monew-Request-User-ID")
      @Parameter(description = "요청자 ID")
      UUID requestUserId
  );

  @Operation(summary = "알람 목록 조회", description = "전체 알림을 한번에 확인합니다.")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "전체 알림 확인 성공",
          content = @Content(schema = @Schema(implementation = String.class))
      ),
      @ApiResponse(
          responseCode = "400", description = "잘못된 요청 (입력값 검증 실패)",
          content = @Content
      ),
      @ApiResponse(
          responseCode = "404", description = "사용자 정보 없음",
          content = @Content
      ),
      @ApiResponse(
          responseCode = "500", description = "서버 내부 오류",
          content = @Content
      )
  })
  @PatchMapping
  ResponseEntity<String> getUserNotificationAllRead(
      @RequestHeader("Monew-Request-User-ID")
      @Parameter(description = "요청자 ID")
      UUID requestUserId
  );

  @Operation(summary = "알람 확인", description = "알림을 확인합니다.")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "전체 알림 확인 성공",
          content = @Content(schema = @Schema(implementation = String.class))
      ),
      @ApiResponse(
          responseCode = "400", description = "잘못된 요청 (입력값 검증 실패)",
          content = @Content
      ),
      @ApiResponse(
          responseCode = "404", description = "사용자 정보 없음",
          content = @Content
      ),
      @ApiResponse(
          responseCode = "500", description = "서버 내부 오류",
          content = @Content
      )
  })
  ResponseEntity<String> getNotificationRead(
      @PathVariable("notificationId")
      @Parameter(description = "알림 ID")
      ObjectId notificationId,
      @RequestHeader("Monew-Request-User-ID")
      @Parameter(description = "요청자 ID")
      UUID requestUserId
  );

}
