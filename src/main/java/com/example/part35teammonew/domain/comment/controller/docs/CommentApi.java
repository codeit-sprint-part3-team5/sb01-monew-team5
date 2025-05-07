package com.example.part35teammonew.domain.comment.controller.docs;

import com.example.part35teammonew.domain.comment.dto.CommentCreateRequest;
import com.example.part35teammonew.domain.comment.dto.CommentDto;
import com.example.part35teammonew.domain.comment.dto.CommentPageResponse;
import com.example.part35teammonew.domain.comment.dto.CommentUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Tag(name = "Comments", description = "댓글 관리 API")
public interface CommentApi {

  @Operation(summary = "댓글 목록 조회", description = "조건에 맞는 댓글 목록을 조회합니다")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "조회 성공",
          content = @Content(schema = @Schema(implementation = CommentPageResponse.class))
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
  ResponseEntity<CommentPageResponse> getComments(
      @RequestParam(required = false)
      @Parameter(description = "기사 ID", required = false)
      UUID articleId,

      @RequestParam(defaultValue = "createdAt")
      @Parameter(
          description = "정렬 속성 이름",
          schema = @Schema(allowableValues = {"createdAt", "likeCount"}),
          required = true
      )
      String orderBy,

      @RequestParam(defaultValue = "DESC")
      @Parameter(
          description = "정렬 방향 (ASC, DESC)",
          schema = @Schema(allowableValues = {"ASC", "DESC"}),
          required = true
      )
      String direction,

      @RequestParam(required = false)
      @Parameter(description = "커서 값", required = false)
      String cursor,

      @RequestParam(required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
      @Parameter(description = "보조 커서(createdAt) 값", required = false)
      LocalDateTime after,

      @RequestParam
      @Parameter(description = "커서 페이지 크기", required = true)
      Integer limit,

      @RequestHeader("Monew-Request-User-ID")
      @Parameter(description = "요청자 ID", required = true)
      UUID requestUserId
  );

  @Operation(summary = "댓글 등록", description = "새로운 댓글을 등록합니다")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201", description = "등록 성공",
          content = @Content(schema = @Schema(implementation = CommentDto.class))
      ),
      @ApiResponse(
          responseCode = "400", description = "잘못된 요청 (입력값 검증 실패)",
          content = @Content
      ),
      @ApiResponse(
          responseCode = "500", description = "서버 내부 오류",
          content = @Content
      )
  })
  ResponseEntity<CommentDto> createComment(
      @Valid @RequestBody @Parameter(description = "댓글 생성 요청", required = true) CommentCreateRequest request
  );

  @Operation(summary = "댓글 정보 수정", description = "댓글의 내용을 수정합니다")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "수정 성공",
          content = @Content(schema = @Schema(implementation = CommentDto.class))
      ),
      @ApiResponse(
          responseCode = "400", description = "잘못된 요청 (입력값 검증 실패)",
          content = @Content
      ),
      @ApiResponse(
          responseCode = "404", description = "댓글 정보 없음",
          content = @Content
      ),
      @ApiResponse(
          responseCode = "403", description = "댓글 수정 권한 없음",
          content = @Content
      ),
      @ApiResponse(
          responseCode = "500", description = "서버 내부 오류",
          content = @Content
      )
  })
  ResponseEntity<CommentDto> updateComment(
      @PathVariable @Parameter(description = "댓글 ID", required = true) UUID commentId,
      @Valid @RequestBody @Parameter(description = "댓글 수정 요청", required = true) CommentUpdateRequest request,
      @RequestHeader("Monew-Request-User-ID") @Parameter(description = "요청자 ID", required = true) UUID requestUserId
  );

  @Operation(summary = "댓글 논리 삭제", description = "댓글을 논리적으로 삭제합니다 (isDeleted=true)")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "204", description = "삭제 성공",
          content = @Content
      ),
      @ApiResponse(
          responseCode = "404", description = "댓글 정보 없음",
          content = @Content
      ),
      @ApiResponse(
          responseCode = "403", description = "댓글 삭제 권한 없음",
          content = @Content
      ),
      @ApiResponse(
          responseCode = "500", description = "서버 내부 오류",
          content = @Content
      )
  })
  ResponseEntity<Void> deleteComment(
      @PathVariable @Parameter(description = "댓글 ID", required = true) UUID commentId,
      @RequestHeader("Monew-Request-User-ID") @Parameter(description = "요청자 ID", required = true) UUID requestUserId
  );

  @Operation(summary = "댓글 물리 삭제", description = "댓글을 물리적으로 삭제합니다")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "204", description = "삭제 성공",
          content = @Content
      ),
      @ApiResponse(
          responseCode = "404", description = "댓글 정보 없음",
          content = @Content
      ),
      @ApiResponse(
          responseCode = "500", description = "서버 내부 오류",
          content = @Content
      )
  })
  ResponseEntity<Void> hardDeleteComment(
      @PathVariable @Parameter(description = "댓글 ID", required = true) UUID commentId
  );
}