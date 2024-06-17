package org.example.inminute_demo.zoom.service;

import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.speech.v1.*;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.protobuf.ByteString;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class SpeechToTextService {
    private final Logger logger = LoggerFactory.getLogger(SpeechToTextService.class);
    private final String bucketName = "stt-demo-1125"; // 여기에 Google Cloud Storage 버킷 이름을 넣으세요

    public String transcribe(MultipartFile audioFile) throws IOException {
        if (audioFile.isEmpty()) {
            throw new IOException("Required part 'audioFile' is not present.");
        }

        // 오디오 파일을 임시 파일로 저장
        Path tempFile = Files.createTempFile("audio", ".flac");
        Files.write(tempFile, audioFile.getBytes());

        // Google Cloud Storage에 업로드
        String gcsUri = uploadFileToGCS(tempFile);

        // 클라이언트 인스턴스화
        try (SpeechClient speechClient = SpeechClient.create()) {
            // 설정 객체 생성
            RecognitionConfig recognitionConfig =
                    RecognitionConfig.newBuilder()
                            .setEncoding(RecognitionConfig.AudioEncoding.FLAC)
                            .setSampleRateHertz(32000)
                            .setLanguageCode("ko-KR")
                            .build();

            // Google Cloud Storage URI를 사용하여 오디오 객체 생성
            RecognitionAudio recognitionAudio = RecognitionAudio.newBuilder()
                    .setUri(gcsUri)
                    .build();

            // LongRunningRecognize 요청 생성
            LongRunningRecognizeRequest request = LongRunningRecognizeRequest.newBuilder()
                    .setConfig(recognitionConfig)
                    .setAudio(recognitionAudio)
                    .build();

            // LongRunningRecognize 수행
            OperationFuture<LongRunningRecognizeResponse, LongRunningRecognizeMetadata> future =
                    speechClient.longRunningRecognizeAsync(request);

            // 결과 대기
            LongRunningRecognizeResponse response = future.get();

            List<SpeechRecognitionResult> results = response.getResultsList();

            if (!results.isEmpty()) {
                // 주어진 말 뭉치에 대해 여러 가능한 스크립트를 제공. 0번(가장 가능성 있는)을 사용한다.

                for (SpeechRecognitionResult result : results) {
                    SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                    System.out.printf("Transcription: %s\n", alternative.getTranscript());
                }

                SpeechRecognitionResult result = results.get(0);
                return result.getAlternatives(0).getTranscript();
            } else {
                logger.error("No transcription result found");
                return "";
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            // 임시 파일 삭제
            Files.deleteIfExists(tempFile);
        }
    }

    private String uploadFileToGCS(Path filePath) {
        Storage storage = StorageOptions.getDefaultInstance().getService();
        String blobName = UUID.randomUUID().toString() + "-" + filePath.getFileName().toString();
        BlobId blobId = BlobId.of(bucketName, blobName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

        try {
            storage.create(blobInfo, Files.readAllBytes(filePath));
            return String.format("gs://%s/%s", bucketName, blobName);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to GCS", e);
        }
    }
}