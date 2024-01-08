package com.websocket.chat.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import redis.embedded.RedisServer;
import redis.embedded.RedisServerBuilder;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.util.Objects;

@Slf4j
@Profile("local")
@Configuration
public class EmbeddedRedisConfig {

    @Value("${spring.redis.port}")
    private int redisPort;
    @Value("${spring.redis.max-memory}")
    private String redisMaxMemory;

    private RedisServer redisServer;

    @PostConstruct
    public void redisServer() {

        if (isArmMac()) {
            redisServer = new RedisServer(getRedisFileForArcMac(), redisPort);
        } else {
            RedisServerBuilder builder = new RedisServerBuilder();
            redisServer = builder.port(redisPort)
                    .setting("maxmemory " + redisMaxMemory)
                    .build();
        }
        redisServer.start();
    }

    @PreDestroy
    public void stopRedis() {
        if (redisServer != null) {
            redisServer.stop();
        }
    }

    /**
     * 현재 시스템이 ARM 아키텍처를 사용하는 MAC인지 확인
     * System.getProperty("os.arch") : JVM이 실행되는 시스템 아키텍처 반환
     * System.getProperty("os.name") : 시스템 이름 반환
     */
    private boolean isArmMac() {
        return Objects.equals(System.getProperty("os.arch"), "aarch64")
                && Objects.equals(System.getProperty("os.name"), "Mac OS X");
    }

    /**
     * ARM 아키텍처를 사용하는 Mac에서 실행할 수 있는 Redis 바이너리 파일을 반환
     */
    private File getRedisFileForArcMac() {
        try {
            return new ClassPathResource("binary/redis/redis-server-mac-arm64").getFile();
        } catch (Exception e) {
            log.error("Redis file load error: ", e);
        }
        return null;
    }
}
