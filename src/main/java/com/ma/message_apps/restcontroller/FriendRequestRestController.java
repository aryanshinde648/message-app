package com.ma.message_apps.restcontroller;

import com.ma.message_apps.dto.FriendRequestsDto;
import com.ma.message_apps.service.FriendRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friend-requests")
public class FriendRequestRestController {
    @Autowired
    private FriendRequestService friendRequestService;

    @GetMapping("/{userId}")
    public List<FriendRequestsDto> getFriendRequests(@PathVariable Integer userId) {
        return friendRequestService.getFriendRequestsForUser(userId);
    }

    @PostMapping("/send")
    public boolean sendFriendRequest(@RequestParam Integer fromUserId, @RequestParam Integer toUserId) {
        return friendRequestService.sendFriendRequest(fromUserId, toUserId);
    }

    @PostMapping("/accept")
    public boolean acceptFriendRequest(@RequestParam Integer requestId) {
        return friendRequestService.acceptFriendRequest(requestId);
    }

    @PostMapping("/reject")
    public boolean rejectFriendRequest(@RequestParam Integer requestId) {
        return friendRequestService.rejectFriendRequest(requestId);
    }
}
