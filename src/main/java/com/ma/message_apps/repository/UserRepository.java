package com.ma.message_apps.repository;

import com.ma.message_apps.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Integer> {
    /**
     * Check if a user with the given username exists
     * @param username the username to check
     * @return true if exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Check if a user with the given email exists
     * @param email the email to check
     * @return true if exists, false otherwise
     */
    boolean existsByEmail(String email);
}
