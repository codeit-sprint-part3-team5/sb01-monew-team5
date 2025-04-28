package com.example.part35teammonew.domain.interest.service;

import com.example.part35teammonew.domain.interest.Enum.SortBy;
import com.example.part35teammonew.domain.interest.dto.InterestCreateRequest;
import com.example.part35teammonew.domain.interest.dto.InterestDto;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class InterestServiceImpl implements InterestService {

  @Override
  public boolean isNameTooSimilar(String name) {
    return false;
  }

  @Override
  public InterestDto createInterest(InterestCreateRequest request) {
    return null;
  }

  @Override
  public InterestDto updateKeywords(UUID interestId, List<String> newKeywords) {
    return null;
  }

  @Override
  public void deleteInterest(UUID interestId) {

  }

  @Override
  public InterestDto getInterestById(UUID interestId, UUID userId) {
    return null;
  }

  @Override
  public Page<InterestDto> listInterests(String search, String cursorValue, SortBy sortBy, int size,
      UUID userId) {
    return null;
  }

  @Override
  public void subscribe(UUID interestId, UUID userId) {

  }

  @Override
  public void unsubscribe(UUID interestId, UUID userId) {

  }

  @Override
  public boolean isSubscribed(UUID interestId, UUID userId) {
    return false;
  }

  @Override
  public long countSubscribers(UUID interestId) {
    return 0;
  }
}
