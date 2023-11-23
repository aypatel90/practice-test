package com.chatbot.service;

import com.chatbot.domain.ChatRoom;
import com.chatbot.domain.ChatRoomMessage;
import com.chatbot.entity.User;
import com.chatbot.entity.dto.*;
import com.chatbot.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final UserRepository userRepository;
    private final ConcurrentHashMap<String, ChatRoom> availableChatRoomMap = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Long, Set<ChatRoomMessage>> userWiseChatMessageMap = new ConcurrentHashMap<>();

    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("Entity Not Found !"));
    }

    @Transactional
    public AppResponse createChatRoom(ChatRoomRequest request, Long userId) {

        // prepare unique name for chat room
        String modifiedChatRoomName = request.getChatRoomName().trim()
                .replaceAll(" ", "_").toUpperCase(Locale.ROOT);

        // check for unique name
        if (availableChatRoomMap.containsKey(modifiedChatRoomName)) {
            return AppResponse.builder()
                    .responseCode("400")
                    .responseMessage("Chat room name is not unique. Please change the name and retry.")
                    .build();
        }

        User user = getUserById(userId);

        // Create chatroom domain object
        ChatRoom chatRoom = ChatRoom.builder()
                .chatRoomName(modifiedChatRoomName)
                .chatRoomDescription(request.getChatRoomDescription())
                .createdBy(user)
                .createdDate(new Date(System.currentTimeMillis()))
                .isActive(true)
                .participantList(new ArrayList<>(Arrays.asList(user)))
                .build();

        // update the concurrentMap
        availableChatRoomMap.put(modifiedChatRoomName, chatRoom);

        return AppResponse.builder()
                .responseCode("200")
                .responseMessage("Chatroom created successfully. You can add other users now.")
                .build();
    }

    @Transactional
    public AppResponse addUserInChatRoom(AddChatRoomUserRequest request) {

        // prepare unique name for chat room
        String modifiedChatRoomName = request.getChatRoomName().trim()
                .replaceAll(" ", "_").toUpperCase(Locale.ROOT);

        // check for unique name
        if (!availableChatRoomMap.containsKey(modifiedChatRoomName)) {
            return AppResponse.builder()
                    .responseCode("400")
                    .responseMessage("Chat room is not available. Please provide correct chatroom name and retry.")
                    .build();
        }

        User user = getUserById(request.getUserId());

        // update the concurrentMap
        availableChatRoomMap.get(modifiedChatRoomName).getParticipantList().add(user);

        return AppResponse.builder()
                .responseCode("200")
                .responseMessage("User added to chatroom successfully.")
                .build();
    }

    @Transactional
    public AppResponse postMessageInChatRoom(PostChatMessageRequest request) {

        ChatRoomMessage message = null;

        User user = getUserById(request.getUserId());

        if(request.getChatRoomName() != null) {

            // prepare unique name for chat room
            String modifiedChatRoomName = request.getChatRoomName().trim()
                    .replaceAll(" ", "_").toUpperCase(Locale.ROOT);

            // check for unique name
            if (!availableChatRoomMap.containsKey(modifiedChatRoomName)) {
                return AppResponse.builder()
                        .responseCode("400")
                        .responseMessage("Chat room is not available. Please provide correct chatroom name and retry.")
                        .build();
            }

            message = createChatRoomMessageDomainObject(modifiedChatRoomName,request,user,null);

            // update the concurrentMap
            availableChatRoomMap.get(modifiedChatRoomName).getChatRoomMessages().add(message);
        } else {
            // one to one chat
            message = createChatRoomMessageDomainObject(null,request,user,getUserById(request.getToUserId()));
        }

        if (userWiseChatMessageMap.containsKey(request.getUserId())) {
            userWiseChatMessageMap.get(request.getUserId()).add(message);
        } else {
            Set<ChatRoomMessage> chatRoomMessageHashSet = new HashSet<>();
            userWiseChatMessageMap.put(request.getUserId(), chatRoomMessageHashSet);
        }

        return AppResponse.builder()
                .responseCode("200")
                .responseMessage("Message posted successfully.")
                .build();
    }

    private ChatRoomMessage createChatRoomMessageDomainObject(String modifiedChatRoomName,
                                                              PostChatMessageRequest request, User user,
                                                              User toUser) {
        return ChatRoomMessage.builder()
                .chatRoomName(modifiedChatRoomName)
                .message(request.getMessage())
                .createdBy(user)
                .toUser(toUser)
                .createdDate(new Date(System.currentTimeMillis()))
                .isActive(true)
                .build();
    }

    @Transactional
    public Set<ChatRoomMessage> getAllMessagesFromChatRoom(String chatRoomName) {
        // prepare unique name for chat room
        String modifiedChatRoomName = chatRoomName.trim()
                .replaceAll(" ", "_").toUpperCase(Locale.ROOT);

        // check for unique name
        if (!availableChatRoomMap.containsKey(modifiedChatRoomName)) {
            return null;
        } else {
            return availableChatRoomMap.get(modifiedChatRoomName).getChatRoomMessages();
        }
    }

    public Integer countAllEnabledUsers() {
        return userRepository.countAllEnabledUser();
    }

}
