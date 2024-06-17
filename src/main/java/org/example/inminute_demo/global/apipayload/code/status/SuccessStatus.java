package org.example.inminute_demo.global.apipayload.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.inminute_demo.global.apipayload.code.BaseCode;
import org.example.inminute_demo.global.apipayload.code.ReasonDto;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {

    // 성공 응답
    OK(HttpStatus.OK, "COMMON200", "OK");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDto getReason() {
        return ReasonDto.builder()
                .isSuccess(true)
                .httpStatus(httpStatus)
                .code(code)
                .message(message)
                .build();
    }
}
