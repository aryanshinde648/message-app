# Message Apps - Complete Guide

## Table of Contents
1. [Introduction](#introduction)
2. [Project Overview](#project-overview)
3. [Technical Stack](#technical-stack)
4. [Project Structure](#project-structure)
5. [Setup and Installation](#setup-and-installation)
6. [Database Configuration](#database-configuration)
7. [Application Features](#application-features)
8. [API Documentation](#api-documentation)
9. [Frontend Structure](#frontend-structure)
10. [Backend Architecture](#backend-architecture)
11. [Websocket Configuration](#websocket-configuration)
12. [Security](#security)
13. [Development Guidelines](#development-guidelines)
14. [Testing](#testing)
15. [Deployment](#deployment)
16. [Troubleshooting](#troubleshooting)

## Introduction

Message Apps is a web-based messaging platform built with Spring Boot that enables users to register, manage friend requests, and exchange messages in real-time. This guide provides comprehensive documentation of the application architecture, features, and instructions for setup and deployment.

## Project Overview

Message Apps is designed to provide a seamless messaging experience with the following core capabilities:
- User registration and authentication
- Friend request management
- Real-time messaging
- User online status tracking
- Responsive web interface

## Technical Stack

The application is built using the following technologies:

### Backend
- Java 17
- Spring Boot 3.5.4
- Spring Data JPA
- Spring WebSocket
- MySQL Database
- Lombok
- MapStruct for object mapping

### Frontend
- Thymeleaf templates
- HTML/CSS
- JavaScript
- WebSocket client

### Tools & Libraries
- Maven for dependency management
- SpringDoc OpenAPI for API documentation
- Spring Boot DevTools for development
- Spring Validation

## Project Structure

The project follows a standard Spring Boot application structure:

```
message-apps/
├── src/
│   ├── main/
│   │   ├── java/com/ma/message_apps/
│   │   │   ├── config/            # Configuration classes
│   │   │   ├── controller/        # MVC controllers for web pages
│   │   │   ├── dto/               # Data Transfer Objects
│   │   │   ├── entity/            # JPA entity classes
│   │   │   ├── enumDto/           # Enum definitions
│   │   │   ├── exception/         # Custom exception classes
│   │   │   ├── mapper/            # MapStruct mappers
│   │   │   ├── repository/        # JPA repositories
│   │   │   ├── restcontroller/    # REST API controllers
│   │   │   ├── service/           # Business logic services
│   │   │   └── util/              # Utility classes
│   │   │
│   │   └── resources/
│   │       ├── application.yml    # Application configuration
│   │       ├── static/            # Static resources (CSS, JS)
│   │       └── templates/         # Thymeleaf templates
│   │
│   └── test/                      # Test classes
├── pom.xml                        # Maven configuration
└── schema.sql                     # Database schema definition
```

## Setup and Installation

### Prerequisites
- JDK 17
- Maven
- MySQL 8.0+

### Steps to Run Locally

1. **Clone the repository**
   ```
   git clone <repository-url>
   cd message-apps
   ```

2. **Configure the Database**
   - Create a MySQL database named `mapp`
   - Ensure MySQL is running on port 3306
   - Default credentials in application.yml are:
     - Username: root
     - Password: admin

3. **Build the Application**
   ```
   mvn clean install
   ```

4. **Run the Application**
   ```
   mvn spring-boot:run
   ```

5. **Access the Application**
   - Web interface: http://localhost:8081
   - API Documentation: http://localhost:8081/swagger

## Database Configuration

The application connects to a MySQL database. The configuration is defined in `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mapp
    username: root
    password: admin
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

### Entity Model

The application has three main entities:

1. **User** - Stores user information
   - User ID, username, password hash, email, online status

2. **Message** - Stores messages between users
   - Message ID, sender, receiver, message text, read status, timestamp

3. **FriendRequests** - Manages friend relationships
   - Request ID, sender, receiver, status, timestamp

## Application Features

### User Management
- User registration with email and username
- Authentication with username and password
- User profile management
- Online status tracking

### Friend Management
- Send friend requests
- Accept/reject friend requests
- View friends list
- View pending friend requests

### Messaging
- Send messages to friends
- Real-time message delivery using WebSockets
- Message history with read receipts
- Conversation view

## API Documentation

The application exposes a REST API for integration. API documentation is available at:
```
http://localhost:8081/swagger
```

## Frontend Structure

The frontend is built with Thymeleaf templates and JavaScript:

### Pages
- **home.html** - Landing page
- **login.html** - User login page
- **register.html** - User registration page
- **dashboard.html** - Main application interface

### JavaScript Modules
- **api-client.js** - API communication layer
- **login.js** - Authentication logic
- **register.js** - Registration logic
- **dashboard.js** - Dashboard functionality and messaging

## Backend Architecture

### Service Layer
The service layer contains the business logic of the application:
- UserService - User management
- MessageService - Message handling
- FriendRequestService - Friend request processing
- WebSocketService - Real-time communication

### Controller Layer
- MVC Controllers - Handle web page requests
- REST Controllers - Handle API requests
- WebSocket Controllers - Handle WebSocket messages

### Data Access Layer
JPA repositories provide database access for each entity.

## WebSocket Configuration

WebSockets are used for real-time messaging. The configuration includes:
- WebSocket endpoint configuration
- Message handling
- Session management
- User presence detection

## Security

The application implements session-based authentication:
- Password hashing for secure storage
- Session-based authentication
- Authorization checks for protected resources

## Development Guidelines

### Code Formatting
- Use standard Java code style
- Follow the principles of clean code

### Git Workflow
- Feature branches for new features
- Pull requests for code review
- Keep commits small and focused

### Naming Conventions
- CamelCase for Java classes and methods
- snake_case for database columns
- Meaningful and descriptive names

## Testing

### Test Categories
- Unit tests for services and utilities
- Integration tests for repositories
- End-to-end tests for controllers

### Running Tests
```
mvn test
```

## Deployment

### Production Environment Setup
- Configure production database in application-prod.yml
- Set appropriate logging levels
- Configure HTTPS for security

### Deployment Options
- JAR deployment on application server
- Docker containerization
- Cloud deployment (AWS, Azure, etc.)

## Troubleshooting

### Common Issues

1. **Database Connection Issues**
   - Verify MySQL is running
   - Check database credentials in application.yml
   - Ensure firewall allows connection to MySQL port

2. **WebSocket Connection Issues**
   - Check browser console for errors
   - Verify network configuration allows WebSocket connections

3. **Application Startup Issues**
   - Verify Java version (requires JDK 17)
   - Check application logs for errors
   - Ensure required ports are available

### Logging

Application logs can be found in the console output and configured in application.yml:

```yaml
logging:
  level:
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql: TRACE
```

---

This guide serves as comprehensive documentation for the Message Apps project. For further information or assistance, please contact the development team.

Last updated: August 12, 2025
