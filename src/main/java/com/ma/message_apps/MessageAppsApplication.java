package com.ma.message_apps;

import com.ma.message_apps.entity.FriendRequests;
import com.ma.message_apps.entity.Message;
import com.ma.message_apps.entity.User;
import com.ma.message_apps.enumDto.UserStatus;
import com.ma.message_apps.repository.FriendRequestsRepository;
import com.ma.message_apps.repository.MessageRepository;
import com.ma.message_apps.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MessageAppsApplication {

	public static void main(String[] args) {
		SpringApplication.run(MessageAppsApplication.class, args);
	}

}
