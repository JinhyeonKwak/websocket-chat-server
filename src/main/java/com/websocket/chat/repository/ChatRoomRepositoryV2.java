package com.websocket.chat.repository;

import com.websocket.chat.dto.ChatRoomV2;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChatRoomRepositoryV2 {
    private static final String CHAT_ROOMS = "CHAT_ROOM";
    private static final String USER_COUNT = "USER_COUNT";
    private static final String ENTER_INFO = "ENTER_INFO";
//    private final RedisMessageListenerContainer redisMessageListener;
//    private final RedisSubscriber redisSubscriber;
//    private final RedisTemplate<String, Object> redisTemplate;
//    private Map<String, ChannelTopic> topics;
    @Resource(name = "redisTemplate")
    private HashOperations<String, String, ChatRoomV2> hashOpsChatRoom;
    @Resource(name = "redisTemplate")
    private HashOperations<String, String, String> hashOpsEnterInfo;
    @Resource(name = "redisTemplate")
    private ValueOperations<String, String> valueOps;

//    @PostConstruct
//    private void init() {
//        hashOpsChatRoom = redisTemplate.opsForHash();
////        topics = new HashMap<>();
//    }

    public List<ChatRoomV2> findAllRoom() {
        return hashOpsChatRoom.values(CHAT_ROOMS);
    }

    public ChatRoomV2 findRoomById(String id) {
        return hashOpsChatRoom.get(CHAT_ROOMS, id);
    }

    public ChatRoomV2 createChatRoom(String name) {
        ChatRoomV2 chatRoom = ChatRoomV2.create(name);
        hashOpsChatRoom.put(CHAT_ROOMS, chatRoom.getRoomId(), chatRoom);
        return chatRoom;
    }

    public void setUserEnterInfo(String sessionId, String roomId) {
        hashOpsEnterInfo.put(ENTER_INFO, sessionId, roomId);
    }

    public String getUserEnterRoomId(String sessionId) {
        return hashOpsEnterInfo.get(ENTER_INFO, sessionId);
    }

    public void removeUserEnterInfo(String sessionId) {
        hashOpsEnterInfo.delete(ENTER_INFO, sessionId);
    }

    public long getUserCount(String roomId) {
        return Long.parseLong(Optional.ofNullable(valueOps.get(USER_COUNT + "_" + roomId)).orElse("0"));
    }

    public long incrementUserCount(String roomId) {
        return Optional.ofNullable(valueOps.increment(USER_COUNT + "_" + roomId)).orElse(0L);
    }

    public long decrementUserCount(String roomId) {
        return Optional.ofNullable(valueOps.decrement(USER_COUNT + "_" + roomId))
                .filter(count -> count > 0).orElse(0L);
    }

//    public void enterChatRoom(String roomId) {
//        ChannelTopic topic = topics.get(roomId);
//        if (topic == null) {
//            topic = new ChannelTopic(roomId);
//            redisMessageListener.addMessageListener(redisSubscriber, topic);
//            topics.put(roomId, topic);
//        }
//    }

//    public ChannelTopic getTopic(String roomId) {
//        return topics.get(roomId);
//    }
}
