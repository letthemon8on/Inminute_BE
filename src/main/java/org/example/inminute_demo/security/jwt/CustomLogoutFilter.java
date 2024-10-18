package org.example.inminute_demo.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HttpHeaders;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.inminute_demo.apipayload.code.status.ErrorStatus;
import org.example.inminute_demo.redis.RedisClient;
import org.springframework.http.ResponseCookie;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

import static org.example.inminute_demo.security.exception.JwtException.jwtExceptionHandler;

@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private final JWTUtil jwtUtil;
    private final RedisClient redisClient;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        // Servlet -> HttpServlet으로 캐스트
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        // 경로("/logout")와 메서드(POST) 검증
        String requestUri = request.getRequestURI();
        if (!requestUri.matches("^\\/logout$")) {

            filterChain.doFilter(request, response);
            return;
        }
        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {

            filterChain.doFilter(request, response);
            return;
        }

        // 쿠키에서 Refresh 토큰 가져옴
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refreshToken")) {
                refresh = cookie.getValue();
            }
            else if (cookie.getName().equals("accessToken")) {

            }
        }

        // 토큰 존재 여부 확인
        if (refresh == null) {

            // response status code
            jwtExceptionHandler(response, ErrorStatus.REFRESH_TOKEN_NOT_FOUND);
            return;
        }

        // 토큰 만료 여부 확인
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {

            // response status code
            jwtExceptionHandler(response, ErrorStatus.REFRESH_TOKEN_EXPIRED);
            return;
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refresh);
        if (!category.equals("refresh")) {

            // response status code
            jwtExceptionHandler(response, ErrorStatus.INVALID_REFRESH_TOKEN);
            return;
        }

        String username = jwtUtil.getUsername(refresh);

        // DB에 저장되어 있는지 확인
        String redisRefresh = redisClient.getValue(username);
        if (StringUtils.isEmpty(redisRefresh) || !refresh.equals(redisRefresh)) {

            // response body
            jwtExceptionHandler(response, ErrorStatus.INVALID_REFRESH_TOKEN);
            return;
        }

        // 로그아웃 진행
        // Refresh 토큰 DB에서 제거
        redisClient.deleteValue(username);

        // 쿠키에 저장되어 있는 Refresh 토큰, Access 토큰 null값 처리
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", null)
                .maxAge(0)
                .secure(true)
                .path("/")
                .httpOnly(true)
                .domain(".inminute.kr")
                .sameSite("None")
                .build();

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", null)
                .maxAge(0)
                .secure(true)
                .path("/")
                .httpOnly(true)
                .domain(".inminute.kr")
                .sameSite("None")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.setStatus(HttpServletResponse.SC_OK);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(response.getWriter(), "로그아웃 성공");
    }
}