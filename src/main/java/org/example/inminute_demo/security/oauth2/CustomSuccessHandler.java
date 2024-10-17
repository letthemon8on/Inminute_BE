package org.example.inminute_demo.security.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HttpHeaders;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import org.example.inminute_demo.redis.RedisClient;
import org.example.inminute_demo.security.dto.CustomOAuth2User;
import org.example.inminute_demo.security.dto.LoginResponse;
import org.example.inminute_demo.security.jwt.JWTUtil;
import org.example.inminute_demo.security.service.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.Iterator;

@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final TokenService tokenService;
    private final RedisClient redisClient;

    // JWT 방식의 LoginFilter에서 로그인 성공 시 jwt 발급하는 메서드와 비슷!
    // 차이점은 OAuth2는 쿠키를 통해 프론트엔드에게 JWT 전달
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // OAuth2User 정보 가져오기
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        String username = customUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        // jwt 발급
        String accessToken = jwtUtil.createJwt("access",username, role, 60000000000L); // 임의로 10000배로 해놓았음. 나중에 수정 필요.
        String refreshToken = jwtUtil.createJwt("refresh",username,role,864000000L);

        System.out.println("---------------customSuccessHandler------------------");

        /*// 엑세스 토큰을 헤더에 저장하여 응답
        response.addHeader("accessToken", accessToken);*/

        /*// 토큰을 쿠키에 저장하여 응답
        response.addCookie(tokenService.createCookie("accessToken", accessToken));
        response.addCookie(tokenService.createCookie("refreshToken", refreshToken));*/

        // ResponseCookie 사용
        response.addHeader(HttpHeaders.SET_COOKIE, tokenService.createResponseCookie("accessToken", accessToken).toString());
        response.addHeader(HttpHeaders.SET_COOKIE, tokenService.createResponseCookie("refreshToken", refreshToken).toString());

        response.setStatus(HttpStatus.OK.value());

        // redis에 refresh 토큰 저장
        redisClient.setValue(username, refreshToken, 864000000L);

        Boolean isFirst = customUserDetails.getIsFirst();

        // Oauth2 리다이렉트 과정에서 uuid가 유지되도록 state 파라미터에 추가
        String encodedState = request.getParameter("state");
        String uuid = null;

        // Oauth2가 자동으로 url 인코딩 -> 디코딩 과정 필요
        if (encodedState != null && !encodedState.isEmpty()) {
            // state 값 디코딩하여 uuid 추출
            uuid = new String(Base64.getDecoder().decode(encodedState), StandardCharsets.UTF_8);
        }

        if (uuid != null) { // state 파라미터에 uuid 값이 존재하는 경우 회의록 링크에 접속한 사용자로 판단
            if (isFirst) { // 처음 로그인한 사용자라면 uuid를 쿼리 파라미터로 설정하여 사용자 추가정보 입력 페이지로 리다이렉트
                response.sendRedirect("https://inminute.kr/?source=login&redirect=" + uuid);
            }
            else {
                response.sendRedirect("https://inminute.kr/note/" + uuid);
            }
        }
        else { // 로그인 성공 후 리다이렉트
            if (isFirst) {
                response.sendRedirect("https://inminute.kr/?source=login");
            }
            else {
                response.sendRedirect("https://inminute.kr/home");
            }
        }

        /*// 로그인 응답 반환
        LoginResponse loginResponse = LoginResponse.builder()
                .username(username)
                .role(role)
                .isFirst(isFirst)
                .build();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(response.getWriter(), loginResponse);*/
    }
}
