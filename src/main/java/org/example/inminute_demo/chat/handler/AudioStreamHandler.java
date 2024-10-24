package org.example.inminute_demo.chat.handler;

import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import lombok.RequiredArgsConstructor;
import org.example.inminute_demo.chat.converter.ChatConverter;
import org.example.inminute_demo.chat.domain.Chat;
import org.example.inminute_demo.chat.domain.MessageType;
import org.example.inminute_demo.chat.dto.request.ChatRequest;
import org.example.inminute_demo.chat.exception.WebSocketException;
import org.example.inminute_demo.chat.repository.ChatRepository;
import org.example.inminute_demo.domain.Member;
import org.example.inminute_demo.repository.MemberRepository;
import org.example.inminute_demo.security.dto.CustomOAuth2User;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.io.ByteArrayOutputStream;
import java.net.URI;

@Component
@RequiredArgsConstructor
public class AudioStreamHandler extends BinaryWebSocketHandler {

    private final SimpMessageSendingOperations messagingTemplate; // WebSocket 메시지 전송
    private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    private final MemberRepository memberRepository;
    private final ChatRepository chatRepository;

    @Override
    public void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        try {
            // WebSocket을 통해 받은 Binary 데이터를 스트림에 저장
            byteArrayOutputStream.write(message.getPayload().array());

            if (byteArrayOutputStream.toString().isEmpty()) {
                throw new WebSocketException("오디오 스트림을 찾지 못했습니다.");
            }

            // Google STT로 변환
            String transcript = transcribeAudio(byteArrayOutputStream.toByteArray());

            // WebSocket 세션에서 사용자 인증정보 가져오기
            CustomOAuth2User customOAuth2User = (CustomOAuth2User) session.getPrincipal();
            Member member = memberRepository.findByUsername(customOAuth2User.getUsername())
                    .orElseThrow(() -> new WebSocketException("사용자 정보를 찾을 수 없습니다."));

            // 특정 채팅방 구독자들에게 변환된 텍스트를 메시지로 전송
            String uuid = parseUUIDFromPath(session);
            ChatRequest chatRequest = new ChatRequest(
                    MessageType.CHAT, member.getNickname(), transcript);
            messagingTemplate.convertAndSend("/topic/public/" + uuid, chatRequest);

            // 변환된 텍스트 Chat 엔티티로 저장
            saveTranscript(member.getUsername(), transcript, uuid);

            byteArrayOutputStream.reset(); // 스트림 초기화

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String parseUUIDFromPath(WebSocketSession session) {
        URI uri = session.getUri();
        // URI에서 UUID 부분 추출 (예: "/audio-stream/{uuid}")
        String path = uri.getPath();
        String[] segments = path.split("/");
        return segments[segments.length - 1]; // 경로의 마지막 부분이 UUID
    }

    @Transactional
    protected void saveTranscript(String username, String transcript, String uuid) {
        Chat chat = ChatConverter.toChatFromTranscript(username, transcript, uuid);
        chatRepository.save(chat);
    }

    // Google STT로 오디오 데이터를 변환하는 메소드
    private String transcribeAudio(byte[] audioData) throws Exception {
        // 클라이언트 인스턴스화
        try (SpeechClient speechClient = SpeechClient.create()) {

            ByteString audioBytes = ByteString.copyFrom(audioData);

            // 설정 객체 생성 -> 추후 데모 환경에 따라 변경 필요
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.WEBM_OPUS)
                    .setSampleRateHertz(48000)
                    .setLanguageCode("ko-KR")  // 한국어
                    .build();

            // 오디오 객체 생성
            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(audioBytes)
                    .build();

            // 오디오 - 텍스트 변환 수행
            RecognizeResponse response = speechClient.recognize(config, audio);
            StringBuilder transcript = new StringBuilder();
            for (SpeechRecognitionResult result : response.getResultsList()) {
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                transcript.append(alternative.getTranscript());
            }

            return transcript.toString();
        }
    }
}