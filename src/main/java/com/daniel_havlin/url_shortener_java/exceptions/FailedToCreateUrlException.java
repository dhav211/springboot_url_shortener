package com.daniel_havlin.url_shortener_java.exceptions;

public class FailedToCreateUrlException extends RuntimeException {
    public FailedToCreateUrlException(String message) {
        super(message);
    }
}
