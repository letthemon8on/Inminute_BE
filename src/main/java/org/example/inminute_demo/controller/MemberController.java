package org.example.inminute_demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.inminute_demo.apipayload.ApiResponse;
import org.example.inminute_demo.dto.member.request.MemberRequest;
import org.example.inminute_demo.dto.member.response.MemberResponse;
import org.example.inminute_demo.security.dto.CustomOAuth2User;
import org.example.inminute_demo.security.dto.LoginResponse;
import org.example.inminute_demo.service.MemberService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Tag(name = "Member", description = "사용자 관련 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @PatchMapping
    @Operation(summary = "사용자 초기정보 추가", description = "사용자의 isFirst 속성이 true인 경우 닉네임을 추가합니다." +
            "</br> RequestBody에 redirectUrl을 추가할 경우 해당 페이지로 리다이렉트되고, 추가하지 않을 경우 메인 페이지로 리다이렉트됩니다.")
    public void addMemberDetail(@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
                                @RequestBody MemberRequest memberRequest,
                                HttpServletResponse response) throws IOException {

        memberService.addDetail(customOAuth2User, memberRequest);

        String uuid = memberRequest.getUuid();
        if (uuid != null && !uuid.isEmpty()) {
            response.sendRedirect("https://inminute.kr/notes/" + uuid);
        } else {
            response.sendRedirect("https://inminute.kr/home");
        }
    }

    @GetMapping
    @Operation(summary = "사용자 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다.")
    public ApiResponse<LoginResponse> getMemberDetail(@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        return ApiResponse.onSuccess(memberService.getMemberDetail(customOAuth2User));
    }
}
