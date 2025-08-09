package com.ma.message_apps.repository;

import com.ma.message_apps.entity.FriendRequests;
import com.ma.message_apps.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FriendRequestsRepository extends JpaRepository<FriendRequests,Integer> {
    List<FriendRequests> findByReceiver_UserId(Integer userId);
    boolean existsBySender_UserIdAndReceiver_UserId(Integer senderId, Integer receiverId);

    @Query("SELECT fr.sender FROM FriendRequests fr WHERE fr.receiver.userId = :userId AND fr.status = com.ma.message_apps.enumDto.FriendStatus.ACCEPTED UNION SELECT fr.receiver FROM FriendRequests fr WHERE fr.sender.userId = :userId AND fr.status = com.ma.message_apps.enumDto.FriendStatus.ACCEPTED")
    List<User> findAcceptedFriends(@org.springframework.data.repository.query.Param("userId") Integer userId);
}
