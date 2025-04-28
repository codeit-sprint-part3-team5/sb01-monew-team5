package com.example.part35teammonew.domain.notification.Dto;

import com.example.part35teammonew.domain.notification.Enum.NotificationType;
import java.time.Instant;
import java.util.UUID;
import org.bson.types.ObjectId;

public record NotificationDto(
    ObjectId id
    , UUID userId
    , Instant createdAt
    , Instant updateAt
    , boolean confirmed
    , String content
    , NotificationType type
    , UUID resourceId
) {

}
