package com.gamerlink.identity.exception;

public class InvalidCodeException extends RuntimeException {
    public InvalidCodeException() {
        super("Invalid Code");
    }
}
