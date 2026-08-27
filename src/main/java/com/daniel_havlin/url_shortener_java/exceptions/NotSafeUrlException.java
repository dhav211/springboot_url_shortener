package com.daniel_havlin.url_shortener_java.exceptions;

public class NotSafeUrlException extends RuntimeException {
    public NotSafeUrlException(String message) {
        super(message);
    }
}
