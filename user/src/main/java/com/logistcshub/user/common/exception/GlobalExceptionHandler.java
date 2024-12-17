package com.logistcshub.user.common.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import java.util.List;

import com.logistcshub.user.common.message.ExceptionMessage;
import com.logistcshub.user.common.response.CommonResponse;
import com.logistcshub.user.common.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = UserException.class)
    public ResponseEntity<? extends CommonResponse> handleProductException(UserException e) {

        return ResponseEntity.status(e.getHttpStatus())
                .body(ErrorResponse.of(e.getMessage()));
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<? extends CommonResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {

        if (e.getCause() instanceof UserException) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(ErrorResponse.of(ExceptionMessage.USER_NOT_FOUND.getMessage()));
        }

        List<InvalidMethodResponse> invalidInputResList = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(ex -> new InvalidMethodResponse(ex.getField(),
                        ex.getDefaultMessage())) // defaultMessage 필드명을 message 변경
                .toList();

        return ResponseEntity.status(BAD_REQUEST)
                .body(ErrorResponse.of(invalidInputResList.toString()));
    }

    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public ResponseEntity<? extends CommonResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e) {

        return ResponseEntity.status(BAD_REQUEST)
                .body(ErrorResponse.of(e.getMessage()));
    }

    @ExceptionHandler(value = AuthorizationDeniedException.class)
    public ResponseEntity<? extends CommonResponse> handleAuthDeniedException(
            AuthorizationDeniedException e) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of(e.getMessage()));
    }


    // 전체 에러 핸들링
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<? extends CommonResponse> handleException(Exception e) {

        log.error("An exception occurred ", e);

        return ResponseEntity.status(BAD_REQUEST).body(ErrorResponse.of(e.getMessage()));
    }
}