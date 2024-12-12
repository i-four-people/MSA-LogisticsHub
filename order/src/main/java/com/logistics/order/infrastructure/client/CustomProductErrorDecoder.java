package com.logistics.order.infrastructure.client;

import com.logistics.order.presentation.exception.BusinessException;
import com.logistics.order.presentation.exception.ErrorCode;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;

public class CustomProductErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus status = HttpStatus.valueOf(response.status());

        return switch (status) {
            case BAD_REQUEST -> new BusinessException(ErrorCode.INVALID_INPUT);
            case NOT_FOUND -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
            case CONFLICT -> new BusinessException(ErrorCode.OUT_OF_STOCK);
            default -> new BusinessException(ErrorCode.INTERNAL_ERROR);
        };
    }

}
