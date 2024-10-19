package org.example.inminute_demo.chat.handler;

import lombok.RequiredArgsConstructor;
import org.example.inminute_demo.chat.domain.MessageType;
import org.example.inminute_demo.chat.dto.request.ChatRequest;
import org.example.inminute_demo.chat.exception.WebSocketException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    private final SimpMessageSendingOperations messagingTemplate;

    // 연결 요청
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("Received a new web socket connection");
    }

    // 구독 요청(입장)
    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
        logger.info("Received a new web socket subscribe");
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String username = (String)getValue(accessor, "username");
        String nickname = (String)getValue(accessor, "nickname");
        String uuid = (String)getValue(accessor, "uuid");

        logger.info("User: {} {} Subscribe Note : {}", username, nickname, uuid);
        ChatRequest chatRequest = new ChatRequest(MessageType.JOIN, nickname,
                nickname + " 님이 입장했습니다.");
        messagingTemplate.convertAndSend("/topic/public/" + uuid, chatRequest);

    }

    // 연결 해제
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String username = (String)getValue(accessor, "username");
        String nickname = (String)getValue(accessor, "nickname");
        String uuid = (String)getValue(accessor, "uuid");

        logger.info("User: {} {} Disconnected Note : {}", username, nickname, uuid);

        ChatRequest chatRequest = new ChatRequest(
                MessageType.LEAVE, nickname, nickname + " 님이 떠났습니다.");

        messagingTemplate.convertAndSend("/topic/public/" + uuid, chatRequest);
    }

    private Object getValue(StompHeaderAccessor accessor, String key) {
        Map<String, Object> sessionAttributes = getSessionAttributes(accessor);
        Object value = sessionAttributes.get(key);

        if (Objects.isNull(value)) {
            throw new WebSocketException(key + " 에 해당하는 값이 없습니다.");
        }

        return value;
    }

    private Map<String, Object> getSessionAttributes(StompHeaderAccessor accessor) {
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();

        if (Objects.isNull(sessionAttributes)) {
            throw new WebSocketException("SessionAttributes가 null입니다.");
        }
        return sessionAttributes;
    }
}