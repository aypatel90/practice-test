package com.chatbot.repository;

import com.chatbot.entity.ChatMessage;
import com.chatbot.entity.Chatroom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

}
