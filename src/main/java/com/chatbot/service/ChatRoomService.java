package com.chatbot.service;

//import com.chatbot.domain.ChatRoom;
import com.chatbot.domain.ChatRoomMessage;
import com.chatbot.entity.Chatroom;
import com.chatbot.entity.ChatroomUsers;
import com.chatbot.entity.User;
import com.chatbot.entity.dto.*;
import com.chatbot.repository.ChatroomRepository;
import com.chatbot.repository.ChatroomUsersRepository;
import com.chatbot.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final UserRepository userRepository;
    private final ConcurrentHashMap<String, Chatroom> availableChatRoomMap = new ConcurrentHashMap<>();

    private final ChatroomRepository chatroomRepository;

    private final ChatroomUsersRepository chatroomUsersRepository;
    private final ConcurrentHashMap<Long, Set<ChatRoomMessage>> userWiseChatMessageMap = new ConcurrentHashMap<>();

    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("Entity Not Found !"));
    }

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

        ChatroomUsers chatroomUser = ChatroomUsers.builder()
                //.chatroom(persistedChatroomEntity)
                .chatroomUser(user)
                .createdBy(user)
                .isActive(true)
                .build();

        Set<ChatroomUsers> chatroomUsersSet = new HashSet<>();
        chatroomUsersSet.add(chatroomUser);

        Chatroom chatroom = Chatroom.builder()
                .chatroomName(modifiedChatRoomName)
                .chatroomDescription(request.getChatRoomDescription())
                .createdBy(user)
                .chatroomUsersSet(chatroomUsersSet)
                .isActive(true)
                .build();


        Chatroom persistedChatroomEntity = chatroomRepository.save(chatroom);
        chatroomUser.setChatroom(persistedChatroomEntity);
        chatroomUsersRepository.save(chatroomUser);

        //chatroomUsersRepository.save(chatroomUser);

        // update the concurrentMap
        //availableChatRoomMap.put(modifiedChatRoomName, chatRoom);

        return AppResponse.builder()
                .responseCode("200")
                .responseMessage("Chatroom created successfully. You can add other users now.")
                .name(modifiedChatRoomName)
                .build();
    }

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
        //availableChatRoomMap.get(modifiedChatRoomName).getParticipantList().add(user);

        return AppResponse.builder()
                .responseCode("200")
                .responseMessage("User added to chatroom successfully.")
                .build();
    }

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
           // availableChatRoomMap.get(modifiedChatRoomName).getChatRoomMessages().add(message);
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
                .name(request.getChatRoomName())
                .build();
    }

    private UserResponse maptoUserResponse(User user) {
        return UserResponse.builder()
                .userId(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .isEnabled(user.isEnabled())
                .build();
    }
    private ChatRoomMessage createChatRoomMessageDomainObject(String modifiedChatRoomName,
                                                              PostChatMessageRequest request, User user,
                                                              User toUser) {
        return ChatRoomMessage.builder()
                .chatRoomName(modifiedChatRoomName)
                .message(request.getMessage())
                .createdBy(maptoUserResponse(user))
                .toUser(toUser == null ? null : maptoUserResponse(toUser))
                .createdDate(new Date(System.currentTimeMillis()))
                .isActive(true)
                .build();
    }

    public GetAllChatMessageResponse getAllMessagesFromChatRoom(String chatRoomName, Long userId) {

        Set<ChatRoomMessage> messageSet = null;

        if(chatRoomName != null) {
            // prepare unique name for chat room
            String modifiedChatRoomName = chatRoomName.trim()
                    .replaceAll(" ", "_").toUpperCase(Locale.ROOT);

            // check for unique name
            if (!availableChatRoomMap.containsKey(modifiedChatRoomName)) {
                messageSet = null;
            } else {
                //messageSet = availableChatRoomMap.get(modifiedChatRoomName).getChatRoomMessages();
            }
        } else {
            messageSet = userWiseChatMessageMap.get(userId);
        }

        return GetAllChatMessageResponse.builder()
                .chatMessageSet(messageSet)
                .chatRoomName(chatRoomName)
                .build();
    }

    public GetAllUsersInChatRoomResponse getAllParticipantsFromChatRoom(String chatRoomName) {

        List<UserResponse> userList = null;

        if(chatRoomName != null && availableChatRoomMap.containsKey(chatRoomName)) {
            //userList = availableChatRoomMap.get(chatRoomName).getParticipantList().stream()
              //      .map(this::maptoUserResponse).collect(Collectors.toList());
        }

        return GetAllUsersInChatRoomResponse.builder()
                .userResponseList(userList)
                .chatRoomName(chatRoomName)
                .build();
    }

    public Integer countAllEnabledUsers() {
        return userRepository.countAllEnabledUser();
    }

}
