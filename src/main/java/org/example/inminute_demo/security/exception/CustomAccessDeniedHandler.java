package org.example.inminute_demo.security.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.inminute_demo.apipayload.code.status.ErrorStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

        response.setStatus(ErrorStatus.FORBIDDEN.getHttpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("code", ErrorStatus.FORBIDDEN.getCode());
        errorResponse.put("message", ErrorStatus.FORBIDDEN.getMessage());

        try {
            String jsonResponse = objectMapper.writeValueAsString(errorResponse);
            response.getWriter().write(jsonResponse);
        } catch (IOException e) {
            throw new AuthenticationServiceException("Failed to write authentication response body", e);
        }
    }
}