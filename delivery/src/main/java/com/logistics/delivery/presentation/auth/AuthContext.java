package com.logistics.delivery.presentation.auth;

public class AuthContext {
    private static final ThreadLocal<AuthHeaderInfo> CONTEXT = new ThreadLocal<>();

    public static void set(AuthHeaderInfo info) {
        CONTEXT.set(info);
    }

    public static AuthHeaderInfo get() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }

}
