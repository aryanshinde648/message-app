package com.ma.message_apps.mapper;

import com.ma.message_apps.dto.UserDto;
import com.ma.message_apps.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-17T19:13:39+0530",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.42.50.v20250729-0351, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class UserConversionImpl implements UserConversion {

    @Override
    public UserDto toUserDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDto userDto = new UserDto();

        userDto.setUserId( user.getUserId() );
        userDto.setCreatedAt( timestampToLocalDateTime( user.getCreatedAt() ) );
        userDto.setUserStatus( user.getStatus() );
        userDto.setEmail( user.getEmail() );
        userDto.setPasswordHash( user.getPasswordHash() );
        userDto.setUsername( user.getUsername() );

        return userDto;
    }

    @Override
    public User toUser(UserDto userDto) {
        if ( userDto == null ) {
            return null;
        }

        User user = new User();

        user.setUserId( userDto.getUserId() );
        user.setCreatedAt( localDateTimeToTimestamp( userDto.getCreatedAt() ) );
        user.setStatus( userDto.getUserStatus() );
        user.setEmail( userDto.getEmail() );
        user.setPasswordHash( userDto.getPasswordHash() );
        user.setUsername( userDto.getUsername() );

        return user;
    }
}
