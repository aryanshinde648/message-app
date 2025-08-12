package com.ma.message_apps.service.impl;

import com.ma.message_apps.dto.UserDto;
import com.ma.message_apps.entity.User;
import com.ma.message_apps.mapper.UserConversion;
import com.ma.message_apps.repository.UserRepository;
import com.ma.message_apps.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserConversion userConversion;


    @Override
    public UserDto registerUser(UserDto userDto) {
        log.info("Registering new user with username: {}", userDto.getUsername());

        userDto.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        // Convert DTO to entity, save it, and convert back to DTO
        User user = userConversion.toUserEntity(userDto);
        User savedUser = userRepository.save(user);

        log.info("User registered successfully with ID: {}", savedUser.getUserId());
        return userConversion.toUserDto(savedUser);
    }

    @Override
    public boolean isUsernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}
