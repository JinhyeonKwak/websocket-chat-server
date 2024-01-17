package com.websocket.chat.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    public enum MessageType {
        ENTER, EXIT, TALK
    }

    private MessageType type;
    private String roomId;
    private String sender;
    private String message;
    private long userCount;

}
