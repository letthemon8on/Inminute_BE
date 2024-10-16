package org.example.inminute_demo.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@OpenAPIDefinition(
        info = @Info(title = "Inminute API 명세서",
                description = "Let On Inminute API 명세서입니다.",
                version = "v1"),
        servers = {@Server(url = "https://api.inminute.kr", description = "https url"),
        @Server(url = "/", description = "Default Server URL")})
@RequiredArgsConstructor
@Configuration
public class SwaggerConfig {

    // 로그아웃 엔드포인트 수동 추가
    PathItem logoutPath = new PathItem()
            .post(new io.swagger.v3.oas.models.Operation()
                    .summary("로그아웃")
                    .description("쿠키의 jwt 토큰 및 Redis에 저장된 Refresh 토큰을 삭제하여 로그아웃을 진행합니다.")
                    .tags(List.of("Logout-endpoint"))  // 태그 추가
                    .responses(new ApiResponses().addApiResponse("200",
                                    new ApiResponse().description("로그아웃 성공"))
                            .addApiResponse("401",
                                    new ApiResponse().description("인증 실패")))
            );

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .paths(new Paths()
                        .addPathItem("/logout", logoutPath)); // 로그아웃 엔드포인트 추가
    }
}