package com.example.part35teammonew.domain.comment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.util.UUID;

@Entity
public class CommentLike {
  @Id
  @GeneratedValue
  @Column(name = "commentLikeId")
  private UUID id;
}
