package com.chatbot.repository;

import com.chatbot.entity.Chatroom;
import com.chatbot.entity.ChatroomUsers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatroomUsersRepository extends JpaRepository<ChatroomUsers, Long> {

}
