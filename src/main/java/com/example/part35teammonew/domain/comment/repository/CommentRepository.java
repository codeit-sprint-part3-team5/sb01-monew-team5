package com.example.part35teammonew.domain.comment.repository;

import com.example.part35teammonew.domain.comment.entity.Comment;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

  // 삭제되지 않은 댓글만 가져오기
  List<Comment> findByIsDeletedFalse();

  // 특정 아티클의 댓글들 (아티클 연동 후 사용)
  List<Comment> findByArticleIdAndIsDeletedFalse(UUID articleId);

  // 특정 유저의 댓글들 (유저 연동 후 사용)
  List<Comment> findByUserIdAndIsDeletedFalse(UUID userId);

  Optional<Comment> findByIdAndIsDeletedFalse(UUID id);
}