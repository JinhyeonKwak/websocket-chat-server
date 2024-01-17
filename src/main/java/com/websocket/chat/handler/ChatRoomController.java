package com.websocket.chat.handler;

import com.websocket.chat.dto.ChatRoomV2;
import com.websocket.chat.dto.LoginInfo;
import com.websocket.chat.repository.ChatRoomRepository;
import com.websocket.chat.service.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomRepository chatRoomRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/room")
    public String rooms(Model model) {
        return "room-view";
    }

    @GetMapping("/rooms")
    @ResponseBody
    public List<ChatRoomV2> room() {
        return chatRoomRepository.findAllRoom();
    }

    @PostMapping("/room")
    @ResponseBody
    public ChatRoomV2 createRoom(@RequestParam String name) {
        return chatRoomRepository.createChatRoom(name);
    }

    @GetMapping("/room/enter/{roomId}")
    public String roomDetail(Model model, @PathVariable String roomId) {
        model.addAttribute("roomId", roomId);
        return "room-detail-view";
    }

    @GetMapping("/room/{roomId}")
    @ResponseBody
    public ChatRoomV2 roomInfo(@PathVariable String roomId) {
        return chatRoomRepository.findRoomById(roomId);
    }

    @GetMapping("/user")
    @ResponseBody
    public LoginInfo getUserInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return LoginInfo.builder()
                .username(username)
                .token(jwtTokenProvider.generateAccessToken(username))
                .build();
    }
}
