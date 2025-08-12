package com.ma.message_apps.dto;

import com.ma.message_apps.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageDto {
    private Integer messageId;
    private User sender;
    private User receiver;
    private String messageText;
    private Boolean isRead;
    private Timestamp createdAt;
}
