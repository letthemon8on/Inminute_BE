package org.example.inminute_demo.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.example.inminute_demo.security.dto.CustomOAuth2User;
import org.example.inminute_demo.security.dto.UserDTO;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        System.out.println("-------------------------JWT FILTER------------------------");

        // JWT 만료 후 재로그인 시 무한 루프 해결
        String requestUri = request.getRequestURI();

        if (requestUri.matches("^\\/login(?:\\/.*)?$")) {

            filterChain.doFilter(request, response);
            return;
        }
        if (requestUri.matches("^\\/oauth2(?:\\/.*)?$")) {

            filterChain.doFilter(request, response);
            return;
        }

        /*// 헤더에서 access키에 담긴 토큰을 꺼냄
        String accessToken = request.getHeader("accessToken");*/

        //cookie들을 불러온 뒤 Authorization Key에 담긴 쿠키를 찾음
        String accessToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {

                System.out.println("cookie = " + cookie.getName() + "= " + cookie.getValue());
                if (cookie.getName().equals("accessToken")) {

                    accessToken = cookie.getValue();
                    break;
                }
            }
        }

        if (accessToken == null && requestUri.startsWith("/notes/detail/")) {

            String uuid = requestUri.substring("/notes/detail/".length()); // "123e4567-e89b-12d3-a456-426614174000"
            String redirectUrl = "https://inminute.kr/?redirect=" + uuid;
            response.sendRedirect(redirectUrl);
            return;
        }

        /*// Authorization 헤더 검증
        if (accessToken == null) {

            System.out.println("token null");
            filterChain.doFilter(request, response);

            // 조건이 해당되면 메소드 종료 (필수)
            return;
        }*/

        // 토큰 소멸 시간 검증
        if (jwtUtil.isExpired(accessToken)) {

            System.out.println("token expired");
            filterChain.doFilter(request, response);

            // 조건이 해당되면 메소드 종료 (필수)
            return;
        }

        // 토큰에서 username과 role 획득
        String username = jwtUtil.getUsername(accessToken);
        String role = jwtUtil.getRole(accessToken);

        // userDTO를 생성하여 값 set
        UserDTO userDTO = UserDTO.builder()
                .username(username)
                .role(role)
                .build();

        System.out.println(userDTO);

        // UserDetails에 회원 정보 객체 담기
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);

        // 스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());
        // 세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
