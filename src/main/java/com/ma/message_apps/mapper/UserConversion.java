package com.ma.message_apps.mapper;

import com.ma.message_apps.dto.UserDto;
import com.ma.message_apps.entity.User;
import org.mapstruct.Mapper;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface UserConversion {

    User toUserEntity(UserDto userDto);
    UserDto toUserDto(User user);


    default Timestamp map(LocalDateTime value) {
        return value == null ? null : Timestamp.valueOf(value);
    }

    default LocalDateTime map(Timestamp value) {
        return value == null ? null : value.toLocalDateTime();
    }
}
