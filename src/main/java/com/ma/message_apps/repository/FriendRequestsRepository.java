package com.ma.message_apps.repository;

import com.ma.message_apps.entity.FriendRequests;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRequestsRepository extends JpaRepository<FriendRequests,Integer> {
}
