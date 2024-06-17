package org.example.inminute_demo.global.exception;

import lombok.Getter;
import org.example.inminute_demo.global.apipayload.code.ReasonDto;
import org.example.inminute_demo.global.apipayload.code.status.ErrorStatus;

@Getter
public class GeneralException extends RuntimeException {

    private final ErrorStatus errorStatus;

    public GeneralException(ErrorStatus errorStatus) {
        super(errorStatus.getMessage());
        this.errorStatus = errorStatus;
    }

    public ReasonDto getErrorStatus() {
        return this.errorStatus.getReason();
    }

}
