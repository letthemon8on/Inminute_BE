package org.example.inminute_demo.chat.config;

import lombok.RequiredArgsConstructor;
import org.example.inminute_demo.chat.handler.StompHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@EnableWebSocketMessageBroker
@Configuration
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompHandler stompHandler; // STOMP 메세지 입출력 담당

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) { // Web Socket 시작 endpoint 등록
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:3000", "http://inminute.kr", "http://api.inminute.kr",
                        "https://inminute.kr", "https://api.inminute.kr")
                .withSockJS(); // Web Socket 지원하지 않는 브라우저에서도 사용 가능하도록
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) { // 메세지 브로커 설정
        registry.setApplicationDestinationPrefixes("/app"); // 클라이언트가 서버로 전송하는 메세지의 경로 앞에 /app 붙임
        registry.enableSimpleBroker("/topic"); // 메세지 브로커가 /topic 경로로 시작하는 메세지를 구독한 클라이언트들에게 보냄
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) { // 클라이언트로부터 들어오는 Web Socket 메세지 인터셉트
        registration.interceptors(stompHandler);
    }

}