package com.example.part35teammonew.domain.article.controller.docs;

import com.example.part35teammonew.domain.article.dto.ArticleEnrollmentResponse;
import com.example.part35teammonew.domain.article.dto.ArticlesResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Articles", description = "기사 관리 API")
public interface ArticleApi {

  @Operation(summary = "기사 커서 기반 목록 조회", description = "검색 조건에 따라 기사 목록을 커서 기반으로 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "조회 성공",
          content = @Content(schema = @Schema(implementation = ArticlesResponse.class))),
      @ApiResponse(responseCode = "400", description = "요청 파라미터 오류", content = @Content),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content)
  })
  @GetMapping("/api/articles")
  ResponseEntity<ArticlesResponse> articles(
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) String interestId,
      @RequestParam(required = false) String[] sourceIn,
      @RequestParam(required = false) String publishDateFrom,
      @RequestParam(required = false) String publishDateTo,
      @RequestParam String orderBy,
      @RequestParam String direction,
      @RequestParam(required = false) String cursor,
      @RequestParam(required = false) String after,
      @RequestParam int limit,
      @RequestHeader("Monew-Request-User-ID") String userId
  );

  @Operation(summary = "기사 조회 기록 등록", description = "기사를 조회했음을 기록하고, 관련 데이터를 반환합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "조회 기록 등록 성공",
          content = @Content(schema = @Schema(implementation = ArticleEnrollmentResponse.class))),
      @ApiResponse(responseCode = "400", description = "요청 파라미터 오류", content = @Content),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content)
  })
  @PostMapping("/api/articles/{articleId}/article-views")
  ResponseEntity<ArticleEnrollmentResponse> articleViewEnrollment(
      @PathVariable @Parameter(description = "기사 ID", required = true) UUID articleId,
      @RequestHeader("Monew-Request-User-ID") @Parameter(description = "요청자 ID", required = true) String userId
  );

  @Operation(summary = "기사 복원", description = "S3에 백업된 기사 파일을 기준으로 날짜 범위 내 기사를 복원합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "복원 시작", content = @Content),
      @ApiResponse(responseCode = "500", description = "복원 실패", content = @Content)
  })
  @GetMapping("/api/articles/restore")
  ResponseEntity<ArticleEnrollmentResponse> articlesRestore(
      @RequestParam("from") @Parameter(description = "복원 시작 날짜 (yyyy-MM-dd)", required = true) String from,
      @RequestParam("to") @Parameter(description = "복원 종료 날짜 (yyyy-MM-dd)", required = true) String to
  ) throws Exception;

  @Operation(summary = "기사 논리 삭제", description = "기사를 논리적으로 삭제합니다 (isDeleted=true).")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "삭제 성공", content = @Content),
      @ApiResponse(responseCode = "404", description = "기사 없음", content = @Content),
      @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
  })
  @DeleteMapping("/api/articles/{articleId}")
  ResponseEntity<Void> articlesDelete(
      @PathVariable @Parameter(description = "기사 ID", required = true) UUID articleId
  );

  @Operation(summary = "기사 물리 삭제", description = "기사를 DB에서 완전히 삭제합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "삭제 성공", content = @Content),
      @ApiResponse(responseCode = "404", description = "기사 없음", content = @Content),
      @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
  })
  @DeleteMapping("/api/articles/{articleId}/hard")
  ResponseEntity<Void> articlesDeleteHard(
      @PathVariable @Parameter(description = "기사 ID", required = true) UUID articleId
  );
}
