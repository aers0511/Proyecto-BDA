package com.tutiket.exception;

public class CancellationExpiredException extends BusinessException {
    public CancellationExpiredException(String message) {
        super(message);
    }
}