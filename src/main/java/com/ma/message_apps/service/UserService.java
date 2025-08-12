package com.ma.message_apps.service;

import com.ma.message_apps.dto.UserDto;

public interface UserService {
    /**
     * Register a new user
     * @param userDto the user data
     * @return the registered user with generated ID
     */
    UserDto registerUser(UserDto userDto);

    /**
     * Check if username already exists
     * @param username the username to check
     * @return true if username exists, false otherwise
     */
    boolean isUsernameExists(String username);

    /**
     * Check if email already exists
     * @param email the email to check
     * @return true if email exists, false otherwise
     */
    boolean isEmailExists(String email);
}
