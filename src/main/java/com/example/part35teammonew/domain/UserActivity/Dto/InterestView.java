package com.example.part35teammonew.domain.UserActivity.Dto;

import java.math.BigInteger;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class InterestView {

  private UUID id;          //interstUserKist의 아이디?
  private UUID interestId;
  private String interestName;
  private List<String> interestKeywords;
  private BigInteger interestSubscriberCount;
  private Instant createdAt;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InterestView that = (InterestView) o;
    return Objects.equals(interestId, that.interestId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(interestId);
  }
}
