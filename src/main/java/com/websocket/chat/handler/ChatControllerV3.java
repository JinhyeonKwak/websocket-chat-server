package com.websocket.chat.handler;

import com.websocket.chat.dto.ChatMessage;
import com.websocket.chat.repository.ChatRoomRepositoryV2;
import com.websocket.chat.service.ChatServiceV2;
import com.websocket.chat.service.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatControllerV3 {

    private final JwtTokenProvider jwtTokenProvider;
    private final ChatRoomRepositoryV2 chatRoomRepository;
    private final ChatServiceV2 chatService;

    @MessageMapping("/chat/message")
    public void message(ChatMessage message, @Header("Authorization") String token) {
        String username = jwtTokenProvider.getUsername(token);
        message.setSender(username);
        message.setUserCount(chatRoomRepository.getUserCount(message.getRoomId()));
        chatService.sendChatMessage(message);
    }
}
