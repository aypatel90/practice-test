package com.chatbot.controller;

import com.chatbot.entity.User;
import com.chatbot.entity.dto.*;
import com.chatbot.service.ChatRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatRoomService chatRoomService;


    @PostMapping("/chatroom/create")
    public ResponseEntity<AppResponse> createChatRoom(@Valid @RequestBody ChatRoomRequest request,
                                                @AuthenticationPrincipal User authenticatedUser) {
        return ResponseEntity.ok(chatRoomService.createChatRoom(request, authenticatedUser.getId()));
    }

    @PostMapping("/chatroom/user")
    public ResponseEntity<AppResponse> addUserInChatroom(@Valid @RequestBody AddChatRoomUserRequest request) {
        return ResponseEntity.ok(chatRoomService.addUserInChatRoom(request));
    }

    @PostMapping("/chatroom/post-message")
    public ResponseEntity<AppResponse> postMessageInChatRoom(@Valid @RequestBody PostChatMessageRequest request) {
        return ResponseEntity.ok(chatRoomService.postMessageInChatRoom(request));
    }

    @GetMapping("/chatroom/all-message")
    public ResponseEntity<GetAllChatMessageResponse> getAllMessagesFromChatRoom(@RequestParam("chatroom-name")
                                                                  String chatRoomName, @AuthenticationPrincipal User authenticatedUser ) {
        return ResponseEntity.ok(chatRoomService.getAllMessagesFromChatRoom(chatRoomName, authenticatedUser.getId()));
    }

    @GetMapping("/chatroom/user")
    public ResponseEntity<GetAllUsersInChatRoomResponse> getAllParticipantsFromChatRoom(@RequestParam("chatroom-name") String chatRoomName) {
        return ResponseEntity.ok(chatRoomService.getAllParticipantsFromChatRoom(chatRoomName));
    }
}
