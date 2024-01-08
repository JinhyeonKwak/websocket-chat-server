package com.websocket.chat.handler;

import com.websocket.chat.dto.ChatMessage;
import com.websocket.chat.repository.ChatRoomRepositoryV2;
import com.websocket.chat.service.RedisPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class ChatControllerV2 {

    private final RedisPublisher redisPublisher;
    private final ChatRoomRepositoryV2 chatRoomRepository;

    @MessageMapping("/chat/message")
    public void message(ChatMessage message) {
        if (ChatMessage.MessageType.ENTER.equals(message.getType())) {
            chatRoomRepository.enterChatRoom(message.getRoomId());
            message.setMessage(message.getSender() + " 님이 입장했습니다.");
        }
        redisPublisher.publish(chatRoomRepository.getTopic(message.getRoomId()), message);
    }
}
