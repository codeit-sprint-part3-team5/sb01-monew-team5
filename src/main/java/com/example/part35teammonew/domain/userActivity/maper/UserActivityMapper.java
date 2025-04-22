package com.example.part35teammonew.domain.userActivity.maper;

import com.example.part35teammonew.domain.userActivity.Dto.UserActivityDto;
import com.example.part35teammonew.domain.userActivity.entity.UserActivity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserActivityMapper {

  UserActivityMapper ISTANCE = Mappers.getMapper(UserActivityMapper.class);

  UserActivityDto toDto(UserActivity userActivity);

}
