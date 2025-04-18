package com.example.part35teammonew.domain.comment.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.UUID;

//import com.example.part35teammonew.domain.user.entity.User;
//import com.example.part35teammonew.domain.article.entity.Article;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comment")
public class Comment {

  @Id @GeneratedValue
  private UUID id; //댓글 고유 식별자

  @Column(name = "content", nullable = false, length = 500)
  private String content; //댓글 내용

  @Column(name = "like_count", nullable = false)
  private Integer likeCount; //좋아요 수

  @Column(name = "created_at", nullable = false)
  private ZonedDateTime createdAt; //생성 시간

  @Column(name = "updated_at", nullable = false)
  private ZonedDateTime updatedAt; //수정 시간

  @Column(name = "is_deleted", nullable = false)
  private boolean isDeleted; //논리삭제여부

/*  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "article_id", nullable = false)
  private Article article; //연관된 기사 아이디 FK

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user; //댓글 작성자 아이디 FK*/

  @Column(name = "user_nickname", nullable = false)
  private String userNickname; //댓글 작성자 닉네임
  //닉네임은 변경될 수 있어서 댓길이 생성 될 때 복사해서 가져올 것
  //그런데 문제는 a시점에서 작성된 댓글의 닉네임과 b시점에 작성된 닉네임이 다를 수 있다는 점을 알고는 있어야할듯


//  public static Comment create(String content, String userNickname, User user, Article article) {
//    return Comment.builder()
//        .content(content)
//        .userNickname(userNickname)
//        .user(user)
//        .article(article)
//        .likeCount(0)
//        .createdAt(ZonedDateTime.now())
//        .updatedAt(ZonedDateTime.now())
//        .isDeleted(false)
//        .build();
//  }

  public void updateContent(String content) {
    this.content = content;
    this.updatedAt = ZonedDateTime.now();
  }

  public void delete() {
    this.isDeleted = true;
    this.updatedAt = ZonedDateTime.now();
  }

  public void incrementLikeCount() {
    this.likeCount += 1;
    this.updatedAt = ZonedDateTime.now();
  }

  public void decrementLikeCount() {
    if (this.likeCount > 0) {
      this.likeCount -= 1;
      this.updatedAt = ZonedDateTime.now();
    }
  }
}