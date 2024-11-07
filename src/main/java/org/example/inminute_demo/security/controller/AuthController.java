package org.example.inminute_demo.security.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.inminute_demo.apipayload.ApiResponse;
import org.example.inminute_demo.security.dto.MemberInfoRequest;
import org.example.inminute_demo.security.service.SocialLoginService;
import org.springframework.web.bind.annotation.*;

@Tag(name = "OAuth2", description = "소셜 로그인 관련 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final SocialLoginService socialLoginService;

    @PostMapping("/google/sign-in")
    @Operation(summary = "구글 로그인", description = "구글 로그인을 통한 회원가입 및 로그인")
    public ApiResponse<?> signInWithGoogle(@RequestParam("code") String code,
                                           @RequestBody(required = false) MemberInfoRequest memberInfoRequest,
                                           HttpServletResponse response) {
        socialLoginService.signInWithGoogle(code, memberInfoRequest, response);
        return ApiResponse.onSuccess("구글 로그인 성공");
    }

    /*@PostMapping("/kakao/sign-in")
    @Operation(summary = "카카오 로그인", description = "카카오 로그인을 통한 회원가입 및 로그인")
    public ApiResponse<?> signInWithKakao(@RequestParam("code") String code,
                                          @RequestBody(required = false) MemberInfoRequest memberInfoRequest,
                                          HttpServletResponse response) {
        socialLoginService.signInWithKakao(code, memberInfoRequest, response);
        return ApiResponse.onSuccess("카카오 로그인 성공");
    }*/
}
