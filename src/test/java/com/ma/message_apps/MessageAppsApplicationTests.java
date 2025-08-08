package com.ma.message_apps;

import com.ma.message_apps.entity.FriendRequests;
import com.ma.message_apps.entity.Message;
import com.ma.message_apps.entity.User;
import com.ma.message_apps.enumDto.FriendStatus;
import com.ma.message_apps.enumDto.UserStatus;
import com.ma.message_apps.repository.FriendRequestsRepository;
import com.ma.message_apps.repository.MessageRepository;
import com.ma.message_apps.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MessageAppsApplicationTests {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private FriendRequestsRepository friendRequestsRepository;
	@Autowired
	private MessageRepository messageRepository;

	private User user1;
	private User user2;

	@BeforeEach
	void setup() {
		// Create test users
		user1 = new User();
		user1.setUsername("test1user");
		user1.setPasswordHash("password123");
		user1.setEmail("test1@gmail.com");
		user1.setStatus(UserStatus.OFFLINE);
		user1.setCreatedAt(new Timestamp(System.currentTimeMillis()));

		user2 = new User();
		user2.setUsername("test2user");
		user2.setPasswordHash("password123");
		user2.setEmail("test2@gmail.com");
		user2.setStatus(UserStatus.OFFLINE);
		user2.setCreatedAt(new Timestamp(System.currentTimeMillis()));
	}

	@Test
	@Order(1)
	void testUserCreation() {
		User savedUser1 = userRepository.save(user1);
		User savedUser2 = userRepository.save(user2);

		assertNotNull(savedUser1.getUserId());
		assertNotNull(savedUser2.getUserId());
		assertEquals("test1user", savedUser1.getUsername());
		assertEquals("test2user", savedUser2.getUsername());
	}

	@Test
	@Order(2)
	void testFriendRequestCreation() {
		User savedUser1 = userRepository.save(user1);
		User savedUser2 = userRepository.save(user2);

		FriendRequests friendRequest = new FriendRequests();
		friendRequest.setSender(savedUser1);
		friendRequest.setReceiver(savedUser2);
		friendRequest.setStatus(FriendStatus.PENDING);
		friendRequest.setCreatedAt(new Timestamp(System.currentTimeMillis()));

		FriendRequests savedRequest = friendRequestsRepository.save(friendRequest);

		assertNotNull(savedRequest.getRequestId());
		assertEquals(FriendStatus.PENDING, savedRequest.getStatus());
		assertEquals(savedUser1.getUserId(), savedRequest.getSender().getUserId());
		assertEquals(savedUser2.getUserId(), savedRequest.getReceiver().getUserId());
	}

	@Test
	@Order(3)
	void testMessageCreation() {
		User savedUser1 = userRepository.save(user1);
		User savedUser2 = userRepository.save(user2);

		Message message = new Message();
		message.setSender(savedUser1);
		message.setReceiver(savedUser2);
		message.setMessageText("Hello, this is a test message!");
		message.setIsRead(false);
		message.setTimestamp(new Timestamp(System.currentTimeMillis()));

		Message savedMessage = messageRepository.save(message);

		assertNotNull(savedMessage.getMessageId());
		assertFalse(savedMessage.getIsRead());
		assertEquals("Hello, this is a test message!", savedMessage.getMessageText());
		assertEquals(savedUser1.getUserId(), savedMessage.getSender().getUserId());
		assertEquals(savedUser2.getUserId(), savedMessage.getReceiver().getUserId());
	}

	@Test
	@Order(4)
	void testUpdateUserStatus() {
		User savedUser = userRepository.save(user1);
		savedUser.setStatus(UserStatus.ONLINE);
		User updatedUser = userRepository.save(savedUser);

		assertEquals(UserStatus.ONLINE, updatedUser.getStatus());
	}

	@AfterEach
	void cleanup() {
		messageRepository.deleteAll();
		friendRequestsRepository.deleteAll();
		userRepository.deleteAll();
	}
}