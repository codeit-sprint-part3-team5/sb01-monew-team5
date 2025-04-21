package com.example.part35teammonew.domain.interestUserList.service;

import com.example.part35teammonew.domain.interestUserList.Dto.InterestUserListDto;
import java.util.UUID;

public interface InterestUserListServiceInterface {

  InterestUserListDto createInterestList(UUID interest);

  boolean addSubscribedUser(UUID id, UUID user);

  boolean subtractSubscribedUser(UUID id, UUID user);

  Long countSubscribedUser(UUID id);

  void deleteInterestList(UUID interest);

  boolean checkUserSubscribe(UUID interestId, UUID userId);


}
