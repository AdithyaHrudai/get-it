package com.getit.exception;

/** Thrown for invalid input (bad URL, alias already taken). Maps to HTTP 400. */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
