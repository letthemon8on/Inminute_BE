package org.example.inminute_demo.chat.handler;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.inminute_demo.domain.Member;
import org.example.inminute_demo.repository.MemberRepository;
import org.example.inminute_demo.repository.NoteJoinMemberRepository;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
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

            // JWT 인증
            Member member = getUserByAuthorizationHeader(
                    accessor.getFirstNativeHeader("Authorization"));
            // 인증 후 데이터를 헤더에 추가
            setValue(accessor, "userId", user.getId());
            setValue(accessor, "username", user.getNickname());
            setValue(accessor, "profileImgUrl", user.getProfileImgUrl());

        } else if (StompCommand.SUBSCRIBE.equals(command)) { // 채팅룸 구독요청(진입) -> CrewMember인지 검증

            Long userId = (Long)getValue(accessor, "userId");
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

    private Member getUserByAuthorizationHeader(String authHeaderValue) {

        String accessToken = getTokenByAuthorizationHeader(authHeaderValue);

        Claims claims = jwtTokenProvider.getClaims(accessToken);
        Long userId = claims.get("userId", Long.class);

        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private String getTokenByAuthorizationHeader(String authHeaderValue) {

        if (Objects.isNull(authHeaderValue) || authHeaderValue.isBlank()) {
            throw new WebSocketException("authHeaderValue: " + authHeaderValue);
        }

        String accessToken = ExtractUtil.extractToken(authHeaderValue);
        jwtTokenProvider.validateToken(accessToken); // 예외 발생할 수 있음

        return accessToken;
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