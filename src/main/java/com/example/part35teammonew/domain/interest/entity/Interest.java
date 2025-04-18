package com.example.part35teammonew.domain.interest.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.util.UUID;

@Entity
public class Interest {
  @Id
  @GeneratedValue
  @Column(name = "interestId")
  private UUID id;
}
