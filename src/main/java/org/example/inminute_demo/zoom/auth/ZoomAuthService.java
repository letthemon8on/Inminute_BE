package org.example.inminute_demo.zoom.auth;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ZoomAuthService {

    private static final String ZOOM_URL = "https://zoom.us/oauth/token";
    private static final OkHttpClient client = new OkHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

    private final ZoomTokenRepository zoomTokenRepository;

    public ZoomToken getAccessToken(String code, String redirectUri, String clientId, String clientSecret) throws IOException, NoSuchAlgorithmException {
        String secretKey = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());

        FormBody formBody = new FormBody.Builder()
                .add("code", code)
                .add("redirect_uri", redirectUri)
                .add("grant_type", "authorization_code")
                .add("code_verifier", EncodeUtil.encode(code))
                .build();

        Request zoomRequest = new Request.Builder()
                .url(ZOOM_URL)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Authorization", "Basic " + secretKey)
                .post(formBody)
                .build();

        Response zoomResponse = client.newCall(zoomRequest).execute();
        String zoomText = zoomResponse.body().string();

        System.out.println("Response Code: " + zoomResponse.code());
        System.out.println("Response Body: " + zoomText);

        if (zoomResponse.code() != 200) {
            throw new IOException("Failed to request access token: " + zoomResponse.message());
        }

        Map<String, String> list = mapper.readValue(zoomText, new TypeReference<>() {});

        if (!list.containsKey("access_token") || !list.containsKey("refresh_token")) {
            throw new IOException("Failed to parse access token or refresh token from response");
        }

        ZoomToken zoomToken = new ZoomToken();
        zoomToken.setId(0L);
        zoomToken.setAccessToken(list.get("access_token"));
        zoomToken.setRefreshToken(list.get("refresh_token"));

        zoomTokenRepository.save(zoomToken);

        return zoomToken;
    }

    public String refreshToken(String clientId, String clientSecret) throws IOException {
        String secretKey = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());

        ZoomToken currentToken = zoomTokenRepository.findById(0L).orElseThrow(() -> new RuntimeException("Token not found"));
        FormBody formBody = new FormBody.Builder()
                .add("grant_type", "refresh_token")
                .add("refresh_token", currentToken.getRefreshToken())
                .build();

        Request zoomRequest = new Request.Builder()
                .url(ZOOM_URL)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Authorization", "Basic " + secretKey)
                .post(formBody)
                .build();

        Response zoomResponse = client.newCall(zoomRequest).execute();
        String zoomText = zoomResponse.body().string();

        System.out.println("Response Code: " + zoomResponse.code());
        System.out.println("Response Body: " + zoomText);

        if (zoomResponse.code() != 200) {
            throw new IOException("Failed to refresh token: " + zoomResponse.message());
        }

        Map<String, String> list = mapper.readValue(zoomText, new TypeReference<>() {});

        if (!list.containsKey("access_token") || !list.containsKey("refresh_token")) {
            throw new IOException("Failed to parse access token or refresh token from response");
        }

        currentToken.setAccessToken(list.get("access_token"));
        currentToken.setRefreshToken(list.get("refresh_token"));

        zoomTokenRepository.save(currentToken);

        return list.get("access_token");
    }
}
