package org.example.inminute_demo.security.service;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.inminute_demo.apipayload.code.status.ErrorStatus;
import org.example.inminute_demo.exception.GeneralException;
import org.example.inminute_demo.redis.RedisClient;
import org.example.inminute_demo.security.jwt.JWTUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JWTUtil jwtUtil;
    private final RedisClient redisClient;

    public Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60 * 60 * 60);
        cookie.setSecure(true); // https를 사용할 경우
        cookie.setPath("/"); // 쿠키가 적용될 경로
        cookie.setDomain(".inminute.kr"); // 루트 도메인과 서브도메인에서 쿠키 공유
        cookie.setHttpOnly(true);
        return cookie;
    }

    public void reissueToken(HttpServletRequest request, HttpServletResponse response) {

        //get refresh token
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("refreshToken")) {
                System.out.println("--------reissueToken-------");

                refresh = cookie.getValue();
            }
        }

        if (refresh == null) {
            //response status code
            throw new GeneralException(ErrorStatus.REFRESH_TOKEN_NOT_FOUND);
        }

        //expired check
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {

            //response status code
            throw new GeneralException(ErrorStatus.REFRESH_TOKEN_EXPIRED);
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refresh);

        if (!category.equals("refresh")) {

            //response status code
            throw new GeneralException(ErrorStatus.INVALID_REFRESH_TOKEN);
        }

        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);

        //DB에 저장되어 있는지 확인
        String redisRefresh = redisClient.getValue(username);
        if (StringUtils.isEmpty(redisRefresh) || !refresh.equals(redisRefresh)) {

            //response body
            throw new GeneralException(ErrorStatus.INVALID_REFRESH_TOKEN);
        }

        //make new JWT
        String newAccess = jwtUtil.createJwt("access", username, role, 6000000000L); // 임의로 10000배 늘림
        String newRefresh = jwtUtil.createJwt("refresh", username, role, 86400000L);

        //Refresh 토큰 저장하고 기존의 Refresh 토큰 삭제 후에 새 refresh 토큰 저장
        redisClient.deleteValue(username);
        redisClient.setValue(username, newRefresh, 864000000L);

        //response
        response.addCookie(createCookie("accessToken", newAccess));
        response.addCookie(createCookie("refreshToken", newRefresh));
        response.setStatus(HttpStatus.OK.value());
    }

}