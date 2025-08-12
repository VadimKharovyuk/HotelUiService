package com.example.hoteluiservice.exception;
// Для проблем с User Service

public class UserServiceClientException extends UiServiceException {
    public UserServiceClientException(String message) {
        super(message);
    }

    public UserServiceClientException(String message, Throwable cause) {
        super(message, cause);
    }
}