package org.example.inminute_demo.apipayload.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.inminute_demo.apipayload.code.BaseCode;
import org.example.inminute_demo.apipayload.code.ReasonDto;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseCode {

    // 사용자
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER4001", "존재하지 않는 사용자입니다."),
    NOT_FIRST_LOGIN(HttpStatus.BAD_REQUEST, "MEMBER4001", "로그인한 이력이 있는 사용자입니다."),

    // 폴더
    FOLDER_NOT_FOUND(HttpStatus.NOT_FOUND, "FOLDER4001", "존재하지 않는 폴더입니다."),

    // 회의록
    NOTE_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTE4001", "존재하지 않는 회의록입니다."),

    // 래디스
    REDIS_NOT_FOUND(HttpStatus.BAD_REQUEST, "REDIS4001", "Redis 설정에 오류가 발생했습니다."),

    // 리프레쉬 토큰
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "REFRESH_TOKEN4001", "리프레쉬 토큰이 없습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "REFRESH_TOKEN4001", "리프레쉬 토큰이 만료되었습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "REFRESH_TOKEN4001", "유효하지 않은 리프레쉬 토큰입니다."),

    // 엑세스 토큰
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "ACCESS_TOKEN4001", "유효하지 않은 엑세스 토큰입니다."),

    // 인가
    FORBIDDEN(HttpStatus.FORBIDDEN, "MEMBER4001", "접근 권한이 없습니다."),

    // 웹 소켓
    EXCEPTION_IN_WEBSOCKET(HttpStatus.UNAUTHORIZED, "W4001", "웹 소켓 연결 중에 예외가 발생하였습니다.");





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