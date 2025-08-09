package com.ma.message_apps.service;

import com.ma.message_apps.dto.FriendRequestsDto;
import com.ma.message_apps.entity.FriendRequests;
import com.ma.message_apps.entity.User;
import com.ma.message_apps.enumDto.FriendStatus;
import com.ma.message_apps.mapper.FriendRequestsConversion;
import com.ma.message_apps.repository.FriendRequestsRepository;
import com.ma.message_apps.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FriendRequestService {
    @Autowired
    private FriendRequestsRepository friendRequestsRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FriendRequestsConversion friendRequestsConversion;

    public List<FriendRequestsDto> getFriendRequestsForUser(Integer userId) {
        List<FriendRequests> requests = friendRequestsRepository.findByReceiver_UserId(userId);
        return requests.stream()
                .map(friendRequestsConversion::fromEntity)
                .collect(Collectors.toList());
    }

    public boolean sendFriendRequest(Integer fromUserId, Integer toUserId) {
        if (friendRequestsRepository.existsBySender_UserIdAndReceiver_UserId(fromUserId, toUserId)) {
            return false;
        }
        FriendRequests request = new FriendRequests();
        User sender = new User();
        sender.setUserId(fromUserId);
        User receiver = new User();
        receiver.setUserId(toUserId);
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setStatus(FriendStatus.PENDING);
        request.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
        friendRequestsRepository.save(request);
        return true;
    }

    public boolean acceptFriendRequest(Integer requestId) {
        Optional<FriendRequests> requestOpt = friendRequestsRepository.findById(requestId);
        if (requestOpt.isPresent()) {
            FriendRequests request = requestOpt.get();
            request.setStatus(FriendStatus.ACCEPTED);
            friendRequestsRepository.save(request);
            return true;
        }
        return false;
    }

    public boolean rejectFriendRequest(Integer requestId) {
        Optional<FriendRequests> requestOpt = friendRequestsRepository.findById(requestId);
        if (requestOpt.isPresent()) {
            FriendRequests request = requestOpt.get();
            request.setStatus(FriendStatus.REJECTED);
            friendRequestsRepository.save(request);
            return true;
        }
        return false;
    }
}
