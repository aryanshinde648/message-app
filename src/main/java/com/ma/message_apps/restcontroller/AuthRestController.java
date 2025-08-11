package com.ma.message_apps.restcontroller;

import com.ma.message_apps.dto.UserDto;
import com.ma.message_apps.entity.User;
import com.ma.message_apps.enumDto.UserStatus;
import com.ma.message_apps.repository.UserRepository;
import com.ma.message_apps.service.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;


import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthRestController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody UserDto userDto) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = userRepository.findByUsername(userDto.getUsername());
            if (user == null) {
                response.put("error", "Invalid username or password");
                return response;
            }
            if (!passwordEncoder.matches(userDto.getPasswordHash(), user.getPasswordHash())) {
                response.put("error", "Invalid username or password");
                return response;
            }
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDto.getUsername(), userDto.getPasswordHash())
            );
            String token = jwtUtil.generateToken(userDto.getUsername());
            // Generate refresh token (UUID for simplicity)
            String refreshToken = UUID.randomUUID().toString();
            user.setRefreshToken(refreshToken);
            userRepository.save(user);
            response.put("token", token);
            response.put("refreshToken", refreshToken);
            log.info("User {} logged in successfully", userDto.getUsername());
            log.info("JWT Token: {}", token);
            response.put("message", "Login successful");
        } catch (AuthenticationException e) {
            response.put("error", "Invalid username or password");
        } catch (Exception e) {
            response.put("error", "An error occurred during login");
        }
        return response;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody UserDto userDto) {
        Map<String, Object> response = new HashMap<>();
        if (userRepository.findByUsername(userDto.getUsername()) != null) {
            response.put("error", "Username already exists");
            return response;
        }
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPasswordHash(passwordEncoder.encode(userDto.getPasswordHash()));
        user.setEmail(userDto.getEmail());
        user.setStatus(UserStatus.OFFLINE);
        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        userRepository.save(user);
        response.put("message", "Registration successful");
        return response;
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                String username = jwtUtil.extractUsername(token);
                if (username != null && jwtUtil.validateToken(token, username)) {
                    return ResponseEntity.ok().body("Token is valid");
                }
            } catch (Exception e) {
                return ResponseEntity.status(401).body("Invalid or expired token");
            }
        }
        return ResponseEntity.status(401).body("Invalid or expired token");
    }

    @GetMapping("/me")
    public UserDto getCurrentUser(HttpServletRequest request) {
        log.info("Received request to /api/auth/me");
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            log.info("Extracted token from Authorization header");
            try {
                String username = jwtUtil.extractUsername(token);
                log.info("Extracted username from token: {}", username);
                if (username != null && jwtUtil.validateToken(token, username)) {
                    log.info("Token is valid for user: {}", username);
                    User user = userRepository.findByUsername(username);
                    if (user == null) {
                        log.warn("User not found in database: {}", username);
                        return null;
                    }
                    log.info("Found user in database: {}", user.getUsername());
                    UserDto userDto = new UserDto();
                    userDto.setUserId(user.getUserId());
                    userDto.setUsername(user.getUsername());
                    userDto.setEmail(user.getEmail());
                    userDto.setStatus(user.getStatus());
                    userDto.setCreatedAt(user.getCreatedAt());
                    log.info("Returning user DTO for user: {}", userDto.getUsername());
                    return userDto;
                } else {
                    log.warn("Token validation failed for username: {}", username);
                }
            } catch (Exception e) {
                log.error("Error processing token: {}", e.getMessage());
                return null;
            }
        } else {
            log.warn("No Authorization header found or invalid format");
        }
        return null;
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> payload) {
        String refreshToken = payload.get("refreshToken");
        log.info("Refresh token request received with token: {}", refreshToken);

        if (refreshToken == null || refreshToken.isEmpty()) {
            log.warn("Refresh token missing in request");
            return ResponseEntity.status(401).body("Refresh token missing");
        }

        User user = userRepository.findByRefreshToken(refreshToken);
        if (user == null) {
            log.warn("Invalid refresh token: {}", refreshToken);
            return ResponseEntity.status(401).body("Invalid refresh token");
        }

        log.info("Valid refresh token found for user: {}", user.getUsername());

        // Generate a new refresh token for better security
        String newRefreshToken = java.util.UUID.randomUUID().toString();
        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);

        // Generate new access token
        String newAccessToken = jwtUtil.generateToken(user.getUsername());

        log.info("New tokens generated for user: {}", user.getUsername());

        Map<String, String> response = new HashMap<>();
        response.put("accessToken", newAccessToken);
        response.put("refreshToken", newRefreshToken);
        return ResponseEntity.ok(response);
    }
}
