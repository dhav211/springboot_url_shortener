package com.daniel_havlin.url_shortener_java.exceptions;

public class UrlTakenException extends RuntimeException {
    public UrlTakenException(String message) {
        super(message);
    }
}
