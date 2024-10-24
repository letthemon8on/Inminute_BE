package org.example.inminute_demo.chat.config;

import lombok.RequiredArgsConstructor;
import org.example.inminute_demo.chat.handler.AudioStreamHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final AudioStreamHandler audioStreamHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(audioStreamHandler, "/audio-stream/{uuid}")
                .setAllowedOrigins("http://localhost:3000", "http://inminute.kr", "http://api.inminute.kr",
                        "https://inminute.kr", "https://api.inminute.kr"); // 필요한 도메인 설정
    }
}