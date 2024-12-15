package com.logistcshub.user.infrastructure.common;

import lombok.NonNull;

public interface CommonResponse {

    boolean success();

    @NonNull
    String message();
}