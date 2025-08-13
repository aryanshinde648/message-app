package com.ma.message_apps.mapper;

import com.ma.message_apps.entity.FriendRequests;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FriendRequestsConversion {
    FriendRequests toFriendRequestsEntity(FriendRequests friendRequests);
    FriendRequests toFriendRequestsDto(FriendRequests friendRequests);
}
