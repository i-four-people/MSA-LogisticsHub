package com.logistcshub.hub.common.exception.application.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import org.springframework.validation.FieldError;

@Builder
public record ErrorResponse(
        String code,
        String message,
        @JsonInclude(JsonInclude.Include.NON_EMPTY) List<ValidationError> errors,
        LocalDateTime timestamp
) {

    @Builder
    public record ValidationError(
            String field,
            String message
    ) {

        public static ValidationError of(final FieldError fieldError) {
            return ValidationError.builder()
                    .field(fieldError.getField())
                    .message(fieldError.getDefaultMessage())
                    .build();
        }
    }
}