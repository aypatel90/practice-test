package com.chatbot.repository;

import com.chatbot.entity.Chatroom;
import com.chatbot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {

}
