package org.example.inminute_demo.zoom.auth;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.inminute_demo.global.apipayload.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequiredArgsConstructor
public class ZoomAuthController {

    private final ZoomAuthService zoomAuthService;

    // Zoom access code endpoint
    // https://zoom.us/oauth/authorize?response_type=code&client_id=G1cmu02jTSaxutApfmFYVA&redirect_uri=http://localhost:8080/zoomApi

    // access code endpoint 입력하면 자동으로 호출 -> ZoomToken 엔티티에 엑세스 토큰, 리프레시 토큰 저장 완료
    @GetMapping("/zoomApi")
    public ApiResponse<?> getZoomToken(@RequestParam String code, @RequestParam Long noteId, HttpServletResponse response) throws IOException, NoSuchAlgorithmException {
        String redirectUri = "http://localhost:8080/zoomApi";
        String clientId = "G1cmu02jTSaxutApfmFYVA";
        String clientSecret = "sZFX1x0JC2fOG2rDUr09iM68Ts6tgA9j";

        zoomAuthService.getAccessToken(code, redirectUri, clientId, clientSecret);
        response.sendRedirect("http://localhost:5137/note/" + noteId);

        return ApiResponse.onSuccess("Access Token 발급됨");
    }

    // access token 만료 시 호출하여 재발급
    @GetMapping("/zoomApi/refresh")
    public ApiResponse<?> refreshZoomToken() throws IOException {
        String clientId = "G1cmu02jTSaxutApfmFYVA";
        String clientSecret = "sZFX1x0JC2fOG2rDUr09iM68Ts6tgA9j";

        zoomAuthService.refreshToken(clientId, clientSecret);

        return ApiResponse.onSuccess("Access Token 재발급됨");
    }
}