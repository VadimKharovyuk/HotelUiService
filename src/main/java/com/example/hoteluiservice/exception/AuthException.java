package com.example.hoteluiservice.exception;

// Для проблем с аутентификацией
public class AuthException extends UiServiceException {
    public AuthException(String message) {
        super(message);
    }

    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }
}