package com.ma.message_apps.mapper;

import com.ma.message_apps.dto.UserDto;
import com.ma.message_apps.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserConversion {
    UserDto toUserDto(User user);
    User toUser(UserDto userDto);
}
