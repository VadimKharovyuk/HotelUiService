package com.example.hoteluiservice.exception;

public class UiServiceException extends RuntimeException {
    public UiServiceException(String message) {
        super(message);
    }

    public UiServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
