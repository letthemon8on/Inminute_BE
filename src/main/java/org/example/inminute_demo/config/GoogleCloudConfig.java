package org.example.inminute_demo.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1.SpeechSettings;
import com.google.cloud.speech.v1.stub.SpeechStubSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.FileInputStream;

@Configuration
public class GoogleCloudConfig {

    @Value("${spring.cloud.gcp.credentials.location}")
    private String gcsCredentials;

    @Bean
    public SpeechSettings speechSettings() {
        try {
            return SpeechSettings.newBuilder()
                    .setCredentialsProvider(() -> GoogleCredentials.fromStream(new FileInputStream(gcsCredentials)))
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public SpeechStubSettings speechStubSettings() {
        try {
            return SpeechStubSettings.newBuilder()
                    .setCredentialsProvider(() -> GoogleCredentials.fromStream(new FileInputStream(gcsCredentials)))
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}