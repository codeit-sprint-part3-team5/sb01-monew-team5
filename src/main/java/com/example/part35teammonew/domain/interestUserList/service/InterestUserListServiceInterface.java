package com.example.part35teammonew.domain.interestUserList.service;

import com.example.part35teammonew.domain.interestUserList.Dto.InterestUserListDto;
import java.util.UUID;
import org.bson.types.ObjectId;

public interface InterestUserListServiceInterface {

  InterestUserListDto createInterestList(UUID interest);

  boolean addSubscribedUser(ObjectId id, UUID user);

  boolean subtractSubscribedUser(ObjectId id, UUID user);

  Long countSubscribedUser(ObjectId id);

  void deleteInterestList(UUID interest);


}
