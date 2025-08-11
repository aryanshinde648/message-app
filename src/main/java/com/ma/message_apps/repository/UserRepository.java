package com.ma.message_apps.repository;

import com.ma.message_apps.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Integer> {
    User findByUsername(String username);

    User findByEmail(String query);

    User findByRefreshToken(String refreshToken);
}
