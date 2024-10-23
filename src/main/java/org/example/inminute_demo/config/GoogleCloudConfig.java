package org.example.inminute_demo.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1.SpeechSettings;
import com.google.cloud.speech.v1.stub.SpeechStubSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.InputStream;

@Configuration
public class GoogleCloudConfig {

    @Value("${spring.cloud.gcp.credentials.location}")
    private Resource gcsCredentials;

    private final ResourceLoader resourceLoader;

    public GoogleCloudConfig(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Bean
    public SpeechSettings speechSettings() {
        try (InputStream credentialsStream = gcsCredentials.getInputStream()) {
            return SpeechSettings.newBuilder()
                    .setCredentialsProvider(() -> GoogleCredentials.fromStream(credentialsStream))
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public SpeechStubSettings speechStubSettings() {
        try (InputStream credentialsStream = gcsCredentials.getInputStream()) {
            return SpeechStubSettings.newBuilder()
                    .setCredentialsProvider(() -> GoogleCredentials.fromStream(credentialsStream))
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
