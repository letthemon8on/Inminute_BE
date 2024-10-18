package org.example.inminute_demo.chat.handler;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.inminute_demo.chat.exception.WebSocketException;
import org.example.inminute_demo.domain.Member;
import org.example.inminute_demo.repository.MemberRepository;
import org.example.inminute_demo.repository.NoteJoinMemberRepository;
import org.example.inminute_demo.security.dto.CustomOAuth2User;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    public static final String DEFAULT_PATH = "/topic/public/";

    // private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final NoteJoinMemberRepository noteJoinMemberRepository;

    // WebSocket으로 들어온 메시지가 처리되기 전에 실행됨
    // STOMP 메시지의 종류에 따라 다른 작업을 수행하며, 메시지를 가로채 필요한 처리 가능
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message); // 메세지에서 헤더 정보를 쉽게 접근할 수 있도록 wrap
        StompCommand command = accessor.getCommand(); // STOMP 명령 가져옴

        if (StompCommand.CONNECT.equals(command)) { // 클라이언트가 WebSocket 연결 요청

            // 쿠키 기반 jwt 인증을 통해 생성된 세션에서 사용자 정보 가져옴
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth != null && auth.isAuthenticated()) {
                CustomOAuth2User customOAuth2User = (CustomOAuth2User) auth.getPrincipal();

                // 사용자 정보 가져오기
                String username = customOAuth2User.getUsername();
                String nickname = customOAuth2User.getNickname();

                // 인증 후 데이터를 헤더에 추가
                setValue(accessor, "username", username);
                setValue(accessor, "username", nickname);
            }

        } else if (StompCommand.SUBSCRIBE.equals(command)) { // 채팅룸 구독요청(진입) -> NoteJoinMember인지 검증

            String username = (String)getValue(accessor, "username");
            Long crewId = parseCrewIdFromPath(accessor);
            log.debug("userId : " + userId + "crewId : " + crewId);
            setValue(accessor, "crewId", crewId);
            validateUserInCrew(userId, crewId);

        } else if (StompCommand.DISCONNECT == command) { // Websocket 연결 종료
            Long userId = (Long)getValue(accessor, "userId");
            log.info("DISCONNECTED userId : {}", userId);
        }

        log.info("header : " + message.getHeaders());
        log.info("message:" + message);

        return message;
    }

    private Long parseCrewIdFromPath(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        return Long.parseLong(destination.substring(DEFAULT_PATH.length()));
    }

    private void validateUserInCrew(Long userId, Long crewId) {
        crewMemberRepository.findCrewMemberByCrewIdAndUserId(crewId, userId)
                .orElseThrow(() -> new WebSocketException(
                        String.format("crew Id : {} userId : {} 로 조회된 결과가 없습니다.", crewId, userId)));
    }

    private Object getValue(StompHeaderAccessor accessor, String key) {
        Map<String, Object> sessionAttributes = getSessionAttributes(accessor);
        Object value = sessionAttributes.get(key);

        if (Objects.isNull(value)) {
            throw new WebSocketException(key + " 에 해당하는 값이 없습니다.");
        }

        return value;
    }

    private void setValue(StompHeaderAccessor accessor, String key, Object value) {
        Map<String, Object> sessionAttributes = getSessionAttributes(accessor);
        sessionAttributes.put(key, value);
    }

    private Map<String, Object> getSessionAttributes(StompHeaderAccessor accessor) {
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();

        if (Objects.isNull(sessionAttributes)) {
            throw new WebSocketException("SessionAttributes가 null입니다.");
        }
        return sessionAttributes;
    }

}