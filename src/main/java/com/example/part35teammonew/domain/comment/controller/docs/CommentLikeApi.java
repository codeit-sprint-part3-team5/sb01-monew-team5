package com.example.part35teammonew.domain.comment.controller.docs;

import com.example.part35teammonew.domain.comment.dto.CommentLikeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@Tag(name = "Comment Likes", description = "댓글 좋아요 관리 API")
public interface CommentLikeApi {

  @Operation(summary = "댓글 좋아요 등록", description = "댓글에 좋아요를 등록합니다")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "좋아요 등록 성공",
          content = @Content(schema = @Schema(implementation = CommentLikeResponse.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "댓글 정보 없음",
          content = @Content
      ),
      @ApiResponse(
          responseCode = "409", description = "이미 좋아요한 댓글",
          content = @Content
      ),
      @ApiResponse(
          responseCode = "500", description = "서버 내부 오류",
          content = @Content
      )
  })
  ResponseEntity<CommentLikeResponse> addLike(
      @PathVariable @Parameter(description = "댓글 ID", required = true) UUID commentId,
      @RequestHeader("Monew-Request-User-ID") @Parameter(description = "요청자 ID", required = true) UUID requestUserId
  );

  @Operation(summary = "댓글 좋아요 취소", description = "댓글의 좋아요를 취소합니다")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "204", description = "좋아요 취소 성공",
          content = @Content
      ),
      @ApiResponse(
          responseCode = "404", description = "댓글 정보 없음 또는 좋아요 정보 없음",
          content = @Content
      ),
      @ApiResponse(
          responseCode = "500", description = "서버 내부 오류",
          content = @Content
      )
  })
  ResponseEntity<Void> removeLike(
      @PathVariable @Parameter(description = "댓글 ID", required = true) UUID commentId,
      @RequestHeader("Monew-Request-User-ID") @Parameter(description = "요청자 ID", required = true) UUID requestUserId
  );
}