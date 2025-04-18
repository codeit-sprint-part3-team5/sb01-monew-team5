package com.example.part35teammonew.domain.UserActivity.Dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class InterestDTO {

  private UUID id;          //interstUserKist의 아이디?
  private UUID interestId;
  private String interestName;
  private List<String> interestKeywords;
  private long interestSubscriberCount;
  private Instant createdAt;


}
