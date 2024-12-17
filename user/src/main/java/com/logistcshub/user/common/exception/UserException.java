package com.logistcshub.user.common.exception;

import com.logistcshub.user.common.message.ExceptionMessage;
import org.springframework.http.HttpStatus;

public class UserException extends RuntimeException {

    private final ExceptionMessage message;

    public UserException(ExceptionMessage message) {
        super("[User Exception] : " + message.getMessage());
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return message.getHttpStatus();
    }

    public String getMessage() {
        return message.getMessage();
    }
}
