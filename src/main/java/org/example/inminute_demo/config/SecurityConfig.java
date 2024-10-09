package org.example.inminute_demo.config;

import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import org.example.inminute_demo.redis.RedisClient;
import org.example.inminute_demo.security.exception.CustomAccessDeniedHandler;
import org.example.inminute_demo.security.exception.CustomAuthenticationEntryPoint;
import org.example.inminute_demo.security.jwt.CustomLogoutFilter;
import org.example.inminute_demo.security.jwt.JWTFilter;
import org.example.inminute_demo.security.jwt.JWTUtil;
import org.example.inminute_demo.security.oauth2.CustomSuccessHandler;
import org.example.inminute_demo.security.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final JWTUtil jwtUtil;
    private final RedisClient redisClient;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> {
            web.ignoring()
                    .requestMatchers("/api-docs/**", "/swagger-ui/**", "/swagger-ui.html/**", "/v3/api-docs/**", "/swagger-ui/index.html#/**");// 필터를 타면 안되는 경로
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {

                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                        CorsConfiguration configuration = new CorsConfiguration();

                        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://inminute.kr", "http://api.inminute.kr",
                                "https://inminute.kr", "https://api.inminute.kr"));
                        configuration.setAllowedMethods(Collections.singletonList("*"));
                        configuration.setAllowCredentials(true);
                        configuration.setAllowedHeaders(Collections.singletonList("*"));
                        configuration.setMaxAge(3600L);

                        // 쿠키를 반환하여 JWT를 전달하기 때문에 허용 필요
                        configuration.setExposedHeaders(Collections.singletonList("Set-Cookie"));
                        configuration.setExposedHeaders(Collections.singletonList("Authorization"));

                        return configuration;
                    }
                }));

        // csrf disable
        http
                .csrf((auth) -> auth.disable());

        // From 로그인 방식 disable
        http
                .formLogin((auth) -> auth.disable());

        // HTTP Basic 인증 방식 disable
        http
                .httpBasic((auth) -> auth.disable());

        // JWTFilter 추가
        http
                .addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        // JWT 만료 후 재로그인 시 무한 루프 해결
        /*http
                .addFilterAfter(new JWTFilter(jwtUtil), OAuth2LoginAuthenticationFilter.class);*/

        // oauth2
        http
                .oauth2Login((oauth2) -> oauth2
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                                .userService(customOAuth2UserService))
                        .successHandler(customSuccessHandler)
                );

        // 로그아웃 필터
        http
                .addFilterBefore(new CustomLogoutFilter(jwtUtil, redisClient), LogoutFilter.class);

        // 시큐리티 필터 내부 예외처리
        http
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint()) // 인증 예외
                        .accessDeniedHandler(new CustomAccessDeniedHandler())); // 인가 예외

        // 경로별 인가 작업
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/zoomApi").permitAll()
                        .requestMatchers("/swagger-ui.html", "/v3/sapi-docs/**", "/swagger-ui/**").permitAll()
                        .anyRequest().authenticated());

        // 세션 설정 : STATELESS
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}