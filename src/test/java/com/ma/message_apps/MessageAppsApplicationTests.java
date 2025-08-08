package com.ma.message_apps;

import com.ma.message_apps.entity.FriendRequests;
import com.ma.message_apps.entity.Message;
import com.ma.message_apps.entity.User;
import com.ma.message_apps.enumDto.UserStatus;
import com.ma.message_apps.repository.FriendRequestsRepository;
import com.ma.message_apps.repository.MessageRepository;
import com.ma.message_apps.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MessageAppsApplicationTests {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private FriendRequestsRepository friendRequestsRepository;
	@Autowired
	private MessageRepository messageRepository;


	@Test
	void contextLoads() {
	}

	@Test
	void saveAllData() {
		User user1 = new User();
		user1.setUsername("test1user");
		user1.setPasswordHash("password123");
		user1.setEmail("test1@gmail.com");
		user1.setStatus(UserStatus.OFFLINE);
		user1.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
		userRepository.save(user1);

		User user2 = new User();
		user2.setUsername("test2user");
		user2.setPasswordHash("password123");
		user2.setEmail("test2@gmail.com");
		user2.setStatus(UserStatus.OFFLINE);
		user2.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
		userRepository.save(user2);

		FriendRequests friendRequest = new FriendRequests();
		friendRequest.setSender(user1);
		friendRequest.setReceiver(user2);
		friendRequest.setStatus(com.ma.message_apps.enumDto.FriendStatus.PENDING);
		friendRequest.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
		friendRequestsRepository.save(friendRequest);

		Message message = new Message();
		message.setSender(user1);
		message.setReceiver(user2);
		message.setMessageText("Hello, this is a test message!");
		message.setIsRead(false);
		message.setTimestamp(new java.sql.Timestamp(System.currentTimeMillis()));
		messageRepository.save(message);
	}

}
