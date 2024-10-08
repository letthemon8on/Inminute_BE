package org.example.inminute_demo.apipayload.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.inminute_demo.apipayload.code.BaseCode;
import org.example.inminute_demo.apipayload.code.ReasonDto;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseCode {

    // 에러 응답
    INTERNER_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "서버 에러"),

    // 로그인 관련
    JOIN_ALREADY_EXISTS(HttpStatus.CONFLICT, "user already exists", "이미 존재하는 아이디입니다."),

    LOGIN_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "LOGIN FAIL", "아이디 또는 비밀번호를 확인하세요"),

    // 세션 에러
    SESSION_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "유효하지 않은 세션입니다."),

    // 폴더 조회 실패
    FOLDER_NOT_FOUND(HttpStatus.NOT_FOUND, "NOT FOUND", "존재하지 않는 폴더입니다."),

    // 회의록 조회 실패
    NOTE_NOT_FOUND(HttpStatus.NOT_FOUND, "NOT FOUND", "존재하지 않는 회의록입니다."),

    REDIS_NOT_FOUND(HttpStatus.BAD_REQUEST, "REDIS4001", "Redis 설정에 오류가 발생했습니다.");





    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDto getReason() {
        return ReasonDto.builder()
                .isSuccess(false)
                .httpStatus(httpStatus)
                .code(code)
                .message(message)
                .build();
    }

}