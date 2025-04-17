package com.example.part35teammonew.domain.UserActivity.repository;

import com.example.part35teammonew.domain.UserActivity.entity.UserActivity;
import java.util.Optional;
import java.util.UUID;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserActivityRepository extends MongoRepository<UserActivity, ObjectId> {

  Optional<UserActivity> findByUserId(UUID userId);
}
