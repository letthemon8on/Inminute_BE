package org.example.inminute_demo.chat.service;

import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import jakarta.xml.bind.DatatypeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import static jakarta.xml.bind.DatatypeConverter.parseBase64Binary;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableAsync
public class TranscribeService {

    // Google STT로 오디오 데이터를 변환하는 메소드
    @Async
    public CompletableFuture<String> transcribeAudio(String audioCode) {

        log.debug("audioCode: " + audioCode);

        // byte code로 수신한 오디오 데이터 디코딩
        byte[] byteAudio = parseBase64Binary(audioCode);

        // 클라이언트 인스턴스화
        try (SpeechClient speechClient = SpeechClient.create()) {

            ByteString audioBytes = ByteString.copyFrom(byteAudio);

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

            return CompletableFuture.completedFuture(transcript.toString());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
