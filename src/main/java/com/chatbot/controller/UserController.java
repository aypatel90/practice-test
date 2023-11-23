package com.chatbot.controller;

import com.chatbot.entity.User;
import com.chatbot.entity.dto.AppResponse;
import com.chatbot.entity.dto.ChangePasswordRequest;
import com.chatbot.entity.dto.RegisterRequest;
import com.chatbot.entity.dto.UserResponse;
import com.chatbot.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/")
    public ResponseEntity<AppResponse> updateUser(@Valid @RequestBody RegisterRequest request,
                                                  @AuthenticationPrincipal User authenticatedUser) {
        return ResponseEntity.ok(userService.updateUser(request, authenticatedUser.getId()));
    }

    @GetMapping("/quantity")
    public ResponseEntity<Integer> countAllEnabledUsers() {
        return ResponseEntity.ok(userService.countAllEnabledUsers());
    }

    @GetMapping("/get")
    public ResponseEntity<UserResponse> getUserById(@AuthenticationPrincipal User authenticateUser) {
        User user = userService.getUserById(authenticateUser.getId());
        return ResponseEntity.ok(userService.maptoUserResponse(user));
    }

    @PutMapping ("/changePassword")
    public ResponseEntity<AppResponse> changePassword(@RequestBody ChangePasswordRequest request,
                                                      Principal authenticatedUser) {
        return ResponseEntity.ok(userService.changePassword(request, authenticatedUser));
    }
}
