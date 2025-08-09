package com.ma.message_apps.restcontroller;

import com.ma.message_apps.dto.MessageDto;
import com.ma.message_apps.dto.UserDto;
import com.ma.message_apps.entity.Message;
import com.ma.message_apps.entity.User;
import com.ma.message_apps.mapper.MessageConversion;
import com.ma.message_apps.mapper.UserConversion;
import com.ma.message_apps.repository.FriendRequestsRepository;
import com.ma.message_apps.repository.MessageRepository;
import com.ma.message_apps.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class MessagingRestController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FriendRequestsRepository friendRequestsRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserConversion userConversion;
    @Autowired
    private MessageConversion messageConversion;

    // List friends/contacts for chat
    @GetMapping("/friends/list/{userId}")
    public List<UserDto> getFriends(@PathVariable Integer userId) {
        List<User> friends = friendRequestsRepository.findAcceptedFriends(userId);
        return friends.stream().map(userConversion::toUserDto).collect(Collectors.toList());
    }

    // Get chat messages between two users
    @GetMapping("/messages/{fromUserId}/{toUserId}")
    public List<MessageDto> getMessages(@PathVariable Integer fromUserId, @PathVariable Integer toUserId) {
        List<Message> messages = messageRepository.findChatMessages(fromUserId, toUserId);
        return messages.stream().map(messageConversion::toMessageDto).collect(Collectors.toList());
    }

    // Send a chat message
    @PostMapping("/messages/send")
    public boolean sendMessage(@RequestParam Integer fromUserId, @RequestParam Integer toUserId, @RequestParam String content) {
        User sender = userRepository.findById(fromUserId).orElse(null);
        User receiver = userRepository.findById(toUserId).orElse(null);
        if (sender == null || receiver == null) return false;
        Message msg = new Message();
        msg.setSender(sender);
        msg.setReceiver(receiver);
        msg.setMessageText(content);
        msg.setIsRead(false);
        msg.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
        messageRepository.save(msg);
        return true;
    }
}

