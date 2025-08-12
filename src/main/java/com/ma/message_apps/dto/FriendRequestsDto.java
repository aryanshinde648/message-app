package com.ma.message_apps.dto;

import com.ma.message_apps.entity.User;
import com.ma.message_apps.enumDto.FriendStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FriendRequestsDto {
    private Integer requestId;
    private User sender;
    private User receiver;
    private FriendStatus status;
    private Timestamp createdAt;
}
