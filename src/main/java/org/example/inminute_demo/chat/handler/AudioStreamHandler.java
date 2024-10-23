package org.example.inminute_demo.chat.handler;

import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.io.ByteArrayOutputStream;

@Component
@RequiredArgsConstructor
public class AudioStreamHandler extends BinaryWebSocketHandler {

    private final SimpMessageSendingOperations messagingTemplate; // WebSocket 메시지 전송
    private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    @Override
    public void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        try {
            // WebSocket을 통해 받은 Binary 데이터를 스트림에 저장
            byteArrayOutputStream.write(message.getPayload().array());

            // 10MB 이상의 데이터가 수신되면 처리 (필요에 따라 조정)
            if (byteArrayOutputStream.size() > 10485760) { // 10MB
                // Google STT로 변환
                String transcript = transcribeAudio(byteArrayOutputStream.toByteArray());

                // 특정 채팅방 구독자들에게 변환된 텍스트를 메시지로 전송
                String uuid = (String) session.getAttributes().get("uuid");
                messagingTemplate.convertAndSend("/topic/public/" + uuid, transcript);

                byteArrayOutputStream.reset(); // 스트림 초기화
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Google STT로 오디오 데이터를 변환하는 메소드
    private String transcribeAudio(byte[] audioData) throws Exception {
        try (SpeechClient speechClient = SpeechClient.create()) {
            ByteString audioBytes = ByteString.copyFrom(audioData);

            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setSampleRateHertz(16000)
                    .setLanguageCode("ko-KR")  // 한국어
                    .build();

            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(audioBytes)
                    .build();

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