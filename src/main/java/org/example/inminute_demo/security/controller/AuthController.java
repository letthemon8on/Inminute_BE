package org.example.inminute_demo.security.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.inminute_demo.apipayload.ApiResponse;
import org.example.inminute_demo.security.service.AuthService;
import org.springframework.web.bind.annotation.*;

@Tag(name = "OAuth2", description = "소셜 로그인 관련 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/google/sign-in")
    @Operation(summary = "구글 로그인", description = "구글 로그인을 통한 회원가입 및 로그인")
    public ApiResponse<?> signInWithGoogle(@RequestParam("code") String code,
                                           HttpServletResponse response) {
        authService.signInWithGoogle(code, response);
        return ApiResponse.onSuccess("구글 로그인 성공");
    }

    @PostMapping("/kakao/sign-in")
    @Operation(summary = "카카오 로그인", description = "카카오 로그인을 통한 회원가입 및 로그인")
    public ApiResponse<?> signInWithKakao(@RequestParam("code") String code,
                                          HttpServletResponse response) {
        authService.signInWithKakao(code, response);
        return ApiResponse.onSuccess("카카오 로그인 성공");
    }

    @PostMapping("/sign-out")
    @Operation(summary = "로그아웃", description = "소셜 로그인 사용자 로그아웃")
    public ApiResponse<?> signOut(HttpServletRequest request, HttpServletResponse response) {

        authService.signOut(request, response);
        return ApiResponse.onSuccess("로그아웃 성공");
    }
}
