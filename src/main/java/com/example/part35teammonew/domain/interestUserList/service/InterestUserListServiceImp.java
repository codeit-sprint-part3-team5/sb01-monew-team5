package com.example.part35teammonew.domain.interestUserList.service;

import static com.example.part35teammonew.domain.interestUserList.entity.InterestUserList.setUpNewInterestUserList;

import com.example.part35teammonew.domain.interestUserList.Dto.InterestUserListDto;
import com.example.part35teammonew.domain.interestUserList.entity.InterestUserList;
import com.example.part35teammonew.domain.interestUserList.mapper.InterestUserListMapper;
import com.example.part35teammonew.domain.interestUserList.repository.InterestUserListRepository;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InterestUserListServiceImp implements InterestUserListServiceInterface {


  private final InterestUserListRepository interestUserListRepository;
  private final InterestUserListMapper interestUserListMapper;

  InterestUserListServiceImp(
      @Autowired InterestUserListRepository interestUserListRepository,
      @Autowired InterestUserListMapper interestUserListMapper
  ) {
    this.interestUserListRepository = interestUserListRepository;
    this.interestUserListMapper = interestUserListMapper;
  }

  @Override
  @Transactional
  public InterestUserListDto createInterestList(UUID interest) {
    InterestUserList interestUserList = setUpNewInterestUserList(interest);
    interestUserListRepository.save(interestUserList);
    return interestUserListMapper.toDto(interestUserList);
  }

  @Override
  @Transactional
  public boolean addSubscribedUser(UUID interestId, UUID userId) {
    return interestUserListRepository.findByInterest(interestId)
        .map(interestUserList -> {
          interestUserList.addUser(userId);
          interestUserListRepository.save(interestUserList);
          return true;
        })
        .orElse(false);
  }

  @Override
  @Transactional
  public boolean subtractSubscribedUser(UUID id, UUID user) {
    return interestUserListRepository.findByInterest(id)
        .map(interestUserList -> {
          interestUserList.subtractUser(user);
          interestUserListRepository.save(interestUserList);
          return true;
        })
        .orElse(false);
  }

  @Override
  @Transactional
  public boolean checkUserSubscribe(UUID interestId, UUID userId) {
    return interestUserListRepository.findByInterest(interestId)
        .map(interestUserList -> {
          return interestUserList.findUser(userId);
        })
        .orElse(false);
  }

  @Override
  @Transactional(readOnly = true)
  public Long countSubscribedUser(UUID interest) {
    return interestUserListRepository.findByInterest(interest)
        .map(InterestUserList::getUserCount)
        .orElse(0L);
  }

  @Override
  @Transactional
  public void deleteInterestList(UUID interest) {
    interestUserListRepository.deleteByInterest(interest);

  }
}
