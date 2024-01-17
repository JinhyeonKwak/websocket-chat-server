package com.websocket.chat.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginInfo {
    private final String username;
    private final String token;

    @Builder
    public LoginInfo(String username, String token) {
        this.username = username;
        this.token = token;
    }
}
