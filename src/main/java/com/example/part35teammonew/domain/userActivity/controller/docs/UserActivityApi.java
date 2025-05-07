package com.example.part35teammonew.domain.userActivity.controller.docs;

import com.example.part35teammonew.domain.comment.dto.CommentPageResponse;
import com.example.part35teammonew.domain.userActivity.Dto.UserActivityDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

public interface UserActivityApi {
  @Operation(summary = "사용자 활동내역 조회", description = "사용자 ID로 활동 내역을 조회합니다")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "사용자 활동 내역 조회 성공",
          content = @Content(schema = @Schema(implementation = UserActivityDto.class))
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
  ResponseEntity<UserActivityDto> getUserActivity(
      @PathVariable
      @Parameter(description = "사용자 ID")
      UUID userId
  );

}
