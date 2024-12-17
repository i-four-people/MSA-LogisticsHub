package com.logiticshub.product.application;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

public class AuthorizationUtil {

    public static void checkUserRole(String role, List<String> allowedRoles){
        if(!allowedRoles.contains(role)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }
    }
}
