package com.ma.message_apps.controller;

import com.ma.message_apps.dto.ApiResponse;
import com.ma.message_apps.dto.UserDto;
import com.ma.message_apps.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@Slf4j
@RequiredArgsConstructor
public class HomePageController {

    private final UserService userService;

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    /**
     * Handle regular form submission for registration (non-AJAX)
     */
    @PostMapping(value = "/register", consumes = "application/x-www-form-urlencoded")
    public String registerUserForm(@ModelAttribute UserDto userDto) {
        log.info("User registration form submission processed for username: {}", userDto.getUsername());

        try {
            userService.registerUser(userDto);
            return "redirect:/login?registered=true";
        } catch (Exception e) {
            log.error("Error during form registration", e);
            return "redirect:/register?error=" + e.getMessage();
        }
    }

    /**
     * Handle AJAX registration requests
     */
    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<ApiResponse<UserDto>> registerUserAjax(@RequestBody UserDto userDto) {
        log.info("Processing AJAX registration request for username: {}", userDto.getUsername());

        // Validate username uniqueness
        if (userService.isUsernameExists(userDto.getUsername())) {
            log.warn("Registration failed: Username already exists: {}", userDto.getUsername());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Username already exists"));
        }

        // Validate email uniqueness
        if (userService.isEmailExists(userDto.getEmail())) {
            log.warn("Registration failed: Email already exists: {}", userDto.getEmail());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Email already exists"));
        }

        try {
            // Process registration
            UserDto registeredUser = userService.registerUser(userDto);

            // Remove password from response for security
            registeredUser.setPasswordHash(null);

            log.info("User registered successfully with ID: {}", registeredUser.getUserId());
            return ResponseEntity.ok(ApiResponse.success("Registration successful", registeredUser));
        } catch (Exception e) {
            log.error("Error during user registration", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Registration failed: " + e.getMessage()));
        }
    }
}
