package com.websocket.chat.handler;

import com.websocket.chat.dto.ChatMessage;
import com.websocket.chat.repository.ChatRoomRepositoryV2;
import com.websocket.chat.service.ChatService;
import com.websocket.chat.service.ChatServiceV2;
import com.websocket.chat.service.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Optional;

import static org.springframework.messaging.simp.stomp.StompCommand.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final ChatRoomRepositoryV2 chatRoomRepository;
    private final ChatServiceV2 chatService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (accessor.getCommand() == CONNECT) { // websocket 연결 요청
            jwtTokenProvider.validateToken(accessor.getFirstNativeHeader("Authorization"));
        } else if (accessor.getCommand() == SUBSCRIBE) { // 채팅 룸 구독 요청
            String roomId = chatService.getRoomId(Optional.ofNullable((String) message.getHeaders()
                    .get("simpDestination"))
                    .orElse("InvalidRoomId"));
            String sessionId = (String) message.getHeaders().get("simpSessionId");
            chatRoomRepository.setUserEnterInfo(sessionId, roomId);
            chatRoomRepository.incrementUserCount(roomId);
            String username = Optional.ofNullable((Principal) message.getHeaders().get("simpUser"))
                    .map(Principal::getName).orElse("UnknownUser");
            chatService.sendChatMessage(
                    ChatMessage.builder()
                            .type(ChatMessage.MessageType.ENTER)
                            .roomId(roomId)
                            .sender(username)
                            .build()
            );
            log.info("SUBSCRIBED {}, {}", username, roomId);
        } else if (accessor.getCommand() == DISCONNECT) { // websocket 연결 종료
            String sessionId = (String) message.getHeaders().get("simpSessionId");
            String roomId = chatRoomRepository.getUserEnterRoomId(sessionId);
            chatRoomRepository.decrementUserCount(roomId);
            String username = Optional.ofNullable((Principal) message.getHeaders().get("simpUser"))
                    .map(Principal::getName)
                    .orElse("UnknownUser");
            chatService.sendChatMessage(
                    ChatMessage.builder()
                            .type(ChatMessage.MessageType.EXIT)
                            .roomId(roomId)
                            .sender(username)
                            .build()
            );
            chatRoomRepository.removeUserEnterInfo(sessionId);
            log.info("DISCONNECTED {}, {}", sessionId, roomId);
        }
        return message;
    }
}
