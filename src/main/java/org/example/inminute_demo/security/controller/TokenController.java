package org.example.inminute_demo.security.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.inminute_demo.apipayload.ApiResponse;
import org.example.inminute_demo.security.service.TokenService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authorization", description = "스프링 시큐리티 관련 API입니다.")
@RestController
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;

    @PostMapping("/reissue")
    @Operation(summary = "Access Token 재발급", description = "Access Token이 만료되었을 경우 Refresh Token 확인 후 Access Token을 재발급합니다.")
    public ApiResponse<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        tokenService.reissueToken(request, response);
        return ApiResponse.onSuccess("토큰 재발급 성공");
    }
}