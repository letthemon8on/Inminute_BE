package org.example.inminute_demo.zoom.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@RestController
public class ZoomController {

    @Autowired
    private ZoomService zoomService;

    @GetMapping("/zoomApi")
    public ZoomToken getZoomToken(@RequestParam String code) throws IOException, NoSuchAlgorithmException {
        String redirectUri = "http://localhost:8080/zoomApi";
        String clientId = "G1cmu02jTSaxutApfmFYVA";
        String clientSecret = "sZFX1x0JC2fOG2rDUr09iM68Ts6tgA9j";

        return zoomService.getAccessToken(code, redirectUri, clientId, clientSecret);
    }

    @GetMapping("/zoomApi/refresh")
    public String refreshZoomToken() throws IOException {
        String clientId = "G1cmu02jTSaxutApfmFYVA";
        String clientSecret = "sZFX1x0JC2fOG2rDUr09iM68Ts6tgA9j";

        return zoomService.refreshToken(clientId, clientSecret);
    }
}