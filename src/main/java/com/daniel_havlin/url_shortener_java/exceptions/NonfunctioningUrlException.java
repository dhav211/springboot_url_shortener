package com.daniel_havlin.url_shortener_java.exceptions;

public class NonfunctioningUrlException extends RuntimeException {
    public NonfunctioningUrlException(String message) {
        super(message);
    }
}
