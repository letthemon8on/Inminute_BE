package org.example.inminute_demo.zoom.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@RestController
public class ZoomAuthController {

    @Autowired
    private ZoomAuthService zoomAuthService;

    // Zoom access code endpoint
    // https://zoom.us/oauth/authorize?response_type=code&client_id=G1cmu02jTSaxutApfmFYVA&redirect_uri=http://localhost:8080/zoomApi

    // access code endpoint 입력하면 자동으로 호출 -> ZoomToken 엔티티에 엑세스 토큰, 리프레시 토큰 저장 완료
    @GetMapping("/zoomApi")
    public ZoomToken getZoomToken(@RequestParam String code) throws IOException, NoSuchAlgorithmException {
        String redirectUri = "http://localhost:8080/zoomApi";
        String clientId = "G1cmu02jTSaxutApfmFYVA";
        String clientSecret = "sZFX1x0JC2fOG2rDUr09iM68Ts6tgA9j";

        return zoomAuthService.getAccessToken(code, redirectUri, clientId, clientSecret);
    }

    // access token 만료 시 호출하여 재발급
    @GetMapping("/zoomApi/refresh")
    public String refreshZoomToken() throws IOException {
        String clientId = "G1cmu02jTSaxutApfmFYVA";
        String clientSecret = "sZFX1x0JC2fOG2rDUr09iM68Ts6tgA9j";

        return zoomAuthService.refreshToken(clientId, clientSecret);
    }
}