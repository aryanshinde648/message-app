package com.ma.message_apps.dto;

import com.ma.message_apps.enumDto.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class UserDto {
    private Integer userId;
    private String username;
    private String passwordHash;
    private String email;
    private UserStatus status;
    private Timestamp createdAt;
}
