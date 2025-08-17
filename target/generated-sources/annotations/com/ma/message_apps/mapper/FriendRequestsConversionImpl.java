package com.ma.message_apps.mapper;

import com.ma.message_apps.dto.FriendRequestsDto;
import com.ma.message_apps.entity.FriendRequests;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-17T19:13:39+0530",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.42.50.v20250729-0351, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class FriendRequestsConversionImpl implements FriendRequestsConversion {

    @Override
    public FriendRequestsDto fromEntity(FriendRequests friendRequests) {
        if ( friendRequests == null ) {
            return null;
        }

        FriendRequestsDto friendRequestsDto = new FriendRequestsDto();

        friendRequestsDto.setCreatedAt( friendRequests.getCreatedAt() );
        friendRequestsDto.setReceiver( friendRequests.getReceiver() );
        friendRequestsDto.setRequestId( friendRequests.getRequestId() );
        friendRequestsDto.setSender( friendRequests.getSender() );
        friendRequestsDto.setStatus( friendRequests.getStatus() );

        return friendRequestsDto;
    }

    @Override
    public FriendRequests toEntity(FriendRequestsDto friendRequestsDto) {
        if ( friendRequestsDto == null ) {
            return null;
        }

        FriendRequests friendRequests = new FriendRequests();

        friendRequests.setCreatedAt( friendRequestsDto.getCreatedAt() );
        friendRequests.setReceiver( friendRequestsDto.getReceiver() );
        friendRequests.setRequestId( friendRequestsDto.getRequestId() );
        friendRequests.setSender( friendRequestsDto.getSender() );
        friendRequests.setStatus( friendRequestsDto.getStatus() );

        return friendRequests;
    }
}
