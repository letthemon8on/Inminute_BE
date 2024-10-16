package org.example.inminute_demo.chat.exception;

import lombok.Getter;
import org.example.inminute_demo.apipayload.code.status.ErrorStatus;

@Getter
public class WebSocketException extends RuntimeException {

    private static final ErrorStatus ERROR_CODE = ErrorStatus.EXCEPTION_IN_WEBSOCKET;
    private static final String MESSAGE_KEY = "exception.websocket";
    private final String message;

    public WebSocketException(String message) {
        this.message = message;
    }
}