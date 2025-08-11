# Complete Guide: Building a Modern Message Application

## Table of Contents
1. [Project Overview](#project-overview)
2. [Prerequisites](#prerequisites)
3. [Project Setup](#project-setup)
4. [Database Design](#database-design)
5. [Backend Development](#backend-development)
6. [Frontend Development](#frontend-development)
7. [Security Implementation](#security-implementation)
8. [Testing](#testing)
9. [Deployment](#deployment)
10. [Advanced Features](#advanced-features)

---

## Project Overview

This is a real-time messaging application built with Spring Boot and modern web technologies. The application includes:

- **User Authentication & Authorization**
- **Real-time Chat Messaging**
- **Friend Request System**
- **Modern Responsive UI**
- **WebSocket Communication**
- **RESTful API Design**

### Technology Stack

**Backend:**
- Spring Boot 3.x
- Spring Security
- Spring Data JPA
- Spring WebSocket
- MySQL Database
- Maven Build Tool

**Frontend:**
- HTML5, CSS3, JavaScript
- Bootstrap 5
- WebSocket Client
- Modern CSS (Glassmorphism, Animations)

**Additional Tools:**
- Thymeleaf Template Engine
- BCrypt Password Encoding
- JWT (Optional for API)

---

## Prerequisites

Before starting, ensure you have:

1. **Java Development Kit (JDK) 17 or higher**
   ```bash
   java -version
   ```

2. **Maven 3.6+**
   ```bash
   mvn -version
   ```

3. **MySQL Server 8.0+**
   ```bash
   mysql --version
   ```

4. **IDE (IntelliJ IDEA, Eclipse, or VS Code)**

5. **Git for Version Control**
   ```bash
   git --version
   ```

---

## Project Setup

### Step 1: Create Spring Boot Project

1. **Using Spring Initializr** (https://start.spring.io/):
   - Project: Maven Project
   - Language: Java
   - Spring Boot: 3.2.x
   - Group: com.ma
   - Artifact: message-apps
   - Name: Message Apps
   - Package name: com.ma.message_apps
   - Packaging: Jar
   - Java: 17

2. **Add Dependencies:**
   - Spring Web
   - Spring Data JPA
   - Spring Security
   - Spring WebSocket
   - Thymeleaf
   - MySQL Driver
   - Spring Boot DevTools
   - Validation

### Step 2: Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/ma/message_apps/
│   │       ├── MessageAppsApplication.java
│   │       ├── config/
│   │       │   ├── SecurityConfig.java
│   │       │   └── WebSocketConfig.java
│   │       ├── controller/
│   │       │   └── MessagingController.java
│   │       ├── restcontroller/
│   │       │   ├── AuthController.java
│   │       │   ├── MessageRestController.java
│   │       │   └── FriendRequestController.java
│   │       ├── entity/
│   │       │   ├── User.java
│   │       │   ├── Message.java
│   │       │   └── FriendRequests.java
│   │       ├── repository/
│   │       │   ├── UserRepository.java
│   │       │   ├── MessageRepository.java
│   │       │   └── FriendRequestsRepository.java
│   │       ├── service/
│   │       │   ├── UserService.java
│   │       │   ├── MessageService.java
│   │       │   └── FriendRequestService.java
│   │       ├── dto/
│   │       │   ├── UserDto.java
│   │       │   ├── MessageDto.java
│   │       │   └── FriendRequestsDto.java
│   │       ├── enumDto/
│   │       │   ├── UserStatus.java
│   │       │   └── FriendStatus.java
│   │       ├── mapper/
│   │       │   └── FriendRequestsConversion.java
│   │       └── exception/
│   │           └── GlobalExceptionHandler.java
│   └── resources/
│       ├── application.yml
│       ├── static/
│       │   ├── css/main.css
│       │   └── js/
│       │       ├── dashboard.js
│       │       ├── login.js
│       │       └── register.js
│       └── templates/
│           ├── dashboard.html
│           ├── home.html
│           ├── login.html
│           └── register.html
```

---

## Database Design

### Step 3: Database Schema

**Create Database:**
```sql
CREATE DATABASE message_app_db;
USE message_app_db;
```

**Tables Structure:**

1. **Users Table:**
```sql
CREATE TABLE users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    user_status ENUM('ONLINE', 'OFFLINE', 'AWAY') DEFAULT 'OFFLINE'
);
```

2. **Messages Table:**
```sql
CREATE TABLE messages (
    message_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    message_content TEXT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_read BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (sender_id) REFERENCES users(user_id),
    FOREIGN KEY (receiver_id) REFERENCES users(user_id)
);
```

3. **Friend Requests Table:**
```sql
CREATE TABLE friend_requests (
    request_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    status ENUM('PENDING', 'ACCEPTED', 'REJECTED') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES users(user_id),
    FOREIGN KEY (receiver_id) REFERENCES users(user_id),
    UNIQUE KEY unique_friend_request (sender_id, receiver_id)
);
```

### Step 4: Application Configuration

**application.yml:**
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/message_app_db
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
        format_sql: true
  
  thymeleaf:
    cache: false
    suffix: .html
    prefix: classpath:/templates/
  
  web:
    resources:
      static-locations: classpath:/static/
  
server:
  port: 8080
  
logging:
  level:
    com.ma.message_apps: DEBUG
    org.springframework.security: DEBUG
```

---

## Backend Development

### Step 5: Entity Classes

#### 5.1 User Entity

Create `src/main/java/com/ma/message_apps/entity/User.java`:

```java
package com.ma.message_apps.entity;

import com.ma.message_apps.enumDto.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Column(unique = true, nullable = false)
    private String username;
    
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    @Column(unique = true, nullable = false)
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "user_status")
    private UserStatus userStatus = UserStatus.OFFLINE;
    
    // Constructors
    public User() {}
    
    public User(String username, String email, String passwordHash) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
    }
    
    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public UserStatus getUserStatus() { return userStatus; }
    public void setUserStatus(UserStatus userStatus) { this.userStatus = userStatus; }
    
    // equals, hashCode, toString methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
    
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", userStatus=" + userStatus +
                '}';
    }
}
```

#### 5.2 Message Entity

Create `src/main/java/com/ma/message_apps/entity/Message.java`:

```java
package com.ma.message_apps.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "messages")
public class Message {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long messageId;
    
    @NotNull(message = "Sender is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;
    
    @NotNull(message = "Receiver is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;
    
    @NotBlank(message = "Message content cannot be empty")
    @Column(name = "message_content", columnDefinition = "TEXT", nullable = false)
    private String messageContent;
    
    @CreationTimestamp
    @Column(name = "timestamp", updatable = false)
    private LocalDateTime timestamp;
    
    @Column(name = "is_read")
    private Boolean isRead = false;
    
    // Constructors
    public Message() {}
    
    public Message(User sender, User receiver, String messageContent) {
        this.sender = sender;
        this.receiver = receiver;
        this.messageContent = messageContent;
    }
    
    // Getters and Setters
    public Long getMessageId() { return messageId; }
    public void setMessageId(Long messageId) { this.messageId = messageId; }
    
    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }
    
    public User getReceiver() { return receiver; }
    public void setReceiver(User receiver) { this.receiver = receiver; }
    
    public String getMessageContent() { return messageContent; }
    public void setMessageContent(String messageContent) { this.messageContent = messageContent; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }
    
    // equals, hashCode, toString methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(messageId, message.messageId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(messageId);
    }
    
    @Override
    public String toString() {
        return "Message{" +
                "messageId=" + messageId +
                ", messageContent='" + messageContent + '\'' +
                ", timestamp=" + timestamp +
                ", isRead=" + isRead +
                '}';
    }
}
```

#### 5.3 FriendRequests Entity

Create `src/main/java/com/ma/message_apps/entity/FriendRequests.java`:

```java
package com.ma.message_apps.entity;

import com.ma.message_apps.enumDto.FriendStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "friend_requests", 
       uniqueConstraints = @UniqueConstraint(
           name = "unique_friend_request", 
           columnNames = {"sender_id", "receiver_id"}
       ))
public class FriendRequests {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long requestId;
    
    @NotNull(message = "Sender is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;
    
    @NotNull(message = "Receiver is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private FriendStatus status = FriendStatus.PENDING;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public FriendRequests() {}
    
    public FriendRequests(User sender, User receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }
    
    // Getters and Setters
    public Long getRequestId() { return requestId; }
    public void setRequestId(Long requestId) { this.requestId = requestId; }
    
    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }
    
    public User getReceiver() { return receiver; }
    public void setReceiver(User receiver) { this.receiver = receiver; }
    
    public FriendStatus getStatus() { return status; }
    public void setStatus(FriendStatus status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // equals, hashCode, toString methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendRequests that = (FriendRequests) o;
        return Objects.equals(requestId, that.requestId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(requestId);
    }
    
    @Override
    public String toString() {
        return "FriendRequests{" +
                "requestId=" + requestId +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}
```

### Step 6: Enum Classes

#### 6.1 UserStatus Enum

Create `src/main/java/com/ma/message_apps/enumDto/UserStatus.java`:

```java
package com.ma.message_apps.enumDto;

public enum UserStatus {
    ONLINE("Online"),
    OFFLINE("Offline"),
    AWAY("Away");
    
    private final String displayName;
    
    UserStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
```

#### 6.2 FriendStatus Enum

Create `src/main/java/com/ma/message_apps/enumDto/FriendStatus.java`:

```java
package com.ma.message_apps.enumDto;

public enum FriendStatus {
    PENDING("Pending"),
    ACCEPTED("Accepted"),
    REJECTED("Rejected");
    
    private final String displayName;
    
    FriendStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
```

### Step 7: Data Transfer Objects (DTOs)

#### 7.1 UserDto

Create `src/main/java/com/ma/message_apps/dto/UserDto.java`:

```java
package com.ma.message_apps.dto;

import com.ma.message_apps.enumDto.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class UserDto {
    
    private Long userId;
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String passwordHash;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserStatus userStatus;
    
    // Constructors
    public UserDto() {}
    
    public UserDto(String username, String email, String passwordHash) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
    }
    
    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public UserStatus getUserStatus() { return userStatus; }
    public void setUserStatus(UserStatus userStatus) { this.userStatus = userStatus; }
    
    @Override
    public String toString() {
        return "UserDto{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", userStatus=" + userStatus +
                '}';
    }
}
```

#### 7.2 MessageDto

Create `src/main/java/com/ma/message_apps/dto/MessageDto.java`:

```java
package com.ma.message_apps.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class MessageDto {
    
    private Long messageId;
    
    @NotNull(message = "Sender ID is required")
    private Long senderId;
    
    @NotNull(message = "Receiver ID is required")
    private Long receiverId;
    
    @NotBlank(message = "Message content cannot be empty")
    private String messageContent;
    
    private LocalDateTime timestamp;
    private Boolean isRead;
    
    // Additional fields for display
    private String senderUsername;
    private String receiverUsername;
    
    // Constructors
    public MessageDto() {}
    
    public MessageDto(Long senderId, Long receiverId, String messageContent) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageContent = messageContent;
    }
    
    // Getters and Setters
    public Long getMessageId() { return messageId; }
    public void setMessageId(Long messageId) { this.messageId = messageId; }
    
    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    
    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }
    
    public String getMessageContent() { return messageContent; }
    public void setMessageContent(String messageContent) { this.messageContent = messageContent; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }
    
    public String getSenderUsername() { return senderUsername; }
    public void setSenderUsername(String senderUsername) { this.senderUsername = senderUsername; }
    
    public String getReceiverUsername() { return receiverUsername; }
    public void setReceiverUsername(String receiverUsername) { this.receiverUsername = receiverUsername; }
    
    @Override
    public String toString() {
        return "MessageDto{" +
                "messageId=" + messageId +
                ", senderId=" + senderId +
                ", receiverId=" + receiverId +
                ", messageContent='" + messageContent + '\'' +
                ", timestamp=" + timestamp +
                ", isRead=" + isRead +
                '}';
    }
}
```

---

This is Part 1 of the documentation. The guide continues with detailed implementation steps for each component. Would you like me to continue with the next sections covering the backend development, entity creation, and service layer implementation?
### Step 8: Repository Layer

#### 8.1 UserRepository

Create `src/main/java/com/ma/message_apps/repository/UserRepository.java`:

```java
package com.ma.message_apps.repository;

import com.ma.message_apps.entity.User;
import com.ma.message_apps.enumDto.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by username
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Find user by username or email
     */
    Optional<User> findByUsernameOrEmail(String username, String email);
    
    /**
     * Check if username exists
     */
    boolean existsByUsername(String username);
    
    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);
    
    /**
     * Find users by status
     */
    List<User> findByUserStatus(UserStatus userStatus);
    
    /**
     * Find users by username containing (for search)
     */
    List<User> findByUsernameContainingIgnoreCase(String username);
    
    /**
     * Update user status
     */
    @Modifying
    @Query("UPDATE User u SET u.userStatus = :status WHERE u.userId = :userId")
    void updateUserStatus(@Param("userId") Long userId, @Param("status") UserStatus status);
    
    /**
     * Find users who are friends with the given user
     */
    @Query("SELECT u FROM User u WHERE u.userId IN " +
           "(SELECT CASE WHEN fr.sender.userId = :userId THEN fr.receiver.userId " +
           "ELSE fr.sender.userId END " +
           "FROM FriendRequests fr " +
           "WHERE (fr.sender.userId = :userId OR fr.receiver.userId = :userId) " +
           "AND fr.status = 'ACCEPTED')")
    List<User> findFriendsByUserId(@Param("userId") Long userId);
}
```

#### 8.2 MessageRepository

Create `src/main/java/com/ma/message_apps/repository/MessageRepository.java`:

```java
package com.ma.message_apps.repository;

import com.ma.message_apps.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    /**
     * Find messages between two users
     */
    @Query("SELECT m FROM Message m WHERE " +
           "(m.sender.userId = :userId1 AND m.receiver.userId = :userId2) OR " +
           "(m.sender.userId = :userId2 AND m.receiver.userId = :userId1) " +
           "ORDER BY m.timestamp ASC")
    List<Message> findMessagesBetweenUsers(@Param("userId1") Long userId1, 
                                          @Param("userId2") Long userId2);
    
    /**
     * Find messages sent by a user
     */
    List<Message> findBySenderUserIdOrderByTimestampDesc(Long senderId);
    
    /**
     * Find messages received by a user
     */
    List<Message> findByReceiverUserIdOrderByTimestampDesc(Long receiverId);
    
    /**
     * Find unread messages for a user
     */
    @Query("SELECT m FROM Message m WHERE m.receiver.userId = :userId AND m.isRead = false")
    List<Message> findUnreadMessagesByReceiverId(@Param("userId") Long userId);
    
    /**
     * Mark messages as read
     */
    @Modifying
    @Query("UPDATE Message m SET m.isRead = true WHERE m.receiver.userId = :receiverId AND m.sender.userId = :senderId")
    void markMessagesAsRead(@Param("receiverId") Long receiverId, @Param("senderId") Long senderId);
    
    /**
     * Count unread messages between users
     */
    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiver.userId = :receiverId AND m.sender.userId = :senderId AND m.isRead = false")
    Long countUnreadMessagesBetweenUsers(@Param("receiverId") Long receiverId, @Param("senderId") Long senderId);
    
    /**
     * Find recent conversations for a user
     */
    @Query("SELECT m FROM Message m WHERE m.messageId IN " +
           "(SELECT MAX(m2.messageId) FROM Message m2 WHERE " +
           "m2.sender.userId = :userId OR m2.receiver.userId = :userId " +
           "GROUP BY CASE WHEN m2.sender.userId = :userId THEN m2.receiver.userId ELSE m2.sender.userId END) " +
           "ORDER BY m.timestamp DESC")
    List<Message> findRecentConversations(@Param("userId") Long userId);
}
```

#### 8.3 FriendRequestsRepository

Create `src/main/java/com/ma/message_apps/repository/FriendRequestsRepository.java`:

```java
package com.ma.message_apps.repository;

import com.ma.message_apps.entity.FriendRequests;
import com.ma.message_apps.enumDto.FriendStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRequestsRepository extends JpaRepository<FriendRequests, Long> {
    
    /**
     * Find friend request between two users
     */
    @Query("SELECT fr FROM FriendRequests fr WHERE " +
           "(fr.sender.userId = :userId1 AND fr.receiver.userId = :userId2) OR " +
           "(fr.sender.userId = :userId2 AND fr.receiver.userId = :userId1)")
    Optional<FriendRequests> findFriendRequestBetweenUsers(@Param("userId1") Long userId1, 
                                                          @Param("userId2") Long userId2);
    
    /**
     * Find friend requests sent by user
     */
    List<FriendRequests> findBySenderUserIdAndStatus(Long senderId, FriendStatus status);
    
    /**
     * Find friend requests received by user
     */
    List<FriendRequests> findByReceiverUserIdAndStatus(Long receiverId, FriendStatus status);
    
    /**
     * Find pending friend requests for user
     */
    List<FriendRequests> findByReceiverUserIdAndStatusOrderByCreatedAtDesc(Long receiverId, FriendStatus status);
    
    /**
     * Check if users are friends
     */
    @Query("SELECT COUNT(fr) > 0 FROM FriendRequests fr WHERE " +
           "((fr.sender.userId = :userId1 AND fr.receiver.userId = :userId2) OR " +
           "(fr.sender.userId = :userId2 AND fr.receiver.userId = :userId1)) " +
           "AND fr.status = 'ACCEPTED'")
    boolean areUsersFriends(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
    
    /**
     * Check if friend request exists
     */
    @Query("SELECT COUNT(fr) > 0 FROM FriendRequests fr WHERE " +
           "((fr.sender.userId = :userId1 AND fr.receiver.userId = :userId2) OR " +
           "(fr.sender.userId = :userId2 AND fr.receiver.userId = :userId1))")
    boolean existsFriendRequestBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
    
    /**
     * Find all accepted friend requests for user
     */
    @Query("SELECT fr FROM FriendRequests fr WHERE " +
           "(fr.sender.userId = :userId OR fr.receiver.userId = :userId) " +
           "AND fr.status = 'ACCEPTED'")
    List<FriendRequests> findAcceptedFriendRequests(@Param("userId") Long userId);
}
```

### Step 9: Service Layer

#### 9.1 UserService

Create `src/main/java/com/ma/message_apps/service/UserService.java`:

```java
package com.ma.message_apps.service;

import com.ma.message_apps.dto.UserDto;
import com.ma.message_apps.entity.User;
import com.ma.message_apps.enumDto.UserStatus;
import com.ma.message_apps.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Register a new user
     */
    public UserDto registerUser(UserDto userDto) {
        // Check if username or email already exists
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        // Create new user entity
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(userDto.getPasswordHash()));
        user.setUserStatus(UserStatus.OFFLINE);
        
        // Save user
        User savedUser = userRepository.save(user);
        
        // Convert to DTO and return
        return convertToDto(savedUser);
    }
    
    /**
     * Authenticate user login
     */
    public UserDto authenticateUser(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsernameOrEmail(username, username);
        
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        
        User user = userOpt.get();
        
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new RuntimeException("Invalid password");
        }
        
        // Update user status to online
        user.setUserStatus(UserStatus.ONLINE);
        userRepository.save(user);
        
        return convertToDto(user);
    }
    
    /**
     * Find user by username
     */
    public Optional<UserDto> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::convertToDto);
    }
    
    /**
     * Find user by ID
     */
    public Optional<UserDto> findById(Long userId) {
        return userRepository.findById(userId)
                .map(this::convertToDto);
    }
    
    /**
     * Search users by username
     */
    public List<UserDto> searchUsers(String username) {
        return userRepository.findByUsernameContainingIgnoreCase(username)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get user's friends
     */
    public List<UserDto> getUserFriends(Long userId) {
        return userRepository.findFriendsByUserId(userId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Update user status
     */
    public void updateUserStatus(Long userId, UserStatus status) {
        userRepository.updateUserStatus(userId, status);
    }
    
    /**
     * Get online users
     */
    public List<UserDto> getOnlineUsers() {
        return userRepository.findByUserStatus(UserStatus.ONLINE)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert User entity to UserDto
     */
    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setUserStatus(user.getUserStatus());
        return dto;
    }
    
    /**
     * Convert UserDto to User entity
     */
    private User convertToEntity(UserDto dto) {
        User user = new User();
        user.setUserId(dto.getUserId());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPasswordHash(dto.getPasswordHash());
        user.setUserStatus(dto.getUserStatus());
        return user;
    }
}
```

#### 9.2 MessageService

Create `src/main/java/com/ma/message_apps/service/MessageService.java`:

```java
package com.ma.message_apps.service;

import com.ma.message_apps.dto.MessageDto;
import com.ma.message_apps.entity.Message;
import com.ma.message_apps.entity.User;
import com.ma.message_apps.repository.MessageRepository;
import com.ma.message_apps.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class MessageService {
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Send a message
     */
    public MessageDto sendMessage(MessageDto messageDto) {
        // Validate users exist
        Optional<User> senderOpt = userRepository.findById(messageDto.getSenderId());
        Optional<User> receiverOpt = userRepository.findById(messageDto.getReceiverId());
        
        if (senderOpt.isEmpty()) {
            throw new RuntimeException("Sender not found");
        }
        
        if (receiverOpt.isEmpty()) {
            throw new RuntimeException("Receiver not found");
        }
        
        // Create message entity
        Message message = new Message();
        message.setSender(senderOpt.get());
        message.setReceiver(receiverOpt.get());
        message.setMessageContent(messageDto.getMessageContent());
        message.setIsRead(false);
        
        // Save message
        Message savedMessage = messageRepository.save(message);
        
        // Convert to DTO and return
        return convertToDto(savedMessage);
    }
    
    /**
     * Get conversation between two users
     */
    public List<MessageDto> getConversation(Long userId1, Long userId2) {
        List<Message> messages = messageRepository.findMessagesBetweenUsers(userId1, userId2);
        return messages.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get recent conversations for a user
     */
    public List<MessageDto> getRecentConversations(Long userId) {
        List<Message> messages = messageRepository.findRecentConversations(userId);
        return messages.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get unread messages for a user
     */
    public List<MessageDto> getUnreadMessages(Long userId) {
        List<Message> messages = messageRepository.findUnreadMessagesByReceiverId(userId);
        return messages.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Mark messages as read
     */
    public void markMessagesAsRead(Long receiverId, Long senderId) {
        messageRepository.markMessagesAsRead(receiverId, senderId);
    }
    
    /**
     * Count unread messages between users
     */
    public Long countUnreadMessages(Long receiverId, Long senderId) {
        return messageRepository.countUnreadMessagesBetweenUsers(receiverId, senderId);
    }
    
    /**
     * Convert Message entity to MessageDto
     */
    private MessageDto convertToDto(Message message) {
        MessageDto dto = new MessageDto();
        dto.setMessageId(message.getMessageId());
        dto.setSenderId(message.getSender().getUserId());
        dto.setReceiverId(message.getReceiver().getUserId());
        dto.setSenderUsername(message.getSender().getUsername());
        dto.setReceiverUsername(message.getReceiver().getUsername());
        dto.setMessageContent(message.getMessageContent());
        dto.setTimestamp(message.getTimestamp());
        dto.setIsRead(message.getIsRead());
        return dto;
    }
}
```

### Step 10: Security Configuration

#### 10.1 SecurityConfig

Create `src/main/java/com/ma/message_apps/config/SecurityConfig.java`:

```java
package com.ma.message_apps.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/login", "/register", "/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll()
            );
        
        return http.build();
    }
}
```

---

This continues the comprehensive documentation with repository interfaces, service implementations, and security configuration. Would you like me to continue with the REST controllers, WebSocket configuration, and frontend development sections?
````
### Step 11: REST Controllers

#### 11.1 AuthController

Create `src/main/java/com/ma/message_apps/restcontroller/AuthController.java`:

```java
package com.ma.message_apps.restcontroller;

import com.ma.message_apps.dto.UserDto;
import com.ma.message_apps.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    /**
     * User registration endpoint
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@Valid @RequestBody UserDto userDto) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            UserDto registeredUser = userService.registerUser(userDto);
            response.put("success", true);
            response.put("message", "User registered successfully");
            response.put("user", registeredUser);
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * User login endpoint
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(
            @RequestParam String username,
            @RequestParam String passwordHash,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            UserDto authenticatedUser = userService.authenticateUser(username, passwordHash);
            
            // Store user in session
            session.setAttribute("loggedInUser", authenticatedUser);
            
            response.put("success", true);
            response.put("message", "Login successful");
            response.put("user", authenticatedUser);
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
    
    /**
     * User logout endpoint
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logoutUser(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Get logged-in user from session
            UserDto loggedInUser = (UserDto) session.getAttribute("loggedInUser");
            
            if (loggedInUser != null) {
                // Update user status to offline
                userService.updateUserStatus(loggedInUser.getUserId(), 
                    com.ma.message_apps.enumDto.UserStatus.OFFLINE);
            }
            
            // Invalidate session
            session.invalidate();
            
            response.put("success", true);
            response.put("message", "Logout successful");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Logout failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get current user endpoint
     */
    @GetMapping("/current-user")
    public ResponseEntity<Map<String, Object>> getCurrentUser(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        UserDto loggedInUser = (UserDto) session.getAttribute("loggedInUser");
        
        if (loggedInUser != null) {
            response.put("success", true);
            response.put("user", loggedInUser);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "No user logged in");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}
```

#### 11.2 MessageRestController

Create `src/main/java/com/ma/message_apps/restcontroller/MessageRestController.java`:

```java
package com.ma.message_apps.restcontroller;

import com.ma.message_apps.dto.MessageDto;
import com.ma.message_apps.dto.UserDto;
import com.ma.message_apps.service.MessageService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
public class MessageRestController {
    
    @Autowired
    private MessageService messageService;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    /**
     * Send a message
     */
    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendMessage(
            @Valid @RequestBody MessageDto messageDto,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check if user is logged in
            UserDto loggedInUser = (UserDto) session.getAttribute("loggedInUser");
            if (loggedInUser == null) {
                response.put("success", false);
                response.put("message", "User not logged in");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            // Set sender ID from session
            messageDto.setSenderId(loggedInUser.getUserId());
            
            // Send message
            MessageDto sentMessage = messageService.sendMessage(messageDto);
            
            // Send real-time notification via WebSocket
            messagingTemplate.convertAndSend("/topic/user/" + messageDto.getReceiverId(), sentMessage);
            
            response.put("success", true);
            response.put("message", "Message sent successfully");
            response.put("data", sentMessage);
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Get conversation between current user and another user
     */
    @GetMapping("/conversation/{otherUserId}")
    public ResponseEntity<Map<String, Object>> getConversation(
            @PathVariable Long otherUserId,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check if user is logged in
            UserDto loggedInUser = (UserDto) session.getAttribute("loggedInUser");
            if (loggedInUser == null) {
                response.put("success", false);
                response.put("message", "User not logged in");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            // Get conversation
            List<MessageDto> conversation = messageService.getConversation(
                loggedInUser.getUserId(), otherUserId);
            
            // Mark messages as read
            messageService.markMessagesAsRead(loggedInUser.getUserId(), otherUserId);
            
            response.put("success", true);
            response.put("data", conversation);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch conversation");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get recent conversations for current user
     */
    @GetMapping("/recent")
    public ResponseEntity<Map<String, Object>> getRecentConversations(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check if user is logged in
            UserDto loggedInUser = (UserDto) session.getAttribute("loggedInUser");
            if (loggedInUser == null) {
                response.put("success", false);
                response.put("message", "User not logged in");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            // Get recent conversations
            List<MessageDto> recentConversations = messageService.getRecentConversations(
                loggedInUser.getUserId());
            
            response.put("success", true);
            response.put("data", recentConversations);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch recent conversations");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get unread messages count
     */
    @GetMapping("/unread-count/{senderId}")
    public ResponseEntity<Map<String, Object>> getUnreadCount(
            @PathVariable Long senderId,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check if user is logged in
            UserDto loggedInUser = (UserDto) session.getAttribute("loggedInUser");
            if (loggedInUser == null) {
                response.put("success", false);
                response.put("message", "User not logged in");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            // Get unread count
            Long unreadCount = messageService.countUnreadMessages(
                loggedInUser.getUserId(), senderId);
            
            response.put("success", true);
            response.put("unreadCount", unreadCount);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch unread count");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
```

#### 11.3 FriendRequestController

Create `src/main/java/com/ma/message_apps/restcontroller/FriendRequestController.java`:

```java
package com.ma.message_apps.restcontroller;

import com.ma.message_apps.dto.FriendRequestsDto;
import com.ma.message_apps.dto.UserDto;
import com.ma.message_apps.service.FriendRequestService;
import com.ma.message_apps.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/friends")
@CrossOrigin(origins = "*")
public class FriendRequestController {
    
    @Autowired
    private FriendRequestService friendRequestService;
    
    @Autowired
    private UserService userService;
    
    /**
     * Send friend request
     */
    @PostMapping("/request")
    public ResponseEntity<Map<String, Object>> sendFriendRequest(
            @RequestParam String usernameOrEmail,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check if user is logged in
            UserDto loggedInUser = (UserDto) session.getAttribute("loggedInUser");
            if (loggedInUser == null) {
                response.put("success", false);
                response.put("message", "User not logged in");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            // Find target user
            UserDto targetUser = userService.findByUsername(usernameOrEmail)
                .orElse(userService.findByEmail(usernameOrEmail).orElse(null));
            
            if (targetUser == null) {
                response.put("success", false);
                response.put("message", "User not found");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Send friend request
            FriendRequestsDto friendRequest = friendRequestService.sendFriendRequest(
                loggedInUser.getUserId(), targetUser.getUserId());
            
            response.put("success", true);
            response.put("message", "Friend request sent successfully");
            response.put("data", friendRequest);
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Get pending friend requests
     */
    @GetMapping("/requests/pending")
    public ResponseEntity<Map<String, Object>> getPendingRequests(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check if user is logged in
            UserDto loggedInUser = (UserDto) session.getAttribute("loggedInUser");
            if (loggedInUser == null) {
                response.put("success", false);
                response.put("message", "User not logged in");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            // Get pending requests
            List<FriendRequestsDto> pendingRequests = friendRequestService.getPendingRequests(
                loggedInUser.getUserId());
            
            response.put("success", true);
            response.put("data", pendingRequests);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch pending requests");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Accept friend request
     */
    @PostMapping("/requests/{requestId}/accept")
    public ResponseEntity<Map<String, Object>> acceptFriendRequest(
            @PathVariable Long requestId,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check if user is logged in
            UserDto loggedInUser = (UserDto) session.getAttribute("loggedInUser");
            if (loggedInUser == null) {
                response.put("success", false);
                response.put("message", "User not logged in");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            // Accept friend request
            FriendRequestsDto acceptedRequest = friendRequestService.acceptFriendRequest(requestId);
            
            response.put("success", true);
            response.put("message", "Friend request accepted");
            response.put("data", acceptedRequest);
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Reject friend request
     */
    @PostMapping("/requests/{requestId}/reject")
    public ResponseEntity<Map<String, Object>> rejectFriendRequest(
            @PathVariable Long requestId,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check if user is logged in
            UserDto loggedInUser = (UserDto) session.getAttribute("loggedInUser");
            if (loggedInUser == null) {
                response.put("success", false);
                response.put("message", "User not logged in");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            // Reject friend request
            FriendRequestsDto rejectedRequest = friendRequestService.rejectFriendRequest(requestId);
            
            response.put("success", true);
            response.put("message", "Friend request rejected");
            response.put("data", rejectedRequest);
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Get user's friends list
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getFriendsList(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check if user is logged in
            UserDto loggedInUser = (UserDto) session.getAttribute("loggedInUser");
            if (loggedInUser == null) {
                response.put("success", false);
                response.put("message", "User not logged in");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            // Get friends list
            List<UserDto> friends = userService.getUserFriends(loggedInUser.getUserId());
            
            response.put("success", true);
            response.put("data", friends);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch friends list");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
```

### Step 12: WebSocket Configuration

#### 12.1 WebSocketConfig

Create `src/main/java/com/ma/message_apps/config/WebSocketConfig.java`:

```java
package com.ma.message_apps.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable a simple in-memory message broker to carry the messages
        // back to the client on destinations prefixed with "/topic"
        config.enableSimpleBroker("/topic", "/queue");
        
        // Designate the "/app" prefix for messages that are bound 
        // for methods annotated with @MessageMapping
        config.setApplicationDestinationPrefixes("/app");
        
        // Set user destination prefix for private messaging
        config.setUserDestinationPrefix("/user");
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register the "/ws" endpoint, enabling SockJS fallback options
        // so that alternate transports can be used if WebSocket is not available
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
```

#### 12.2 WebSocket Message Controller

Create `src/main/java/com/ma/message_apps/controller/WebSocketController.java`:

```java
package com.ma.message_apps.controller;

import com.ma.message_apps.dto.MessageDto;
import com.ma.message_apps.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
    
    @Autowired
    private MessageService messageService;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    /**
     * Handle private messages
     */
    @MessageMapping("/private-message")
    public void sendPrivateMessage(@Payload MessageDto messageDto) {
        try {
            // Save message to database
            MessageDto savedMessage = messageService.sendMessage(messageDto);
            
            // Send to receiver via WebSocket
            messagingTemplate.convertAndSendToUser(
                savedMessage.getReceiverId().toString(),
                "/queue/private",
                savedMessage
            );
            
            // Send confirmation to sender
            messagingTemplate.convertAndSendToUser(
                savedMessage.getSenderId().toString(),
                "/queue/sent",
                savedMessage
            );
            
        } catch (Exception e) {
            // Handle error - could send error message back to sender
            System.err.println("Error sending private message: " + e.getMessage());
        }
    }
    
    /**
     * Handle typing indicators
     */
    @MessageMapping("/typing")
    public void handleTyping(@Payload TypingIndicator typingIndicator) {
        // Send typing indicator to the target user
        messagingTemplate.convertAndSendToUser(
            typingIndicator.getReceiverId().toString(),
            "/queue/typing",
            typingIndicator
        );
    }
    
    /**
     * Handle user status updates
     */
    @MessageMapping("/status")
    @SendTo("/topic/status")
    public UserStatusUpdate handleStatusUpdate(@Payload UserStatusUpdate statusUpdate) {
        // Broadcast status update to all users
        return statusUpdate;
    }
    
    // Helper classes for WebSocket messages
    public static class TypingIndicator {
        private Long senderId;
        private Long receiverId;
        private String senderUsername;
        private boolean isTyping;
        
        // Constructors, getters, setters
        public TypingIndicator() {}
        
        public Long getSenderId() { return senderId; }
        public void setSenderId(Long senderId) { this.senderId = senderId; }
        
        public Long getReceiverId() { return receiverId; }
        public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }
        
        public String getSenderUsername() { return senderUsername; }
        public void setSenderUsername(String senderUsername) { this.senderUsername = senderUsername; }
        
        public boolean isTyping() { return isTyping; }
        public void setTyping(boolean typing) { isTyping = typing; }
    }
    
    public static class UserStatusUpdate {
        private Long userId;
        private String username;
        private String status;
        
        // Constructors, getters, setters
        public UserStatusUpdate() {}
        
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
```

### Step 13: View Controllers

#### 13.1 Main Controller for Pages

Create `src/main/java/com/ma/message_apps/controller/MessagingController.java`:

```java
package com.ma.message_apps.controller;

import com.ma.message_apps.dto.UserDto;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MessagingController {
    
    /**
     * Home page
     */
    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        UserDto loggedInUser = (UserDto) session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            return "redirect:/dashboard";
        }
        return "home";
    }
    
    /**
     * Login page
     */
    @GetMapping("/login")
    public String login(Model model, HttpSession session) {
        UserDto loggedInUser = (UserDto) session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            return "redirect:/dashboard";
        }
        return "login";
    }
    
    /**
     * Registration page
     */
    @GetMapping("/register")
    public String register(Model model, HttpSession session) {
        UserDto loggedInUser = (UserDto) session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            return "redirect:/dashboard";
        }
        return "register";
    }
    
    /**
     * Dashboard page
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        UserDto loggedInUser = (UserDto) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", loggedInUser);
        return "dashboard";
    }
}
```

---

## Frontend Development

### Step 14: JavaScript Implementation

#### 14.1 Login JavaScript

Update `src/main/resources/static/js/login.js`:

```javascript
document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('loginForm');
    const loginMessage = document.getElementById('loginMessage');
    
    loginForm.addEventListener('submit', function(e) {
        e.preventDefault();
        
        const username = document.getElementById('username').value;
        const password = document.getElementById('passwordHash').value;
        
        // Create form data
        const formData = new FormData();
        formData.append('username', username);
        formData.append('passwordHash', password);
        
        // Show loading state
        const submitBtn = loginForm.querySelector('button[type="submit"]');
        const originalText = submitBtn.innerHTML;
        submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Signing In...';
        submitBtn.disabled = true;
        
        // Send login request
        fetch('/api/auth/login', {
            method: 'POST',
            body: formData
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                showMessage('Login successful! Redirecting...', 'success');
                setTimeout(() => {
                    window.location.href = '/dashboard';
                }, 1000);
            } else {
                showMessage(data.message || 'Login failed', 'error');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showMessage('An error occurred. Please try again.', 'error');
        })
        .finally(() => {
            // Reset button
            submitBtn.innerHTML = originalText;
            submitBtn.disabled = false;
        });
    });
    
    function showMessage(message, type) {
        loginMessage.textContent = message;
        loginMessage.className = `auth-message ${type}`;
        loginMessage.style.display = 'block';
        
        // Auto-hide after 5 seconds
        setTimeout(() => {
            loginMessage.style.display = 'none';
        }, 5000);
    }
});
```

#### 14.2 Registration JavaScript

Update `src/main/resources/static/js/register.js`:

```javascript
document.addEventListener('DOMContentLoaded', function() {
    const registerForm = document.getElementById('registerForm');
    const registerMessage = document.getElementById('registerMessage');
    
    registerForm.addEventListener('submit', function(e) {
        e.preventDefault();
        
        const username = document.getElementById('username').value;
        const email = document.getElementById('email').value;
        const password = document.getElementById('passwordHash').value;
        
        // Basic validation
        if (!validateForm(username, email, password)) {
            return;
        }
        
        // Create user data
        const userData = {
            username: username,
            email: email,
            passwordHash: password
        };
        
        // Show loading state
        const submitBtn = registerForm.querySelector('button[type="submit"]');
        const originalText = submitBtn.innerHTML;
        submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Creating Account...';
        submitBtn.disabled = true;
        
        // Send registration request
        fetch('/api/auth/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(userData)
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                showMessage('Registration successful! Please login.', 'success');
                setTimeout(() => {
                    window.location.href = '/login';
                }, 2000);
            } else {
                showMessage(data.message || 'Registration failed', 'error');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showMessage('An error occurred. Please try again.', 'error');
        })
        .finally(() => {
            // Reset button
            submitBtn.innerHTML = originalText;
            submitBtn.disabled = false;
        });
    });
    
    function validateForm(username, email, password) {
        if (username.length < 3) {
            showMessage('Username must be at least 3 characters long', 'error');
            return false;
        }
        
        if (!isValidEmail(email)) {
            showMessage('Please enter a valid email address', 'error');
            return false;
        }
        
        if (password.length < 6) {
            showMessage('Password must be at least 6 characters long', 'error');
            return false;
        }
        
        return true;
    }
    
    function isValidEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }
    
    function showMessage(message, type) {
        registerMessage.textContent = message;
        registerMessage.className = `auth-message ${type}`;
        registerMessage.style.display = 'block';
        
        // Auto-hide after 5 seconds
        setTimeout(() => {
            registerMessage.style.display = 'none';
        }, 5000);
    }
});
```

#### 14.3 Dashboard JavaScript

Update `src/main/resources/static/js/dashboard.js`:

```javascript
// Global variables
let currentUser = null;
let selectedUserId = null;
let stompClient = null;
let connected = false;

document.addEventListener('DOMContentLoaded', function() {
    // Initialize dashboard
    initializeDashboard();
    
    // Set up event listeners
    setupEventListeners();
    
    // Connect to WebSocket
    connectWebSocket();
    
    // Load initial data
    loadCurrentUser();
    loadFriends();
    loadFriendRequests();
});

/**
 * Initialize dashboard components
 */
function initializeDashboard() {
    console.log('Initializing dashboard...');
    
    // Update footer username
    updateFooterUsername();
}

/**
 * Set up all event listeners
 */
function setupEventListeners() {
    // Chat form submission
    const chatForm = document.getElementById('chat-form');
    if (chatForm) {
        chatForm.addEventListener('submit', function(e) {
            e.preventDefault();
            sendMessage();
        });
    }
    
    // Send friend request form
    const sendFriendForm = document.getElementById('send-friend-form');
    if (sendFriendForm) {
        sendFriendForm.addEventListener('submit', function(e) {
            e.preventDefault();
            sendFriendRequest();
        });
    }
    
    // Logout functionality
    window.logout = function() {
        fetch('/api/auth/logout', {
            method: 'POST'
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                window.location.href = '/';
            }
        })
        .catch(error => {
            console.error('Logout error:', error);
            window.location.href = '/';
        });
    };
}

/**
 * Connect to WebSocket
 */
function connectWebSocket() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    
    stompClient.connect({}, function(frame) {
        console.log('Connected to WebSocket: ' + frame);
        connected = true;
        
        // Subscribe to personal message queue
        if (currentUser) {
            stompClient.subscribe('/topic/user/' + currentUser.userId, function(message) {
                const messageData = JSON.parse(message.body);
                handleIncomingMessage(messageData);
            });
        }
        
    }, function(error) {
        console.error('WebSocket connection error:', error);
        connected = false;
        
        // Retry connection after 5 seconds
        setTimeout(connectWebSocket, 5000);
    });
}

/**
 * Load current user information
 */
function loadCurrentUser() {
    fetch('/api/auth/current-user')
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                currentUser = data.user;
                updateUserDisplay();
                
                // Subscribe to WebSocket if connected
                if (connected && stompClient) {
                    stompClient.subscribe('/topic/user/' + currentUser.userId, function(message) {
                        const messageData = JSON.parse(message.body);
                        handleIncomingMessage(messageData);
                    });
                }
            } else {
                window.location.href = '/login';
            }
        })
        .catch(error => {
            console.error('Error loading current user:', error);
            window.location.href = '/login';
        });
}

/**
 * Update user display elements
 */
function updateUserDisplay() {
    const userNameElement = document.getElementById('current-user-name');
    if (userNameElement && currentUser) {
        userNameElement.textContent = currentUser.username;
    }
}

/**
 * Update footer username
 */
function updateFooterUsername() {
    const footerUsername = document.querySelector('.footer-username');
    if (footerUsername && currentUser) {
        footerUsername.textContent = currentUser.username;
    }
}

/**
 * Load friends list
 */
function loadFriends() {
    fetch('/api/friends/list')
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                displayFriends(data.data);
            } else {
                console.error('Failed to load friends:', data.message);
            }
        })
        .catch(error => {
            console.error('Error loading friends:', error);
        });
}

/**
 * Display friends in the contacts list
 */
function displayFriends(friends) {
    const contactsList = document.getElementById('chat-contacts-list');
    if (!contactsList) return;
    
    contactsList.innerHTML = '';
    
    if (friends.length === 0) {
        contactsList.innerHTML = '<li class="list-group-item text-center text-muted">No friends yet</li>';
        return;
    }
    
    friends.forEach(friend => {
        const listItem = document.createElement('li');
        listItem.className = 'list-group-item d-flex justify-content-between align-items-center';
        listItem.style.cursor = 'pointer';
        listItem.innerHTML = `
            <div>
                <strong>${friend.username}</strong>
                <span class="badge bg-${friend.userStatus === 'ONLINE' ? 'success' : 'secondary'} ms-2">
                    ${friend.userStatus}
                </span>
            </div>
        `;
        
        listItem.addEventListener('click', function() {
            selectContact(friend);
        });
        
        contactsList.appendChild(listItem);
    });
}

/**
 * Select a contact for chatting
 */
function selectContact(friend) {
    selectedUserId = friend.userId;
    
    // Update UI to show selected contact
    const contactItems = document.querySelectorAll('#chat-contacts-list .list-group-item');
    contactItems.forEach(item => item.classList.remove('active', 'selected'));
    
    event.target.closest('.list-group-item').classList.add('active', 'selected');
    
    // Update chat header
    const chatLabel = document.getElementById('chat-with-label');
    if (chatLabel) {
        chatLabel.textContent = `Chat with ${friend.username}`;
    }
    
    // Show chat form
    const chatForm = document.getElementById('chat-form');
    if (chatForm) {
        chatForm.style.display = 'flex';
    }
    
    // Load conversation
    loadConversation(friend.userId);
}

/**
 * Load conversation with selected user
 */
function loadConversation(userId) {
    fetch(`/api/messages/conversation/${userId}`)
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                displayMessages(data.data);
            } else {
                console.error('Failed to load conversation:', data.message);
            }
        })
        .catch(error => {
            console.error('Error loading conversation:', error);
        });
}

/**
 * Display messages in chat area
 */
function displayMessages(messages) {
    const messagesContainer = document.getElementById('chat-messages');
    if (!messagesContainer) return;
    
    messagesContainer.innerHTML = '';
    
    messages.forEach(message => {
        const messageElement = createMessageElement(message);
        messagesContainer.appendChild(messageElement);
    });
    
    // Scroll to bottom
    messagesContainer.scrollTop = messagesContainer.scrollHeight;
}

/**
 * Create message element
 */
function createMessageElement(message) {
    const messageDiv = document.createElement('div');
    const isOwnMessage = message.senderId === currentUser.userId;
    
    messageDiv.className = isOwnMessage ? 'chat-message-own' : 'chat-message-other';
    
    const timestamp = new Date(message.timestamp).toLocaleTimeString();
    
    messageDiv.innerHTML = `
        <div class="chat-message-header">
            <span>${isOwnMessage ? 'You' : message.senderUsername}</span>
            <span>${timestamp}</span>
        </div>
        <div class="chat-message-body">${escapeHtml(message.messageContent)}</div>
    `;
    
    return messageDiv;
}

/**
 * Send a message
 */
function sendMessage() {
    const messageInput = document.getElementById('chat-input');
    const messageContent = messageInput.value.trim();
    
    if (!messageContent || !selectedUserId) {
        return;
    }
    
    const messageData = {
        receiverId: selectedUserId,
        messageContent: messageContent
    };
    
    fetch('/api/messages/send', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(messageData)
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            // Clear input
            messageInput.value = '';
            
            // Add message to chat
            const messageElement = createMessageElement(data.data);
            const messagesContainer = document.getElementById('chat-messages');
            messagesContainer.appendChild(messageElement);
            messagesContainer.scrollTop = messagesContainer.scrollHeight;
            
        } else {
            showNotification('Failed to send message: ' + data.message, 'error');
        }
    })
    .catch(error => {
        console.error('Error sending message:', error);
        showNotification('Failed to send message', 'error');
    });
}

/**
 * Handle incoming WebSocket messages
 */
function handleIncomingMessage(message) {
    // If we're currently chatting with the sender, add message to chat
    if (selectedUserId === message.senderId) {
        const messageElement = createMessageElement(message);
        const messagesContainer = document.getElementById('chat-messages');
        messagesContainer.appendChild(messageElement);
        messagesContainer.scrollTop = messagesContainer.scrollHeight;
    }
    
    // Show notification
    showNotification(`New message from ${message.senderUsername}`, 'info');
}

/**
 * Load friend requests
 */
function loadFriendRequests() {
    fetch('/api/friends/requests/pending')
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                displayFriendRequests(data.data);
            } else {
                console.error('Failed to load friend requests:', data.message);
            }
        })
        .catch(error => {
            console.error('Error loading friend requests:', error);
        });
}

/**
 * Display friend requests
 */
function displayFriendRequests(requests) {
    const requestsList = document.getElementById('friend-requests-list');
    if (!requestsList) return;
    
    requestsList.innerHTML = '';
    
    if (requests.length === 0) {
        requestsList.innerHTML = '<tr><td colspan="3" class="text-center text-muted">No pending requests</td></tr>';
        return;
    }
    
    requests.forEach(request => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${request.senderUsername}</td>
            <td><span class="badge bg-warning">${request.status}</span></td>
            <td>
                <button class="btn btn-success btn-sm me-2" onclick="acceptFriendRequest(${request.requestId})">
                    Accept
                </button>
                <button class="btn btn-danger btn-sm" onclick="rejectFriendRequest(${request.requestId})">
                    Reject
                </button>
            </td>
        `;
        requestsList.appendChild(row);
    });
}

/**
 * Send friend request
 */
function sendFriendRequest() {
    const usernameInput = document.getElementById('friend-username');
    const username = usernameInput.value.trim();
    
    if (!username) {
        showNotification('Please enter a username or email', 'error');
        return;
    }
    
    const formData = new FormData();
    formData.append('usernameOrEmail', username);
    
    fetch('/api/friends/request', {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            showNotification('Friend request sent successfully', 'success');
            usernameInput.value = '';
        } else {
            showNotification(data.message, 'error');
        }
    })
    .catch(error => {
        console.error('Error sending friend request:', error);
        showNotification('Failed to send friend request', 'error');
    });
}

/**
 * Accept friend request
 */
window.acceptFriendRequest = function(requestId) {
    fetch(`/api/friends/requests/${requestId}/accept`, {
        method: 'POST'
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            showNotification('Friend request accepted', 'success');
            loadFriendRequests();
            loadFriends();
        } else {
            showNotification(data.message, 'error');
        }
    })
    .catch(error => {
        console.error('Error accepting friend request:', error);
        showNotification('Failed to accept friend request', 'error');
    });
};

/**
 * Reject friend request
 */
window.rejectFriendRequest = function(requestId) {
    fetch(`/api/friends/requests/${requestId}/reject`, {
        method: 'POST'
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            showNotification('Friend request rejected', 'success');
            loadFriendRequests();
        } else {
            showNotification(data.message, 'error');
        }
    })
    .catch(error => {
        console.error('Error rejecting friend request:', error);
        showNotification('Failed to reject friend request', 'error');
    });
};

/**
 * Show notification
 */
function showNotification(message, type) {
    // Create notification element
    const notification = document.createElement('div');
    notification.className = `alert alert-${type === 'error' ? 'danger' : type === 'success' ? 'success' : 'info'} notification`;
    notification.textContent = message;
    notification.style.position = 'fixed';
    notification.style.top = '20px';
    notification.style.right = '20px';
    notification.style.zIndex = '9999';
    notification.style.minWidth = '300px';
    
    document.body.appendChild(notification);
    
    // Remove after 5 seconds
    setTimeout(() => {
        if (notification.parentNode) {
            notification.parentNode.removeChild(notification);
        }
    }, 5000);
}

/**
 * Escape HTML to prevent XSS
 */
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// Add SockJS and Stomp libraries
const script1 = document.createElement('script');
script1.src = 'https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js';
document.head.appendChild(script1);

const script2 = document.createElement('script');
script2.src = 'https://cdn.jsdelivr.net/npm/stompjs@2.3.3/lib/stomp.min.js';
document.head.appendChild(script2);
```

---

## Testing

### Step 15: Unit Testing

#### 15.1 Service Layer Tests

Create `src/test/java/com/ma/message_apps/service/UserServiceTest.java`:

```java
package com.ma.message_apps.service;

import com.ma.message_apps.dto.UserDto;
import com.ma.message_apps.entity.User;
import com.ma.message_apps.enumDto.UserStatus;
import com.ma.message_apps.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UserService userService;
    
    private UserDto testUserDto;
    private User testUser;
    
    @BeforeEach
    void setUp() {
        testUserDto = new UserDto();
        testUserDto.setUsername("testuser");
        testUserDto.setEmail("test@example.com");
        testUserDto.setPasswordHash("password123");
        
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("encodedPassword");
        testUser.setUserStatus(UserStatus.OFFLINE);
    }
    
    @Test
    void registerUser_Success() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // When
        UserDto result = userService.registerUser(testUserDto);
        
        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void registerUser_UsernameExists_ThrowsException() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(true);
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(testUserDto);
        });
        
        assertEquals("Username already exists", exception.getMessage());
    }
    
    @Test
    void authenticateUser_Success() {
        // Given
        when(userRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // When
        UserDto result = userService.authenticateUser("testuser", "password123");
        
        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void authenticateUser_InvalidPassword_ThrowsException() {
        // Given
        when(userRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.authenticateUser("testuser", "wrongpassword");
        });
        
        assertEquals("Invalid password", exception.getMessage());
    }
}
```

#### 15.2 Controller Tests

Create `src/test/java/com/ma/message_apps/restcontroller/AuthControllerTest.java`:

```java
package com.ma.message_apps.restcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ma.message_apps.dto.UserDto;
import com.ma.message_apps.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserService userService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private UserDto testUserDto;
    private MockHttpSession session;
    
    @BeforeEach
    void setUp() {
        testUserDto = new UserDto();
        testUserDto.setUserId(1L);
        testUserDto.setUsername("testuser");
        testUserDto.setEmail("test@example.com");
        
        session = new MockHttpSession();
    }
    
    @Test
    void registerUser_Success() throws Exception {
        // Given
        UserDto inputDto = new UserDto();
        inputDto.setUsername("newuser");
        inputDto.setEmail("new@example.com");
        inputDto.setPasswordHash("password123");
        
        when(userService.registerUser(any(UserDto.class))).thenReturn(testUserDto);
        
        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User registered successfully"));
    }
    
    @Test
    void loginUser_Success() throws Exception {
        // Given
        when(userService.authenticateUser(anyString(), anyString())).thenReturn(testUserDto);
        
        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .param("username", "testuser")
                .param("passwordHash", "password123")
                .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login successful"));
    }
}
```

### Step 16: Integration Testing

#### 16.1 Application Tests

Create `src/test/java/com/ma/message_apps/MessageAppsApplicationTests.java`:

```java
package com.ma.message_apps;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class MessageAppsApplicationTests {
    
    @Test
    void contextLoads() {
        // This test ensures that the Spring application context loads successfully
    }
}
```

#### 16.2 Test Configuration

Create `src/test/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password: password
  
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.H2Dialect
  
  h2:
    console:
      enabled: true

logging:
  level:
    com.ma.message_apps: DEBUG
```

---

## Deployment

### Step 17: Production Configuration

#### 17.1 Production application.yml

Create `src/main/resources/application-prod.yml`:

```yaml
spring:
  datasource:
    url: ${DATABASE_URL:jdbc:mysql://localhost:3306/message_app_db}
    username: ${DATABASE_USERNAME:root}
    password: ${DATABASE_PASSWORD:your_password}
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      idle-timeout: 300000
      max-lifetime: 600000
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
        format_sql: false
  
  thymeleaf:
    cache: true
  
  web:
    resources:
      cache:
        period: 31536000 # 1 year

server:
  port: ${PORT:8080}
  compression:
    enabled: true
    mime-types: text/html,text/xml,text/plain,text/css,text/javascript,application/javascript,application/json
  
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: when-authorized

logging:
  level:
    root: INFO
    com.ma.message_apps: INFO
  file:
    name: logs/message-app.log
```

#### 17.2 Docker Configuration

Create `Dockerfile`:

```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/message-apps-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

Create `docker-compose.yml`:

```yaml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DATABASE_URL=jdbc:mysql://db:3306/message_app_db
      - DATABASE_USERNAME=root
      - DATABASE_PASSWORD=rootpassword
    depends_on:
      - db
    networks:
      - message-app-network

  db:
    image: mysql:8.0
    environment:
      - MYSQL_ROOT_PASSWORD=rootpassword
      - MYSQL_DATABASE=message_app_db
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
    networks:
      - message-app-network

volumes:
  mysql_data:

networks:
  message-app-network:
    driver: bridge
```

### Step 18: Build and Run Instructions

#### 18.1 Development Setup

**Prerequisites:**
```bash
# Install Java 17
# Install Maven
# Install MySQL

# Clone repository
git clone <repository-url>
cd message-apps

# Create database
mysql -u root -p
CREATE DATABASE message_app_db;
exit

# Update database credentials in application.yml
# Build and run
mvn clean install
mvn spring-boot:run
```

#### 18.2 Production Deployment

**Using Docker:**
```bash
# Build the application
mvn clean package -DskipTests

# Build and run with Docker Compose
docker-compose up --build
```

**Manual Deployment:**
```bash
# Build application
mvn clean package -Pprod

# Run with production profile
java -jar target/message-apps-*.jar --spring.profiles.active=prod
```

---

## Advanced Features

### Step 19: Additional Enhancements

#### 19.1 Message Search Feature

Add to `MessageRepository.java`:
```java
@Query("SELECT m FROM Message m WHERE " +
       "(m.sender.userId = :userId OR m.receiver.userId = :userId) " +
       "AND LOWER(m.messageContent) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
       "ORDER BY m.timestamp DESC")
List<Message> searchMessages(@Param("userId") Long userId, @Param("searchTerm") String searchTerm);
```

#### 19.2 File Upload Support

Add to `MessageDto.java`:
```java
private String fileUrl;
private String fileName;
private String fileType;
```

#### 19.3 Message Reactions

Create `MessageReaction.java` entity:
```java
@Entity
@Table(name = "message_reactions")
public class MessageReaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reactionId;
    
    @ManyToOne
    @JoinColumn(name = "message_id")
    private Message message;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    private String reactionType; // LIKE, LOVE, LAUGH, etc.
    
    // Getters and setters...
}
```

#### 19.4 Group Chat Feature

Create `ChatGroup.java` entity:
```java
@Entity
@Table(name = "chat_groups")
public class ChatGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupId;
    
    private String groupName;
    private String description;
    
    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;
    
    @ManyToMany
    @JoinTable(name = "group_members",
               joinColumns = @JoinColumn(name = "group_id"),
               inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> members;
    
    // Getters and setters...
}
```

### Step 20: Performance Optimization

#### 20.1 Caching Configuration

Add to `pom.xml`:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
<dependency>
    <groupId>com.github.ben-manes.caffeine</groupId>
    <artifactId>caffeine</artifactId>
</dependency>
```

Create `CacheConfig.java`:
```java
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(10, TimeUnit.MINUTES));
        return cacheManager;
    }
}
```

#### 20.2 Database Indexing

Add to `schema.sql`:
```sql
-- Indexes for better performance
CREATE INDEX idx_messages_sender_receiver ON messages(sender_id, receiver_id);
CREATE INDEX idx_messages_timestamp ON messages(timestamp);
CREATE INDEX idx_friend_requests_receiver_status ON friend_requests(receiver_id, status);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
```

---

## Conclusion

This comprehensive guide covers building a complete real-time messaging application from scratch. The application includes:

✅ **Complete Backend Architecture**
✅ **Modern Frontend with Animations**
✅ **Real-time WebSocket Communication**
✅ **User Authentication & Authorization**
✅ **Friend Request System**
✅ **Responsive Design**
✅ **Testing Framework**
✅ **Production Deployment**
✅ **Performance Optimization**

**Next Steps:**
1. Follow the step-by-step implementation
2. Test each component as you build
3. Customize the UI/UX to your preferences
4. Add additional features as needed
5. Deploy to production environment

The application is now ready for production use with a professional, modern design and robust functionality!
