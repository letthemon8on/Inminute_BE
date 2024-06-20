package org.example.inminute_demo.zoom.auth;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.inminute_demo.global.apipayload.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequiredArgsConstructor
public class ZoomAuthController {

    private final ZoomAuthService zoomAuthService;

    // access code endpoint 입력하면 자동으로 호출 -> ZoomToken 엔티티에 엑세스 토큰, 리프레시 토큰 저장 완료
    @GetMapping("/zoomApi")
    public ModelAndView getZoomToken(@RequestParam String code) throws IOException, NoSuchAlgorithmException {
        zoomAuthService.getAccessToken(code);
        return new ModelAndView("returnPage");
    }

    // access token 만료 시 호출하여 재발급
    @GetMapping("/zoomApi/refresh")
    public ApiResponse<?> refreshZoomToken() throws IOException {
        zoomAuthService.refreshToken();
        return ApiResponse.onSuccess("Access Token 재발급됨");
    }
}