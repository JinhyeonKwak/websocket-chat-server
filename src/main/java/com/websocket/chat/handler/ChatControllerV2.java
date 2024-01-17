package com.websocket.chat.handler;

import com.websocket.chat.dto.ChatMessage;
import com.websocket.chat.repository.ChatRoomRepositoryV2;
import com.websocket.chat.service.JwtTokenProvider;
import com.websocket.chat.service.RedisPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

//@Controller
@RequiredArgsConstructor
public class ChatControllerV2 {

//    private final RedisPublisher redisPublisher;
    private final RedisTemplate redisTemplate;
    private final ChannelTopic channelTopic;
    private final ChatRoomRepositoryV2 chatRoomRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @MessageMapping("/chat/message")
    public void message(ChatMessage message, @Header("Authorization") String token) {
        String username = jwtTokenProvider.getUsername(token);
        message.setSender(username);
        if (ChatMessage.MessageType.ENTER.equals(message.getType())) {
//            chatRoomRepository.enterChatRoom(message.getRoomId());
            message.setSender("[알림]");
            message.setMessage(username + " 님이 입장했습니다.");
        }
//        redisPublisher.publish(chatRoomRepository.getTopic(message.getRoomId()), message);
        redisTemplate.convertAndSend(channelTopic.getTopic(), message);
    }
}
