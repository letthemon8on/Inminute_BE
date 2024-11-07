package org.example.inminute_demo.security.service;

import com.google.common.net.HttpHeaders;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.inminute_demo.apipayload.code.status.ErrorStatus;
import org.example.inminute_demo.domain.LoginType;
import org.example.inminute_demo.domain.Member;
import org.example.inminute_demo.exception.GeneralException;
import org.example.inminute_demo.redis.RedisClient;
import org.example.inminute_demo.repository.MemberRepository;
import org.example.inminute_demo.security.dto.MemberInfoRequest;
import org.example.inminute_demo.security.google.GoogleClient;
import org.example.inminute_demo.security.google.dto.GoogleProfile;
import org.example.inminute_demo.security.google.dto.GoogleToken;
import org.example.inminute_demo.security.jwt.JWTUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SocialLoginService {

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUrl;

    private final MemberRepository memberRepository;
    private final JWTUtil jwtUtil;
    private final TokenService tokenService;

    // private final KakaoClient kakaoClient;
    private final RedisClient redisClient;
    private final GoogleClient googleClient;

    @Transactional
    public void signInWithGoogle(String code, MemberInfoRequest memberInfoRequest, HttpServletResponse response) {
        // 구글로 액세스 토큰 요청하기
        GoogleToken googleAccessToken = googleClient.getGoogleAccessToken(code, googleRedirectUrl);

        // 구글에 있는 사용자 정보 반환
        GoogleProfile googleProfile = googleClient.getMemberInfo(googleAccessToken);

        // 반환된 정보의 이메일 기반으로 사용자 테이블에서 계정 정보 조회 진행
        String email = googleProfile.email();
        if (email == null) {
            throw new GeneralException(ErrorStatus.MEMBER_NOT_FOUND);
        }

        // 리소스 서버에서 발급 받은 정보로 사용자를 특정할 아이디값 생성
        String username = "google" + email;

        //bussiness logic: 사용자 정보가 이미 있다면 로그인 타입 확인 후 해당 사용자 정보를 반환하고, 없다면 새로운 사용자 정보를 생성하여 반환
        Member member = memberRepository.findByUsername(username)
                .orElseGet(() -> createUser(username, googleProfile.email(), memberInfoRequest, LoginType.GOOGLE));

        if (member.getLoginType() != LoginType.GOOGLE) {
            throw new GeneralException(ErrorStatus.ALREADY_EXIST_MEMBER);
        }

        // jwt 발급
        String accessToken = jwtUtil.createJwt("access", username, "ROLE_USER", 60000000000L); // 임의로 10000배로 해놓았음. 나중에 수정 필요.
        String refreshToken = jwtUtil.createJwt("refresh",username, "ROLE_USER",864000000L);

        // ResponseCookie 사용
        response.addHeader(HttpHeaders.SET_COOKIE, tokenService.createResponseCookie("accessToken", accessToken).toString());
        response.addHeader(HttpHeaders.SET_COOKIE, tokenService.createResponseCookie("refreshToken", refreshToken).toString());

        response.setStatus(HttpStatus.OK.value());

        redisClient.setValue(member.getUsername(), refreshToken, 1000 * 60 * 60 * 24 * 7L);
    }

    /*@Transactional
    public SignInResponse signInWithKakao(String code, UserInfoRequest userInfoRequest) {
        // 카카오로 액세스 토큰 요청하기
        KakaoToken kakaoToken = kakaoClient.getAccessTokenFromKakao(code);

        // 카카오에 있는 사용자 정보 반환
        KakaoProfile kakaoProfile = kakaoClient.getMemberInfo(kakaoToken);

        // 반환된 정보의 이메일 기반으로 사용자 테이블에서 계정 정보 조회 진행
        String email = kakaoProfile.kakao_account().email();
        if (email == null) {
            throw new GeneralException(ErrorStatus.USER_NOT_FOUND);
        }

        //bussiness logic: 사용자 정보가 이미 있다면 로그인 타입 확인 후 해당 사용자 정보를 반환하고, 없다면 새로운 사용자 정보를 생성하여 반환
        User user = userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseGet(() -> createUser(email, userInfoRequest, LoginType.KAKAO));

        if (user.getLoginType() != LoginType.KAKAO) {
            throw new GeneralException(ErrorStatus.ALREADY_EXIST_USER);
        }

        TokenResponse tokenResponse = jwtProvider.createToken(user);
        redisClient.setValue(user.getEmail(), tokenResponse.getRefreshToken(), 1000 * 60 * 60 * 24 * 7L);

        return AuthConverter.toSignInResDto(user, tokenResponse);
    }*/

    @Transactional
    public Member createUser(String username, String email, MemberInfoRequest memberInfoRequest, LoginType loginType) {
        Member member = Member.builder()
                .username(username)
                .email(email)
                .role("ROLE_USER")
                .name(memberInfoRequest.name())
                .isFirst(true)
                .loginType(loginType)
                .build();
        return memberRepository.save(member);
    }
}